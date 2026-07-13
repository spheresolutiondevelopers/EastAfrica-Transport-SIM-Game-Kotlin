package com.transportsim.app.ui.missions.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.transportsim.app.ui.theme.*
import com.transportsim.domain.models.Mission
import com.transportsim.domain.models.MissionStatus

@Composable
fun MissionCard(
    mission: Mission,
    isActive: Boolean,
    onAccept: () -> Unit,
    onComplete: () -> Unit,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (icon, iconColor) = when (mission.type) {
        MissionType.CARGO -> "📦" to Orange
        MissionType.PASSENGER -> "🧑‍🤝‍🧑" to Cyan
        MissionType.EXPRESS -> "⚡" to Purple
        MissionType.VIP -> "⭐" to Gold
        MissionType.TRAINING -> "🎓" to Teal
    }
    
    Card(
        modifier = modifier
            .clickable { onSelect() },
        colors = CardDefaults.cardColors(
            containerColor = if (isActive)
                if (mission.status == MissionStatus.IN_PROGRESS)
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
                else
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            else
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            when {
                isActive && mission.status == MissionStatus.IN_PROGRESS -> Gold.copy(alpha = 0.5f)
                isActive -> Green.copy(alpha = 0.5f)
                else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            }
        ),
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(icon, fontSize = 24.sp)
            }
            
            // Content
            Column(modifier = Modifier.weight(1f)) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = mission.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "KSH ${mission.rewardKsh}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Gold
                    )
                }
                
                // Description
                Text(
                    text = mission.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    modifier = Modifier.padding(top = 4.dp)
                )
                
                // Requirements tags
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    MissionRequirementTag(
                        label = mission.requirements.requiredVehicleCategory?.name?.replace("_", " ") ?: "Any Vehicle"
                    )
                    MissionRequirementTag(
                        label = "⭐ Lv.${mission.requirements.minLevel}+"
                    )
                    MissionRequirementTag(
                        label = "⏱ ${mission.requirements.timeLimitMinutes} min"
                    )
                    if (mission.requirements.passengerCount != null) {
                        MissionRequirementTag(
                            label = "👥 ${mission.requirements.passengerCount} pax"
                        )
                    }
                    if (mission.requirements.cargoCapacityKg != null) {
                        MissionRequirementTag(
                            label = "📦 ${mission.requirements.cargoCapacityKg} kg"
                        )
                    }
                }
                
                // Progress bar (for active missions)
                if (isActive) {
                    Column(modifier = Modifier.padding(top = 8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Progress",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${mission.progress.current}/${mission.progress.target} ${mission.progress.unit}",
                                style = MaterialTheme.typography.labelSmall,
                                color = Cyan
                            )
                        }
                        LinearProgressIndicator(
                            progress = if (mission.progress.target > 0) 
                                mission.progress.current.toFloat() / mission.progress.target 
                            else 0f,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .padding(top = 4.dp),
                            color = if (mission.progress.current >= mission.progress.target) Green else Gold,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    }
                }
                
                // Footer
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "⏰ ${mission.expiresAt ?: "Expires soon"}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    when {
                        mission.status == MissionStatus.IN_PROGRESS -> {
                            if (mission.progress.current >= mission.progress.target) {
                                Button(
                                    onClick = onComplete,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Green,
                                        contentColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(5.dp)
                                ) {
                                    Text(
                                        text = "Complete",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            } else {
                                Surface(
                                    color = Cyan.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(5.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Cyan.copy(alpha = 0.3f))
                                ) {
                                    Text(
                                        text = "In Progress",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Cyan,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                        isActive && mission.status == MissionStatus.COMPLETED -> {
                            Surface(
                                color = Green.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(5.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Green)
                            ) {
                                Text(
                                    text = "✅ Completed",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Green,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                        else -> {
                            Button(
                                onClick = onAccept,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Green,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(5.dp)
                            ) {
                                Text(
                                    text = "Accept",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MissionRequirementTag(label: String) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        shape = RoundedCornerShape(4.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
        )
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}