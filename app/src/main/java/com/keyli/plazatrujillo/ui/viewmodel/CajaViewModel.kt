package com.keyli.plazatrujillo.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keyli.plazatrujillo.data.model.*
import com.keyli.plazatrujillo.data.repository.CajaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CajaUiState(
    val transactions: List<Transaction> = emptyList(),
    val totals: Totals? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
)

class CajaViewModel(
    private val repository: CajaRepository = CajaRepository()
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(CajaUiState())
    val uiState: StateFlow<CajaUiState> = _uiState.asStateFlow()
    
    fun loadTodayTransactions(date: String? = null) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = repository.listTodayTransactions(date)
            
            result.fold(
                onSuccess = { transactions ->
                    _uiState.value = _uiState.value.copy(
                        transactions = transactions,
                        isLoading = false
                    )
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Error al cargar transacciones"
                    )
                }
            )
        }
    }
    
    fun loadTodayTotals(date: String? = null) {
        viewModelScope.launch {
            val result = repository.todayTotals(date)
            result.fold(
                onSuccess = { totals ->
                    _uiState.value = _uiState.value.copy(totals = totals)
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        error = exception.message ?: "Error al cargar totales"
                    )
                }
            )
        }
    }
    
    fun createPayment(request: CreatePaymentRequest) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, successMessage = null)
            val result = repository.createPayment(request)
            
            result.fold(
                onSuccess = { transaction ->
                    _uiState.value = _uiState.value.copy(
                        transactions = _uiState.value.transactions + transaction,
                        isLoading = false,
                        successMessage = "Pago creado exitosamente"
                    )
                    // Recargar totales después de crear un pago
                    loadTodayTotals()
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Error al crear pago"
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
    
    fun refresh(date: String? = null) {
        loadTodayTransactions(date)
        loadTodayTotals(date)
    }
}

