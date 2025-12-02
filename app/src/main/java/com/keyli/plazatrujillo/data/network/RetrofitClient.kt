package com.keyli.plazatrujillo.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // ⚠️ IMPORTANTE:
    // Si usas EMULADOR usa: "http://10.0.2.2:8000/api/"
    // Si usas CELULAR FÍSICO usa la IP de tu PC: "http://192.168.1.X:8000/api/"
    private const val BASE_URL = "http://192.168.1.6:8000/api/"

    val apiService: HotelApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(HotelApiService::class.java)
    }
}