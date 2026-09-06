package com.transportsim.app.ui.app

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.transportsim.app.ui.dashboard.DashboardScreen
import com.transportsim.app.ui.fleet.FleetDetailScreen
import com.transportsim.app.ui.fleet.FleetScreen
import com.transportsim.app.ui.garage.GarageDetailScreen
import com.transportsim.app.ui.garage.GarageScreen
import com.transportsim.app.ui.missions.MissionDetailScreen
import com.transportsim.app.ui.missions.MissionsScreen
import com.transportsim.app.ui.onboarding.OnboardingScreen
import com.transportsim.app.ui.routes.RouteDetailScreen
import com.transportsim.app.ui.routes.RoutesScreen
import com.transportsim.app.ui.settings.SettingsScreen
import com.transportsim.app.ui.simulation.SimulationScreen
import com.transportsim.app.ui.training.TrainingScreen

@Composable
fun AppNavHost(
    appState: AppState,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = appState.navController,
        startDestination = Destinations.DASHBOARD,
        modifier = modifier,
        enterTransition = {
            fadeIn(animationSpec = tween(300)) + slideInHorizontally(
                animationSpec = tween(300),
                initialOffsetX = { it / 4 }
            )
        },
        exitTransition = {
            fadeOut(animationSpec = tween(200)) + slideOutHorizontally(
                animationSpec = tween(200),
                targetOffsetX = { -it / 4 }
            )
        },
        popEnterTransition = {
            fadeIn(animationSpec = tween(200)) + slideInHorizontally(
                animationSpec = tween(200),
                initialOffsetX = { -it / 4 }
            )
        },
        popExitTransition = {
            fadeOut(animationSpec = tween(200)) + slideOutHorizontally(
                animationSpec = tween(200),
                targetOffsetX = { it / 4 }
            )
        }
    ) {
        composable(Destinations.DASHBOARD) {
            DashboardScreen(
                onVehicleSelected = appState::navigateToFleetDetail,
                onRouteSelected = appState::navigateToRouteDetail,
                onMissionSelected = appState::navigateToMissionDetail,
                onStartSimulation = { routeId, vehicleId ->
                    appState.navigateToSimulation(routeId, vehicleId)
                },
                onStartTraining = { appState.navigateTo(Destinations.TRAINING) },
                onNavigateToFleet = { appState.navigateTo(Destinations.FLEET) },
                onNavigateToRoutes = { appState.navigateTo(Destinations.ROUTES) },
                onNavigateToMissions = { appState.navigateTo(Destinations.MISSIONS) },
                onNavigateToGarage = { appState.navigateTo(Destinations.GARAGE) },
                onNavigateToSettings = { appState.navigateTo(Destinations.SETTINGS) }
            )
        }
        
        composable(Destinations.FLEET) {
            FleetScreen(
                onVehicleSelected = appState::navigateToFleetDetail,
                onNavigateBack = appState::popBackStack
            )
        }
        
        composable(
            route = "${Destinations.FLEET_DETAIL}/{vehicleId}",
            arguments = listOf(navArgument("vehicleId") { type = NavType.IntType })
        ) { backStackEntry ->
            val vehicleId = backStackEntry.arguments?.getInt("vehicleId") ?: 0
            FleetDetailScreen(
                vehicleId = vehicleId,
                onNavigateBack = appState::popBackStack,
                onStartSimulation = { routeId, vid ->
                    appState.navigateToSimulation(routeId, vid)
                },
                onNavigateToGarage = { appState.navigateToGarage(vehicleId) }
            )
        }
        
        composable(Destinations.ROUTES) {
            RoutesScreen(
                onRouteSelected = appState::navigateToRouteDetail,
                onNavigateBack = appState::popBackStack,
                onNavigateToSettings = { appState.navigateTo(Destinations.SETTINGS) }
            )
        }
        
        composable(
            route = "${Destinations.ROUTE_DETAIL}/{routeId}",
            arguments = listOf(navArgument("routeId") { type = NavType.StringType })
        ) { backStackEntry ->
            val routeId = backStackEntry.arguments?.getString("routeId") ?: ""
            RouteDetailScreen(
                routeId = routeId,
                onNavigateBack = appState::popBackStack,
                onStartSimulation = { routeId, vehicleId ->
                    appState.navigateToSimulation(routeId, vehicleId)
                }
            )
        }
        
        composable(Destinations.MISSIONS) {
            MissionsScreen(
                onMissionSelected = appState::navigateToMissionDetail,
                onNavigateBack = appState::popBackStack,
                onNavigateToSettings = { appState.navigateTo(Destinations.SETTINGS) }
            )
        }
        
        composable(
            route = "${Destinations.MISSION_DETAIL}/{missionId}",
            arguments = listOf(navArgument("missionId") { type = NavType.StringType })
        ) { backStackEntry ->
            val missionId = backStackEntry.arguments?.getString("missionId") ?: ""
            MissionDetailScreen(
                missionId = missionId,
                onNavigateBack = appState::popBackStack
            )
        }
        
        composable(Destinations.GARAGE) {
            GarageScreen(
                onVehicleSelected = appState::navigateToGarage,
                onNavigateBack = appState::popBackStack,
                onNavigateToSettings = { appState.navigateTo(Destinations.SETTINGS) }
            )
        }
        
        composable(
            route = "${Destinations.GARAGE_DETAIL}/{vehicleId}",
            arguments = listOf(navArgument("vehicleId") { type = NavType.IntType })
        ) { backStackEntry ->
            val vehicleId = backStackEntry.arguments?.getInt("vehicleId") ?: 0
            GarageDetailScreen(
                vehicleId = vehicleId,
                onNavigateBack = appState::popBackStack
            )
        }
        
        composable(
            route = "${Destinations.SIMULATION_ROUTE}/{routeId}/{vehicleId}",
            arguments = listOf(
                navArgument("routeId") { type = NavType.StringType },
                navArgument("vehicleId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val routeId = backStackEntry.arguments?.getString("routeId") ?: ""
            val vehicleId = backStackEntry.arguments?.getInt("vehicleId") ?: 0
            SimulationScreen(
                routeId = routeId,
                vehicleId = vehicleId,
                onExit = appState::popBackStack
            )
        }
        
        composable(Destinations.SETTINGS) {
            SettingsScreen(
                onNavigateBack = appState::popBackStack
            )
        }
        
        composable(Destinations.TRAINING) {
            TrainingScreen(
                onNavigateBack = appState::popBackStack
            )
        }
        
        composable(Destinations.ONBOARDING) {
            OnboardingScreen(
                onComplete = {
                    appState.navigateTo(Destinations.DASHBOARD)
                }
            )
        }
    }
}