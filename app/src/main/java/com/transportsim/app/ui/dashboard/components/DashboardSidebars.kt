package com.transportsim.app.ui.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.transportsim.app.ui.dashboard.models.*
import com.transportsim.app.ui.theme.*
import com.transportsim.domain.models.VehicleCategory

@Composable
fun DashLeft(
    categories: List<CategoryProgress>,
    selectedCategory: VehicleCategory?,
    onCategorySelected: (VehicleCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(220.dp)
            .fillMaxHeight()
            .background(Brush.verticalGradient(listOf(Color(0xFF060F1E), Color(0xFF040A14))))
            .drawBehind {
                drawLine(
                    color = Cyan.copy(alpha = 0.2f),
                    start = Offset(size.width, 0f),
                    end = Offset(size.width, size.height),
                    strokeWidth = 1.dp.toPx()
                )
            }
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        PanelTitle("Fleet")

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(categories) { category ->
                VehicleCategoryCard(
                    category = category,
                    isSelected = selectedCategory == category.category,
                    onClick = { onCategorySelected(category.category) }
                )
            }
        }

        // Managed dispatch hint
        Text(
            text = "Managing dispatch, routes & live status? Head to Fleet →",
            style = MaterialTheme.typography.labelSmall,
            color = TextDim,
            lineHeight = 14.sp,
            modifier = Modifier.padding(top = 12.dp)
        )
    }
}

@Composable
fun VehicleCategoryCard(
    category: CategoryProgress,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val accentColor = when (category.category) {
        VehicleCategory.BUS -> Cyan
        VehicleCategory.MATATU -> Gold
        VehicleCategory.PICKUP -> Green
        VehicleCategory.LORRY -> Orange
        VehicleCategory.BODA -> Purple
        VehicleCategory.TAXI -> Pink
    }

    val background = Brush.linearGradient(
        listOf(accentColor.copy(alpha = 0.06f), Color(0xFF04080F).copy(alpha = 0.95f))
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .border(
                width = 1.dp,
                color = if (isSelected) accentColor else Border.copy(alpha = 0.7f),
                shape = RoundedCornerShape(8.dp)
            )
            .background(background)
            .clickable { onClick() }
            .padding(10.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${getCategoryIcon(category.category)} ${category.category.name.lowercase().capitalize()}",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color.White
                    )
                )
                Text(
                    text = "${category.owned}/${category.total} OWNED",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Gold,
                        fontFamily = ShareTechMonoFamily
                    ),
                    modifier = Modifier
                        .background(Gold.copy(alpha = 0.1f), RoundedCornerShape(10.dp))
                        .border(1.dp, Gold.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { category.progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = accentColor,
                trackColor = Border
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "5 variants",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextDim
                )
                Text(
                    text = "Tap to browse →",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextDim
                )
            }
        }
    }
}

@Composable
fun DashRight(
    revenue: Int,
    passengers: Int,
    cargo: Int,
    onTimeRate: Float,
    routeStatuses: List<RouteStatus>,
    fuelAlerts: List<FuelAlert>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(220.dp)
            .fillMaxHeight()
            .background(Brush.verticalGradient(listOf(Color(0xFF07101E), Color(0xFF04090F))))
            .drawBehind {
                drawLine(
                    color = Purple.copy(alpha = 0.2f),
                    start = Offset(0f, 0f),
                    end = Offset(0f, size.height),
                    strokeWidth = 1.dp.toPx()
                )
            }
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        PanelTitle("Today's Performance")

        MiniStatCard("Revenue", "KSH ${revenue.toLocaleString()}", Green, "")
        MiniStatCard("Passengers", passengers.toString(), Cyan, "")
        MiniStatCard("Cargo (kg)", cargo.toLocaleString(), Orange, "")
        MiniStatCard("On-Time Rate", "${onTimeRate}%", Gold, "")

        Spacer(modifier = Modifier.height(4.dp))
        PanelTitle("Route Status")
        
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .background(BgCard)
                .border(1.dp, Border, RoundedCornerShape(6.dp))
                .padding(10.dp)
        ) {
            routeStatuses.take(5).forEach { status ->
                RouteStatusMiniRow(status)
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
        PanelTitle("Fuel Alert")
        
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            fuelAlerts.take(2).forEach { alert ->
                FuelAlertMiniItem(alert)
            }
        }
    }
}

@Composable
fun MiniStatCard(label: String, value: String, color: Color, change: String) {
    val background = Brush.linearGradient(
        listOf(color.copy(alpha = 0.1f), Color(0xFF050C16).copy(alpha = 0.95f))
    )
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, Border.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
            .background(background)
            .padding(vertical = 11.dp, horizontal = 12.dp)
    ) {
        Column {
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = TextSec,
                    letterSpacing = 1.sp
                )
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontFamily = OrbitronFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = color
                )
            )
            if (change.isNotEmpty()) {
                Text(
                    text = change,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = if (change.contains("▲")) Green else Red,
                        fontSize = 11.sp
                    )
                )
            }
        }
        // Right accent bar
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight()
                .width(3.dp)
                .background(color.copy(alpha = 0.5f))
        )
    }
}

@Composable
fun RouteStatusMiniRow(route: RouteStatus) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = route.name,
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
            color = TextSec,
            maxLines = 1
        )
        
        val isLive = route.status == "LIVE"
        val statusBrush = if (isLive) {
            Brush.horizontalGradient(listOf(Green.copy(alpha = 0.2f), Cyan.copy(alpha = 0.15f)))
        } else {
            Brush.horizontalGradient(listOf(Gold.copy(alpha = 0.22f), Orange.copy(alpha = 0.15f)))
        }
        
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(statusBrush)
                .border(1.dp, (if (isLive) Cyan else Gold).copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                .padding(horizontal = 8.dp, vertical = 2.dp)
        ) {
            Text(
                text = statusBrush.toString().let { route.status }, // Hack to avoid unused statusBrush warning if needed, but we use it in background
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = ShareTechMonoFamily,
                    color = if (isLive) Color(0xFF00E676) else Gold
                )
            )
        }
    }
}

@Composable
fun FuelAlertMiniItem(alert: FuelAlert) {
    Column {
        Text(
            text = "Vehicle #${alert.vehicleId} fuel: ${alert.fuelPct.toInt()}%",
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
            color = TextSec,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        LinearProgressIndicator(
            progress = { alert.fuelPct / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
                .clip(RoundedCornerShape(2.dp)),
            color = if (alert.fuelPct < 20) Red else Gold,
            trackColor = Border
        )
    }
}

@Composable
fun PanelTitle(title: String) {
    Column(modifier = Modifier.padding(bottom = 6.dp)) {
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = OrbitronFamily,
                fontSize = 12.sp,
                letterSpacing = 2.sp,
                brush = Brush.horizontalGradient(listOf(Cyan, Purple))
            )
        )
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Cyan.copy(alpha = 0.2f))
        )
    }
}

fun getCategoryIcon(category: VehicleCategory): String = when (category) {
    VehicleCategory.BUS -> "🚌"
    VehicleCategory.MATATU -> "🚐"
    VehicleCategory.PICKUP -> "🛻"
    VehicleCategory.LORRY -> "🚛"
    VehicleCategory.BODA -> "🏍"
    VehicleCategory.TAXI -> "🚕"
}

private fun String.capitalize() = replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
