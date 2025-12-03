package com.keyli.plazatrujillo.data.repository

import com.keyli.plazatrujillo.data.api.ApiClient
import com.keyli.plazatrujillo.data.api.ApiService
import com.keyli.plazatrujillo.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MessagingRepository {
    private val apiService: ApiService = ApiClient.create()
    
    suspend fun listUsersForMessaging(): Result<List<MessagingUser>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.listUsersForMessaging()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.users ?: emptyList())
            } else {
                val errorBody = response.errorBody()?.string() ?: "Error desconocido"
                Result.failure(Exception("Error: ${response.code()} - $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getMessages(otherUserUid: String): Result<List<ChatMessageResponse>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getMessages(otherUserUid)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.messages ?: emptyList())
            } else {
                val errorBody = response.errorBody()?.string() ?: "Error desconocido"
                Result.failure(Exception("Error: ${response.code()} - $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun sendMessage(otherUserUid: String, text: String): Result<ChatMessageResponse> = withContext(Dispatchers.IO) {
        try {
            val request = SendMessageRequest(text = text)
            val response = apiService.sendMessage(otherUserUid, request)
            if (response.isSuccessful && response.body() != null && response.body()!!.message != null) {
                Result.success(response.body()!!.message!!)
            } else {
                val errorBody = response.errorBody()?.string() ?: "Error desconocido"
                Result.failure(Exception("Error: ${response.code()} - $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun sendMessageWithAttachment(
        otherUserUid: String, 
        text: String,
        messageType: String,
        attachmentBase64: String,
        attachmentName: String,
        attachmentSize: Long
    ): Result<ChatMessageResponse> = withContext(Dispatchers.IO) {
        try {
            val request = SendMessageRequest(
                text = text,
                messageType = messageType,
                attachment = attachmentBase64,
                attachmentName = attachmentName,
                attachmentSize = attachmentSize
            )
            val response = apiService.sendMessage(otherUserUid, request)
            if (response.isSuccessful && response.body() != null && response.body()!!.message != null) {
                Result.success(response.body()!!.message!!)
            } else {
                val errorBody = response.errorBody()?.string() ?: "Error desconocido"
                Result.failure(Exception("Error: ${response.code()} - $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

