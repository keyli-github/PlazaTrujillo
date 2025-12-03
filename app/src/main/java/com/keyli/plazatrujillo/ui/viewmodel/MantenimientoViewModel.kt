package com.keyli.plazatrujillo.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keyli.plazatrujillo.data.model.*
import com.keyli.plazatrujillo.data.repository.MantenimientoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class MantenimientoUiState(
    val systemStatus: SystemStatus? = null,
    val briquetteHistory: List<BriquetteRecord> = emptyList(),
    val issues: List<MaintenanceIssue> = emptyList(),
    val blockedRooms: List<BlockedRoom> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
)

class MantenimientoViewModel(
    private val repository: MantenimientoRepository = MantenimientoRepository()
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(MantenimientoUiState())
    val uiState: StateFlow<MantenimientoUiState> = _uiState.asStateFlow()
    
    fun loadSystemStatus() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = repository.getSystemStatus()
            
            result.fold(
                onSuccess = { status ->
                    _uiState.value = _uiState.value.copy(
                        systemStatus = status,
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
    
    fun updateSystemStatus(request: UpdateSystemStatusRequest) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, successMessage = null)
            val result = repository.updateSystemStatus(request)
            
            result.fold(
                onSuccess = { status ->
                    _uiState.value = _uiState.value.copy(
                        systemStatus = status,
                        isLoading = false,
                        successMessage = "Estado del sistema actualizado exitosamente"
                    )
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Error al actualizar estado del sistema"
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
    
    fun registerBriquetteChange(request: RegisterBriquetteChangeRequest) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, successMessage = null)
            val result = repository.registerBriquetteChange(request)
            
            result.fold(
                onSuccess = { record ->
                    _uiState.value = _uiState.value.copy(
                        briquetteHistory = _uiState.value.briquetteHistory + record,
                        isLoading = false,
                        successMessage = "Cambio de briquetas registrado exitosamente"
                    )
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
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
    
    fun reportIssue(request: ReportIssueRequest) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, successMessage = null)
            val result = repository.reportIssue(request)
            
            result.fold(
                onSuccess = { issue ->
                    _uiState.value = _uiState.value.copy(
                        issues = _uiState.value.issues + issue,
                        isLoading = false,
                        successMessage = "Incidencia reportada exitosamente"
                    )
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Error al reportar incidencia"
                    )
                }
            )
        }
    }
    
    fun deleteIssue(issueId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, successMessage = null)
            val result = repository.deleteIssue(issueId)
            
            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        issues = _uiState.value.issues.filter { it.id != issueId },
                        isLoading = false,
                        successMessage = "Incidencia eliminada exitosamente"
                    )
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
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
    
    fun blockRoom(request: BlockRoomRequest) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, successMessage = null)
            val result = repository.blockRoom(request)
            
            result.fold(
                onSuccess = { room ->
                    _uiState.value = _uiState.value.copy(
                        blockedRooms = _uiState.value.blockedRooms + room,
                        isLoading = false,
                        successMessage = "Habitación bloqueada exitosamente"
                    )
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Error al bloquear habitación"
                    )
                }
            )
        }
    }
    
    fun unblockRoom(roomId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, successMessage = null)
            val result = repository.unblockRoom(roomId)
            
            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        blockedRooms = _uiState.value.blockedRooms.filter { it.id != roomId },
                        isLoading = false,
                        successMessage = "Habitación desbloqueada exitosamente"
                    )
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Error al desbloquear habitación"
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
        loadSystemStatus()
        loadBriquetteHistory()
        loadIssues()
        loadBlockedRooms()
    }
}

