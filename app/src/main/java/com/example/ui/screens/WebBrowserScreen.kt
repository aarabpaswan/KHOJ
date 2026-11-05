package com.example.ui.screens

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ui.theme.KhojAccentOrange
import com.example.ui.theme.KhojPrimaryBlue
import com.example.viewmodel.BrowserViewModel
import com.example.viewmodel.KhojScreen

@SuppressLint("SetJavaScriptEnabled")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebBrowserScreen(viewModel: BrowserViewModel) {
    val activeTab = viewModel.activeTab
    val tabs by viewModel.tabs.collectAsState()
    val isBookmarked = viewModel.isCurrentTabBookmarked()
    val settings by viewModel.settings.collectAsState()

    var webViewRef by remember { mutableStateOf<WebView?>(null) }
    var currentUrlInput by remember(activeTab?.url) { mutableStateOf(activeTab?.url ?: "") }
    var showMenuDropdown by remember { mutableStateOf(false) }
    var isDesktopSite by remember { mutableStateOf(settings.desktopSiteDefault) }
    val focusManager = LocalFocusManager.current

    BackHandler {
        if (webViewRef?.canGoBack() == true) {
            webViewRef?.goBack()
        } else {
            viewModel.navigateTo(KhojScreen.HOME)
        }
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .statusBarsPadding()
            ) {
                // Address Bar & Controls
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { viewModel.navigateTo(KhojScreen.HOME) },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(Icons.Default.Home, contentDescription = "Home", tint = MaterialTheme.colorScheme.onSurface)
                    }

                    // Omnibox URL card
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .padding(horizontal = 4.dp),
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                if (activeTab?.url?.startsWith("https") == true) Icons.Default.Lock else Icons.Default.Language,
                                contentDescription = "Security Status",
                                tint = if (activeTab?.url?.startsWith("https") == true) Color(0xFF10B981) else Color.Gray,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            TextField(
                                value = currentUrlInput,
                                onValueChange = { currentUrlInput = it },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("browser_address_bar"),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent
                                ),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
                                keyboardActions = KeyboardActions(
                                    onGo = {
                                        focusManager.clearFocus()
                                        viewModel.handleSearchOrUrl(currentUrlInput)
                                        webViewRef?.loadUrl(
                                            if (currentUrlInput.startsWith("http://") || currentUrlInput.startsWith("https://"))
                                                currentUrlInput
                                            else "https://$currentUrlInput"
                                        )
                                    }
                                )
                            )
                            if (activeTab?.isLoading == true) {
                                IconButton(
                                    onClick = { webViewRef?.stopLoading() },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Stop", tint = Color.Gray)
                                }
                            } else {
                                IconButton(
                                    onClick = { webViewRef?.reload() },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(Icons.Default.Refresh, contentDescription = "Reload", tint = Color.Gray)
                                }
                            }
                        }
                    }

                    // Tab Count Pill
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(KhojPrimaryBlue.copy(alpha = 0.15f))
                            .clickable { viewModel.navigateTo(KhojScreen.TAB_MANAGER) }
                            .testTag("browser_tab_count_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tabs.size.toString(),
                            fontWeight = FontWeight.Bold,
                            color = KhojPrimaryBlue,
                            fontSize = 13.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    // Overflow Menu
                    Box {
                        IconButton(
                            onClick = { showMenuDropdown = true },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Browser Menu")
                        }

                        DropdownMenu(
                            expanded = showMenuDropdown,
                            onDismissRequest = { showMenuDropdown = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(if (isBookmarked) "Remove Bookmark" else "Bookmark Page") },
                                onClick = {
                                    viewModel.toggleBookmarkActiveTab()
                                    showMenuDropdown = false
                                },
                                leadingIcon = {
                                    Icon(
                                        if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                        contentDescription = null,
                                        tint = if (isBookmarked) KhojAccentOrange else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("New Tab") },
                                onClick = {
                                    viewModel.openNewTab("https://en.wikipedia.org")
                                    showMenuDropdown = false
                                },
                                leadingIcon = { Icon(Icons.Default.Add, contentDescription = null) }
                            )
                            DropdownMenuItem(
                                text = { Text("New Incognito Tab") },
                                onClick = {
                                    viewModel.openNewTab("https://en.wikipedia.org", isIncognito = true)
                                    showMenuDropdown = false
                                },
                                leadingIcon = { Icon(Icons.Default.VisibilityOff, contentDescription = null) }
                            )
                            DropdownMenuItem(
                                text = { Text(if (isDesktopSite) "Mobile Site" else "Desktop Site") },
                                onClick = {
                                    isDesktopSite = !isDesktopSite
                                    webViewRef?.settings?.userAgentString = if (isDesktopSite) {
                                        "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
                                    } else null
                                    webViewRef?.reload()
                                    showMenuDropdown = false
                                },
                                leadingIcon = { Icon(Icons.Default.Laptop, contentDescription = null) }
                            )
                            DropdownMenuItem(
                                text = { Text("Explain with KHOJ AI") },
                                onClick = {
                                    viewModel.sendAiPrompt("Summarize page: ${activeTab?.url}")
                                    viewModel.navigateTo(KhojScreen.KHOJ_AI)
                                    showMenuDropdown = false
                                },
                                leadingIcon = { Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = KhojAccentOrange) }
                            )
                            DropdownMenuItem(
                                text = { Text("Download Webpage Guide") },
                                onClick = {
                                    val title = activeTab?.title ?: "Webpage"
                                    viewModel.repository.addDownload("$title.pdf", activeTab?.url ?: "https://khoj.com")
                                    showMenuDropdown = false
                                },
                                leadingIcon = { Icon(Icons.Default.Download, contentDescription = null) }
                            )
                        }
                    }
                }

                // Loading Progress Bar
                if (activeTab?.isLoading == true) {
                    LinearProgressIndicator(
                        progress = { (activeTab.progress) / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.5.dp),
                        color = KhojAccentOrange,
                        trackColor = Color.Transparent
                    )
                }
            }
        },
        bottomBar = {
            // Bottom Action Navigation Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .height(48.dp)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = { webViewRef?.goBack() },
                    enabled = webViewRef?.canGoBack() == true
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = if (webViewRef?.canGoBack() == true) MaterialTheme.colorScheme.onSurface else Color.LightGray)
                }

                IconButton(
                    onClick = { webViewRef?.goForward() },
                    enabled = webViewRef?.canGoForward() == true
                ) {
                    Icon(Icons.Default.ArrowForward, contentDescription = "Forward", tint = if (webViewRef?.canGoForward() == true) MaterialTheme.colorScheme.onSurface else Color.LightGray)
                }

                IconButton(
                    onClick = { viewModel.toggleBookmarkActiveTab() }
                ) {
                    Icon(
                        if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Bookmark",
                        tint = if (isBookmarked) KhojAccentOrange else MaterialTheme.colorScheme.onSurface
                    )
                }

                IconButton(
                    onClick = { viewModel.navigateTo(KhojScreen.SEARCH_RESULTS) }
                ) {
                    Icon(Icons.Default.Search, contentDescription = "Search", tint = MaterialTheme.colorScheme.onSurface)
                }

                IconButton(
                    onClick = { viewModel.navigateTo(KhojScreen.TAB_MANAGER) }
                ) {
                    Icon(Icons.Default.FilterNone, contentDescription = "Tabs", tint = MaterialTheme.colorScheme.onSurface)
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            AndroidView(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("active_webview"),
                factory = { context ->
                    val webView = WebView(context)
                    val ws: WebSettings = webView.settings
                    ws.javaScriptEnabled = true
                    ws.domStorageEnabled = true
                    ws.loadWithOverviewMode = true
                    ws.useWideViewPort = true
                    ws.mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
                    webView.apply {

                        webViewClient = object : WebViewClient() {
                            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                super.onPageStarted(view, url, favicon)
                                viewModel.updateActiveTab(url = url, isLoading = true, progress = 15)
                                currentUrlInput = url ?: ""
                            }

                            override fun onPageFinished(view: WebView?, url: String?) {
                                super.onPageFinished(view, url)
                                viewModel.updateActiveTab(
                                    url = url,
                                    title = view?.title ?: url,
                                    canGoBack = view?.canGoBack(),
                                    canGoForward = view?.canGoForward(),
                                    isLoading = false,
                                    progress = 100
                                )
                                currentUrlInput = url ?: ""
                            }

                            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                                return false
                            }
                        }

                        webChromeClient = object : WebChromeClient() {
                            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                super.onProgressChanged(view, newProgress)
                                viewModel.updateActiveTab(progress = newProgress, isLoading = newProgress < 100)
                            }

                            override fun onReceivedTitle(view: WebView?, title: String?) {
                                super.onReceivedTitle(view, title)
                                if (!title.isNullOrBlank()) {
                                    viewModel.updateActiveTab(title = title)
                                }
                            }
                        }

                        loadUrl(activeTab?.url ?: "https://en.wikipedia.org")
                        webViewRef = this
                    }
                },
                update = { webView ->
                    webViewRef = webView
                    val targetUrl = activeTab?.url ?: "https://en.wikipedia.org"
                    if (webView.url != targetUrl && !targetUrl.startsWith("about:")) {
                        webView.loadUrl(targetUrl)
                    }
                }
            )
        }
    }
}
