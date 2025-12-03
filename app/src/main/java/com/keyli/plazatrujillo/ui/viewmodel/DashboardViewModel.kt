package com.keyli.plazatrujillo.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keyli.plazatrujillo.data.model.*
import com.keyli.plazatrujillo.data.repository.DashboardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DashboardUiState(
    val metrics: DashboardMetricsResponse? = null,
    val monthlyRevenue: List<Double> = emptyList(),
    val paymentMethods: Map<String, Double> = emptyMap(),
    val occupancyWeekly: List<Double> = emptyList(),
    val todayCheckinsCheckouts: TodayCheckinsCheckoutsResponse? = null,
    val recentReservations: List<RecentReservationItem> = emptyList(),
    val statistics: StatisticsResponse? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class DashboardViewModel(
    private val repository: DashboardRepository = DashboardRepository()
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()
    
    init {
        loadDashboardData()
    }
    
    fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            // Cargar todas las métricas en paralelo
            val metricsResult = repository.getDashboardMetrics()
            val monthlyRevenueResult = repository.getMonthlyRevenue()
            val paymentMethodsResult = repository.getPaymentMethods()
            val occupancyResult = repository.getOccupancyWeekly()
            val checkinsCheckoutsResult = repository.getTodayCheckinsCheckouts()
            val recentReservationsResult = repository.getRecentReservations()
            val statisticsResult = repository.getStatistics()
            
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                metrics = metricsResult.getOrNull(),
                monthlyRevenue = monthlyRevenueResult.getOrElse { emptyList() },
                paymentMethods = paymentMethodsResult.getOrElse { emptyMap() },
                occupancyWeekly = occupancyResult.getOrElse { emptyList() },
                todayCheckinsCheckouts = checkinsCheckoutsResult.getOrNull(),
                recentReservations = recentReservationsResult.getOrElse { emptyList() },
                statistics = statisticsResult.getOrNull(),
                error = listOfNotNull(
                    metricsResult.exceptionOrNull()?.message,
                    monthlyRevenueResult.exceptionOrNull()?.message,
                    paymentMethodsResult.exceptionOrNull()?.message,
                    occupancyResult.exceptionOrNull()?.message,
                    checkinsCheckoutsResult.exceptionOrNull()?.message,
                    recentReservationsResult.exceptionOrNull()?.message,
                    statisticsResult.exceptionOrNull()?.message
                ).firstOrNull()
            )
        }
    }
    
    fun refresh() {
        loadDashboardData()
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

