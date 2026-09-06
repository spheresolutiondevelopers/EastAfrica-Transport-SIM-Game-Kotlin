package com.transportsim.app.ui.fleet.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.transportsim.app.ui.theme.*
import com.transportsim.domain.models.FleetVehicle
import com.transportsim.domain.models.VehicleCategory
import com.transportsim.domain.models.VehicleStatus

@Composable
fun FleetListItem(
    fleetVehicle: FleetVehicle,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val vehicle = fleetVehicle.catalogEntry
    val isOwned = fleetVehicle.isOwned
    val statusColor = when (fleetVehicle.status) {
        VehicleStatus.ACTIVE -> Green
        VehicleStatus.IDLE -> Gold
        VehicleStatus.GARAGE -> Red
        else -> Color.Gray.copy(alpha = 0.5f)
    }
    
    val icon = when (vehicle.category) {
        VehicleCategory.BUS -> "🚌"
        VehicleCategory.MATATU -> "🚐"
        VehicleCategory.PICKUP -> "🛻"
        VehicleCategory.LORRY -> "🚛"
        VehicleCategory.BODA -> "🏍"
        VehicleCategory.TAXI -> "🚕"
    }
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .clip(RoundedCornerShape(4.dp))
            .then(
                if (isSelected) Modifier.shadow(elevation = 8.dp, shape = RoundedCornerShape(4.dp), ambientColor = Cyan, spotColor = Cyan) else Modifier
            )
            .background(
                if (isSelected) Cyan.copy(alpha = 0.15f) 
                else Color.White.copy(alpha = 0.03f)
            )
            .clickable { onClick() }
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Condition ring (Minimal)
            Box(
                modifier = Modifier.size(24.dp),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = 2f
                    val condition = fleetVehicle.conditionPct ?: 0f
                    val color = when {
                        condition >= 80 -> Green
                        condition >= 50 -> Gold
                        else -> Red
                    }
                    drawArc(
                        color = Color.White.copy(alpha = 0.1f),
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = strokeWidth)
                    )
                    drawArc(
                        color = color,
                        startAngle = -90f,
                        sweepAngle = 360f * (condition / 100f),
                        useCenter = false,
                        style = Stroke(width = strokeWidth)
                    )
                }
            }
            
            // Icon + ID
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = icon, fontSize = 14.sp)
                    Text(
                        text = vehicle.id,
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isSelected) Cyan else Color.White.copy(alpha = 0.8f),
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = vehicle.category.name,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.4f),
                    fontSize = 7.sp
                )
            }
            
            // Status dot
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(RoundedCornerShape(50))
                    .background(if (isOwned) statusColor else Color.Gray.copy(alpha = 0.3f))
            )
        }
    }
}
