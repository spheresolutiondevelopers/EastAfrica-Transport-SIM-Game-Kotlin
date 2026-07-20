package com.transportsim.app.ui.dashboard.components

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.transportsim.app.ui.theme.*

@Composable
fun QuickStatsRow(
    revenue: Int,
    passengers: Int,
    cargo: Int,
    onTimeRate: Float,
    modifier: Modifier = Modifier
) {
    val stats = listOf(
        QuickStat("Revenue", "KSH $revenue", Green, "+12.4%"),
        QuickStat("Passengers", passengers.toString(), Cyan, "+8.1%"),
        QuickStat("Cargo (kg)", cargo.toString(), Orange, "-3.2%"),
        QuickStat("On-Time", "${onTimeRate}%", Gold, "+1.8%")
    )
    
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(stats) { stat ->
            QuickStatCard(stat)
        }
    }
}

@Composable
fun QuickStatCard(stat: QuickStat) {
    Card(
        modifier = Modifier
            .width(140.dp)
            .height(80.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            stat.color.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stat.label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 1.sp
            )
            Text(
                text = stat.value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = stat.color
            )
            Text(
                text = stat.change,
                style = MaterialTheme.typography.labelSmall,
                color = if (stat.change.startsWith("+")) Green else Red
            )
        }
    }
}

data class QuickStat(
    val label: String,
    val value: String,
    val color: Color,
    val change: String
)