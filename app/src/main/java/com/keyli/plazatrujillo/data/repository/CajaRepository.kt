package com.keyli.plazatrujillo.data.repository

import com.keyli.plazatrujillo.data.api.ApiClient
import com.keyli.plazatrujillo.data.api.ApiService
import com.keyli.plazatrujillo.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CajaRepository {
    private val apiService: ApiService = ApiClient.create()
    
    suspend fun listTodayTransactions(date: String? = null): Result<List<CajaTransaction>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.listTodayTransactions(date)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.transactions ?: emptyList())
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun todayTotals(date: String? = null): Result<CajaTotals> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.todayTotals(date)
            if (response.isSuccessful && response.body() != null && response.body()!!.totals != null) {
                Result.success(response.body()!!.totals!!)
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun createPayment(request: CreatePaymentRequest): Result<CreatePaymentResponse> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.createPayment(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception(errorBody ?: "Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun emitReceipt(request: EmitReceiptRequest): Result<CajaReceipt> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.emitReceipt(request)
            if (response.isSuccessful && response.body() != null && response.body()!!.receipt != null) {
                Result.success(response.body()!!.receipt!!)
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception(errorBody ?: "Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun todayClients(): Result<List<CajaClient>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.todayClients()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.clients ?: emptyList())
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun allClients(): Result<List<CajaClient>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.allClients()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.clients ?: emptyList())
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun paidClients(): Result<List<PaidClient>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.paidClients()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.clients ?: emptyList())
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

