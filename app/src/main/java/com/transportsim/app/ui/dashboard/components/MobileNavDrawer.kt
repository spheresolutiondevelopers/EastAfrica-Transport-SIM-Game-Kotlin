package com.transportsim.app.ui.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.transportsim.app.ui.theme.*

@Composable
fun MobileNavDrawer(
    currentScreen: String,
    onScreenSelected: (String) -> Unit,
    balance: Int,
    level: Int,
    xp: Int,
    onClose: () -> Unit
) {
    ModalDrawerSheet(
        drawerContainerColor = BgDeep,
        drawerContentColor = Color.White,
        modifier = Modifier.width(280.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            // Logo
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(bottom = 32.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Brush.linearGradient(listOf(Cyan, Purple))),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🚌", fontSize = 16.sp)
                }
                Text(
                    text = "TRANSPORTSIM",
                    style = MaterialTheme.typography.titleMedium.copy(
                        brush = Brush.horizontalGradient(listOf(Cyan, Purple)),
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                )
            }

            // Nav Items
            val navItems = listOf(
                NavDrawerItem("dashboard", "Dashboard", Icons.Default.Map),
                NavDrawerItem("fleet", "My Fleet", Icons.Default.DirectionsBus),
                NavDrawerItem("routes", "Route Editor", Icons.Default.EditLocation),
                NavDrawerItem("missions", "Missions", Icons.Default.Assignment),
                NavDrawerItem("garage", "Garage", Icons.Default.Build),
                NavDrawerItem("settings", "Settings", Icons.Default.Settings)
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                navItems.forEach { item ->
                    DrawerButton(
                        item = item,
                        isActive = currentScreen == item.id,
                        onClick = {
                            onScreenSelected(item.id)
                            onClose()
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Stats at the bottom
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.05f))
                    .border(1.dp, Border.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatRow("BALANCE", "KSH ${balance.toLocaleString()}", Cyan)
                Divider(color = Border.copy(alpha = 0.2f))
                StatRow("LEVEL", "$level", Orange)
                
                // XP Bar
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("XP", style = MaterialTheme.typography.labelSmall, color = TextDim)
                        Text("$xp / 5000", style = MaterialTheme.typography.labelSmall, color = TextDim)
                    }
                    LinearProgressIndicator(
                        progress = xp / 5000f,
                        modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                        color = Cyan,
                        trackColor = Color.White.copy(alpha = 0.1f)
                    )
                }
            }
        }
    }
}

data class NavDrawerItem(val id: String, val label: String, val icon: ImageVector)

@Composable
fun DrawerButton(item: NavDrawerItem, isActive: Boolean, onClick: () -> Unit) {
    val background = if (isActive) {
        Brush.horizontalGradient(listOf(Cyan.copy(alpha = 0.15f), Color.Transparent))
    } else {
        Brush.horizontalGradient(listOf(Color.Transparent, Color.Transparent))
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(background)
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = null,
            tint = if (isActive) Cyan else TextSec,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = item.label,
            style = MaterialTheme.typography.bodyLarge.copy(
                color = if (isActive) Color.White else TextSec,
                fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal
            )
        )
    }
}

@Composable
fun StatRow(label: String, value: String, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = TextDim)
        Text(value, style = MaterialTheme.typography.bodyLarge.copy(color = color, fontWeight = FontWeight.Bold, fontFamily = OrbitronFamily))
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, backgroundColor = 0xFF04080F, widthDp = 280, heightDp = 600)
@Composable
fun MobileNavDrawerPreview() {
    TransportSimTheme {
        MobileNavDrawer(
            currentScreen = "dashboard",
            onScreenSelected = {},
            balance = 1250000,
            level = 24,
            xp = 3450,
            onClose = {}
        )
    }
}
