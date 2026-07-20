package com.transportsim.app.ui.training.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.transportsim.domain.models.TrainingDifficulty
import com.transportsim.domain.models.TrainingScenarioWithProgress
import com.transportsim.app.ui.theme.*

@Composable
fun TrainingScenarioCard(
    scenario: TrainingScenarioWithProgress,
    onStart: () -> Unit,
    onUnlock: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isUnlocked = scenario.isUnlocked
    val isCompleted = scenario.isCompleted
    
    val difficultyColor = when (scenario.scenario.difficulty) {
        TrainingDifficulty.BEGINNER -> Green
        TrainingDifficulty.INTERMEDIATE -> Gold
        TrainingDifficulty.ADVANCED -> Red
    }
    
    val difficultyLabel = when (scenario.scenario.difficulty) {
        TrainingDifficulty.BEGINNER -> "Beginner"
        TrainingDifficulty.INTERMEDIATE -> "Intermediate"
        TrainingDifficulty.ADVANCED -> "Advanced"
    }

    Card(
        modifier = modifier
            .clickable(enabled = isUnlocked) { if (isUnlocked) onStart() },
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted)
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
            else if (isUnlocked)
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            else
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            when {
                isCompleted -> Green.copy(alpha = 0.5f)
                isUnlocked -> difficultyColor.copy(alpha = 0.4f)
                else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
            }
        ),
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(difficultyColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(scenario.scenario.icon, fontSize = 24.sp)
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
                        text = scenario.scenario.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isUnlocked)
                            MaterialTheme.colorScheme.onSurface
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Difficulty badge
                    Surface(
                        color = difficultyColor.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(4.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            difficultyColor.copy(alpha = 0.3f)
                        )
                    ) {
                        Text(
                            text = difficultyLabel,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = difficultyColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }

                // Description
                Text(
                    text = scenario.scenario.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isUnlocked)
                        MaterialTheme.colorScheme.onSurfaceVariant
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    maxLines = 2,
                    modifier = Modifier.padding(top = 4.dp)
                )

                // Stats footer
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        TrainingMetaChip("⏱ ${scenario.scenario.durationMinutes}min")
                        TrainingMetaChip("🏆 +${scenario.scenario.xpReward} XP")
                        if (isCompleted && scenario.bestScore > 0) {
                            TrainingMetaChip("⭐ ${scenario.bestScore}%", Green)
                        }
                        if (scenario.timesCompleted > 0) {
                            TrainingMetaChip("🏁 ${scenario.timesCompleted}x", Cyan)
                        }
                    }

                    // Action button
                    when {
                        isCompleted -> {
                            Text(
                                text = "✅ Completed",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Green
                            )
                        }
                        !isUnlocked -> {
                            Button(
                                onClick = onUnlock,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Gold.copy(alpha = 0.2f),
                                    contentColor = Gold
                                ),
                                shape = RoundedCornerShape(5.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "🔒 Unlock (Lv.${scenario.scenario.unlockLevel})",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        else -> {
                            Button(
                                onClick = onStart,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Purple,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(5.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Start",
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
fun TrainingMetaChip(label: String, color: Color? = null) {
    Text(
        text = label,
        style = MaterialTheme.typography.labelSmall,
        color = color ?: MaterialTheme.colorScheme.onSurfaceVariant
    )
}
