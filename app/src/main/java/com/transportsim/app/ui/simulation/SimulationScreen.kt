package com.transportsim.app.ui.simulation

import android.view.MotionEvent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.transportsim.app.ui.simulation.components.*
import com.transportsim.app.ui.theme.*
import com.transportsim.bridge.SimulationSession

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimulationScreen(
    routeId: String,
    vehicleId: Int,
    onExit: () -> Unit,
    viewModel: SimulationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val session = viewModel.simulationSession
    
    // Initialize simulation when screen appears
    LaunchedEffect(routeId, vehicleId) {
        viewModel.initializeSimulation(routeId, vehicleId)
    }
    
    // Handle back press
    BackHandler(enabled = uiState.isRunning) {
        viewModel.pauseSimulation()
        // Show exit confirmation dialog
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
        // GLSurfaceView for 3D rendering
        AndroidView(
            factory = { ctx ->
                SimulationGLSurfaceView(ctx).apply {
                    setSession(session)
                    setOnTouchListener { _, event ->
                        when (event.action) {
                            MotionEvent.ACTION_DOWN -> {
                                // Handle touch for camera control
                                true
                            }
                            MotionEvent.ACTION_MOVE -> {
                                // Handle drag for camera rotation
                                true
                            }
                            else -> false
                        }
                    }
                }
            },
            update = { view ->
                view.setSession(session)
                view.requestRender()
            },
            modifier = Modifier.fillMaxSize()
        )
        
        // Loading overlay
        if (uiState.isLoading) {
            SimulationLoadingOverlay(
                progress = uiState.loadProgress,
                status = uiState.loadStatus,
                modifier = Modifier.fillMaxSize()
            )
        }
        
        // Top HUD
        SimulationTopHud(
            speed = uiState.speedKph,
            passengers = uiState.passengerCount,
            distance = uiState.distanceKm,
            routeName = uiState.routeName,
            isDayMode = uiState.isDayMode,
            onToggleDayNight = viewModel::toggleDayNight,
            onExit = { viewModel.pauseSimulation() },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(top = 8.dp)
        )
        
        // Bottom HUD
        SimulationBottomHud(
            speed = uiState.speedKph,
            gear = uiState.gear,
            fuelPercent = uiState.fuelPercent,
            engineTemp = uiState.engineTemp,
            satisfactionPercent = uiState.satisfactionPercent,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )
        
        // Input controls
        SimulationControls(
            onAccelerate = viewModel::startAccelerate,
            onBrake = viewModel::startBrake,
            onSteerLeft = viewModel::startSteerLeft,
            onSteerRight = viewModel::startSteerRight,
            onHorn = viewModel::horn,
            onRelease = viewModel::releaseControls,
            modifier = Modifier.fillMaxSize()
        )
        
        // Pause overlay
        if (uiState.isPaused) {
            SimulationPauseOverlay(
                onResume = viewModel::resumeSimulation,
                onExit = {
                    viewModel.endSimulation()
                    onExit()
                },
                modifier = Modifier.fillMaxSize()
            )
        }
        
        // Mini map
        SimulationMiniMap(
            routeProgress = uiState.routeProgress,
            waypoints = uiState.waypoints,
            trafficVehicles = uiState.trafficVehicles,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .size(160.dp, 120.dp)
        )
        
        // Score popups
        uiState.scorePopups.forEach { popup ->
            ScorePopup(
                text = popup.text,
                color = popup.color,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 80.dp)
            )
        }
        
        // Bus stop prompt
        if (uiState.busStopPrompt != null) {
            BusStopPrompt(
                stopName = uiState.busStopPrompt!!.name,
                actionText = uiState.busStopPrompt!!.actionText,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(bottom = 100.dp)
            )
        }
        
        // Overspeed warning
        if (uiState.isOverspeeding) {
            OverspeedWarning(
                speed = uiState.speedKph,
                limit = uiState.speedLimit,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 60.dp)
            )
        }
        
        // Collision flash
        if (uiState.collisionFlash) {
            CollisionFlash(
                modifier = Modifier.fillMaxSize()
            )
        }
        
        // Boarding overlay
        if (uiState.isBoarding) {
            BoardingOverlay(
                passengerCount = uiState.boardingPassengers ?: 0,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 200.dp, bottom = 100.dp)
            )
        }
        
        // Traffic light indicator
        if (uiState.trafficLight != null) {
            TrafficLightIndicator(
                phase = uiState.trafficLight!!.phase,
                distanceM = uiState.trafficLight!!.distanceM,
                timerSeconds = uiState.trafficLight!!.timerSeconds,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 60.dp, end = 220.dp)
            )
        }
        
        // Key hints
        KeyHints(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 60.dp, start = 16.dp)
        )
    }
}