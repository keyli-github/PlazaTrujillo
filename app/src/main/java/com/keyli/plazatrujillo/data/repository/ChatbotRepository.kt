package com.keyli.plazatrujillo.data.repository

import com.keyli.plazatrujillo.data.api.ApiClient
import com.keyli.plazatrujillo.data.api.ApiService
import com.keyli.plazatrujillo.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ChatbotRepository {
    private val apiService: ApiService = ApiClient.create()
    
    suspend fun sendMessage(message: String, sessionId: String?): Result<ProcessChatbotMessageResponse> = withContext(Dispatchers.IO) {
        try {
            val request = ProcessChatbotMessageRequest(
                message = message,
                sessionId = sessionId
            )
            val response = apiService.processChatbotMessage(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string() ?: "Error desconocido"
                Result.failure(Exception("Error: ${response.code()} - $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getHistory(sessionId: String? = null): Result<ChatbotHistoryResponse> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getChatbotHistory(sessionId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun endSession(sessionId: String): Result<EndChatbotSessionResponse> = withContext(Dispatchers.IO) {
        try {
            val request = EndChatbotSessionRequest(sessionId = sessionId)
            val response = apiService.endChatbotSession(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

