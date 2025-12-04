package com.keyli.plazatrujillo.data.repository

import com.keyli.plazatrujillo.data.api.ApiClient
import com.keyli.plazatrujillo.data.api.ApiService
import com.keyli.plazatrujillo.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ReservationRepository {
    private val apiService: ApiService = ApiClient.create()
    
    suspend fun listReservations(): Result<List<Reservation>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.listReservations()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.reservations ?: emptyList())
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getCalendarEvents(): Result<List<CalendarEvent>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getCalendarEvents()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.events ?: emptyList())
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun createReservation(request: CreateReservationRequest): Result<Reservation> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.createReservation(request)
            if (response.isSuccessful && response.body() != null && response.body()!!.reservation != null) {
                Result.success(response.body()!!.reservation!!)
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateReservation(reservationId: String, request: UpdateReservationRequest): Result<Reservation> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.updateReservation(reservationId, request)
            if (response.isSuccessful && response.body() != null && response.body()!!.reservation != null) {
                Result.success(response.body()!!.reservation!!)
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deleteReservation(reservationId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.deleteReservation(reservationId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getAvailableRooms(
        checkIn: String, 
        checkOut: String, 
        excludeReservation: String? = null
    ): Result<List<Room>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getAvailableRooms(checkIn, checkOut, excludeReservation)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.rooms ?: emptyList())
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getAllRooms(): Result<List<Room>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getAllRooms()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.rooms ?: emptyList())
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getReservationDetail(reservationId: String): Result<Reservation> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getReservationDetail(reservationId)
            if (response.isSuccessful && response.body() != null && response.body()!!.reservation != null) {
                Result.success(response.body()!!.reservation!!)
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun lookupDocument(type: String, number: String): Result<LookupDocumentResponse> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.lookupDocument(type, number)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string() ?: response.message()
                Result.failure(Exception("Error: ${response.code()} - $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getCalendarNotes(): Result<List<CalendarNote>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getCalendarNotes()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.notes ?: emptyList())
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun setCalendarNote(date: String, text: String): Result<CalendarNote> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.setCalendarNote(date, SetCalendarNoteRequest(text))
            if (response.isSuccessful && response.body() != null && response.body()!!.note != null) {
                Result.success(response.body()!!.note!!)
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deleteCalendarNote(date: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.deleteCalendarNote(date)
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

