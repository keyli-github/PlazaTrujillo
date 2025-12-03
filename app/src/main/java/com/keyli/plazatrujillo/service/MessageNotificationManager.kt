package com.keyli.plazatrujillo.service

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.keyli.plazatrujillo.data.websocket.WebSocketEvent
import com.keyli.plazatrujillo.data.websocket.WebSocketService
import com.keyli.plazatrujillo.util.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

/**
 * Servicio singleton para manejar notificaciones de mensajes en tiempo real
 * Escucha eventos del WebSocket y muestra notificaciones locales
 */
object MessageNotificationManager {
    
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var isInitialized = false
    private var applicationContext: Context? = null
    
    // Track de la conversación actualmente visible para no mostrar notificaciones
    val activeConversationUid = MutableStateFlow<String?>(null)
    
    // Track si la app está en primer plano
    var isAppInForeground = true
    
    fun initialize(context: Context) {
        if (isInitialized) return
        
        applicationContext = context.applicationContext
        isInitialized = true
        
        observeWebSocketEvents()
    }
    
    private fun observeWebSocketEvents() {
        scope.launch {
            WebSocketService.events.collect { event ->
                when (event) {
                    is WebSocketEvent.NewMessage -> {
                        handleNewMessage(event)
                    }
                    is WebSocketEvent.GeneralNotification -> {
                        showGeneralNotification(event)
                    }
                    else -> {}
                }
            }
        }
    }
    
    private fun handleNewMessage(event: WebSocketEvent.NewMessage) {
        val context = applicationContext ?: return
        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid
        
        // No mostrar notificación si:
        // 1. El mensaje es del usuario actual
        // 2. La conversación está activa
        if (event.senderUid == currentUserUid) return
        if (event.senderUid == activeConversationUid.value) return
        
        // Construir texto de la notificación
        val notificationText = when (event.message.messageType) {
            "image" -> "📷 Te envió una imagen"
            "file" -> "📎 Te envió: ${event.message.attachmentName ?: "archivo"}"
            else -> event.message.text ?: "Nuevo mensaje"
        }
        
        // Buscar el nombre del remitente (idealmente lo tendríamos cacheado)
        // Por ahora usamos un texto genérico
        val title = "Nuevo mensaje"
        
        NotificationHelper.showMessageNotification(
            context = context,
            title = title,
            body = notificationText,
            senderUid = event.senderUid
        )
    }
    
    private fun showGeneralNotification(event: WebSocketEvent.GeneralNotification) {
        val context = applicationContext ?: return
        
        NotificationHelper.showMessageNotification(
            context = context,
            title = event.title,
            body = event.message
        )
    }
    
    /**
     * Llamar cuando el usuario entra a una conversación
     */
    fun setActiveConversation(uid: String?) {
        activeConversationUid.value = uid
    }
    
    /**
     * Llamar cuando el usuario sale de una conversación
     */
    fun clearActiveConversation() {
        activeConversationUid.value = null
    }
    
    /**
     * Llamar cuando la app va a background/foreground
     */
    fun setAppForegroundState(inForeground: Boolean) {
        isAppInForeground = inForeground
    }
}
