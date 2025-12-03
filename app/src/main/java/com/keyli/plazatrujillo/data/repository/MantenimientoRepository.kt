package com.keyli.plazatrujillo.data.repository

import com.keyli.plazatrujillo.data.api.ApiClient
import com.keyli.plazatrujillo.data.api.ApiService
import com.keyli.plazatrujillo.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MantenimientoRepository {
    private val apiService: ApiService = ApiClient.create()
    
    // ==================== SISTEMA DE AGUA CALIENTE ====================
    
    suspend fun getSystemStatus(): Result<SystemStatusResponse> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getSystemStatus()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateSystemStatus(request: UpdateSystemStatusRequest): Result<UpdateSystemStatusResponse> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.updateSystemStatus(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // ==================== HISTORIAL DE BRIQUETAS ====================
    
    suspend fun getBriquetteHistory(): Result<List<BriquetteRecord>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getBriquetteHistory()
            if (response.isSuccessful && response.body() != null) {
                val historyResponse = response.body()!!
                Result.success(historyResponse.history ?: emptyList())
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun registerBriquetteChange(request: RegisterBriquetteChangeRequest): Result<RegisterBriquetteChangeResponse> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.registerBriquetteChange(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // ==================== INCIDENCIAS ====================
    
    suspend fun getMaintenanceIssues(): Result<List<MaintenanceIssue>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getMaintenanceIssues()
            if (response.isSuccessful && response.body() != null) {
                val issuesResponse = response.body()!!
                Result.success(issuesResponse.issues ?: emptyList())
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun reportIssue(request: ReportIssueRequest): Result<ReportIssueResponse> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.reportIssue(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deleteIssue(issueId: Int): Result<DeleteIssueResponse> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.deleteIssue(issueId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // ==================== HABITACIONES BLOQUEADAS ====================
    
    suspend fun getBlockedRooms(): Result<List<BlockedRoom>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getBlockedRooms()
            if (response.isSuccessful && response.body() != null) {
                val blockedResponse = response.body()!!
                Result.success(blockedResponse.rooms ?: emptyList())
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun blockRoom(request: BlockRoomRequest): Result<BlockRoomResponse> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.blockRoom(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun unblockRoom(roomId: Int): Result<UnblockRoomResponse> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.unblockRoom(roomId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // ==================== HABITACIONES ====================
    
    suspend fun getAllRooms(): Result<List<Room>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getAllRooms()
            if (response.isSuccessful && response.body() != null) {
                val roomsResponse = response.body()!!
                Result.success(roomsResponse.rooms ?: emptyList())
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

