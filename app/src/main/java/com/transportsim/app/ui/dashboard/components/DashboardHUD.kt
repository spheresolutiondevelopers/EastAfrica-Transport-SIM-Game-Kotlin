package com.transportsim.app.ui.dashboard.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.transportsim.app.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DashboardHUD(
    balance: Int,
    fleetSize: Int,
    activeSize: Int,
    level: Int,
    currentScreen: String,
    onScreenSelected: (String) -> Unit,
    onMenuClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val isSmallHeight = configuration.screenHeightDp < 520
    
    val hudBackground = Brush.horizontalGradient(
        0.0f to Color(0xFF040C18),
        0.2f to Color(0xFF071428),
        0.4f to Color(0xFF0A0E22),
        0.6f to Color(0xFF070E1E),
        1.0f to Color(0xFF060C18)
    )

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(hudBackground)
                .padding(horizontal = if (isSmallHeight) 10.dp else 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (isSmallHeight) {
                IconButton(onClick = onMenuClick) {
                    Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Cyan)
                }
            }

            // Logo Section
            HUDLogo(isSmallHeight)

            // Stats Section
            HUDStats(balance, fleetSize, activeSize, level, isSmallHeight)

            // Navigation Section (Scrollable)
            HUDNav(
                currentScreen = currentScreen,
                onScreenSelected = onScreenSelected,
                isSmallHeight = isSmallHeight,
                modifier = Modifier.weight(1f, fill = false).padding(horizontal = 8.dp)
            )

            // Time Section (Hidden on very small screens to save space if needed, but keeping for now)
            HUDTime(isSmallHeight)
        }

        // Animated HUD Line
        HUDAnimatedLine()
    }
}

@Composable
fun HUDLogo(isSmallHeight: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(if (isSmallHeight) 24.dp else 36.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Brush.linearGradient(listOf(Cyan, Purple))),
            contentAlignment = Alignment.Center
        ) {
            Text("🚌", fontSize = if (isSmallHeight) 13.sp else 18.sp)
        }
        Column {
            Text(
                text = "TRANSPORTSIM",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontSize = if (isSmallHeight) 12.sp else 16.sp,
                    brush = Brush.horizontalGradient(listOf(Cyan, Purple))
                )
            )
            if (!isSmallHeight) {
                Text(
                    text = "REAL MAP-DRIVEN SIMULATION",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextDim,
                    letterSpacing = 2.sp
                )
            }
        }
    }
}

@Composable
fun HUDStats(balance: Int, fleetSize: Int, activeSize: Int, level: Int, isSmallHeight: Boolean) {
    Row(
        modifier = Modifier
            .border(1.dp, Border.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            .background(Color.White.copy(alpha = 0.03f))
            .padding(vertical = if (isSmallHeight) 2.dp else 4.dp, horizontal = if (isSmallHeight) 8.dp else 14.dp),
        horizontalArrangement = Arrangement.spacedBy(if (isSmallHeight) 0.dp else 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HUDStatItem("KSH ${balance.toLocaleString()}", "Balance", Cyan, isSmallHeight)
        HUDStatDivider()
        HUDStatItem(fleetSize.toString(), "Fleet", Gold, isSmallHeight)
        HUDStatDivider()
        HUDStatItem(activeSize.toString(), "Active", Green, isSmallHeight)
        HUDStatDivider()
        HUDStatItem("Lvl $level", "Level", Orange, isSmallHeight)
    }
}

@Composable
fun HUDStatItem(value: String, label: String, color: Color, isSmallHeight: Boolean) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = if (isSmallHeight) 6.dp else 12.dp)
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(
                fontFamily = OrbitronFamily,
                fontWeight = FontWeight.Bold,
                fontSize = if (isSmallHeight) 10.sp else 15.sp,
                color = color
            )
        )
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = if (isSmallHeight) 6.sp else 9.sp,
                color = TextDim,
                letterSpacing = if (isSmallHeight) 0.5.sp else 1.sp
            )
        )
    }
}

@Composable
fun HUDStatDivider() {
    Box(
        modifier = Modifier
            .height(if (LocalConfiguration.current.screenHeightDp < 520) 16.dp else 24.dp)
            .width(1.dp)
            .background(Border.copy(alpha = 0.6f))
    )
}

@Composable
fun HUDNav(
    currentScreen: String,
    onScreenSelected: (String) -> Unit,
    isSmallHeight: Boolean,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    
    val showLeftArrow by remember {
        derivedStateOf { scrollState.value > 0 }
    }
    val showRightArrow by remember {
        derivedStateOf { scrollState.value < scrollState.maxValue && scrollState.maxValue > 0 }
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (showLeftArrow) {
            IconButton(
                onClick = { scope.launch { scrollState.animateScrollTo(scrollState.value - 150) } },
                modifier = Modifier.size(if (isSmallHeight) 20.dp else 28.dp)
            ) {
                Icon(Icons.Default.ChevronLeft, contentDescription = "Scroll Left", tint = Cyan)
            }
        }

        Row(
            modifier = Modifier
                .weight(1f, fill = false)
                .horizontalScroll(scrollState)
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val navItems = listOf(
                "dashboard" to "🗺 Dashboard",
                "fleet" to "🚛 Fleet",
                "routes" to "📍 Routes",
                "missions" to "📋 Missions",
                "garage" to "🔧 Garage",
                "settings" to "⚙ Settings"
            )

            navItems.forEach { (id, label) ->
                HUDNavButton(
                    label = label,
                    isActive = currentScreen == id,
                    isSmallHeight = isSmallHeight,
                    onClick = { onScreenSelected(id) }
                )
            }
        }

        if (showRightArrow) {
            IconButton(
                onClick = { scope.launch { scrollState.animateScrollTo(scrollState.value + 150) } },
                modifier = Modifier.size(if (isSmallHeight) 20.dp else 28.dp)
            ) {
                Icon(Icons.Default.ChevronRight, contentDescription = "Scroll Right", tint = Cyan)
            }
        }
    }
}

@Composable
fun HUDNavButton(label: String, isActive: Boolean, isSmallHeight: Boolean, onClick: () -> Unit) {
    val borderColor = if (isActive) Cyan else Border.copy(alpha = 0.8f)
    val background = if (isActive) {
        Brush.linearGradient(listOf(Cyan.copy(alpha = 0.22f), Purple.copy(alpha = 0.18f)))
    } else {
        Brush.linearGradient(listOf(Color.White.copy(alpha = 0.03f), Color.White.copy(alpha = 0.03f)))
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(5.dp))
            .border(1.dp, borderColor, RoundedCornerShape(5.dp))
            .background(background)
            .clickable { onClick() }
            .padding(
                vertical = if (isSmallHeight) 4.dp else 7.dp,
                horizontal = if (isSmallHeight) 8.dp else 16.dp
            )
    ) {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = if (isSmallHeight) 9.sp else 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isActive) Color.White else TextSec,
                letterSpacing = if (isSmallHeight) 0.5.sp else 1.sp
            )
        )
    }
}

@Composable
fun HUDTime(isSmallHeight: Boolean) {
    var time by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        while (true) {
            val now = Calendar.getInstance().time
            time = SimpleDateFormat("HH:mm:ss", Locale.US).format(now)
            date = SimpleDateFormat("EEE dd MMM yyyy — 'NAIROBI'", Locale.US).format(now).uppercase()
            kotlinx.coroutines.delay(1000)
        }
    }

    Column(horizontalAlignment = Alignment.End) {
        Text(
            text = time,
            style = MaterialTheme.typography.labelLarge.copy(
                color = Cyan,
                fontSize = if (isSmallHeight) 10.sp else 13.sp
            )
        )
        if (!isSmallHeight) {
            Text(
                text = date,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = TextDim,
                    fontSize = 10.sp
                )
            )
        }
    }
}

@Composable
fun HUDAnimatedLine() {
    val infiniteTransition = rememberInfiniteTransition(label = "hudLine")
    val offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2000f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "hudLineOffset"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(2.dp)
            .drawBehind {
                val brush = Brush.linearGradient(
                    colors = listOf(Cyan, Purple, Orange, Green, Cyan),
                    start = Offset(offset, 0f),
                    end = Offset(offset + size.width, 0f),
                    tileMode = androidx.compose.ui.graphics.TileMode.Repeated
                )
                drawRect(brush)
            }
    )
}

fun Int.toLocaleString(): String {
    return String.format("%,d", this)
}
