package com.keyli.plazatrujillo.ui.viewmodel // O el paquete donde lo crees

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keyli.plazatrujillo.data.AuthRepository
import com.keyli.plazatrujillo.data.ReservationRepository
import com.keyli.plazatrujillo.data.model.Reservation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ReservaViewModel : ViewModel() {

    // Repositorios
    private val authRepository = AuthRepository()
    private val reservationRepository = ReservationRepository()

    // Estado de la UI (Cargando, Lista de Reservas, o Error)
    private val _reservas = MutableStateFlow<List<Reservation>>(emptyList())
    val reservas: StateFlow<List<Reservation>> = _reservas

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        // Al crearse el ViewModel, intentamos cargar los datos
        cargarReservas()
    }

    fun cargarReservas() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            // 1. Obtener Token de Firebase (usando tu AuthRepository existente)
            // Nota: Aquí simplificamos la llamada para obtener el token directo
            // En un caso real, idealmente tu AuthRepository tendría una función simple 'getToken'
            val tokenResult = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.getIdToken(true)?.await()
            val token = tokenResult?.token

            if (token != null) {
                // 2. Pedir reservas a Django
                val lista = reservationRepository.obtenerReservas(token)

                if (lista != null) {
                    _reservas.value = lista
                } else {
                    _error.value = "Error al obtener datos del servidor"
                }
            } else {
                _error.value = "No se pudo autenticar (Token nulo)"
            }

            _isLoading.value = false
        }
    }
}