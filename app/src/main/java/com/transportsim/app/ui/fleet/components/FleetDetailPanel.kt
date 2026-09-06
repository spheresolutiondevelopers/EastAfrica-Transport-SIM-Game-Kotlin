package com.transportsim.app.ui.fleet.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.transportsim.app.ui.theme.*
import com.transportsim.domain.models.FleetVehicle

@Composable
fun FleetDetailPanel(
    fleetVehicle: FleetVehicle?,
    onService: (Int) -> Unit,
    onUpgrade: (Int) -> Unit,
    onCustomize: (Int) -> Unit,
    onPurchase: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val vehicle = fleetVehicle?.catalogEntry
    val isOwned = fleetVehicle?.isOwned ?: false
    val ownedVehicle = fleetVehicle?.ownedVehicle
    
    if (vehicle == null) return

    Box(
        modifier = modifier
            .width(220.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.Black.copy(alpha = 0.2f)) // Glass effect
            .padding(12.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header: Icon + ID + Status Badge + Rev Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(text = vehicle.emoji, fontSize = 18.sp)
                Text(
                    text = vehicle.id,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Cyan,
                    modifier = Modifier.weight(1f)
                )
                
                // Status Badge
                Surface(
                    color = if (isOwned) Green.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(2.dp)
                ) {
                    Text(
                        text = if (isOwned) "OWNED" else "AVAILABLE",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        fontSize = 7.sp,
                        color = if (isOwned) Green else Color.White.copy(alpha = 0.5f),
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
                
                // Engine Rev Button
                IconButton(
                    onClick = { /* TODO: Play synthesized rev */ },
                    modifier = Modifier.size(24.dp)
                ) {
                    Text("🔊", fontSize = 12.sp)
                }
            }
            
            // Nickname + Subtype
            Column {
                Text(
                    text = vehicle.displayName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Gold
                )
                Text(
                    text = vehicle.id, // Using ID as surrogate for subtype
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.4f),
                    fontSize = 9.sp
                )
            }
            
            // Spec Grid (Exactly 4 fields)
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    CompactSpecItem(label = "MAX SPEED", value = "${vehicle.maxSpeedKph.toInt()} KM/H", modifier = Modifier.weight(1f))
                    CompactSpecItem(label = "POWER", value = "${vehicle.enginePowerKw.toInt()} HP", modifier = Modifier.weight(1f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    CompactSpecItem(label = "0-100 KM/H", value = "${vehicle.peakAccelMs2}S", modifier = Modifier.weight(1f))
                    CompactSpecItem(label = "PRICE", value = "KSH ${(vehicle.purchaseCostKsh / 1000)}K", modifier = Modifier.weight(1f), isPrice = true)
                }
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Actions
            if (isOwned && ownedVehicle != null) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        DetailActionButton(label = "🔧 SERVICE", onClick = { onService(ownedVehicle.vehicleId) }, color = Cyan, modifier = Modifier.weight(1f))
                        DetailActionButton(label = "⬆ UPGRADE", onClick = { onUpgrade(ownedVehicle.vehicleId) }, color = Gold, modifier = Modifier.weight(1f))
                    }
                    DetailActionButton(label = "🎨 CUSTOMIZE", onClick = { onCustomize(ownedVehicle.vehicleId) }, color = Purple, modifier = Modifier.fillMaxWidth())
                }
            } else {
                Button(
                    onClick = { onPurchase(vehicle.id) },
                    modifier = Modifier.fillMaxWidth().height(40.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Green, contentColor = Color.Black),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "💳 PURCHASE · KSH ${vehicle.purchaseCostKsh.toLocaleString()}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun CompactSpecItem(label: String, value: String, isPrice: Boolean = false, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(2.dp))
            .padding(6.dp)
    ) {
        Column {
            Text(text = label, style = MaterialTheme.typography.labelSmall, fontSize = 7.sp, color = Color.White.copy(alpha = 0.4f))
            Text(text = value, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = if (isPrice) Gold else Cyan)
        }
    }
}

@Composable
fun DetailActionButton(label: String, onClick: () -> Unit, color: Color, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        modifier = modifier.height(32.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color.copy(alpha = 0.15f), contentColor = color),
        shape = RoundedCornerShape(2.dp),
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, fontSize = 8.sp, fontWeight = FontWeight.Bold)
    }
}

fun Int.toLocaleString(): String {
    return "%,d".format(this)
}
