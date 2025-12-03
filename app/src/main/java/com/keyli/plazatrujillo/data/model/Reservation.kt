package com.keyli.plazatrujillo.data.model // O tu paquete correspondiente

import com.google.gson.annotations.SerializedName

// Esta clase representa la respuesta completa: { "reservations": [ ... ] }
data class ReservationResponse(
    @SerializedName("reservations") val reservations: List<Reservation>
)

// Esta clase representa una sola reserva
data class Reservation(
    val id: Int,
    @SerializedName("reservationId") val reservationId: String, // Coincide con tu serializer
    val guest: String,
    val room: String?,
    val checkIn: String,
    val checkOut: String,
    val total: String,
    val status: String,
    val paid: Boolean
    // Puedes agregar más campos si los necesitas, pero estos son los principales
)