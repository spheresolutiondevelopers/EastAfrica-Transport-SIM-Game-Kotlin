package com.transportsim.app.ui.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.transportsim.app.ui.dashboard.models.MapVehicle
import com.transportsim.app.ui.theme.*
import com.transportsim.domain.models.FleetVehicle
import com.transportsim.domain.models.Vehicle

@Composable
fun DashCenter(
    routeId: String?,
    mapVehicles: List<MapVehicle>,
    selectedVehicle: Vehicle?,
    turntableVehicle: FleetVehicle?,
    onNextTurntable: () -> Unit,
    onPrevTurntable: () -> Unit,
    isMuted: Boolean,
    onToggleAudio: () -> Unit,
    onStartSimulation: () -> Unit,
    onStartTraining: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .background(BgDeep)
    ) {
        // Map Area
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            // The map itself
            MapView(
                routeId = routeId,
                vehicles = mapVehicles,
                modifier = Modifier.fillMaxSize()
            )
            
            // 3D TURNTABLE overlay (top-left)
            VehicleTurntable(
                fleetVehicle = turntableVehicle,
                onNext = onNextTurntable,
                onPrevious = onPrevTurntable,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(10.dp)
                    .size(200.dp, 148.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xE005090E))
                    .border(1.dp, Cyan.copy(alpha = 0.25f), RoundedCornerShape(10.dp))
            )
            
            // TOP-RIGHT OVERLAYS (Toggles + Details)
            Column(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(10.dp),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ThemeToggleButton()
                    AudioToggleButton(
                        isMuted = isMuted,
                        onToggle = onToggleAudio
                    )
                }

                // VEHICLE DETAILS PANEL (below toggles)
                if (turntableVehicle != null) {
                    val catalog = turntableVehicle.catalogEntry
                    VehicleDetailPanel(
                        vehicleName = catalog.displayName,
                        maxSpeed = "${catalog.maxSpeedKph.toInt()} km/h",
                        power = "${catalog.enginePowerKw.toInt()} kW",
                        capacity = if (catalog.passengerCapacity > 0) "${catalog.passengerCapacity} pax" else "${catalog.cargoCapacityKg.toInt()} kg",
                        isOwned = turntableVehicle.isOwned,
                        modifier = Modifier
                            .width(180.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xE005090E))
                            .border(1.dp, Cyan.copy(alpha = 0.25f), RoundedCornerShape(10.dp))
                    )
                }
            }
        }

        // Map Info Bar (Bottom)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .background(Brush.horizontalGradient(listOf(Color(0xF704080F), Color(0xF7060A16))))
                .drawBehind {
                    drawLine(
                        color = Cyan.copy(alpha = 0.25f),
                        start = Offset(0f, 0f),
                        end = Offset(size.width, 0f),
                        strokeWidth = 1.dp.toPx()
                    )
                }
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.wrapContentWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StartSimulationButton(
                    onClick = onStartSimulation,
                    isEnabled = routeId != null && selectedVehicle != null,
                    modifier = Modifier.width(220.dp)
                )
                TrainingButton(
                    onClick = onStartTraining,
                    modifier = Modifier.width(160.dp)
                )
            }
        }
    }
}
