package com.transportsim.app.ui.garage.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.transportsim.app.ui.theme.*
import com.transportsim.domain.models.Vehicle

@Composable
fun GarageVehicleList(
    vehicles: List<Vehicle>,
    selectedVehicleId: Int?,
    onVehicleSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "VEHICLES",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            letterSpacing = 2.sp,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        )
        
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            contentPadding = PaddingValues(horizontal = 8.dp)
        ) {
            items(vehicles) { vehicle ->
                GarageVehicleItem(
                    vehicle = vehicle,
                    isSelected = vehicle.vehicleId == selectedVehicleId,
                    onClick = { onVehicleSelected(vehicle.vehicleId) }
                )
            }
        }
    }
}

@Composable
fun GarageVehicleItem(
    vehicle: Vehicle,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val icon = when {
        vehicle.typeId.startsWith("bus") -> "🚌"
        vehicle.typeId.startsWith("matatu") -> "🚐"
        vehicle.typeId.startsWith("pickup") -> "🛻"
        vehicle.typeId.startsWith("lorry") -> "🚛"
        vehicle.typeId.startsWith("boda") -> "🏍"
        vehicle.typeId.startsWith("taxi") -> "🚕"
        else -> "🚗"
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
            else
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f)
        ),
        border = if (isSelected)
            androidx.compose.foundation.BorderStroke(1.dp, Cyan)
        else
            androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(icon, fontSize = 28.sp)
            Text(
                text = vehicle.displayName ?: vehicle.typeId,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                color = if (isSelected) Cyan else MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "LVL ${(vehicle.odometerKm / 100).toInt() + 1}",
                style = MaterialTheme.typography.labelSmall,
                color = Gold
            )
        }
    }
}