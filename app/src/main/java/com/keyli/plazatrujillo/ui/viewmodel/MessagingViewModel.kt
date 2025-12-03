package com.keyli.plazatrujillo.ui.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Base64
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keyli.plazatrujillo.data.model.ChatMessageResponse
import com.keyli.plazatrujillo.data.model.MessagingUser
import com.keyli.plazatrujillo.data.repository.MessagingRepository
import com.keyli.plazatrujillo.data.websocket.WebSocketEvent
import com.keyli.plazatrujillo.data.websocket.WebSocketService
import com.keyli.plazatrujillo.service.MessageNotificationManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

// UI Models
data class Contact(
    val uid: String,
    val name: String,
    val email: String,
    val role: String,
    val photo: String?,
    val lastMessage: String?,
    val lastMessageTime: String?,
    val unreadCount: Int,
    val online: Boolean = false
)

data class DisplayMessage(
    val id: Int,
    val text: String,
    val isMe: Boolean,
    val timestamp: String,
    val senderUid: String,
    val messageType: String = "text", // "text", "image", "file"
    val attachment: String? = null,
    val attachmentName: String? = null,
    val attachmentSize: Long? = null
)

data class AttachmentState(
    val uri: Uri? = null,
    val name: String? = null,
    val size: Long = 0,
    val type: String = "file", // "image" or "file"
    val base64: String? = null
)

data class MessagingUiState(
    val contacts: List<Contact> = emptyList(),
    val messages: List<DisplayMessage> = emptyList(),
    val selectedContact: Contact? = null,
    val isLoadingContacts: Boolean = false,
    val isLoadingMessages: Boolean = false,
    val isSendingMessage: Boolean = false,
    val isConnected: Boolean = false,
    val attachment: AttachmentState? = null,
    val error: String? = null
)

class MessagingViewModel(
    private val repository: MessagingRepository = MessagingRepository()
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(MessagingUiState())
    val uiState: StateFlow<MessagingUiState> = _uiState.asStateFlow()
    
    private val currentUserUid: String?
        get() = FirebaseAuth.getInstance().currentUser?.uid
    
    init {
        loadContacts()
        connectWebSocket()
        observeWebSocketEvents()
    }
    
    private fun connectWebSocket() {
        WebSocketService.connect()
        
        // Observar estado de conexión
        viewModelScope.launch {
            WebSocketService.connectionState.collect { isConnected ->
                _uiState.value = _uiState.value.copy(isConnected = isConnected)
            }
        }
    }
    
    private fun observeWebSocketEvents() {
        viewModelScope.launch {
            WebSocketService.events.collect { event ->
                when (event) {
                    is WebSocketEvent.NewMessage -> {
                        handleNewMessage(event)
                    }
                    is WebSocketEvent.UserOnline -> {
                        updateUserOnlineStatus(event.userUid, true)
                    }
                    is WebSocketEvent.UserOffline -> {
                        updateUserOnlineStatus(event.userUid, false)
                    }
                    is WebSocketEvent.Connected -> {
                        _uiState.value = _uiState.value.copy(isConnected = true)
                    }
                    is WebSocketEvent.Disconnected -> {
                        _uiState.value = _uiState.value.copy(isConnected = false)
                    }
                    is WebSocketEvent.Error -> {
                        // Podrías mostrar el error si quieres
                    }
                    else -> {}
                }
            }
        }
    }
    
    private fun handleNewMessage(event: WebSocketEvent.NewMessage) {
        val selectedContact = _uiState.value.selectedContact
        
        // Si el mensaje es para la conversación activa
        if (selectedContact != null && event.senderUid == selectedContact.uid) {
            val newMessage = DisplayMessage(
                id = event.message.id ?: System.currentTimeMillis().toInt(),
                text = event.message.text ?: "",
                isMe = false,
                timestamp = formatMessageTime(event.message.timestamp) ?: getCurrentTime(),
                senderUid = event.senderUid,
                messageType = event.message.messageType ?: "text",
                attachment = event.message.attachment,
                attachmentName = event.message.attachmentName,
                attachmentSize = event.message.attachmentSize
            )
            
            _uiState.value = _uiState.value.copy(
                messages = _uiState.value.messages + newMessage
            )
        } else {
            // Si no es la conversación activa, incrementar badge
            val lastMsgText = when (event.message.messageType) {
                "image" -> "📷 Imagen"
                "file" -> "📎 ${event.message.attachmentName ?: "Archivo"}"
                else -> event.message.text ?: ""
            }
            _uiState.value = _uiState.value.copy(
                contacts = _uiState.value.contacts.map { contact ->
                    if (contact.uid == event.senderUid) {
                        contact.copy(
                            unreadCount = contact.unreadCount + 1,
                            lastMessage = lastMsgText,
                            lastMessageTime = getCurrentTime()
                        )
                    } else {
                        contact
                    }
                }
            )
        }
    }
    
    private fun updateUserOnlineStatus(userUid: String, online: Boolean) {
        _uiState.value = _uiState.value.copy(
            contacts = _uiState.value.contacts.map { contact ->
                if (contact.uid == userUid) {
                    contact.copy(online = online)
                } else {
                    contact
                }
            },
            selectedContact = _uiState.value.selectedContact?.let { selected ->
                if (selected.uid == userUid) selected.copy(online = online) else selected
            }
        )
    }
    
    override fun onCleared() {
        super.onCleared()
        // No desconectamos el WebSocket aquí porque es un singleton
        // y puede ser usado por otras partes de la app
    }
    
    fun loadContacts() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingContacts = true, error = null)
            
            val result = repository.listUsersForMessaging()
            
            result.fold(
                onSuccess = { users ->
                    val contacts = users.map { user ->
                        Contact(
                            uid = user.uid ?: "",
                            name = user.name ?: user.email?.split("@")?.firstOrNull() ?: "Usuario",
                            email = user.email ?: "",
                            role = getRoleName(user.role),
                            photo = user.photo,
                            lastMessage = user.lastMessage,
                            lastMessageTime = formatMessageTime(user.lastMessageTime),
                            unreadCount = user.unreadCount ?: 0
                        )
                    }
                    _uiState.value = _uiState.value.copy(
                        contacts = contacts,
                        isLoadingContacts = false
                    )
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoadingContacts = false,
                        error = exception.message
                    )
                }
            )
        }
    }
    
    fun selectContact(contact: Contact) {
        _uiState.value = _uiState.value.copy(
            selectedContact = contact,
            messages = emptyList()
        )
        loadMessages(contact.uid)
        
        // Marcar esta conversación como activa para evitar notificaciones
        MessageNotificationManager.setActiveConversation(contact.uid)
        
        // Marcar como leídos los mensajes de este contacto
        _uiState.value = _uiState.value.copy(
            contacts = _uiState.value.contacts.map { 
                if (it.uid == contact.uid) it.copy(unreadCount = 0) else it 
            }
        )
    }
    
    fun clearSelectedContact() {
        // Limpiar conversación activa
        MessageNotificationManager.clearActiveConversation()
        
        _uiState.value = _uiState.value.copy(
            selectedContact = null,
            messages = emptyList()
        )
    }
    
    private fun loadMessages(otherUserUid: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingMessages = true)
            
            val result = repository.getMessages(otherUserUid)
            
            result.fold(
                onSuccess = { apiMessages ->
                    val displayMessages = apiMessages.map { msg ->
                        DisplayMessage(
                            id = msg.id ?: 0,
                            text = msg.text ?: "",
                            isMe = msg.senderUid == currentUserUid,
                            timestamp = formatMessageTime(msg.timestamp) ?: "",
                            senderUid = msg.senderUid ?: "",
                            messageType = msg.messageType ?: "text",
                            attachment = msg.attachment,
                            attachmentName = msg.attachmentName,
                            attachmentSize = msg.attachmentSize
                        )
                    }
                    _uiState.value = _uiState.value.copy(
                        messages = displayMessages,
                        isLoadingMessages = false
                    )
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoadingMessages = false,
                        error = exception.message
                    )
                }
            )
        }
    }
    
    // Métodos para manejar archivos adjuntos
    fun setAttachment(uri: Uri, name: String, size: Long, type: String, base64: String) {
        _uiState.value = _uiState.value.copy(
            attachment = AttachmentState(
                uri = uri,
                name = name,
                size = size,
                type = type,
                base64 = base64
            )
        )
    }
    
    fun clearAttachment() {
        _uiState.value = _uiState.value.copy(attachment = null)
    }
    
    fun sendMessage(text: String) {
        val contact = _uiState.value.selectedContact ?: return
        val attachment = _uiState.value.attachment
        
        // Si no hay texto ni archivo, no enviar
        if (text.isBlank() && attachment == null) return
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSendingMessage = true)
            
            val result = if (attachment != null && attachment.base64 != null) {
                // Enviar con archivo adjunto
                repository.sendMessageWithAttachment(
                    otherUserUid = contact.uid,
                    text = text.trim(),
                    messageType = attachment.type,
                    attachmentBase64 = attachment.base64,
                    attachmentName = attachment.name ?: "archivo",
                    attachmentSize = attachment.size
                )
            } else {
                // Enviar solo texto
                repository.sendMessage(contact.uid, text.trim())
            }
            
            result.fold(
                onSuccess = { response ->
                    val newMessage = DisplayMessage(
                        id = response.id ?: System.currentTimeMillis().toInt(),
                        text = response.text ?: text,
                        isMe = true,
                        timestamp = formatMessageTime(response.timestamp) ?: getCurrentTime(),
                        senderUid = currentUserUid ?: "",
                        messageType = response.messageType ?: "text",
                        attachment = response.attachment,
                        attachmentName = response.attachmentName,
                        attachmentSize = response.attachmentSize
                    )
                    
                    _uiState.value = _uiState.value.copy(
                        messages = _uiState.value.messages + newMessage,
                        isSendingMessage = false,
                        attachment = null // Limpiar attachment después de enviar
                    )
                    
                    // Actualizar último mensaje del contacto
                    val lastMsgText = when (response.messageType) {
                        "image" -> "📷 Imagen"
                        "file" -> "📎 ${response.attachmentName ?: "Archivo"}"
                        else -> text.ifBlank { "📎 Archivo" }
                    }
                    updateContactLastMessage(contact.uid, lastMsgText)
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isSendingMessage = false,
                        error = exception.message
                    )
                }
            )
        }
    }
    
    private fun updateContactLastMessage(uid: String, message: String) {
        _uiState.value = _uiState.value.copy(
            contacts = _uiState.value.contacts.map { contact ->
                if (contact.uid == uid) {
                    contact.copy(
                        lastMessage = message,
                        lastMessageTime = getCurrentTime()
                    )
                } else {
                    contact
                }
            }
        )
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    private fun getRoleName(role: String?): String {
        return when (role) {
            "admin" -> "Administrador"
            "receptionist" -> "Recepcionista"
            "housekeeping" -> "Housekeeping"
            else -> "Usuario"
        }
    }
    
    private fun formatMessageTime(isoTime: String?): String? {
        if (isoTime.isNullOrEmpty()) return null
        
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val date = inputFormat.parse(isoTime.substringBefore(".").substringBefore("+"))
            
            if (date != null) {
                val now = Calendar.getInstance()
                val messageDate = Calendar.getInstance().apply { time = date }
                
                when {
                    isSameDay(now, messageDate) -> {
                        SimpleDateFormat("HH:mm", Locale.getDefault()).format(date)
                    }
                    isYesterday(now, messageDate) -> "Ayer"
                    else -> SimpleDateFormat("dd/MM", Locale.getDefault()).format(date)
                }
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
    
    private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }
    
    private fun isYesterday(now: Calendar, date: Calendar): Boolean {
        val yesterday = now.clone() as Calendar
        yesterday.add(Calendar.DAY_OF_YEAR, -1)
        return isSameDay(yesterday, date)
    }
    
    private fun getCurrentTime(): String {
        return SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
    }
}
