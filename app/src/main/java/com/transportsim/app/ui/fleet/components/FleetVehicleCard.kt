package com.transportsim.app.ui.fleet.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.transportsim.app.ui.theme.*
import com.transportsim.domain.models.Vehicle
import com.transportsim.domain.models.VehicleStatus

@Composable
fun FleetVehicleCard(
    vehicle: Vehicle,
    onClick: () -> Unit,
    onDeploy: () -> Unit,
    onService: () -> Unit,
    modifier: Modifier = Modifier
) {
    val categoryColor = when {
        vehicle.typeId.startsWith("bus") -> Cyan
        vehicle.typeId.startsWith("matatu") -> Gold
        vehicle.typeId.startsWith("pickup") -> Green
        vehicle.typeId.startsWith("lorry") -> Orange
        vehicle.typeId.startsWith("boda") -> Purple
        vehicle.typeId.startsWith("taxi") -> Pink
        else -> MaterialTheme.colorScheme.onSurface
    }
    
    Card(
        modifier = modifier
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            categoryColor.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Header with level badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icon and name
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
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
                    Text(
                        text = icon,
                        fontSize = 18.sp
                    )
                    Column {
                        Text(
                            text = vehicle.displayName ?: vehicle.typeId,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                        Text(
                            text = "SN: ${vehicle.serialNumber}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                // Level badge (simplified)
                Surface(
                    color = Gold.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(4.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Gold.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = "LVL ${(vehicle.odometerKm / 100).toInt() + 1}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Gold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Stats grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FleetStatItem(
                    label = "Speed",
                    value = "80 km/h",
                    modifier = Modifier.weight(1f)
                )
                FleetStatItem(
                    label = "Capacity",
                    value = "48 pax",
                    modifier = Modifier.weight(1f)
                )
                FleetStatItem(
                    label = "Condition",
                    value = "${vehicle.conditionPct.toInt()}%",
                    modifier = Modifier.weight(1f)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Condition bar
            LinearProgressIndicator(
                progress = vehicle.conditionPct / 100f,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp),
                color = when {
                    vehicle.conditionPct > 80 -> Green
                    vehicle.conditionPct > 50 -> Gold
                    else -> Red
                },
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Status and action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Status badge
                Surface(
                    color = when (vehicle.status) {
                        VehicleStatus.ACTIVE -> Green.copy(alpha = 0.15f)
                        VehicleStatus.IDLE -> Gold.copy(alpha = 0.15f)
                        VehicleStatus.GARAGE -> Red.copy(alpha = 0.15f)
                    },
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        when (vehicle.status) {
                            VehicleStatus.ACTIVE -> Green
                            VehicleStatus.IDLE -> Gold
                            VehicleStatus.GARAGE -> Red
                        }
                    ),
                    modifier = Modifier.weight(0.5f)
                ) {
                    Text(
                        text = vehicle.status.name,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = when (vehicle.status) {
                            VehicleStatus.ACTIVE -> Green
                            VehicleStatus.IDLE -> Gold
                            VehicleStatus.GARAGE -> Red
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                
                // Deploy button (only if idle)
                if (vehicle.status == VehicleStatus.IDLE) {
                    Button(
                        onClick = onDeploy,
                        modifier = Modifier.weight(0.5f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Green,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(5.dp)
                    ) {
                        Text(
                            text = "Deploy",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    Button(
                        onClick = onService,
                        modifier = Modifier.weight(0.5f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        shape = RoundedCornerShape(5.dp)
                    ) {
                        Text(
                            text = "Service",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FleetStatItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(5.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Cyan
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}