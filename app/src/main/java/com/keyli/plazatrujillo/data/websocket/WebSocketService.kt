package com.keyli.plazatrujillo.data.websocket

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import okhttp3.*
import java.util.concurrent.TimeUnit

// Modelos de eventos WebSocket
sealed class WebSocketEvent {
    object Connected : WebSocketEvent()
    object Disconnected : WebSocketEvent()
    data class Error(val message: String) : WebSocketEvent()
    data class NewMessage(
        val message: MessageData,
        val senderUid: String
    ) : WebSocketEvent()
    data class UserOnline(val userUid: String, val userEmail: String?) : WebSocketEvent()
    data class UserOffline(val userUid: String, val userEmail: String?) : WebSocketEvent()
    data class GeneralNotification(
        val title: String,
        val message: String,
        val notificationType: String,
        val data: Map<String, Any>?
    ) : WebSocketEvent()
}

data class MessageData(
    val id: Int?,
    val text: String?,
    val senderUid: String?,
    val timestamp: String?,
    val messageType: String?,
    val attachment: String?,
    val attachmentName: String?,
    val attachmentSize: Long?
)

object WebSocketService {
    private const val TAG = "WebSocketService"
    private const val BASE_WS_URL = "wss://web-m6c7e8zv43a9.up-de-fra1-k8s-1.apps.run-on-seenode.com/ws/presence/"
    
    private var webSocket: WebSocket? = null
    private var isConnecting = false
    private var reconnectAttempts = 0
    private val maxReconnectAttempts = 5
    private var reconnectJob: Job? = null
    private var pingJob: Job? = null
    
    private val gson = Gson()
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    private val _connectionState = MutableStateFlow(false)
    val connectionState: StateFlow<Boolean> = _connectionState.asStateFlow()
    
    private val _events = MutableSharedFlow<WebSocketEvent>(replay = 0, extraBufferCapacity = 10)
    val events: SharedFlow<WebSocketEvent> = _events.asSharedFlow()
    
    private val client = OkHttpClient.Builder()
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .pingInterval(30, TimeUnit.SECONDS)
        .build()
    
    fun connect() {
        if (isConnecting || webSocket != null) {
            Log.d(TAG, "Ya conectado o conectando")
            return
        }
        
        scope.launch {
            connectInternal()
        }
    }
    
    private suspend fun connectInternal() {
        if (isConnecting) return
        
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            Log.e(TAG, "No hay usuario autenticado")
            return
        }
        
        try {
            isConnecting = true
            val token = currentUser.getIdToken(false).await().token
            
            if (token == null) {
                Log.e(TAG, "No se pudo obtener el token")
                isConnecting = false
                return
            }
            
            val url = "$BASE_WS_URL?token=$token"
            val request = Request.Builder()
                .url(url)
                .build()
            
            Log.d(TAG, "Conectando a WebSocket...")
            
            webSocket = client.newWebSocket(request, object : WebSocketListener() {
                override fun onOpen(webSocket: WebSocket, response: Response) {
                    Log.d(TAG, "WebSocket conectado")
                    isConnecting = false
                    reconnectAttempts = 0
                    _connectionState.value = true
                    
                    scope.launch {
                        _events.emit(WebSocketEvent.Connected)
                    }
                    
                    startPingPong()
                }
                
                override fun onMessage(webSocket: WebSocket, text: String) {
                    Log.d(TAG, "Mensaje recibido: $text")
                    handleMessage(text)
                }
                
                override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                    Log.d(TAG, "WebSocket cerrando: $code - $reason")
                }
                
                override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                    Log.d(TAG, "WebSocket cerrado: $code - $reason")
                    handleDisconnect()
                }
                
                override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                    Log.e(TAG, "WebSocket error: ${t.message}")
                    isConnecting = false
                    handleDisconnect()
                    
                    scope.launch {
                        _events.emit(WebSocketEvent.Error(t.message ?: "Error de conexión"))
                    }
                    
                    scheduleReconnect()
                }
            })
        } catch (e: Exception) {
            Log.e(TAG, "Error conectando: ${e.message}")
            isConnecting = false
            scheduleReconnect()
        }
    }
    
    private fun handleMessage(text: String) {
        try {
            val json = gson.fromJson(text, JsonObject::class.java)
            val type = json.get("type")?.asString ?: return
            
            scope.launch {
                when (type) {
                    "pong" -> {
                        Log.d(TAG, "Pong recibido")
                    }
                    "connection_status" -> {
                        val status = json.get("status")?.asString
                        Log.d(TAG, "Estado de conexión: $status")
                    }
                    "new_message" -> {
                        val messageObj = json.getAsJsonObject("message")
                        val senderUid = json.get("sender_uid")?.asString ?: ""
                        
                        val messageData = MessageData(
                            id = messageObj?.get("id")?.asInt,
                            text = messageObj?.get("text")?.asString,
                            senderUid = messageObj?.get("sender_uid")?.asString ?: senderUid,
                            timestamp = messageObj?.get("timestamp")?.asString,
                            messageType = messageObj?.get("message_type")?.asString,
                            attachment = messageObj?.get("attachment")?.asString,
                            attachmentName = messageObj?.get("attachment_name")?.asString,
                            attachmentSize = messageObj?.get("attachment_size")?.asLong
                        )
                        
                        _events.emit(WebSocketEvent.NewMessage(messageData, senderUid))
                    }
                    "user_online" -> {
                        val userUid = json.get("user_uid")?.asString ?: ""
                        val userEmail = json.get("user_email")?.asString
                        _events.emit(WebSocketEvent.UserOnline(userUid, userEmail))
                    }
                    "user_offline" -> {
                        val userUid = json.get("user_uid")?.asString ?: ""
                        val userEmail = json.get("user_email")?.asString
                        _events.emit(WebSocketEvent.UserOffline(userUid, userEmail))
                    }
                    "general_notification" -> {
                        val title = json.get("title")?.asString ?: "Notificación"
                        val message = json.get("message")?.asString ?: ""
                        val notificationType = json.get("notification_type")?.asString ?: "info"
                        
                        _events.emit(WebSocketEvent.GeneralNotification(
                            title = title,
                            message = message,
                            notificationType = notificationType,
                            data = null
                        ))
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parseando mensaje: ${e.message}")
        }
    }
    
    private fun handleDisconnect() {
        webSocket = null
        _connectionState.value = false
        pingJob?.cancel()
        
        scope.launch {
            _events.emit(WebSocketEvent.Disconnected)
        }
    }
    
    private fun startPingPong() {
        pingJob?.cancel()
        pingJob = scope.launch {
            while (isActive && webSocket != null) {
                delay(30_000) // Cada 30 segundos
                sendPing()
            }
        }
    }
    
    private fun sendPing() {
        try {
            val pingMessage = gson.toJson(mapOf("type" to "ping"))
            webSocket?.send(pingMessage)
        } catch (e: Exception) {
            Log.e(TAG, "Error enviando ping: ${e.message}")
        }
    }
    
    private fun scheduleReconnect() {
        if (reconnectAttempts >= maxReconnectAttempts) {
            Log.d(TAG, "Máximo de intentos de reconexión alcanzado")
            return
        }
        
        reconnectJob?.cancel()
        reconnectJob = scope.launch {
            reconnectAttempts++
            val delay = minOf(3000L * (1 shl (reconnectAttempts - 1)), 30000L)
            Log.d(TAG, "Reconectando en ${delay}ms (intento $reconnectAttempts)")
            delay(delay)
            
            if (FirebaseAuth.getInstance().currentUser != null) {
                connectInternal()
            }
        }
    }
    
    fun disconnect() {
        Log.d(TAG, "Desconectando WebSocket")
        reconnectJob?.cancel()
        pingJob?.cancel()
        webSocket?.close(1000, "Usuario desconectado")
        webSocket = null
        isConnecting = false
        reconnectAttempts = 0
        _connectionState.value = false
    }
    
    fun isConnected(): Boolean = _connectionState.value
}
