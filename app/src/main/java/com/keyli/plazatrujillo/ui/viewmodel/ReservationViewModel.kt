package com.keyli.plazatrujillo.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keyli.plazatrujillo.data.model.*
import com.keyli.plazatrujillo.data.repository.ReservationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ReservationUiState(
    val reservations: List<Reservation> = emptyList(),
    val calendarEvents: List<CalendarEvent> = emptyList(),
    val calendarNotes: Map<String, String> = emptyMap(), // date -> text
    val availableRooms: List<Room> = emptyList(),
    val allRooms: List<Room> = emptyList(),
    val selectedReservation: Reservation? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null,
    val isLookingUpDocument: Boolean = false,
    val lookupResult: LookupDocumentResponse? = null,
    val lookupError: String? = null,
    val isSavingNote: Boolean = false
)

class ReservationViewModel(
    private val repository: ReservationRepository = ReservationRepository()
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ReservationUiState())
    val uiState: StateFlow<ReservationUiState> = _uiState.asStateFlow()
    
    fun loadReservations() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = repository.listReservations()
            
            _uiState.value = result.fold(
                onSuccess = { reservations ->
                    _uiState.value.copy(
                        reservations = reservations,
                        isLoading = false,
                        error = null
                    )
                },
                onFailure = { exception ->
                    _uiState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Error al cargar reservas"
                    )
                }
            )
        }
    }
    
    fun loadCalendarEvents() {
        viewModelScope.launch {
            val result = repository.getCalendarEvents()
            result.fold(
                onSuccess = { events ->
                    _uiState.value = _uiState.value.copy(calendarEvents = events)
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        error = exception.message ?: "Error al cargar eventos del calendario"
                    )
                }
            )
        }
    }
    
    fun loadCalendarNotes() {
        viewModelScope.launch {
            val result = repository.getCalendarNotes()
            result.fold(
                onSuccess = { notes ->
                    val notesMap = notes.associate { (it.date ?: "") to (it.text ?: "") }
                    _uiState.value = _uiState.value.copy(calendarNotes = notesMap)
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        error = exception.message ?: "Error al cargar notas del calendario"
                    )
                }
            )
        }
    }
    
    fun saveCalendarNote(date: String, text: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSavingNote = true)
            val result = repository.setCalendarNote(date, text)
            result.fold(
                onSuccess = { note ->
                    val updatedNotes = _uiState.value.calendarNotes.toMutableMap()
                    updatedNotes[note.date ?: date] = note.text ?: text
                    _uiState.value = _uiState.value.copy(
                        calendarNotes = updatedNotes,
                        isSavingNote = false,
                        successMessage = "Nota guardada"
                    )
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isSavingNote = false,
                        error = exception.message ?: "Error al guardar nota"
                    )
                }
            )
        }
    }
    
    fun deleteCalendarNote(date: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSavingNote = true)
            val result = repository.deleteCalendarNote(date)
            result.fold(
                onSuccess = {
                    val updatedNotes = _uiState.value.calendarNotes.toMutableMap()
                    updatedNotes.remove(date)
                    _uiState.value = _uiState.value.copy(
                        calendarNotes = updatedNotes,
                        isSavingNote = false,
                        successMessage = "Nota eliminada"
                    )
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isSavingNote = false,
                        error = exception.message ?: "Error al eliminar nota"
                    )
                }
            )
        }
    }
    
    fun loadAvailableRooms(checkIn: String, checkOut: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = repository.getAvailableRooms(checkIn, checkOut)
            
            result.fold(
                onSuccess = { rooms ->
                    _uiState.value = _uiState.value.copy(
                        availableRooms = rooms,
                        isLoading = false
                    )
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Error al cargar habitaciones disponibles"
                    )
                }
            )
        }
    }
    
    fun loadAllRooms() {
        viewModelScope.launch {
            val result = repository.getAllRooms()
            result.fold(
                onSuccess = { rooms ->
                    _uiState.value = _uiState.value.copy(allRooms = rooms)
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        error = exception.message ?: "Error al cargar habitaciones"
                    )
                }
            )
        }
    }
    
    fun createReservation(request: CreateReservationRequest) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, successMessage = null)
            val result = repository.createReservation(request)
            
            result.fold(
                onSuccess = { reservation ->
                    _uiState.value = _uiState.value.copy(
                        reservations = _uiState.value.reservations + reservation,
                        isLoading = false,
                        successMessage = "Reserva creada exitosamente"
                    )
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Error al crear la reserva"
                    )
                }
            )
        }
    }
    
    fun updateReservation(reservationId: String, request: UpdateReservationRequest) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, successMessage = null)
            val result = repository.updateReservation(reservationId, request)
            
            result.fold(
                onSuccess = { updatedReservation ->
                    _uiState.value = _uiState.value.copy(
                        reservations = _uiState.value.reservations.map { 
                            if (it.reservationId == reservationId) updatedReservation else it 
                        },
                        isLoading = false,
                        successMessage = "Reserva actualizada exitosamente"
                    )
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Error al actualizar la reserva"
                    )
                }
            )
        }
    }
    
    fun deleteReservation(reservationId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, successMessage = null)
            val result = repository.deleteReservation(reservationId)
            
            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        reservations = _uiState.value.reservations.filter { it.reservationId != reservationId },
                        isLoading = false,
                        successMessage = "Reserva eliminada exitosamente"
                    )
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Error al eliminar la reserva"
                    )
                }
            )
        }
    }
    
    fun getReservationDetail(reservationId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = repository.getReservationDetail(reservationId)
            
            result.fold(
                onSuccess = { reservation ->
                    _uiState.value = _uiState.value.copy(
                        selectedReservation = reservation,
                        isLoading = false
                    )
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Error al cargar detalles de la reserva"
                    )
                }
            )
        }
    }
    
    fun lookupDocument(type: String, number: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLookingUpDocument = true, 
                lookupError = null, 
                lookupResult = null
            )
            val result = repository.lookupDocument(type, number)
            
            result.fold(
                onSuccess = { response ->
                    _uiState.value = _uiState.value.copy(
                        lookupResult = response,
                        isLookingUpDocument = false,
                        lookupError = null
                    )
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLookingUpDocument = false,
                        lookupError = exception.message ?: "Error al buscar documento"
                    )
                }
            )
        }
    }
    
    fun clearLookupResult() {
        _uiState.value = _uiState.value.copy(lookupResult = null, lookupError = null)
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    fun clearSuccessMessage() {
        _uiState.value = _uiState.value.copy(successMessage = null)
    }
    
    fun refresh() {
        loadReservations()
        loadCalendarEvents()
    }
}

