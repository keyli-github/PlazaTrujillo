package com.keyli.plazatrujillo.data

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
) {

    // Login
    suspend fun loginWithFirebase(email: String, password: String): Result<String> {
        return try {
            val user = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val token = user.user?.getIdToken(true)?.await()?.token

            if (token != null) {
                Result.success(token)
            } else {
                Result.failure(Exception("Error desconocido: No se pudo obtener el token."))
            }

        } catch (e: Exception) {
            // Aquí mapeamos la excepción para saber qué pasó
            val customError = mapFirebaseError(e)
            Result.failure(customError)
        }
    }

    // Recuperar Contraseña
    suspend fun sendPasswordRecovery(email: String): Result<Unit> {
        return try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            val customError = mapFirebaseError(e)
            Result.failure(customError)
        }
    }

    // Helper para identificar errores de red vs credenciales
    private fun mapFirebaseError(e: Exception): Exception {
        return when (e) {
            is FirebaseNetworkException -> Exception("Sin conexión a internet. Verifica tu red.")
            is FirebaseAuthInvalidCredentialsException -> Exception("Correo o contraseña incorrectos.")
            is FirebaseAuthInvalidUserException -> Exception("La cuenta no existe o ha sido deshabilitada.")
            else -> Exception("Ocurrió un error inesperado: ${e.message}")
        }
    }
}