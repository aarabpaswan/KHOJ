package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.ui.screens.*
import com.example.ui.theme.KhojTheme
import com.example.viewmodel.BrowserViewModel
import com.example.viewmodel.KhojScreen

class MainActivity : ComponentActivity() {
    private val viewModel: BrowserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val settings by viewModel.settings.collectAsState()

            val isDarkTheme = when (settings.themeMode) {
                "Dark" -> true
                "Light" -> false
                else -> isSystemInDarkTheme()
            }

            KhojTheme(
                darkTheme = isDarkTheme,
                themePalette = settings.themePalette
            ) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    KhojAppNavigation(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun KhojAppNavigation(viewModel: BrowserViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()

    when (currentScreen) {
        KhojScreen.SPLASH -> SplashScreen(viewModel)
        KhojScreen.WELCOME -> WelcomeScreen(viewModel)
        KhojScreen.FEATURES -> FeaturesScreen(viewModel)
        KhojScreen.APPEARANCE -> AppearanceScreen(viewModel)
        KhojScreen.NOTIFICATIONS -> NotificationPermissionScreen(viewModel)
        KhojScreen.KMAIL_AUTH -> KMailAuthScreen(viewModel)
        KhojScreen.HOME -> BrowserHomeScreen(viewModel)
        KhojScreen.WEB_BROWSER -> WebBrowserScreen(viewModel)
        KhojScreen.SEARCH_RESULTS -> SearchResultsScreen(viewModel)
        KhojScreen.TAB_MANAGER -> TabManagerScreen(viewModel)
        KhojScreen.BOOKMARKS -> BookmarksScreen(viewModel)
        KhojScreen.HISTORY -> HistoryScreen(viewModel)
        KhojScreen.DOWNLOADS -> DownloadsScreen(viewModel)
        KhojScreen.CUSTOMIZE -> CustomizationScreen(viewModel)
        KhojScreen.PRIVACY -> PrivacyScreen(viewModel)
        KhojScreen.KHOJ_AI -> KhojAiScreen(viewModel)
        KhojScreen.KHOJ_DOMAINS -> KhojDomainsScreen(viewModel)
        KhojScreen.ANNOUNCEMENTS -> AnnouncementsScreen(viewModel)
        KhojScreen.PROFILE -> ProfileScreen(viewModel)
        KhojScreen.SETTINGS -> SettingsScreen(viewModel)
    }
}
