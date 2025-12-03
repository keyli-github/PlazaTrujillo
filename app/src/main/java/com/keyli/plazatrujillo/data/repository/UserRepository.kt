package com.keyli.plazatrujillo.data.repository

import com.keyli.plazatrujillo.data.api.ApiClient
import com.keyli.plazatrujillo.data.api.ApiService
import com.keyli.plazatrujillo.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository {
    private val apiService: ApiService = ApiClient.create()
    
    suspend fun listUsers(): Result<List<User>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.listUsers()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.users ?: emptyList())
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun createUser(request: CreateUserRequest): Result<CreateUserResponse> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.createUser(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string() ?: ""
                Result.failure(Exception("Error: ${response.code()} - ${response.message()} - $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateUser(uid: String, request: UpdateUserRequest): Result<String> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.updateUser(uid, request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.message ?: "Usuario actualizado exitosamente")
            } else {
                val errorBody = response.errorBody()?.string() ?: ""
                Result.failure(Exception("Error: ${response.code()} - ${response.message()} - $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deleteUser(uid: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.deleteUser(uid)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun toggleUserStatus(uid: String): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.toggleUserStatus(uid)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.disabled ?: false)
            } else {
                val errorBody = response.errorBody()?.string() ?: ""
                Result.failure(Exception("Error: ${response.code()} - ${response.message()} - $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getOwnProfile(): Result<UserProfile?> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getOwnProfile()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.profile)
            } else if (response.code() == 401) {
                Result.success(null)
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateOwnProfile(request: UpdateProfileRequest): Result<UserProfile> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.updateOwnProfile(request)
            if (response.isSuccessful && response.body() != null && response.body()!!.profile != null) {
                Result.success(response.body()!!.profile!!)
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

