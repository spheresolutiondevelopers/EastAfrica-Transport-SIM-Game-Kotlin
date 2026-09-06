package com.transportsim.app.ui.fleet.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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

    val categoryColor = when (catalog.category) {
        com.transportsim.domain.models.VehicleCategory.BUS -> Cyan
        com.transportsim.domain.models.VehicleCategory.MATATU -> Gold
        com.transportsim.domain.models.VehicleCategory.PICKUP -> Green
        com.transportsim.domain.models.VehicleCategory.LORRY -> Orange
        com.transportsim.domain.models.VehicleCategory.BODA -> Purple
        com.transportsim.domain.models.VehicleCategory.TAXI -> Pink
    }

    val cardBackground = Brush.linearGradient(
        colors = listOf(Color(0xFF0B1422), Color(0xFF050A12)),
        start = androidx.compose.ui.geometry.Offset(0f, 0f),
        end = androidx.compose.ui.geometry.Offset.Infinite
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(cardBackground)
            .border(
                width = 1.dp,
                color = if (isOwned) Border.copy(alpha = 0.7f) else Border.copy(alpha = 0.3f),
                shape = RoundedCornerShape(10.dp)
            )
            .clickable { if (isOwned) onSelect() }
    ) {
        // Top accent line
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(Color.Transparent, Cyan.copy(alpha = 0.5f), Color.Transparent)
                    )
                )
                .align(Alignment.TopCenter)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Top Row: Icon, Info, Level Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Category Icon
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(categoryColor.copy(alpha = 0.3f), categoryColor.copy(alpha = 0.06f))
                            )
                        )
                        .border(
                            width = 1.dp,
                            color = categoryColor.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(10.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(catalog.emoji, fontSize = 26.sp)
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = catalog.displayName,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontFamily = RajdhaniFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        ),
                        color = TextPri
                    )
                    Text(
                        text = if (isOwned) "SN: KE-${catalog.id.uppercase().take(3)}-${vehicle?.vehicleId}" else "Not yet purchased",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = ShareTechMonoFamily,
                            fontSize = 11.sp
                        ),
                        color = TextDim
                    )
                }

                // Level Badge
                if (isOwned) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = BgPanel,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Gold),
                        modifier = Modifier.align(Alignment.Top)
                    ) {
                        Text(
                            text = "LVL 1", // Placeholder for level
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = OrbitronFamily,
                                fontSize = 10.sp
                            ),
                            color = Gold
                        )
                    }
                } else {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = BgPanel,
                        border = androidx.compose.foundation.BorderStroke(1.dp, TextDim),
                        modifier = Modifier.align(Alignment.Top)
                    ) {
                        Text(
                            text = "LOCKED",
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = OrbitronFamily,
                                fontSize = 10.sp
                            ),
                            color = TextDim
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Stats Grid (2x2)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FleetStatCell("Max Speed", "${catalog.maxSpeedKph.toInt()} km/h", Cyan, Modifier.weight(1f))
                    FleetStatCell("Capacity", "${catalog.passengerCapacity} pax", Green, Modifier.weight(1f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FleetStatCell("Power", "6.2L Diesel", TextSec, Modifier.weight(1f)) // Using dummy power for now
                    FleetStatCell("Condition", "${vehicle?.conditionPct?.toInt() ?: 0}%", Gold, Modifier.weight(1f))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Actions
            if (isOwned) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FleetActionButton("Deploy", onClick = onDeploy, isPrimary = true, modifier = Modifier.weight(1f))
                    FleetActionButton("Upgrade", onClick = onSelect, modifier = Modifier.weight(1f))
                    FleetActionButton("Service", onClick = onService, modifier = Modifier.weight(1f))
                }
            } else {
                Button(
                    onClick = { onPurchase(catalog.id) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(5.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = Green
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Green),
                    contentPadding = PaddingValues(6.dp)
                ) {
                    Text(
                        text = "PURCHASE — KSH ${catalog.purchaseCostKsh}",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontFamily = RajdhaniFamily,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun FleetStatCell(
    label: String,
    value: String,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(Color.White.copy(alpha = 0.04f))
            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(6.dp))
            .padding(8.dp)
    ) {
        Column {
            Text(
                text = value,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontFamily = OrbitronFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                ),
                color = valueColor
            )
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 9.sp,
                    letterSpacing = 1.sp
                ),
                color = TextDim,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

@Composable
fun FleetActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isPrimary: Boolean = false
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(5.dp))
            .border(
                width = 1.dp,
                color = if (isPrimary) Green else Border,
                shape = RoundedCornerShape(5.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = RajdhaniFamily,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp
            ),
            color = if (isPrimary) Green else TextSec
        )
    }
}

