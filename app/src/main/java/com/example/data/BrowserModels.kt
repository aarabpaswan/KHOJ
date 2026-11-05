package com.example.data

data class WebTab(
    val id: String,
    val title: String,
    val url: String,
    val isIncognito: Boolean = false,
    val canGoBack: Boolean = false,
    val canGoForward: Boolean = false,
    val isLoading: Boolean = false,
    val progress: Int = 0,
    val faviconUrl: String? = null
)

data class BookmarkItem(
    val id: String,
    val title: String,
    val url: String,
    val folder: String = "Mobile Bookmarks",
    val timestamp: Long = System.currentTimeMillis()
)

data class HistoryItem(
    val id: String,
    val title: String,
    val url: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class DownloadItem(
    val id: String,
    val fileName: String,
    val url: String,
    val totalBytes: Long,
    val downloadedBytes: Long,
    val status: DownloadStatus,
    val timestamp: Long = System.currentTimeMillis()
)

enum class DownloadStatus {
    DOWNLOADING, COMPLETED, PAUSED, FAILED, CANCELLED
}

data class QuickShortcut(
    val id: String,
    val name: String,
    val url: String,
    val iconCategory: String = "web"
)

data class SearchResult(
    val title: String,
    val url: String,
    val description: String,
    val source: String,
    val category: String = "Web"
)

data class KMailUser(
    val username: String, // e.g. "aarab" -> aarab@khoj.com
    val fullName: String,
    val email: String,
    val dateOfBirth: String = "",
    val joinedDate: String = "Nov 2026",
    val isGuest: Boolean = false
)

data class Announcement(
    val id: String,
    val title: String,
    val description: String,
    val date: String,
    val category: String,
    val readMoreUrl: String
)

data class DomainSearchResult(
    val domain: String,
    val extension: String,
    val isAvailable: Boolean,
    val pricePerYear: String,
    val registrarPartner: String = "KHOJ Domain Registry (Nepal/Global)"
)

data class UserSettings(
    val themeMode: String = "System", // Light, Dark, System
    val themePalette: String = "Ocean Blue",
    val selectedWallpaper: String = "Nepal Mountains",
    val defaultSearchEngine: String = "KHOJ Search",
    val language: String = "English", // English, Nepali, Hindi
    val trackingProtection: Boolean = true,
    val safeSearch: Boolean = true,
    val desktopSiteDefault: Boolean = false,
    val showRecentVisited: Boolean = true,
    val showTrendingSearches: Boolean = true,
    val showAnnouncements: Boolean = true,
    val notificationsEnabled: Boolean = false,
    val hasCompletedOnboarding: Boolean = false
)
