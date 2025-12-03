package com.keyli.plazatrujillo.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keyli.plazatrujillo.data.model.*
import com.keyli.plazatrujillo.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class UserCreationSuccess(
    val email: String,
    val name: String,
    val password: String,
    val role: String
)

data class UserUiState(
    val users: List<User> = emptyList(),
    val currentProfile: UserProfile? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null,
    val creationSuccess: UserCreationSuccess? = null  // Para mostrar la contraseña después de crear
)

class UserViewModel(
    private val repository: UserRepository = UserRepository()
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(UserUiState())
    val uiState: StateFlow<UserUiState> = _uiState.asStateFlow()
    
    fun loadUsers() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = repository.listUsers()
            
            result.fold(
                onSuccess = { users ->
                    _uiState.value = _uiState.value.copy(
                        users = users,
                        isLoading = false
                    )
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Error al cargar usuarios"
                    )
                }
            )
        }
    }
    
    fun loadOwnProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = repository.getOwnProfile()
            
            result.fold(
                onSuccess = { profile ->
                    _uiState.value = _uiState.value.copy(
                        currentProfile = profile,
                        isLoading = false
                    )
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Error al cargar perfil"
                    )
                }
            )
        }
    }
    
    fun createUser(request: CreateUserRequest) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, successMessage = null)
            val result = repository.createUser(request)
            
            result.fold(
                onSuccess = { response ->
                    val newUser = response.user
                    val creationData = UserCreationSuccess(
                        email = response.email ?: request.email,
                        name = response.displayName ?: request.displayName ?: "",
                        password = response.password ?: "",
                        role = roleApiToLabel(response.role)
                    )
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        creationSuccess = creationData,
                        successMessage = null  // No mostrar mensaje genérico, mostrar diálogo con contraseña
                    )
                    // Refrescar lista de usuarios para obtener todos los datos actualizados
                    loadUsers()
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Error al crear usuario"
                    )
                }
            )
        }
    }
    
    fun updateUser(uid: String, request: UpdateUserRequest) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, successMessage = null)
            val result = repository.updateUser(uid, request)
            
            result.fold(
                onSuccess = { message ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = message
                    )
                    // Refrescar lista completa después de actualizar
                    loadUsers()
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Error al actualizar usuario"
                    )
                }
            )
        }
    }
    
    fun deleteUser(uid: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, successMessage = null)
            val result = repository.deleteUser(uid)
            
            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = "Usuario eliminado exitosamente"
                    )
                    // Refrescar lista completa después de eliminar
                    loadUsers()
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Error al eliminar usuario"
                    )
                }
            )
        }
    }
    
    fun toggleUserStatus(uid: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = repository.toggleUserStatus(uid)
            
            result.fold(
                onSuccess = { disabled ->
                    val statusText = if (disabled) "inhabilitado" else "habilitado"
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = "Usuario $statusText exitosamente"
                    )
                    // Refrescar lista completa para obtener estados actualizados
                    loadUsers()
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Error al cambiar estado del usuario"
                    )
                }
            )
        }
    }
    
    fun updateOwnProfile(request: UpdateProfileRequest) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, successMessage = null)
            val result = repository.updateOwnProfile(request)
            
            result.fold(
                onSuccess = { updatedProfile ->
                    _uiState.value = _uiState.value.copy(
                        currentProfile = updatedProfile,
                        isLoading = false,
                        successMessage = "Perfil actualizado exitosamente"
                    )
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Error al actualizar perfil"
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
    
    fun clearCreationSuccess() {
        _uiState.value = _uiState.value.copy(creationSuccess = null)
    }
    
    private fun roleApiToLabel(role: String?): String {
        return when (role?.lowercase()) {
            "admin" -> "Administrador"
            "housekeeping" -> "Hotelero"
            "receptionist" -> "Recepcionista"
            else -> role ?: "Sin rol"
        }
    }
    
    fun refresh() {
        loadUsers()
        loadOwnProfile()
    }
}

