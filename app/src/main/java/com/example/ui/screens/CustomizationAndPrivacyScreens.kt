package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.*
import com.example.viewmodel.BrowserViewModel
import com.example.viewmodel.KhojScreen

@Composable
fun CustomizationScreen(viewModel: BrowserViewModel) {
    val settings by viewModel.settings.collectAsState()

    val palettes = listOf(
        "Ocean Blue" to KhojPrimaryBlue,
        "Midnight Blue" to KhojDeepBlue,
        "Sunset Orange" to SunsetOrange,
        "Emerald" to EmeraldGreen,
        "Purple Dream" to PurpleDream,
        "Midnight Black" to MidnightBlack
    )

    val wallpapers = listOf(
        "Nepal Mountains",
        "Clean Minimal",
        "Himalayan Dusk",
        "Deep Ocean"
    )

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.navigateTo(KhojScreen.SETTINGS) }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Text("Customize Your KHOJ", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Theme Mode
            Text("APPEARANCE MODE", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = KhojPrimaryBlue)
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    listOf("System", "Light", "Dark").forEach { mode ->
                        val isSelected = settings.themeMode == mode
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { viewModel.updateSettings(settings.copy(themeMode = mode)) }
                                .padding(vertical = 10.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(selected = isSelected, onClick = null)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                when (mode) {
                                    "System" -> "Follow Android System"
                                    "Light" -> "Always Light Mode"
                                    else -> "Always Dark Mode"
                                },
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Color Themes
            Text("BRAND PALETTES", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = KhojPrimaryBlue)
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(palettes) { (name, color) ->
                    val isSelected = settings.themePalette == name
                    Card(
                        modifier = Modifier
                            .width(110.dp)
                            .height(90.dp)
                            .clickable { viewModel.updateSettings(settings.copy(themePalette = name)) }
                            .testTag("palette_$name"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = if (isSelected) CardDefaults.outlinedCardBorder() else null
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(color)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(name, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Wallpapers & Backgrounds
            Text("HOME BACKGROUND GALLERY", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = KhojPrimaryBlue)
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    wallpapers.forEach { wp ->
                        val isSelected = settings.selectedWallpaper == wp
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { viewModel.updateSettings(settings.copy(selectedWallpaper = wp)) }
                                .padding(vertical = 10.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(selected = isSelected, onClick = null)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(wp, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.weight(1f))
                            if (wp == "Nepal Mountains") {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.img_nepal_mountains),
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Home Page Modules Toggle
            Text("HOME SCREEN SECTIONS", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = KhojPrimaryBlue)
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Show Trending Searches", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            Text("Display popular topics and queries in Nepal", fontSize = 12.sp, color = Color.Gray)
                        }
                        Switch(
                            checked = settings.showTrendingSearches,
                            onCheckedChange = { viewModel.updateSettings(settings.copy(showTrendingSearches = it)) }
                        )
                    }

                    Divider(modifier = Modifier.padding(vertical = 10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Show Recently Visited Sites", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            Text("Quick jump back into regular browsing", fontSize = 12.sp, color = Color.Gray)
                        }
                        Switch(
                            checked = settings.showRecentVisited,
                            onCheckedChange = { viewModel.updateSettings(settings.copy(showRecentVisited = it)) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun PrivacyScreen(viewModel: BrowserViewModel) {
    val settings by viewModel.settings.collectAsState()
    var showClearDataDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.navigateTo(KhojScreen.SETTINGS) }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Text("Privacy & Security Center", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Shield, contentDescription = null, tint = KhojPrimaryBlue, modifier = Modifier.size(36.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Active Protection Enabled", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text("KHOJ blocks cross-site trackers and invasive cookies by default.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text("TRACKING & SAFETY", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = KhojPrimaryBlue)
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Tracking Protection", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            Text("Stop tracking cookies and script beacons", fontSize = 12.sp, color = Color.Gray)
                        }
                        Switch(
                            checked = settings.trackingProtection,
                            onCheckedChange = { viewModel.updateSettings(settings.copy(trackingProtection = it)) }
                        )
                    }

                    Divider(modifier = Modifier.padding(vertical = 10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Safe Search Engine Mode", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            Text("Filter explicit results from search indexing", fontSize = 12.sp, color = Color.Gray)
                        }
                        Switch(
                            checked = settings.safeSearch,
                            onCheckedChange = { viewModel.updateSettings(settings.copy(safeSearch = it)) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text("DATA CLEARING", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = KhojPrimaryBlue)
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Button(
                        onClick = { showClearDataDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("clear_browsing_data_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Icon(Icons.Default.DeleteSweep, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Clear All Browsing Data & History")
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showClearDataDialog) {
        AlertDialog(
            onDismissRequest = { showClearDataDialog = false },
            title = { Text("Clear Browsing Data?") },
            text = { Text("This will permanently delete your browsing history and recent searches from this device.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.repository.clearAllHistory()
                        viewModel.repository.clearRecentSearches()
                        showClearDataDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Clear Everything")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDataDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
