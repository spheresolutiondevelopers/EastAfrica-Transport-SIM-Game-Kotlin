package com.transportsim.app.ui.fleet.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.transportsim.app.ui.theme.*
import com.transportsim.domain.models.FleetVehicle

@Composable
fun FleetVehicleCard(
    fleetVehicle: FleetVehicle,
    onPurchase: (String) -> Unit,
    onDeploy: () -> Unit,
    onService: () -> Unit,
    onSelect: () -> Unit
) {
    val isOwned = fleetVehicle.isOwned
    val vehicle = fleetVehicle.ownedVehicle
    val catalog = fleetVehicle.catalogEntry

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { if (isOwned) onSelect() },
        colors = CardDefaults.cardColors(
            containerColor = if (isOwned)
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            else
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f)
        ),
        border = if (isOwned)
            androidx.compose.foundation.BorderStroke(1.dp, Cyan.copy(alpha = 0.4f))
        else
            androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Header: icon, name, status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(catalog.emoji, fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = catalog.displayName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                if (isOwned) {
                    Text("✓ Owned", color = Green, style = MaterialTheme.typography.labelSmall)
                } else {
                    Text("🔒 Locked", color = Red, style = MaterialTheme.typography.labelSmall)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FleetStatItem("Speed", "${catalog.maxSpeedKph.toInt()} km/h", Modifier.weight(1f))
                FleetStatItem("Capacity", "${catalog.passengerCapacity}", Modifier.weight(1f))
                FleetStatItem("Fuel", "${catalog.fuelConsumptionL100km.toInt()} L/100", Modifier.weight(1f))
            }

            // Condition bar (if owned)
            if (isOwned && vehicle != null) {
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { vehicle.conditionPct / 100f },
                    modifier = Modifier.fillMaxWidth().height(4.dp),
                    color = when {
                        vehicle.conditionPct > 80 -> Green
                        vehicle.conditionPct > 50 -> Gold
                        else -> Red
                    },
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
                Text(
                    text = "Condition: ${vehicle.conditionPct.toInt()}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.End)
                )
            }

            // Action button
            Spacer(modifier = Modifier.height(8.dp))
            if (isOwned && vehicle != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onDeploy,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Green,
                            contentColor = Color.White
                        ),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("Deploy", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = onService,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("Service", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                Button(
                    onClick = { onPurchase(catalog.id) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Gold,
                        contentColor = Color.Black
                    )
                ) {
                    Text("Purchase — KSH ${catalog.purchaseCostKsh}", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
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
        shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.labelLarge,
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
