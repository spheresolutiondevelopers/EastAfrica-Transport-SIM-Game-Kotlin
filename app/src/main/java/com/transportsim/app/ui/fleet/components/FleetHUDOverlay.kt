package com.transportsim.app.ui.fleet.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.transportsim.app.ui.theme.*
import com.transportsim.domain.models.FleetVehicle
import com.transportsim.domain.models.VehicleStatus

@Composable
fun FleetHUDOverlay(
    vehicle: FleetVehicle?,
    modifier: Modifier = Modifier
) {
    if (vehicle == null) return
    
    val statusColor = when (vehicle.status) {
        VehicleStatus.ACTIVE -> Green
        VehicleStatus.IDLE -> Gold
        VehicleStatus.GARAGE -> Red
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    val statusText = when (vehicle.status) {
        VehicleStatus.ACTIVE -> "ACTIVE"
        VehicleStatus.IDLE -> "IDLE"
        VehicleStatus.GARAGE -> "GARAGE"
        else -> "AVAILABLE"
    }
    
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            statusColor.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = vehicle.catalogEntry.id,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Cyan
            )
            Text(
                text = vehicle.catalogEntry.displayName,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(statusColor, RoundedCornerShape(50))
                )
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = statusColor
                )
            }
        }
    }
}
