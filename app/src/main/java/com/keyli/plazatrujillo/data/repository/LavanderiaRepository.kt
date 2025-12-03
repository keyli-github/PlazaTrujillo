package com.keyli.plazatrujillo.data.repository

import com.keyli.plazatrujillo.data.api.ApiClient
import com.keyli.plazatrujillo.data.api.ApiService
import com.keyli.plazatrujillo.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LavanderiaRepository {
    private val apiService: ApiService = ApiClient.create()
    
    suspend fun getStock(): Result<List<StockItem>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getStock()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.stock ?: emptyList())
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun upsertStock(request: UpsertStockRequest): Result<List<StockItem>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.upsertStock(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.updated ?: emptyList())
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun sendLaundry(request: SendLaundryRequest): Result<LaundryOrder> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.sendLaundry(request)
            if (response.isSuccessful && response.body() != null && response.body()!!.order != null) {
                Result.success(response.body()!!.order!!)
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun returnOrder(orderCode: String): Result<LaundryOrder> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.returnOrder(orderCode)
            if (response.isSuccessful && response.body() != null && response.body()!!.order != null) {
                Result.success(response.body()!!.order!!)
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun listOrders(): Result<List<LaundryOrder>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.listOrders()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.orders ?: emptyList())
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateDamage(request: UpdateDamageRequest): Result<Map<String, Int>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.updateDamage(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.damage ?: emptyMap())
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

