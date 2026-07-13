package com.transportsim.app.ui.simulation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.transportsim.bridge.SimulationSession
import com.transportsim.bridge.models.NativeRoadSegment
import com.transportsim.bridge.models.NativeBusStop
import com.transportsim.domain.models.*
import com.transportsim.domain.repositories.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.*

@HiltViewModel
class SimulationViewModel @Inject constructor(
    private val routeRepository: RouteRepository,
    private val playerRepository: PlayerRepository,
    private val catalogRepository: CatalogRepository,
    private val fleetRepository: FleetRepository,
    private val missionRepository: MissionRepository,
    private val economyRepository: EconomyRepository,
    private val savedStateHandle: SavedStateHandle,
    val simulationSession: SimulationSession
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(SimulationUiState())
    val uiState: StateFlow<SimulationUiState> = _uiState.asStateFlow()
    
    // Input state
    private var acceleratePressed = false
    private var brakePressed = false
    private var steerLeftPressed = false
    private var steerRightPressed = false
    
    // Simulation loop job
    private var simulationJob: kotlinx.coroutines.Job? = null
    
    init {
        // Observe vehicle state from native engine
        viewModelScope.launch {
            simulationSession.vehicleState
                .collect { state ->
                    _uiState.update { uiState ->
                        uiState.copy(
                            speedKph = state.speedKph,
                            gear = state.gear,
                            rpm = state.rpm,
                            fuelPercent = (state.fuelL / 200f * 100f).coerceIn(0f, 100f),
                            engineTemp = state.engineTempC,
                            distanceKm = state.odometerKm / 1000f,
                            routeProgress = state.odometerKm / 14000f // 14km route
                        )
                    }
                }
        }
        
        // Observe callbacks from native engine
        viewModelScope.launch {
            simulationSession.nativeCallbacks.collisionEvents
                .collect { collision ->
                    if (collision != null) {
                        _uiState.update { state ->
                            state.copy(
                                collisionFlash = true,
                                scorePopups = state.scorePopups + ScorePopup(
                                    text = "-KSH ${collision.severity * 200} COLLISION",
                                    color = Red
                                )
                            )
                        }
                        // Reset collision flash after delay
                        viewModelScope.launch {
                            kotlinx.coroutines.delay(300)
                            _uiState.update { it.copy(collisionFlash = false) }
                        }
                    }
                }
        }
        
        viewModelScope.launch {
            simulationSession.nativeCallbacks.stopReachedEvents
                .collect { stop ->
                    if (stop != null) {
                        _uiState.update { state ->
                            state.copy(
                                busStopPrompt = BusStopPrompt(
                                    name = stop.stopName,
                                    actionText = "SLOW TO <20 KM/H TO BOARD PASSENGERS"
                                ),
                                isBoarding = true,
                                boardingPassengers = stop.passengers
                            )
                        }
                        // Auto-dismiss after boarding
                        viewModelScope.launch {
                            kotlinx.coroutines.delay(2000)
                            _uiState.update { state ->
                                state.copy(
                                    busStopPrompt = null,
                                    isBoarding = false,
                                    passengerCount = state.passengerCount + (state.boardingPassengers ?: 0)
                                )
                            }
                        }
                    }
                }
        }
        
        viewModelScope.launch {
            simulationSession.nativeCallbacks.scoreUpdates
                .collect { score ->
                    _uiState.update { state ->
                        state.copy(
                            score = score,
                            scorePopups = state.scorePopups + ScorePopup(
                                text = "+${score - state.score} XP",
                                color = Green
                            )
                        )
                    }
                }
        }
        
        viewModelScope.launch {
            simulationSession.nativeCallbacks.routeProgress
                .collect { progress ->
                    _uiState.update { state ->
                        state.copy(routeProgress = progress)
                    }
                }
        }
        
        viewModelScope.launch {
            simulationSession.nativeCallbacks.lowFuelEvents
                .collect { fuelPct ->
                    _uiState.update { state ->
                        state.copy(
                            scorePopups = state.scorePopups + ScorePopup(
                                text = "⚠️ LOW FUEL: ${fuelPct.toInt()}%",
                                color = Red
                            )
                        )
                    }
                }
        }
        
        viewModelScope.launch {
            simulationSession.nativeCallbacks.overspeedEvents
                .collect { (speed, limit) ->
                    _uiState.update { state ->
                        state.copy(
                            isOverspeeding = true,
                            speedLimit = limit,
                            speedKph = speed
                        )
                    }
                    // Reset after delay
                    viewModelScope.launch {
                        kotlinx.coroutines.delay(2000)
                        _uiState.update { it.copy(isOverspeeding = false) }
                    }
                }
        }
    }
    
    fun initializeSimulation(routeId: String, vehicleId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, loadProgress = 0f, loadStatus = "Initializing...") }
            
            try {
                // Load route data
                _uiState.update { it.copy(loadProgress = 0.2f, loadStatus = "Loading route data...") }
                val route = routeRepository.getRoute(routeId)
                val waypoints = routeRepository.getRouteWaypoints(routeId)
                
                // Resolve vehicle config
                _uiState.update { it.copy(loadProgress = 0.4f, loadStatus = "Resolving vehicle config...") }
                val vehicle = playerRepository.getPlayerVehicle(vehicleId)
                val catalog = catalogRepository.getVehicleSpec(vehicle.typeId)
                
                // Build native road segments and bus stops
                _uiState.update { it.copy(loadProgress = 0.6f, loadStatus = "Building road network...") }
                val segments = buildRoadSegments(routeId)
                val stops = buildBusStops(routeId)
                
                // Initialize session
                _uiState.update { it.copy(loadProgress = 0.8f, loadStatus = "Starting physics engine...") }
                
                // Start the session
                val config = ResolvedVehicleConfig(
                    vehicleId = vehicleId,
                    typeId = vehicle.typeId,
                    massKg = catalog.massKg,
                    maxPayloadKg = catalog.maxPayloadKg,
                    wheelbaseM = catalog.wheelbaseM,
                    comHeightLadenM = catalog.comHeightLadenM,
                    momentInertiaIzz = catalog.momentInertiaIzz,
                    pacejkaB = catalog.pacejka.b,
                    pacejkaC = catalog.pacejka.c,
                    pacejkaD = catalog.pacejka.d * (vehicle.tyreConditionPct / 100f),
                    pacejkaE = catalog.pacejka.e,
                    frontSpringRate = catalog.suspension.frontSpringNm,
                    rearSpringRate = catalog.suspension.rearSpringNm,
                    frontDamping = catalog.suspension.frontDampingNsm,
                    rearDamping = catalog.suspension.rearDampingNsm,
                    maxSpeedKph = catalog.maxSpeedKph,
                    passengerCapacity = catalog.passengerCapacity,
                    fuelEffMult = 1f,
                    brakeMs2 = catalog.emergencyBrakeMs2,
                    navAccuracyPct = 100f,
                    dragCd = catalog.aeroDragCd,
                    frontalAreaM2 = catalog.frontalAreaM2,
                    enginePowerKw = catalog.enginePowerKw,
                    peakTorqueNm = catalog.peakTorqueNm,
                    fuelCapacityL = catalog.fuelCapacityL,
                    fuelConsumptionBase = catalog.fuelConsumptionL100km,
                    currentFuelL = vehicle.fuelLevelL,
                    engineHealthPct = vehicle.engineHealthPct,
                    tyreConditionPct = vehicle.tyreConditionPct
                )
                
                simulationSession.startSession(config, segments, stops)
                
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        loadProgress = 1f,
                        loadStatus = "Ready!",
                        isRunning = true,
                        routeName = route?.name ?: routeId,
                        waypoints = waypoints,
                        vehicleId = vehicleId,
                        routeId = routeId,
                        passengerCount = 8,
                        speedLimit = 50,
                        isDayMode = true
                    )
                }
                
                // Start physics loop
                startSimulationLoop()
                
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    isLoading = false,
                    loadProgress = 0f,
                    loadStatus = "Error: ${e.message}"
                ) }
            }
        }
    }
    
    private fun buildRoadSegments(routeId: String): List<NativeRoadSegment> {
        // In a real implementation, this would query the route repository
        // For now, return mock data
        return listOf(
            NativeRoadSegment(
                segmentId = 1,
                distAlongRouteM = 0f,
                segmentLengthM = 100f,
                frictionMuDry = 0.82f,
                frictionMuWet = 0.50f,
                iriValue = 2.5f,
                iriClass = 2,
                superelevationDeg = 0f,
                speedLimitKph = 50,
                lanes = 4,
                roadWidthM = 7.4f,
                hasCrosswind = false,
                hasPothole = false,
                isIntersection = false,
                surfaceTypeId = "tarmac"
            )
        )
    }
    
    private fun buildBusStops(routeId: String): List<NativeBusStop> {
        // In a real implementation, this would query the route repository
        return listOf(
            NativeBusStop(
                stopId = 1,
                stopOrder = 1,
                stopName = "Railways Bus Stop",
                worldX = 0f,
                worldZ = 0f,
                distFromOriginKm = 2.1f,
                dwellTimeS = 12,
                onTimeToleranceM = 2f,
                maxPassengers = 48,
                passengerDemandPeak = 1.0f,
                stopType = "standard",
                hasShelter = true,
                isTerminal = false
            )
        )
    }
    
    private fun startSimulationLoop() {
        simulationJob = viewModelScope.launch {
            while (_uiState.value.isRunning) {
                if (!_uiState.value.isPaused) {
                    // Process input
                    processInput()
                    
                    // Step physics
                    simulationSession.stepPhysics()
                    
                    // Update state at 60Hz
                    kotlinx.coroutines.delay(16)
                } else {
                    kotlinx.coroutines.delay(100)
                }
            }
        }
    }
    
    private fun processInput() {
        val throttle = if (acceleratePressed) 1f else 0f
        val brake = if (brakePressed) 1f else 0f
        val steer = when {
            steerLeftPressed -> -1f
            steerRightPressed -> 1f
            else -> 0f
        }
        simulationSession.setInput(
            throttle = throttle,
            brake = brake,
            steerAngle = steer * 35f,
            horn = false
        )
    }
    
    // ─── Input Controls ──────────────────────────────────────────
    
    fun startAccelerate() {
        acceleratePressed = true
    }
    
    fun startBrake() {
        brakePressed = true
    }
    
    fun startSteerLeft() {
        steerLeftPressed = true
    }
    
    fun startSteerRight() {
        steerRightPressed = true
    }
    
    fun horn() {
        simulationSession.setInput(horn = true)
        _uiState.update { state ->
            state.copy(
                scorePopups = state.scorePopups + ScorePopup(
                    text = "📯 HONK!",
                    color = Gold
                )
            )
        }
        viewModelScope.launch {
            kotlinx.coroutines.delay(200)
            processInput()
        }
    }
    
    fun releaseControls() {
        acceleratePressed = false
        brakePressed = false
        steerLeftPressed = false
        steerRightPressed = false
        processInput()
    }
    
    fun toggleDayNight() {
        _uiState.update { state ->
            state.copy(isDayMode = !state.isDayMode)
        }
    }
    
    fun pauseSimulation() {
        _uiState.update { it.copy(isPaused = true) }
        simulationSession.pause()
    }
    
    fun resumeSimulation() {
        _uiState.update { it.copy(isPaused = false) }
        simulationSession.resume()
    }
    
    fun endSimulation() {
        simulationJob?.cancel()
        _uiState.update { it.copy(isRunning = false) }
        viewModelScope.launch {
            simulationSession.stopSession()
        }
    }
}

data class SimulationUiState(
    // Vehicle state
    val speedKph: Float = 0f,
    val gear: Int = 1,
    val rpm: Float = 600f,
    val fuelPercent: Float = 100f,
    val engineTemp: Float = 65f,
    val distanceKm: Float = 0f,
    val passengerCount: Int = 0,
    val score: Int = 0,
    val routeProgress: Float = 0f,
    val speedLimit: Int = 50,
    val isOverspeeding: Boolean = false,
    val satisfactionPercent: Float = 88f,
    
    // Simulation state
    val isLoading: Boolean = true,
    val loadProgress: Float = 0f,
    val loadStatus: String = "Initializing...",
    val isRunning: Boolean = false,
    val isPaused: Boolean = false,
    val isDayMode: Boolean = true,
    
    // Route info
    val routeId: String = "",
    val routeName: String = "",
    val waypoints: List<RouteWaypoint> = emptyList(),
    val vehicleId: Int = 0,
    
    // UI overlays
    val busStopPrompt: BusStopPrompt? = null,
    val trafficLight: TrafficLightIndicatorState? = null,
    val collisionFlash: Boolean = false,
    val isBoarding: Boolean = false,
    val boardingPassengers: Int? = null,
    val scorePopups: List<ScorePopup> = emptyList(),
    val trafficVehicles: List<TrafficVehicle> = emptyList()
)

data class ScorePopup(
    val text: String,
    val color: Color
)

data class BusStopPrompt(
    val name: String,
    val actionText: String
)

data class TrafficLightIndicatorState(
    val phase: TrafficLightPhase,
    val distanceM: Float,
    val timerSeconds: Int
)

data class TrafficVehicle(
    val id: String,
    val progress: Float,
    val speed: Float,
    val lane: Int,
    val type: String
)