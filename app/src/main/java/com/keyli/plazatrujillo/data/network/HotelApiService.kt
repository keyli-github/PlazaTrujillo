package com.keyli.plazatrujillo.data.network

import com.keyli.plazatrujillo.data.model.ReservationResponse // Asegúrate de importar tu modelo
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface HotelApiService {

    // Django URL: /api/reservations/
    // Como definiremos la base URL hasta "api/", aquí solo ponemos lo que sigue.
    @GET("reservations/list/")
    suspend fun getReservations(
        @Header("Authorization") token: String
    ): Response<ReservationResponse>

}