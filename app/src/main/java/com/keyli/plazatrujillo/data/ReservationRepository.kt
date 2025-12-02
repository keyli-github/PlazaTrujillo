package com.keyli.plazatrujillo.data

import com.keyli.plazatrujillo.data.model.Reservation
import com.keyli.plazatrujillo.data.network.RetrofitClient

class ReservationRepository {

    // Función que llamará tu ViewModel
    suspend fun obtenerReservas(token: String): List<Reservation>? {
        return try {
            val response = RetrofitClient.apiService.getReservations("Bearer $token")

            if (response.isSuccessful) {
                // Devolvemos la lista limpia
                response.body()?.reservations
            } else {
                // Hubo error del servidor (401, 404, 500)
                null
            }
        } catch (e: Exception) {
            // Error de conexión (internet, servidor apagado)
            e.printStackTrace()
            null
        }
    }
}