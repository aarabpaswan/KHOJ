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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.QuickShortcut
import com.example.ui.theme.KhojAccentOrange
import com.example.ui.theme.KhojDeepBlue
import com.example.ui.theme.KhojPrimaryBlue
import com.example.viewmodel.BrowserViewModel
import com.example.viewmodel.KhojScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowserHomeScreen(viewModel: BrowserViewModel) {
    val settings by viewModel.settings.collectAsState()
    val user by viewModel.currentUser.collectAsState()
    val shortcuts by viewModel.shortcuts.collectAsState()
    val recentSearches by viewModel.recentSearches.collectAsState()
    val history by viewModel.history.collectAsState()
    val tabs by viewModel.tabs.collectAsState()

    var searchInput by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    var showAddShortcutDialog by remember { mutableStateOf(false) }
    var newShortcutName by remember { mutableStateOf("") }
    var newShortcutUrl by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { viewModel.navigateTo(KhojScreen.HOME) }
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.khoj_brand_logo),
                        contentDescription = "KHOJ Logo",
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "KHOJ",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = KhojPrimaryBlue,
                        letterSpacing = 1.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { viewModel.navigateTo(KhojScreen.ANNOUNCEMENTS) },
                        modifier = Modifier.testTag("home_notifications_button")
                    ) {
                        BadgedBox(
                            badge = { Badge { Text("3") } }
                        ) {
                            Icon(Icons.Default.Notifications, contentDescription = "Announcements")
                        }
                    }

                    IconButton(
                        onClick = {
                            if (user == null || user?.isGuest == true) {
                                viewModel.navigateTo(KhojScreen.KMAIL_AUTH)
                            } else {
                                viewModel.navigateTo(KhojScreen.PROFILE)
                            }
                        },
                        modifier = Modifier.testTag("home_profile_button")
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(KhojPrimaryBlue),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = (user?.fullName?.firstOrNull() ?: 'K').uppercase(),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            KhojBottomNavigationBar(viewModel = viewModel, currentScreen = KhojScreen.HOME, tabCount = tabs.size)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Hero Wallpaper or Nepal Mountain Visual
            if (settings.selectedWallpaper == "Nepal Mountains") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .height(130.dp)
                        .clip(RoundedCornerShape(18.dp))
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_nepal_mountains),
                        contentDescription = "Himalayan Sunrise",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.65f))
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(14.dp)
                    ) {
                        Text(
                            text = "Born in Nepal. Built for the World.",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Your Internet. Your Discovery.",
                            color = KhojAccentOrange,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Main Omnibox (Search & Address Bar)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(26.dp)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "Search",
                        tint = KhojPrimaryBlue,
                        modifier = Modifier.padding(start = 4.dp)
                    )

                    TextField(
                        value = searchInput,
                        onValueChange = { searchInput = it },
                        placeholder = { Text("Search or enter website address...", fontSize = 14.sp) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("home_search_input"),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                focusManager.clearFocus()
                                viewModel.handleSearchOrUrl(searchInput)
                            }
                        )
                    )

                    if (searchInput.isNotEmpty()) {
                        IconButton(onClick = { searchInput = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear", tint = Color.Gray)
                        }
                    }

                    IconButton(
                        onClick = {
                            searchInput = "Kathmandu tourism and Everest exploration"
                            viewModel.handleSearchOrUrl(searchInput)
                        },
                        modifier = Modifier.testTag("home_voice_search_button")
                    ) {
                        Icon(Icons.Default.Mic, contentDescription = "Voice Search", tint = KhojAccentOrange)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Quick Website Shortcuts Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Quick Shortcuts",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                TextButton(
                    onClick = { showAddShortcutDialog = true },
                    modifier = Modifier.testTag("add_shortcut_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add", fontSize = 13.sp)
                }
            }

            // Shortcuts Grid / Row
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(shortcuts) { shortcut ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .width(68.dp)
                            .clickable { viewModel.handleSearchOrUrl(shortcut.url) }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            val icon = when (shortcut.iconCategory) {
                                "video" -> Icons.Default.PlayCircle
                                "code" -> Icons.Default.Code
                                "news" -> Icons.Default.Article
                                "community" -> Icons.Default.Forum
                                "wikipedia" -> Icons.Default.MenuBook
                                else -> Icons.Default.Public
                            }
                            Icon(icon, contentDescription = shortcut.name, tint = KhojPrimaryBlue)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = shortcut.name,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Trending Searches Section
            if (settings.showTrendingSearches) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Icon(Icons.Default.TrendingUp, contentDescription = null, tint = KhojAccentOrange, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Trending in Nepal & Global", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            recentSearches.take(4).forEach { query ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { viewModel.handleSearchOrUrl(query) }
                                        .padding(vertical = 8.dp, horizontal = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.History, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(query, fontSize = 13.sp, modifier = Modifier.weight(1f))
                                    Icon(Icons.Default.NorthWest, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Recently Visited Websites
            if (settings.showRecentVisited && history.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text("Recently Visited", fontSize = 15.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            history.take(3).forEach { item ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { viewModel.handleSearchOrUrl(item.url) }
                                        .padding(vertical = 8.dp, horizontal = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(KhojPrimaryBlue.copy(alpha = 0.1f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.Language, contentDescription = null, tint = KhojPrimaryBlue, modifier = Modifier.size(18.dp))
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(item.title, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
                                        Text(item.url, fontSize = 11.sp, color = Color.Gray, maxLines = 1)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Add Shortcut Dialog
    if (showAddShortcutDialog) {
        AlertDialog(
            onDismissRequest = { showAddShortcutDialog = false },
            title = { Text("Add Quick Shortcut") },
            text = {
                Column {
                    OutlinedTextField(
                        value = newShortcutName,
                        onValueChange = { newShortcutName = it },
                        label = { Text("Shortcut Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newShortcutUrl,
                        onValueChange = { newShortcutUrl = it },
                        label = { Text("URL (e.g. kathmandupost.com)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newShortcutName.isNotBlank() && newShortcutUrl.isNotBlank()) {
                            viewModel.repository.addShortcut(newShortcutName, newShortcutUrl)
                            newShortcutName = ""
                            newShortcutUrl = ""
                            showAddShortcutDialog = false
                        }
                    }
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddShortcutDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun KhojBottomNavigationBar(
    viewModel: BrowserViewModel,
    currentScreen: KhojScreen,
    tabCount: Int = 1
) {
    NavigationBar(
        modifier = Modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
            .testTag("khoj_bottom_navigation"),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp
    ) {
        NavigationBarItem(
            selected = currentScreen == KhojScreen.HOME,
            onClick = { viewModel.navigateTo(KhojScreen.HOME) },
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home", fontSize = 11.sp) }
        )
        NavigationBarItem(
            selected = currentScreen == KhojScreen.SEARCH_RESULTS,
            onClick = { viewModel.navigateTo(KhojScreen.SEARCH_RESULTS) },
            icon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            label = { Text("Search", fontSize = 11.sp) }
        )
        NavigationBarItem(
            selected = currentScreen == KhojScreen.TAB_MANAGER,
            onClick = { viewModel.navigateTo(KhojScreen.TAB_MANAGER) },
            icon = {
                BadgedBox(
                    badge = { Badge { Text(tabCount.toString()) } }
                ) {
                    Icon(Icons.Default.FilterNone, contentDescription = "Tabs")
                }
            },
            label = { Text("Tabs", fontSize = 11.sp) }
        )
        NavigationBarItem(
            selected = currentScreen == KhojScreen.BOOKMARKS,
            onClick = { viewModel.navigateTo(KhojScreen.BOOKMARKS) },
            icon = { Icon(Icons.Default.BookmarkBorder, contentDescription = "Bookmarks") },
            label = { Text("Saved", fontSize = 11.sp) }
        )
        NavigationBarItem(
            selected = currentScreen == KhojScreen.SETTINGS,
            onClick = { viewModel.navigateTo(KhojScreen.SETTINGS) },
            icon = { Icon(Icons.Default.Menu, contentDescription = "Menu") },
            label = { Text("Menu", fontSize = 11.sp) }
        )
    }
}
