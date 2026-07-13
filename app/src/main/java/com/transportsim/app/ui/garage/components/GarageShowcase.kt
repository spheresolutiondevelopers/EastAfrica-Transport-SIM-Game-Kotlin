package com.transportsim.app.ui.garage.components

import androidx.compose.foundation.background
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

@Composable
fun GarageShowcase(
    vehicle: Vehicle?,
    upgrades: List<UpgradeModule>,
    onEditLivery: () -> Unit,
    onFullService: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (vehicle == null) {
        Box(
            modifier = modifier
                .height(160.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Text("Select a vehicle", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        return
    }
    
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
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            Orange.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Vehicle icon
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Cyan.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(icon, fontSize = 48.sp)
            }
            
            // Details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = (vehicle.displayName ?: vehicle.typeId).uppercase(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Orange
                )
                Text(
                    text = "⭐ Level ${(vehicle.odometerKm / 100).toInt() + 1} — ${vehicle.conditionPct.toInt()}% condition",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Gold
                )
                
                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    GarageStat("Max km/h", "80", Cyan)
                    GarageStat("Capacity", "48", Green)
                    GarageStat("Fuel", "6.2L", Orange)
                    GarageStat("Turn Rad.", "12.5m", Purple)
                }
            }
            
            // Actions
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onEditLivery,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Cyan,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(5.dp)
                ) {
                    Text("🎨 Edit Livery", fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = onFullService,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Orange.copy(alpha = 0.2f),
                        contentColor = Orange
                    ),
                    shape = RoundedCornerShape(5.dp)
                ) {
                    Text("🔧 Full Service", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun GarageStat(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}