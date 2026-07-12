package com.transportsim.app.ui.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Stable
class AppState(
    val navController: NavHostController,
    private val coroutineScope: CoroutineScope
) {
    val currentDestination: NavDestination?
        @Composable get() = navController.currentBackStackEntryAsState().value?.destination
    
    val currentRoute: String?
        @Composable get() = currentDestination?.route
    
    fun shouldShowBottomBar(): Boolean {
        return currentDestination?.route !in listOf(
            Destinations.SIMULATION,
            Destinations.SIMULATION_ROUTE
        )
    }
    
    fun navigateTo(route: String) {
        coroutineScope.launch {
            navController.navigate(route) {
                // Avoid multiple copies of the same destination
                launchSingleTop = true
                // Restore state when re-selecting a previously selected item
                restoreState = true
                // Pop up to the start destination to avoid building up a large stack
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
            }
        }
    }
    
    fun navigateToFleetDetail(vehicleId: Int) {
        coroutineScope.launch {
            navController.navigate("${Destinations.FLEET_DETAIL}/$vehicleId")
        }
    }
    
    fun navigateToRouteDetail(routeId: String) {
        coroutineScope.launch {
            navController.navigate("${Destinations.ROUTE_DETAIL}/$routeId")
        }
    }
    
    fun navigateToMissionDetail(missionId: String) {
        coroutineScope.launch {
            navController.navigate("${Destinations.MISSION_DETAIL}/$missionId")
        }
    }
    
    fun navigateToSimulation(routeId: String, vehicleId: Int) {
        coroutineScope.launch {
            navController.navigate("${Destinations.SIMULATION_ROUTE}/$routeId/$vehicleId")
        }
    }
    
    fun navigateToGarage(vehicleId: Int) {
        coroutineScope.launch {
            navController.navigate("${Destinations.GARAGE_DETAIL}/$vehicleId")
        }
    }
    
    fun navigateUp() {
        coroutineScope.launch {
            navController.navigateUp()
        }
    }
    
    fun popBackStack() {
        coroutineScope.launch {
            navController.popBackStack()
        }
    }
}

@Composable
fun rememberAppState(
    navController: NavHostController = rememberNavController(),
    coroutineScope: CoroutineScope = rememberCoroutineScope()
): AppState {
    return remember(navController, coroutineScope) {
        AppState(navController, coroutineScope)
    }
}

object Destinations {
    const val DASHBOARD = "dashboard"
    const val FLEET = "fleet"
    const val FLEET_DETAIL = "fleet_detail"
    const val ROUTES = "routes"
    const val ROUTE_DETAIL = "route_detail"
    const val MISSIONS = "missions"
    const val MISSION_DETAIL = "mission_detail"
    const val GARAGE = "garage"
    const val GARAGE_DETAIL = "garage_detail"
    const val SIMULATION = "simulation"
    const val SIMULATION_ROUTE = "simulation_route"
    const val SETTINGS = "settings"
    const val TRAINING = "training"
    const val ONBOARDING = "onboarding"
}