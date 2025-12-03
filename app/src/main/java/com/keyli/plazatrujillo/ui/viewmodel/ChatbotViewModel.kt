package com.keyli.plazatrujillo.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keyli.plazatrujillo.data.repository.ChatbotRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val type: MessageType,
    val text: String,
    val timestamp: Date = Date()
)

enum class MessageType {
    USER,
    BOT
}

data class ChatbotUiState(
    val messages: List<ChatMessage> = listOf(
        ChatMessage(
            type = MessageType.BOT,
            text = "¡Hola! 👋 Soy tu asistente virtual del Hotel Plaza. Puedo ayudarte con información sobre ingresos, ocupación, reservas y más. ¿En qué puedo asistirte hoy?"
        )
    ),
    val isLoading: Boolean = false,
    val isTyping: Boolean = false,
    val sessionId: String = "session_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(9)}",
    val error: String? = null
)

class ChatbotViewModel(
    private val repository: ChatbotRepository = ChatbotRepository()
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ChatbotUiState())
    val uiState: StateFlow<ChatbotUiState> = _uiState.asStateFlow()
    
    val suggestedQuestions = listOf(
        "¿Cuáles son las ganancias del mes?",
        "¿Cuál es la tasa de ocupación actual?",
        "¿Cuántas reservas tenemos hoy?",
        "Muéstrame los ingresos de esta semana",
        "¿Qué habitaciones están disponibles?",
        "Resumen de check-ins de hoy"
    )
    
    init {
        loadChatHistory()
    }
    
    private fun loadChatHistory() {
        viewModelScope.launch {
            val result = repository.getHistory()
            result.fold(
                onSuccess = { response ->
                    val conversations = response.conversations
                    if (!conversations.isNullOrEmpty()) {
                        val latestConv = conversations.first()
                        val formattedMessages = mutableListOf<ChatMessage>()
                        
                        latestConv.messages?.forEachIndexed { index, msg ->
                            val messageType = if (msg.role == "user") MessageType.USER else MessageType.BOT
                            val timestamp = try {
                                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                                    .parse(msg.timestamp ?: "") ?: Date()
                            } catch (e: Exception) {
                                Date()
                            }
                            
                            formattedMessages.add(
                                ChatMessage(
                                    id = "history_$index",
                                    type = messageType,
                                    text = msg.content ?: "",
                                    timestamp = timestamp
                                )
                            )
                        }
                        
                        if (formattedMessages.isNotEmpty()) {
                            _uiState.value = _uiState.value.copy(
                                messages = formattedMessages,
                                sessionId = latestConv.sessionId ?: _uiState.value.sessionId
                            )
                        }
                    }
                },
                onFailure = { /* Mantener mensaje de bienvenida por defecto */ }
            )
        }
    }
    
    fun sendMessage(text: String) {
        if (text.isBlank() || _uiState.value.isLoading) return
        
        val userMessage = ChatMessage(
            type = MessageType.USER,
            text = text.trim()
        )
        
        _uiState.value = _uiState.value.copy(
            messages = _uiState.value.messages + userMessage,
            isLoading = true,
            isTyping = true,
            error = null
        )
        
        viewModelScope.launch {
            val result = repository.sendMessage(text.trim(), _uiState.value.sessionId)
            
            result.fold(
                onSuccess = { response ->
                    val botMessage = ChatMessage(
                        type = MessageType.BOT,
                        text = response.message ?: "No se pudo obtener respuesta",
                        timestamp = try {
                            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                                .parse(response.timestamp ?: "") ?: Date()
                        } catch (e: Exception) {
                            Date()
                        }
                    )
                    
                    _uiState.value = _uiState.value.copy(
                        messages = _uiState.value.messages + botMessage,
                        isLoading = false,
                        isTyping = false,
                        sessionId = response.sessionId ?: _uiState.value.sessionId
                    )
                },
                onFailure = { exception ->
                    val errorMessage = ChatMessage(
                        type = MessageType.BOT,
                        text = "Lo siento, ha ocurrido un error al procesar tu mensaje. Por favor, inténtalo de nuevo."
                    )
                    
                    _uiState.value = _uiState.value.copy(
                        messages = _uiState.value.messages + errorMessage,
                        isLoading = false,
                        isTyping = false,
                        error = exception.message
                    )
                }
            )
        }
    }
    
    fun endSession() {
        viewModelScope.launch {
            repository.endSession(_uiState.value.sessionId)
            _uiState.value = ChatbotUiState()
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

