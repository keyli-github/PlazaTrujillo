package com.keyli.plazatrujillo.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keyli.plazatrujillo.data.model.*
import com.keyli.plazatrujillo.data.repository.CajaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class CajaUiState(
    val transactions: List<CajaTransaction> = emptyList(),
    val totals: CajaTotals? = null,
    val paidClients: List<PaidClient> = emptyList(),
    val selectedDate: String = "", // formato yyyy-MM-dd para API
    val displayDate: String = "", // formato dd/MM/yyyy para UI
    val isLoading: Boolean = false,
    val isCreatingPayment: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
) {
    // Totales formateados
    val totalYape: Double get() = totals?.methods?.yape ?: 0.0
    val totalEfectivo: Double get() = totals?.methods?.efectivo ?: 0.0
    val totalTarjeta: Double get() = totals?.methods?.tarjeta ?: 0.0
    val totalTransferencia: Double get() = totals?.methods?.transferencia ?: 0.0
    val totalDia: Double get() = totals?.total ?: 0.0
}

class CajaViewModel(
    private val repository: CajaRepository = CajaRepository()
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(CajaUiState())
    val uiState: StateFlow<CajaUiState> = _uiState.asStateFlow()
    
    private val apiDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val displayDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    
    init {
        // Inicializar con la fecha de hoy
        val today = Date()
        val apiDate = apiDateFormat.format(today)
        val displayDate = displayDateFormat.format(today)
        _uiState.value = _uiState.value.copy(
            selectedDate = apiDate,
            displayDate = displayDate
        )
        loadTransactions()
        loadPaidClients()
    }
    
    fun setDate(dateMillis: Long) {
        val date = Date(dateMillis)
        val apiDate = apiDateFormat.format(date)
        val displayDate = displayDateFormat.format(date)
        _uiState.value = _uiState.value.copy(
            selectedDate = apiDate,
            displayDate = displayDate
        )
        loadTransactions()
    }
    
    fun loadTransactions() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            val date = _uiState.value.selectedDate.ifEmpty { null }
            
            // Cargar transacciones
            val transactionsResult = repository.listTodayTransactions(date)
            transactionsResult.fold(
                onSuccess = { transactions ->
                    _uiState.value = _uiState.value.copy(transactions = transactions)
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        error = exception.message ?: "Error al cargar transacciones"
                    )
                }
            )
            
            // Cargar totales
            val totalsResult = repository.todayTotals(date)
            totalsResult.fold(
                onSuccess = { totals ->
                    _uiState.value = _uiState.value.copy(totals = totals)
                },
                onFailure = { /* Ignorar error de totales */ }
            )
            
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }
    
    fun loadTotalsForArqueo(date: String): CajaTotals? {
        var result: CajaTotals? = null
        viewModelScope.launch {
            val response = repository.todayTotals(date)
            response.fold(
                onSuccess = { totals -> result = totals },
                onFailure = { }
            )
        }
        return result
    }
    
    fun loadPaidClients() {
        viewModelScope.launch {
            val result = repository.paidClients()
            result.fold(
                onSuccess = { clients ->
                    _uiState.value = _uiState.value.copy(paidClients = clients)
                },
                onFailure = { /* Ignorar */ }
            )
        }
    }
    
    fun createPayment(
        type: String,
        guest: String,
        method: String,
        amount: Double,
        reservationCode: String? = null
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isCreatingPayment = true,
                error = null,
                successMessage = null
            )
            
            val request = CreatePaymentRequest(
                type = type,
                guest = guest,
                method = method,
                amount = amount,
                reservationCode = reservationCode
            )
            
            val result = repository.createPayment(request)
            result.fold(
                onSuccess = { response ->
                    _uiState.value = _uiState.value.copy(
                        isCreatingPayment = false,
                        successMessage = "Cobro de S/ ${String.format("%.2f", amount)} registrado ($method)"
                    )
                    loadTransactions()
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isCreatingPayment = false,
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
    
    fun refresh() {
        loadTransactions()
        loadPaidClients()
    }
}

