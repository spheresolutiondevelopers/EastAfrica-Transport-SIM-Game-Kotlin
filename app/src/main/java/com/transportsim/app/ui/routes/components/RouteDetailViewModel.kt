package com.transportsim.app.ui.routes

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.transportsim.domain.models.Route
import com.transportsim.domain.models.RouteWaypoint
import com.transportsim.domain.repositories.RouteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RouteDetailViewModel @Inject constructor(
    private val routeRepository: RouteRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(RouteDetailUiState())
    val uiState: StateFlow<RouteDetailUiState> = _uiState.asStateFlow()
    
    fun loadRouteDetail(routeId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            val route = routeRepository.getRoute(routeId)
            val waypoints = route?.waypoints ?: emptyList()
            
            _uiState.update { state ->
                state.copy(
                    route = route,
                    waypoints = waypoints,
                    isLoading = false
                )
            }
        }
    }
}

data class RouteDetailUiState(
    val route: Route? = null,
    val waypoints: List<RouteWaypoint> = emptyList(),
    val isLoading: Boolean = false
)