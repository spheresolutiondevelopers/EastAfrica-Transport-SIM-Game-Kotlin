package com.transportsim.app.ui.fleet

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.input.key.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.transportsim.app.ui.dashboard.components.VehicleTurntable
import com.transportsim.app.ui.fleet.components.*
import com.transportsim.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FleetScreen(
    onVehicleSelected: (Int) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: FleetViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    val focusRequester = remember { androidx.compose.ui.focus.FocusRequester() }
    
    // Track selected vehicle for 3D view
    val selectedVehicle = uiState.fleetVehicles.getOrNull(uiState.selectedIndex)
    
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDeep)
            .focusRequester(focusRequester)
            .focusable()
            .onKeyEvent { event ->
                if (event.type == KeyEventType.KeyDown) {
                    when (event.key) {
                        Key.V -> { viewModel.toggleViewMode(); true }
                        Key.DirectionLeft -> { viewModel.previousVehicle(); true }
                        Key.DirectionRight -> { viewModel.nextVehicle(); true }
                        Key.R -> { /* TODO: Reset Camera */ true }
                        else -> false
                    }
                } else false
            }
    ) {
        // ─── BACKGROUND: 3D SHOWROOM (Full-Bleed) ───────────────────
        VehicleTurntable(
            fleetVehicle = selectedVehicle,
            onNext = { viewModel.nextVehicle() },
            onPrevious = { viewModel.previousVehicle() },
            showControls = false,
            modifier = Modifier.fillMaxSize()
        )

        // ─── LAYER 1: GLOBAL HEADER (Floating, Borderless) ─────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(Color.Black.copy(alpha = 0.15f)) // Faint dark wash
                .align(Alignment.TopCenter)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: Wordmark
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White.copy(alpha = 0.8f)
                        )
                    }
                    Text(
                        text = "FLEET MANAGER",
                        style = MaterialTheme.typography.titleMedium,
                        color = Cyan,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp,
                        fontFamily = OrbitronFamily
                    )
                }
                
                Spacer(modifier = Modifier.weight(0.1f))
                
                // Center: Category Chips
                FleetCategoryChips(
                    categories = uiState.categories,
                    selectedCategory = uiState.selectedCategory,
                    onCategorySelected = viewModel::setFilter,
                    modifier = Modifier.weight(1f)
                )
                
                Spacer(modifier = Modifier.weight(0.1f))
                
                // Right: Purchase Button
                Button(
                    onClick = { viewModel.showPurchaseModal() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Green.copy(alpha = 0.9f),
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.height(32.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                ) {
                    Text(
                        text = "+ PURCHASE VEHICLE",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }

        if (uiState.isLoading) {
            CircularProgressIndicator(
                color = Cyan,
                modifier = Modifier.align(Alignment.Center)
            )
        } else if (uiState.error != null) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = uiState.error ?: "Unknown error", color = Red)
                Button(onClick = { viewModel.loadFleetData() }) {
                    Text("Retry")
                }
            }
        } else {
            // ─── LAYER 2: SIDEBARS & PANELS ─────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 64.dp) // Below header
            ) {
                // LEFT SIDEBAR: Vehicle List
                Column(
                    modifier = Modifier
                        .width(200.dp)
                        .fillMaxHeight()
                        .padding(start = 12.dp, bottom = 12.dp)
                ) {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        contentPadding = PaddingValues(vertical = 4.dp)
                    ) {
                        itemsIndexed(uiState.fleetVehicles) { index, fleetVehicle ->
                            FleetListItem(
                                fleetVehicle = fleetVehicle,
                                isSelected = index == uiState.selectedIndex,
                                onClick = { viewModel.selectVehicle(index) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                // RIGHT PANEL: Vehicle Details
                Column(
                    modifier = Modifier
                        .width(220.dp)
                        .fillMaxHeight()
                        .padding(end = 12.dp, bottom = 12.dp)
                ) {
                    FleetDetailPanel(
                        fleetVehicle = selectedVehicle,
                        onService = { viewModel.serviceVehicle(it) },
                        onUpgrade = { viewModel.upgradeVehicle(it) },
                        onCustomize = { viewModel.customizeVehicle(it) },
                        onPurchase = { viewModel.purchaseVehicle(it) },
                        modifier = Modifier.fillMaxWidth().weight(1f)
                    )
                }
            }
            
            // ─── LAYER 3: CANVAS OVERLAYS ───────────────────────────
            // View toggle button
            FleetViewToggleButton(
                isInteriorMode = uiState.isInteriorMode,
                onClick = { viewModel.toggleViewMode() },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 72.dp, end = 240.dp) // Adjusted to avoid detail panel overlap
            )
            
            // HUD status line (Bottom Left)
            Text(
                text = if (uiState.isInteriorMode) 
                    "CABIN VIEW · DRAG TO LOOK AROUND" 
                else 
                    "EXTERIOR ORBIT · DRAG TO ROTATE · SCROLL TO ZOOM",
                style = MaterialTheme.typography.labelSmall,
                color = TextDim.copy(alpha = 0.7f),
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            )
            
            // Navigation chevrons (Center)
            FleetChevronControls(
                onPrev = { viewModel.previousVehicle() },
                onNext = { viewModel.nextVehicle() },
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxSize()
            )
        }
    }
    
    // Purchase Modal
    if (uiState.showPurchaseModal) {
        FleetPurchaseModal(
            availableVehicles = uiState.availableVehicles,
            onPurchase = { viewModel.purchaseVehicle(it) },
            onDismiss = { viewModel.dismissPurchaseModal() }
        )
    }
}
