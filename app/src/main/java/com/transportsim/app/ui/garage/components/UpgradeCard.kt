package com.transportsim.app.ui.garage.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.transportsim.app.ui.theme.*
import com.transportsim.domain.models.UpgradeWithLevel

@Composable
fun UpgradeCard(
    upgrade: UpgradeWithLevel,
    onUpgrade: () -> Unit,
    modifier: Modifier = Modifier
) {
    val def = upgrade.definition
    val isMaxed = upgrade.isMaxed

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp, if (isMaxed) Gold.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
            // Header
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(def.icon, fontSize = 20.sp)
                    Text(def.displayName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                }
                Text("LVL ${upgrade.currentLevel} / ${def.maxLevel}", style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold, color = Gold)
            }

            // Progress bars
            Row(modifier = Modifier.fillMaxWidth().padding(top = 4.dp), horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                for (i in 0 until def.maxLevel) {
                    Surface(modifier = Modifier.weight(1f).height(6.dp), shape = MaterialTheme.shapes.small,
                        color = if (i < upgrade.currentLevel) Cyan else MaterialTheme.colorScheme.surfaceVariant) {}
                }
            }

            // Description
            Text(def.description, style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 8.dp))

            // Effect display
            if (upgrade.currentLevel > 0) {
                Text("+ ${upgrade.displayEffect}",
                    style = MaterialTheme.typography.labelSmall, color = Green, modifier = Modifier.padding(top = 4.dp))
            }

            // Footer
            Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                if (isMaxed) {
                    Surface(color = Gold.copy(alpha = 0.15f), shape = MaterialTheme.shapes.small,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Gold)) {
                        Text("MAXED", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold,
                            color = Gold, modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp))
                    }
                } else {
                    Text("KSH ${upgrade.nextCost}", style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold, color = Gold)
                    Button(onClick = onUpgrade, colors = ButtonDefaults.buttonColors(containerColor = Cyan,
                        contentColor = Color.Black), shape = MaterialTheme.shapes.small) {
                        Text("Upgrade", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
