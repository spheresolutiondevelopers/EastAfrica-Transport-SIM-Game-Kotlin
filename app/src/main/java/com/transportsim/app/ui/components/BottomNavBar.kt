package com.transportsim.app.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.transportsim.app.R
import com.transportsim.app.ui.app.AppState
import com.transportsim.app.ui.app.Destinations

@Composable
fun BottomNavBar(
    appState: AppState
) {
    val items = listOf(
        NavItem(Destinations.DASHBOARD, "Dashboard", ImageVector.vectorResource(R.drawable.ic_dashboard)),
        NavItem(Destinations.FLEET, "Fleet", ImageVector.vectorResource(R.drawable.ic_fleet)),
        NavItem(Destinations.ROUTES, "Routes", ImageVector.vectorResource(R.drawable.ic_routes)),
        NavItem(Destinations.MISSIONS, "Missions", ImageVector.vectorResource(R.drawable.ic_missions)),
        NavItem(Destinations.GARAGE, "Garage", ImageVector.vectorResource(R.drawable.ic_garage)),
        NavItem(Destinations.SETTINGS, "Settings", ImageVector.vectorResource(R.drawable.ic_settings))
    )
    
    NavigationBar {
        items.forEach { item ->
            val selected = appState.currentRoute == item.route
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = selected,
                onClick = {
                    if (!selected) {
                        appState.navigateTo(item.route)
                    }
                }
            )
        }
    }
}

private data class NavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)