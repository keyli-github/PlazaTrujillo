package com.keyli.plazatrujillo.data

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

private const val TAG = "UserSession"

/**
 * Singleton para manejar la sesión del usuario y su rol
 */
object UserSession {
    private val _userRole = MutableStateFlow(UserRole.ADMIN)
    val userRole: StateFlow<UserRole> = _userRole.asStateFlow()
    
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()
    
    private val _userName = MutableStateFlow<String?>(null)
    val userName: StateFlow<String?> = _userName.asStateFlow()
    
    private val _userEmail = MutableStateFlow<String?>(null)
    val userEmail: StateFlow<String?> = _userEmail.asStateFlow()
    
    /**
     * Inicializa la sesión con el usuario de Firebase
     */
    suspend fun initialize(firebaseUser: com.google.firebase.auth.FirebaseUser) {
        try {
            Log.d(TAG, "Inicializando sesión para: ${firebaseUser.email}")
            val tokenResult = firebaseUser.getIdToken(true).await()
            val roleString = tokenResult.claims["role"] as? String
            
            Log.d(TAG, "Role claim del token: $roleString")
            Log.d(TAG, "Todos los claims: ${tokenResult.claims}")
            
            _userRole.value = when (roleString?.lowercase()) {
                "admin" -> UserRole.ADMIN
                "receptionist" -> UserRole.RECEPTIONIST
                "housekeeping" -> UserRole.HOUSEKEEPING
                else -> {
                    Log.w(TAG, "Rol no reconocido o nulo: $roleString, usando ADMIN por defecto")
                    UserRole.ADMIN
                }
            }
            
            Log.d(TAG, "Rol asignado: ${_userRole.value}")
            
            _isLoggedIn.value = true
            _userName.value = firebaseUser.displayName ?: firebaseUser.email?.substringBefore("@")
            _userEmail.value = firebaseUser.email
            
            Log.d(TAG, "Sesión inicializada: email=${_userEmail.value}, rol=${_userRole.value}")
        } catch (e: Exception) {
            Log.e(TAG, "Error inicializando sesión", e)
            // En caso de error, asignar rol por defecto
            _userRole.value = UserRole.ADMIN
            _isLoggedIn.value = true
        }
    }
    
    /**
     * Limpia la sesión al hacer logout
     */
    fun clear() {
        _userRole.value = UserRole.ADMIN
        _isLoggedIn.value = false
        _userName.value = null
        _userEmail.value = null
        FirebaseAuth.getInstance().signOut()
    }
    
    // Helpers para verificar roles
    fun isAdmin(): Boolean = _userRole.value == UserRole.ADMIN
    fun isReceptionist(): Boolean = _userRole.value == UserRole.RECEPTIONIST
    fun isHousekeeping(): Boolean = _userRole.value == UserRole.HOUSEKEEPING
    
    // Verificar acceso a módulos (igual que React)
    fun canAccessUsers(): Boolean = isAdmin()
    fun canAccessCaja(): Boolean = isAdmin() || isReceptionist()
    fun canAccessLavanderia(): Boolean = isAdmin() || isHousekeeping()
    fun canAccessMantenimiento(): Boolean = isAdmin() || isHousekeeping()
    fun canAccessReservas(): Boolean = true // Todos pueden ver
    fun canAccessMensajes(): Boolean = true // Todos pueden ver
    fun canAccessChatbot(): Boolean = true // Todos pueden ver
    
    // Housekeeping tiene acceso de solo lectura en Reservas
    fun canEditReservas(): Boolean = !isHousekeeping()
}
