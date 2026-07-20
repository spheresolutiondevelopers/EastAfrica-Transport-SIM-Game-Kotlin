package com.transportsim.app.ui.missions.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.transportsim.app.ui.missions.LeaderboardEntry
import com.transportsim.app.ui.theme.*

@Composable
fun LeaderboardCard(
    entries: List<LeaderboardEntry>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "🏆 Leaderboard (Kenya)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            entries.forEach { entry ->
                LeaderboardRow(entry)
            }
        }
    }
}

@Composable
fun LeaderboardRow(entry: LeaderboardEntry) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = entry.name,
            style = MaterialTheme.typography.bodySmall,
            color = if (entry.name.contains("You")) Cyan else MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "${entry.xp} XP",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = if (entry.name.contains("You")) FontWeight.Bold else FontWeight.Normal,
            color = if (entry.name.contains("You")) Cyan else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}