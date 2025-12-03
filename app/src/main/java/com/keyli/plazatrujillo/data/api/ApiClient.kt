package com.keyli.plazatrujillo.data.api

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    // URL base del backend desplegado
    private const val BASE_URL = "https://web-m6c7e8zv43a9.up-de-fra1-k8s-1.apps.run-on-seenode.com"
    
    private val auth = FirebaseAuth.getInstance()
    
    // Interceptor para agregar el token de Firebase a todas las peticiones
    private val authInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()
        val currentUser = auth.currentUser
        
        val requestBuilder = originalRequest.newBuilder()
            .addHeader("Content-Type", "application/json")
        
        if (currentUser != null) {
            try {
                // Obtener el token de forma síncrona
                val tokenResult = kotlinx.coroutines.runBlocking {
                    currentUser.getIdToken(true).await()
                }
                requestBuilder.addHeader("Authorization", "Bearer ${tokenResult.token}")
            } catch (e: Exception) {
                // Si hay error obteniendo el token, continuar sin el header
                e.printStackTrace()
            }
        }
        
        val request = requestBuilder.build()
        chain.proceed(request)
    }
    
    // Cliente HTTP con logging y autenticación
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    
    // Instancia de Retrofit
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    // Función helper para obtener el token de forma asíncrona
    suspend fun getFirebaseToken(): String? {
        return try {
            val currentUser = auth.currentUser
            currentUser?.getIdToken(true)?.await()?.token
        } catch (e: Exception) {
            null
        }
    }
    
    // Crear instancia del servicio de API
    fun create(): ApiService {
        return retrofit.create(ApiService::class.java)
    }
    
    // Método genérico para crear otros servicios si es necesario en el futuro
    fun <T> create(service: Class<T>): T {
        return retrofit.create(service)
    }
}

