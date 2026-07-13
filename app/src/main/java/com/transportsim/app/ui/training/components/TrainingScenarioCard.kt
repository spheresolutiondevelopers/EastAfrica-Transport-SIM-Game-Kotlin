package com.transportsim.app.ui.training.components

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
import com.transportsim.domain.models.TrainingScenario
import com.transportsim.domain.models.TrainingScenarioDifficulty

@Composable
fun TrainingScenarioCard(
    scenario: TrainingScenario,
    isUnlocked: Boolean,
    onStart: () -> Unit,
    onUnlock: () -> Unit,
    modifier: Modifier = Modifier
) {
    val difficultyColor = when (scenario.difficulty) {
        TrainingScenarioDifficulty.BEGINNER -> Green
        TrainingScenarioDifficulty.INTERMEDIATE -> Gold
        TrainingScenarioDifficulty.ADVANCED -> Red
    }
    
    val difficultyLabel = when (scenario.difficulty) {
        TrainingScenarioDifficulty.BEGINNER -> "Beginner"
        TrainingScenarioDifficulty.INTERMEDIATE -> "Intermediate"
        TrainingScenarioDifficulty.ADVANCED -> "Advanced"
    }
    
    Card(
        modifier = modifier
            .clickable(enabled = isUnlocked) { 
                if (isUnlocked) onStart() 
            },
        colors = CardDefaults.cardColors(
            containerColor = if (scenario.isCompleted)
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
            else if (isUnlocked)
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            else
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (scenario.isCompleted) Green.copy(alpha = 0.5f)
            else if (isUnlocked) difficultyColor.copy(alpha = 0.4f)
            else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
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
                    .background(
                        when (scenario.difficulty) {
                            TrainingScenarioDifficulty.BEGINNER -> Green.copy(alpha = 0.15f)
                            TrainingScenarioDifficulty.INTERMEDIATE -> Gold.copy(alpha = 0.15f)
                            TrainingScenarioDifficulty.ADVANCED -> Red.copy(alpha = 0.15f)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = scenario.icon,
                    fontSize = 24.sp
                )
            }
            
            // Content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = scenario.title,
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
                    text = scenario.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isUnlocked)
                        MaterialTheme.colorScheme.onSurfaceVariant
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    maxLines = 2,
                    modifier = Modifier.padding(top = 4.dp)
                )
                
                // Footer
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
                        TrainingMetaChip("⏱ ${scenario.durationMinutes}min")
                        TrainingMetaChip("🏆 +${scenario.xpReward} XP")
                        if (scenario.isCompleted && scenario.bestScore != null) {
                            TrainingMetaChip("⭐ ${scenario.bestScore}%", Green)
                        }
                    }
                    
                    // Action button
                    when {
                        scenario.isCompleted -> {
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
                                shape = RoundedCornerShape(5.dp)
                            ) {
                                Text(
                                    text = "🔒 Unlock (Lv.${scenario.unlockLevel})",
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
                                shape = RoundedCornerShape(5.dp)
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
fun TrainingMetaChip(
    label: String,
    color: Color? = null
) {
    Text(
        text = label,
        style = MaterialTheme.typography.labelSmall,
        color = color ?: MaterialTheme.colorScheme.onSurfaceVariant
    )
}