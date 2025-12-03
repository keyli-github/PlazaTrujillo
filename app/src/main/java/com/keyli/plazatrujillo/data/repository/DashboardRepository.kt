package com.keyli.plazatrujillo.data.repository

import com.keyli.plazatrujillo.data.api.ApiClient
import com.keyli.plazatrujillo.data.api.ApiService
import com.keyli.plazatrujillo.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DashboardRepository {
    private val apiService: ApiService = ApiClient.create()
    
    suspend fun getDashboardMetrics(): Result<DashboardMetricsResponse> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getDashboardMetrics()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getMonthlyRevenue(): Result<List<Double>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getMonthlyRevenue()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.data ?: emptyList())
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getPaymentMethods(): Result<Map<String, Double>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getPaymentMethods()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.data ?: emptyMap())
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getOccupancyWeekly(): Result<List<Double>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getOccupancyWeekly()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.data ?: emptyList())
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getTodayCheckinsCheckouts(): Result<TodayCheckinsCheckoutsResponse> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getTodayCheckinsCheckouts()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getRecentReservations(): Result<List<RecentReservationItem>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getRecentReservations()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.reservations ?: emptyList())
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getStatistics(): Result<StatisticsResponse> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getStatistics()
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

