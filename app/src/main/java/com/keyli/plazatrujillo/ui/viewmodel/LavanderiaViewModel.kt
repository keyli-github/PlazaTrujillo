package com.keyli.plazatrujillo.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keyli.plazatrujillo.data.model.*
import com.keyli.plazatrujillo.data.repository.LavanderiaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Categorías de lavandería que coinciden con el backend Django
data class LaundryCategory(
    val key: String,
    val label: String
)

val LAUNDRY_CATEGORIES = listOf(
    LaundryCategory("TOALLAS_GRANDE", "Toalla grande"),
    LaundryCategory("TOALLAS_MEDIANA", "Toalla mediana"),
    LaundryCategory("TOALLAS_CHICA", "Toalla chica"),
    LaundryCategory("SABANAS_MEDIA", "Sábana 1/2 plaza"),
    LaundryCategory("SABANAS_UNA", "Sábana 1 plaza"),
    LaundryCategory("CUBRECAMAS_MEDIA", "Cubrecama 1/2 plaza"),
    LaundryCategory("CUBRECAMAS_UNA", "Cubrecama 1 plaza"),
    LaundryCategory("FUNDAS", "Funda de almohada")
)

data class LavanderiaUiState(
    val stock: List<StockItem> = emptyList(),
    val orders: List<LaundryOrder> = emptyList(),
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
) {
    // Mapa de stock por categoría para acceso rápido
    val stockMap: Map<String, StockItem> by lazy {
        stock.associateBy { it.category ?: "" }
    }
    
    // Totales calculados para las tarjetas de resumen
    val totalInventario: Int get() = stock.sumOf { it.total ?: 0 }
    val totalDisponible: Int get() = stock.sumOf { it.disponible ?: 0 }
    val totalLavanderia: Int get() = stock.sumOf { it.lavanderia ?: 0 }
    val totalDanado: Int get() = stock.sumOf { it.danado ?: 0 }
}

class LavanderiaViewModel(
    private val repository: LavanderiaRepository = LavanderiaRepository()
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(LavanderiaUiState())
    val uiState: StateFlow<LavanderiaUiState> = _uiState.asStateFlow()
    
    init {
        loadStock()
    }
    
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
    
    fun updateStockField(category: String, field: String, value: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, error = null)
            
            val request = when (field) {
                "total" -> StockItemRequest(category = category, total = value)
                "disponible" -> StockItemRequest(category = category, disponible = value)
                "lavanderia" -> StockItemRequest(category = category, lavanderia = value)
                "danado" -> StockItemRequest(category = category, danado = value)
                else -> null
            }
            
            if (request == null) {
                _uiState.value = _uiState.value.copy(isSaving = false)
                return@launch
            }
            
            val result = repository.upsertStock(listOf(request))
            result.fold(
                onSuccess = { updatedStock ->
                    _uiState.value = _uiState.value.copy(
                        stock = updatedStock,
                        isSaving = false,
                        successMessage = "Stock actualizado"
                    )
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        error = exception.message ?: "Error al actualizar stock"
                    )
                }
            )
        }
    }
    
    fun loadOrders() {
        viewModelScope.launch {
            val result = repository.listOrders()
            
            result.fold(
                onSuccess = { orders ->
                    _uiState.value = _uiState.value.copy(orders = orders)
                },
                onFailure = { /* Ignorar errores de órdenes */ }
            )
        }
    }
    
    fun sendLaundry(
        toallaGrande: Int,
        toallaMediana: Int,
        toallaChica: Int,
        sabanaMedia: Int,
        sabanaUna: Int,
        cubrecamaMedia: Int,
        cubrecamaUna: Int,
        funda: Int
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, error = null, successMessage = null)
            
            val request = SendLaundryRequest(
                toallaGrande = toallaGrande,
                toallaMediana = toallaMediana,
                toallaChica = toallaChica,
                sabanaMediaPlaza = sabanaMedia,
                sabanaUnaPlaza = sabanaUna,
                cubrecamaMediaPlaza = cubrecamaMedia,
                cubrecamaUnaPlaza = cubrecamaUna,
                funda = funda
            )
            
            val result = repository.sendLaundry(request)
            result.fold(
                onSuccess = { order ->
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        successMessage = "Enviado a lavandería: ${order.orderCode}"
                    )
                    loadStock()
                    loadOrders()
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        error = exception.message ?: "Error al enviar a lavandería"
                    )
                }
            )
        }
    }
    
    fun returnOrder(orderCode: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, error = null, successMessage = null)
            
            val result = repository.returnOrder(orderCode)
            result.fold(
                onSuccess = { response ->
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        successMessage = "Orden $orderCode retornada exitosamente"
                    )
                    loadStock()
                    loadOrders()
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        error = exception.message ?: "Error al devolver orden"
                    )
                }
            )
        }
    }
    
    fun updateDamage(category: String, quantity: Int, action: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, error = null)
            
            val result = repository.updateDamage(category, quantity, action)
            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        successMessage = if (action == "add") "Marcado como dañado" else "Reparado exitosamente"
                    )
                    loadStock()
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        error = exception.message ?: "Error al actualizar daños"
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

