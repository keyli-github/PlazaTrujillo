package com.keyli.plazatrujillo.data.repository

import com.keyli.plazatrujillo.data.api.ApiClient
import com.keyli.plazatrujillo.data.api.ApiService
import com.keyli.plazatrujillo.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MantenimientoRepository {
    private val apiService: ApiService = ApiClient.create()
    
    suspend fun getSystemStatus(): Result<SystemStatus> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getSystemStatus()
            if (response.isSuccessful && response.body() != null && response.body()!!.status != null) {
                Result.success(response.body()!!.status!!)
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateSystemStatus(request: UpdateSystemStatusRequest): Result<SystemStatus> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.updateSystemStatus(request)
            if (response.isSuccessful && response.body() != null && response.body()!!.status != null) {
                Result.success(response.body()!!.status!!)
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getBriquetteHistory(): Result<List<BriquetteRecord>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getBriquetteHistory()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.history ?: emptyList())
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun registerBriquetteChange(request: RegisterBriquetteChangeRequest): Result<BriquetteRecord> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.registerBriquetteChange(request)
            if (response.isSuccessful && response.body() != null && response.body()!!.record != null) {
                Result.success(response.body()!!.record!!)
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getMaintenanceIssues(): Result<List<MaintenanceIssue>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getMaintenanceIssues()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.issues ?: emptyList())
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun reportIssue(request: ReportIssueRequest): Result<MaintenanceIssue> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.reportIssue(request)
            if (response.isSuccessful && response.body() != null && response.body()!!.issue != null) {
                Result.success(response.body()!!.issue!!)
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deleteIssue(issueId: Int): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.deleteIssue(issueId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getBlockedRooms(): Result<List<BlockedRoom>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getBlockedRooms()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.rooms ?: emptyList())
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun blockRoom(request: BlockRoomRequest): Result<BlockedRoom> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.blockRoom(request)
            if (response.isSuccessful && response.body() != null && response.body()!!.room != null) {
                Result.success(response.body()!!.room!!)
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun unblockRoom(roomId: Int): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.unblockRoom(roomId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

