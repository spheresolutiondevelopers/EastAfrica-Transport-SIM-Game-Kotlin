package com.transportsim.app.ui.missions.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.transportsim.app.ui.theme.*
import com.transportsim.domain.models.DailyReward

@Composable
fun RewardTrackCard(
    currentStep: Int,
    totalSteps: Int,
    rewards: List<DailyReward>,
    onClaim: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Gold.copy(alpha = 0.08f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            Gold.copy(alpha = 0.3f)
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🏆 Daily Reward Track",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Gold
                )
                if (currentStep < totalSteps) {
                    Button(
                        onClick = onClaim,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Gold,
                            contentColor = Color.Black
                        ),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text("Claim Reward", fontWeight = FontWeight.Bold)
                    }
                } else {
                    Surface(
                        color = Green.copy(alpha = 0.15f),
                        shape = MaterialTheme.shapes.small,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Green)
                    ) {
                        Text(
                            text = "✅ All Claimed",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Green,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
            
            // Progress steps
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                for (i in 0 until totalSteps) {
                    val isCompleted = i < currentStep
                    val isCurrent = i == currentStep
                    Surface(
                        modifier = Modifier.weight(1f).height(6.dp),
                        shape = MaterialTheme.shapes.small,
                        color = when {
                            isCompleted -> Gold
                            isCurrent -> Gold.copy(alpha = 0.5f)
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        }
                    ) {}
                }
            }
            
            // Reward icons
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(rewards) { reward ->
                    RewardStepItem(
                        reward = reward,
                        isUnlocked = reward.step <= currentStep,
                        modifier = Modifier.width(40.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun RewardStepItem(
    reward: DailyReward,
    isUnlocked: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = reward.icon,
            fontSize = 20.sp,
            alpha = if (isUnlocked) 1f else 0.3f
        )
        Text(
            text = if (reward.type.name == "CURRENCY") "+${reward.amount}" else "",
            style = MaterialTheme.typography.labelSmall,
            color = if (isUnlocked) Gold else MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1
        )
    }
}