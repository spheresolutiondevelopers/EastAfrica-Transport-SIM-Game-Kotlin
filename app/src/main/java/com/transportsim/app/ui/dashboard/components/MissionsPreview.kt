package com.transportsim.app.ui.dashboard.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.transportsim.app.ui.theme.*
import com.transportsim.domain.models.Mission
import com.transportsim.domain.models.MissionStatus

@Composable
fun MissionsPreview(
    missions: List<Mission>,
    onMissionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Active Missions",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "See All →",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { /* Navigate to missions */ }
            )
        }
        
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(missions) { mission ->
                MissionPreviewCard(
                    mission = mission,
                    onClick = { onMissionSelected(mission.missionId) }
                )
            }
        }
    }
}

@Composable
fun MissionPreviewCard(
    mission: Mission,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(240.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (mission.status == MissionStatus.IN_PROGRESS)
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                else MaterialTheme.colorScheme.surfaceVariant
        ),
        border = if (mission.status == MissionStatus.IN_PROGRESS)
            androidx.compose.foundation.BorderStroke(1.dp, Gold)
            else androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = mission.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )
                Text(
                    text = "KSH ${mission.rewardKsh}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Gold,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Text(
                text = mission.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                modifier = Modifier.padding(vertical = 4.dp)
            )
            
            if (mission.status == MissionStatus.IN_PROGRESS) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${mission.progress.current}/${mission.progress.target} ${mission.progress.unit}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Cyan
                    )
                    Text(
                        text = "In Progress",
                        style = MaterialTheme.typography.labelSmall,
                        color = Green
                    )
                }
            } else {
                Text(
                    text = "Accept →",
                    style = MaterialTheme.typography.labelSmall,
                    color = Cyan,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}