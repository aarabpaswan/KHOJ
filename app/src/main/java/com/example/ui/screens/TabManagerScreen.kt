package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.KhojAccentOrange
import com.example.ui.theme.KhojPrimaryBlue
import com.example.viewmodel.BrowserViewModel
import com.example.viewmodel.KhojScreen

@Composable
fun TabManagerScreen(viewModel: BrowserViewModel) {
    val tabs by viewModel.tabs.collectAsState()
    val activeTabId by viewModel.activeTabId.collectAsState()

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { viewModel.navigateTo(KhojScreen.WEB_BROWSER) }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                    Text("${tabs.size} Open Tabs", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }

                Row {
                    IconButton(
                        onClick = { viewModel.openNewTab("https://en.wikipedia.org", isIncognito = true) },
                        modifier = Modifier.testTag("new_incognito_tab_button")
                    ) {
                        Icon(Icons.Default.VisibilityOff, contentDescription = "New Incognito Tab")
                    }
                    IconButton(
                        onClick = { viewModel.openNewTab("https://en.wikipedia.org") },
                        modifier = Modifier.testTag("new_tab_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "New Tab", tint = KhojPrimaryBlue)
                    }
                }
            }
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(
                    onClick = { viewModel.closeAllTabs() },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("close_all_tabs_button")
                ) {
                    Text("Close All Tabs", color = MaterialTheme.colorScheme.error)
                }

                Button(
                    onClick = { viewModel.openNewTab() },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = KhojPrimaryBlue)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("New Tab")
                }
            }
        }
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(tabs) { tab ->
                val isActive = tab.id == activeTabId
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { viewModel.selectTab(tab.id) }
                        .testTag("tab_card_${tab.id}"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (tab.isIncognito) Color(0xFF1E2433) else MaterialTheme.colorScheme.surface
                    ),
                    border = if (isActive) CardDefaults.outlinedCardBorder() else null
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(KhojPrimaryBlue.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    if (tab.isIncognito) Icons.Default.VisibilityOff else Icons.Default.Language,
                                    contentDescription = null,
                                    tint = if (tab.isIncognito) KhojAccentOrange else KhojPrimaryBlue,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = tab.title,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                modifier = Modifier.weight(1f),
                                color = if (tab.isIncognito) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                            IconButton(
                                onClick = { viewModel.closeTab(tab.id) },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Close Tab",
                                    tint = Color.Gray,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Divider(modifier = Modifier.padding(vertical = 8.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = tab.url,
                                    fontSize = 11.sp,
                                    color = Color.Gray,
                                    maxLines = 2,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                                if (isActive) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    SuggestionChip(
                                        onClick = { },
                                        label = { Text("Active", fontSize = 10.sp) },
                                        modifier = Modifier.height(24.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
