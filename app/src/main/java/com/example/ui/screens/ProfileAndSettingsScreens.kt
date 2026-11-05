package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.KhojAccentOrange
import com.example.ui.theme.KhojPrimaryBlue
import com.example.viewmodel.BrowserViewModel
import com.example.viewmodel.KhojScreen

@Composable
fun ProfileScreen(viewModel: BrowserViewModel) {
    val user by viewModel.currentUser.collectAsState()
    val bookmarks by viewModel.bookmarks.collectAsState()
    val history by viewModel.history.collectAsState()
    val downloads by viewModel.downloads.collectAsState()

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.navigateTo(KhojScreen.HOME) }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Text("KMail Account Profile", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Avatar
            Box(
                modifier = Modifier
                    .size(84.dp)
                    .clip(CircleShape)
                    .background(KhojPrimaryBlue),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = (user?.fullName?.firstOrNull() ?: 'K').uppercase(),
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = user?.fullName ?: "Guest Explorer",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = user?.email ?: "guest@khoj.com",
                fontSize = 14.sp,
                color = KhojPrimaryBlue,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Account Stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(bookmarks.size.toString(), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = KhojPrimaryBlue)
                        Text("Bookmarks", fontSize = 11.sp, color = Color.Gray)
                    }
                }
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(history.size.toString(), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = KhojPrimaryBlue)
                        Text("History", fontSize = 11.sp, color = Color.Gray)
                    }
                }
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(downloads.size.toString(), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = KhojPrimaryBlue)
                        Text("Downloads", fontSize = 11.sp, color = Color.Gray)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Account Details Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("ACCOUNT DETAILS", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = KhojPrimaryBlue)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("KMail Username: ${user?.username ?: "guest"}", fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Joined: ${user?.joinedDate ?: "Nov 2026"}", fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Sync Status: Connected & Encrypted", fontSize = 14.sp, color = Color(0xFF10B981))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    viewModel.logout()
                    viewModel.navigateTo(KhojScreen.KMAIL_AUTH)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("logout_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Icon(Icons.Default.Logout, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Log Out of KMail")
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun SettingsScreen(viewModel: BrowserViewModel) {
    val settings by viewModel.settings.collectAsState()
    val user by viewModel.currentUser.collectAsState()
    val tabs by viewModel.tabs.collectAsState()

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.navigateTo(KhojScreen.HOME) }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Text("KHOJ Menu & Settings", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        },
        bottomBar = {
            KhojBottomNavigationBar(viewModel = viewModel, currentScreen = KhojScreen.SETTINGS, tabCount = tabs.size)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Profile Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        if (user == null || user?.isGuest == true) {
                            viewModel.navigateTo(KhojScreen.KMAIL_AUTH)
                        } else {
                            viewModel.navigateTo(KhojScreen.PROFILE)
                        }
                    },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(KhojPrimaryBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = (user?.fullName?.firstOrNull() ?: 'K').uppercase(),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            user?.fullName ?: "Guest Explorer",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            user?.email ?: "Tap to log in or create KMail",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = KhojPrimaryBlue)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Navigation Sections
            SettingsHeader(title = "KHOJ ECOSYSTEM")
            SettingsItem(
                icon = Icons.Default.Palette,
                title = "Customize Your KHOJ",
                subtitle = "Themes, wallpapers, search bar styles",
                onClick = { viewModel.navigateTo(KhojScreen.CUSTOMIZE) }
            )
            SettingsItem(
                icon = Icons.Default.AutoAwesome,
                title = "KHOJ AI Companion",
                subtitle = "Research topics, explain pages and code",
                onClick = { viewModel.navigateTo(KhojScreen.KHOJ_AI) }
            )
            SettingsItem(
                icon = Icons.Default.Language,
                title = "KHOJ Domains Registry",
                subtitle = ".np & global domain search and DNS",
                onClick = { viewModel.navigateTo(KhojScreen.KHOJ_DOMAINS) }
            )
            SettingsItem(
                icon = Icons.Default.Newspaper,
                title = "KHOJ Updates & Releases",
                subtitle = "Official news from Kathmandu engineering",
                onClick = { viewModel.navigateTo(KhojScreen.ANNOUNCEMENTS) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            SettingsHeader(title = "BROWSING & TOOLS")
            SettingsItem(
                icon = Icons.Default.Bookmark,
                title = "Saved Bookmarks",
                subtitle = "Folders, favorites, and reading list",
                onClick = { viewModel.navigateTo(KhojScreen.BOOKMARKS) }
            )
            SettingsItem(
                icon = Icons.Default.History,
                title = "Browsing History",
                subtitle = "View and manage visited sites",
                onClick = { viewModel.navigateTo(KhojScreen.HISTORY) }
            )
            SettingsItem(
                icon = Icons.Default.Download,
                title = "Downloads Manager",
                subtitle = "Manage downloaded files",
                onClick = { viewModel.navigateTo(KhojScreen.DOWNLOADS) }
            )
            SettingsItem(
                icon = Icons.Default.FilterNone,
                title = "Open Tabs Manager",
                subtitle = "Manage active and incognito tabs",
                onClick = { viewModel.navigateTo(KhojScreen.TAB_MANAGER) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            SettingsHeader(title = "SECURITY & GENERAL")
            SettingsItem(
                icon = Icons.Default.Security,
                title = "Privacy & Tracking Protection",
                subtitle = "Cookies, safe search, clear data",
                onClick = { viewModel.navigateTo(KhojScreen.PRIVACY) }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // About KHOJ Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.khoj_brand_logo),
                        contentDescription = "KHOJ Logo",
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(14.dp))
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("KHOJ Browser 1.0.0", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text("Born in Nepal. Built for the World.", fontSize = 12.sp, color = KhojAccentOrange, fontWeight = FontWeight.SemiBold)
                    Text("Your Internet. Your Discovery.", fontSize = 11.sp, color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun SettingsHeader(title: String) {
    Text(
        text = title,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = KhojPrimaryBlue,
        modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp),
        letterSpacing = 1.sp
    )
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(KhojPrimaryBlue.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = KhojPrimaryBlue, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Text(subtitle, fontSize = 12.sp, color = Color.Gray)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.LightGray)
        }
    }
}
