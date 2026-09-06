package com.transportsim.app.ui.fleet.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.transportsim.app.ui.fleet.FleetCategory
import com.transportsim.app.ui.theme.*
import com.transportsim.domain.models.VehicleCategory

@Composable
fun FleetCategoryChips(
    categories: List<FleetCategory>,
    selectedCategory: VehicleCategory?,
    onCategorySelected: (VehicleCategory?) -> Unit,
    modifier: Modifier = Modifier
) {
    val allCategories = listOf(null as VehicleCategory?) + categories.map { it.category }
    val allLabels = listOf("ALL") + categories.map { it.category.name }
    val allIcons = listOf("◈") + categories.map { 
        when (it.category) {
            VehicleCategory.BUS -> "🚌"
            VehicleCategory.MATATU -> "🚐"
            VehicleCategory.PICKUP -> "🛻"
            VehicleCategory.LORRY -> "🚛"
            VehicleCategory.BODA -> "🏍"
            VehicleCategory.TAXI -> "🚕"
        }
    }
    
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items(allCategories.indices.toList()) { index ->
            val category = allCategories[index]
            val label = allLabels[index]
            val icon = allIcons[index]
            val isSelected = selectedCategory == category
            
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .then(
                        if (isSelected) {
                            Modifier.shadow(elevation = 8.dp, shape = RoundedCornerShape(20.dp), ambientColor = Cyan, spotColor = Cyan)
                        } else Modifier
                    )
                    .background(
                        if (isSelected) Cyan.copy(alpha = 0.25f) 
                        else Color.White.copy(alpha = 0.05f)
                    )
                    .clickable { onCategorySelected(category) }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = icon,
                        style = MaterialTheme.typography.labelMedium,
                        fontSize = 12.sp
                    )
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isSelected) Cyan else Color.White.copy(alpha = 0.7f),
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }
}
