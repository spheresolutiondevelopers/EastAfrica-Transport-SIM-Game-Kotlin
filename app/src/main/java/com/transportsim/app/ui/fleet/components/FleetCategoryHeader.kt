package com.transportsim.app.ui.fleet.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.transportsim.app.ui.theme.*

@Composable
fun FleetCategoryHeader(
    category: String,
    count: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category icon and name
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val icon = when (category.uppercase()) {
                    "BUS" -> "🚌"
                    "MATATU" -> "🚐"
                    "PICKUP" -> "🛻"
                    "LORRY" -> "🚛"
                    "BODA" -> "🏍"
                    "TAXI" -> "🚕"
                    else -> "🚗"
                }
                Text(
                    text = "$icon $category",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = OrbitronFamily,
                        letterSpacing = 2.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = TextSec,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            }

            Text(
                text = "$count vehicles",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = ShareTechMonoFamily,
                    letterSpacing = 0.sp
                ),
                color = TextDim
            )
        }
        
        // Bottom divider matching design
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Border)
        )
    }
}
