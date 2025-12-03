package com.keyli.plazatrujillo.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keyli.plazatrujillo.data.model.*
import com.keyli.plazatrujillo.data.repository.LavanderiaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LavanderiaUiState(
    val stock: List<StockItem> = emptyList(),
    val orders: List<LaundryOrder> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
)

class LavanderiaViewModel(
    private val repository: LavanderiaRepository = LavanderiaRepository()
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(LavanderiaUiState())
    val uiState: StateFlow<LavanderiaUiState> = _uiState.asStateFlow()
    
    fun loadStock() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = repository.getStock()
            
            result.fold(
                onSuccess = { stock ->
                    _uiState.value = _uiState.value.copy(
                        stock = stock,
                        isLoading = false
                    )
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Error al cargar stock"
                    )
                }
            )
        }
    }
    
    fun upsertStock(request: UpsertStockRequest) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, successMessage = null)
            val result = repository.upsertStock(request)
            
            result.fold(
                onSuccess = { updated ->
                    _uiState.value = _uiState.value.copy(
                        stock = updated,
                        isLoading = false,
                        successMessage = "Stock actualizado exitosamente"
                    )
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Error al actualizar stock"
                    )
                }
            )
        }
    }
    
    fun loadOrders() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = repository.listOrders()
            
            result.fold(
                onSuccess = { orders ->
                    _uiState.value = _uiState.value.copy(
                        orders = orders,
                        isLoading = false
                    )
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Error al cargar órdenes"
                    )
                }
            )
        }
    }
    
    fun sendLaundry(request: SendLaundryRequest) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, successMessage = null)
            val result = repository.sendLaundry(request)
            
            result.fold(
                onSuccess = { order ->
                    _uiState.value = _uiState.value.copy(
                        orders = _uiState.value.orders + order,
                        isLoading = false,
                        successMessage = "Lavandería enviada exitosamente"
                    )
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Error al enviar lavandería"
                    )
                }
            )
        }
    }
    
    fun returnOrder(orderCode: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, successMessage = null)
            val result = repository.returnOrder(orderCode)
            
            result.fold(
                onSuccess = { order ->
                    _uiState.value = _uiState.value.copy(
                        orders = _uiState.value.orders.map { 
                            if (it.code == orderCode) order else it 
                        },
                        isLoading = false,
                        successMessage = "Orden devuelta exitosamente"
                    )
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Error al devolver orden"
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
        loadStock()
        loadOrders()
    }
}

