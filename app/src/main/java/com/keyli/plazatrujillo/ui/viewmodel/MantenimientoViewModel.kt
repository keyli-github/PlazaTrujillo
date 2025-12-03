package com.keyli.plazatrujillo.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keyli.plazatrujillo.data.model.*
import com.keyli.plazatrujillo.data.repository.MantenimientoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Estado del Sistema de Agua Caliente
data class WaterHeatingSystemState(
    val operationalStatus: String = "Operativo",
    val briquettesThisMonth: Int = 0,
    val lastMaintenanceDate: String? = null,
    val lastMaintenanceTime: String? = null,
    val nextMaintenanceDate: String? = null,
    val nextMaintenanceTime: String? = null
)

data class MantenimientoUiState(
    val waterHeatingSystem: WaterHeatingSystemState = WaterHeatingSystemState(),
    val briquetteHistory: List<BriquetteRecord> = emptyList(),
    val issues: List<MaintenanceIssue> = emptyList(),
    val blockedRooms: List<BlockedRoom> = emptyList(),
    val allRooms: List<Room> = emptyList(),
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
)

class MantenimientoViewModel(
    private val repository: MantenimientoRepository = MantenimientoRepository()
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(MantenimientoUiState())
    val uiState: StateFlow<MantenimientoUiState> = _uiState.asStateFlow()
    
    init {
        refresh()
    }
    
    fun loadSystemStatus() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = repository.getSystemStatus()
            
            result.fold(
                onSuccess = { response ->
                    _uiState.value = _uiState.value.copy(
                        waterHeatingSystem = WaterHeatingSystemState(
                            operationalStatus = response.operationalStatus ?: "Operativo",
                            briquettesThisMonth = response.briquettesThisMonth ?: 0,
                            lastMaintenanceDate = response.lastMaintenance?.date,
                            lastMaintenanceTime = response.lastMaintenance?.time,
                            nextMaintenanceDate = response.nextMaintenance?.date,
                            nextMaintenanceTime = response.nextMaintenance?.time
                        ),
                        isLoading = false
                    )
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Error al cargar estado del sistema"
                    )
                }
            )
        }
    }
    
    fun loadBriquetteHistory() {
        viewModelScope.launch {
            val result = repository.getBriquetteHistory()
            result.fold(
                onSuccess = { history ->
                    _uiState.value = _uiState.value.copy(briquetteHistory = history)
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        error = exception.message ?: "Error al cargar historial de briquetas"
                    )
                }
            )
        }
    }
    
    fun registerBriquetteChange(
        quantity: Int,
        date: String,
        time: String,
        operationalStatus: String
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, error = null, successMessage = null)
            
            val request = RegisterBriquetteChangeRequest(
                quantity = quantity,
                date = date,
                time = time,
                operationalStatus = operationalStatus
            )
            
            val result = repository.registerBriquetteChange(request)
            
            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        successMessage = "Cambio de briquetas registrado exitosamente"
                    )
                    refresh()
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        error = exception.message ?: "Error al registrar cambio de briquetas"
                    )
                }
            )
        }
    }
    
    fun loadIssues() {
        viewModelScope.launch {
            val result = repository.getMaintenanceIssues()
            result.fold(
                onSuccess = { issues ->
                    _uiState.value = _uiState.value.copy(issues = issues)
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        error = exception.message ?: "Error al cargar incidencias"
                    )
                }
            )
        }
    }
    
    fun reportIssue(
        room: String,
        problem: String,
        priority: String,
        technician: String?
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, error = null, successMessage = null)
            
            val request = ReportIssueRequest(
                room = room,
                problem = problem,
                priority = priority,
                technician = technician
            )
            
            val result = repository.reportIssue(request)
            
            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        successMessage = "Incidencia reportada exitosamente"
                    )
                    refresh()
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        error = exception.message ?: "Error al reportar incidencia"
                    )
                }
            )
        }
    }
    
    fun deleteIssue(issueId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, error = null, successMessage = null)
            val result = repository.deleteIssue(issueId)
            
            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        issues = _uiState.value.issues.filter { it.id != issueId },
                        isSaving = false,
                        successMessage = "Incidencia eliminada exitosamente"
                    )
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        error = exception.message ?: "Error al eliminar incidencia"
                    )
                }
            )
        }
    }
    
    fun loadBlockedRooms() {
        viewModelScope.launch {
            val result = repository.getBlockedRooms()
            result.fold(
                onSuccess = { rooms ->
                    _uiState.value = _uiState.value.copy(blockedRooms = rooms)
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        error = exception.message ?: "Error al cargar habitaciones bloqueadas"
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
                onFailure = { /* Ignorar */ }
            )
        }
    }
    
    fun blockRoom(
        room: String,
        reason: String,
        blockedUntil: String,
        blockedBy: String?
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, error = null, successMessage = null)
            
            val request = BlockRoomRequest(
                room = room,
                reason = reason,
                blockedUntil = blockedUntil,
                blockedBy = blockedBy
            )
            
            val result = repository.blockRoom(request)
            
            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        successMessage = "Habitación bloqueada exitosamente"
                    )
                    refresh()
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        error = exception.message ?: "Error al bloquear habitación"
                    )
                }
            )
        }
    }
    
    fun unblockRoom(roomId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, error = null, successMessage = null)
            val result = repository.unblockRoom(roomId)
            
            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        blockedRooms = _uiState.value.blockedRooms.filter { it.id != roomId },
                        isSaving = false,
                        successMessage = "Habitación liberada exitosamente"
                    )
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        error = exception.message ?: "Error al liberar habitación"
                    )
                }
            )
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    fun clearSuccessMessage() {
        _uiState.value = _uiState.value.copy(successMessage = null)
    }
    
    fun refresh() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            loadSystemStatus()
            loadBriquetteHistory()
            loadIssues()
            loadBlockedRooms()
        }
    }
}

