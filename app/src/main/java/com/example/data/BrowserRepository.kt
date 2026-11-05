package com.example.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

class BrowserRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("khoj_browser_prefs", Context.MODE_PRIVATE)

    private val _settings = MutableStateFlow(loadSettings())
    val settings: StateFlow<UserSettings> = _settings.asStateFlow()

    private val _currentUser = MutableStateFlow(loadUser())
    val currentUser: StateFlow<KMailUser?> = _currentUser.asStateFlow()

    private val _bookmarks = MutableStateFlow(loadBookmarks())
    val bookmarks: StateFlow<List<BookmarkItem>> = _bookmarks.asStateFlow()

    private val _history = MutableStateFlow(loadHistory())
    val history: StateFlow<List<HistoryItem>> = _history.asStateFlow()

    private val _shortcuts = MutableStateFlow(loadShortcuts())
    val shortcuts: StateFlow<List<QuickShortcut>> = _shortcuts.asStateFlow()

    private val _downloads = MutableStateFlow(loadDownloads())
    val downloads: StateFlow<List<DownloadItem>> = _downloads.asStateFlow()

    private val _recentSearches = MutableStateFlow(loadRecentSearches())
    val recentSearches: StateFlow<List<String>> = _recentSearches.asStateFlow()

    // ---------------- Settings ----------------
    private fun loadSettings(): UserSettings {
        return UserSettings(
            themeMode = prefs.getString("themeMode", "System") ?: "System",
            themePalette = prefs.getString("themePalette", "Ocean Blue") ?: "Ocean Blue",
            selectedWallpaper = prefs.getString("selectedWallpaper", "Nepal Mountains") ?: "Nepal Mountains",
            defaultSearchEngine = prefs.getString("defaultSearchEngine", "KHOJ Search") ?: "KHOJ Search",
            language = prefs.getString("language", "English") ?: "English",
            trackingProtection = prefs.getBoolean("trackingProtection", true),
            safeSearch = prefs.getBoolean("safeSearch", true),
            desktopSiteDefault = prefs.getBoolean("desktopSiteDefault", false),
            showRecentVisited = prefs.getBoolean("showRecentVisited", true),
            showTrendingSearches = prefs.getBoolean("showTrendingSearches", true),
            showAnnouncements = prefs.getBoolean("showAnnouncements", true),
            notificationsEnabled = prefs.getBoolean("notificationsEnabled", false),
            hasCompletedOnboarding = prefs.getBoolean("hasCompletedOnboarding", false)
        )
    }

    fun updateSettings(newSettings: UserSettings) {
        _settings.value = newSettings
        prefs.edit().apply {
            putString("themeMode", newSettings.themeMode)
            putString("themePalette", newSettings.themePalette)
            putString("selectedWallpaper", newSettings.selectedWallpaper)
            putString("defaultSearchEngine", newSettings.defaultSearchEngine)
            putString("language", newSettings.language)
            putBoolean("trackingProtection", newSettings.trackingProtection)
            putBoolean("safeSearch", newSettings.safeSearch)
            putBoolean("desktopSiteDefault", newSettings.desktopSiteDefault)
            putBoolean("showRecentVisited", newSettings.showRecentVisited)
            putBoolean("showTrendingSearches", newSettings.showTrendingSearches)
            putBoolean("showAnnouncements", newSettings.showAnnouncements)
            putBoolean("notificationsEnabled", newSettings.notificationsEnabled)
            putBoolean("hasCompletedOnboarding", newSettings.hasCompletedOnboarding)
            apply()
        }
    }

    // ---------------- User & KMail Authentication ----------------
    private fun loadUser(): KMailUser? {
        val username = prefs.getString("kmail_username", null) ?: return null
        val fullName = prefs.getString("kmail_fullname", "") ?: ""
        val email = prefs.getString("kmail_email", "$username@khoj.com") ?: "$username@khoj.com"
        val dob = prefs.getString("kmail_dob", "") ?: ""
        val isGuest = prefs.getBoolean("kmail_is_guest", false)
        return KMailUser(username = username, fullName = fullName, email = email, dateOfBirth = dob, isGuest = isGuest)
    }

    fun registerKMail(fullName: String, username: String, dob: String): Boolean {
        val cleanUser = username.trim().lowercase().removeSuffix("@khoj.com")
        if (cleanUser.length < 3) return false
        val email = "$cleanUser@khoj.com"
        val user = KMailUser(username = cleanUser, fullName = fullName, email = email, dateOfBirth = dob, isGuest = false)
        _currentUser.value = user
        prefs.edit().apply {
            putString("kmail_username", cleanUser)
            putString("kmail_fullname", fullName)
            putString("kmail_email", email)
            putString("kmail_dob", dob)
            putBoolean("kmail_is_guest", false)
            apply()
        }
        return true
    }

    fun loginKMail(username: String): Boolean {
        val cleanUser = username.trim().lowercase().removeSuffix("@khoj.com")
        if (cleanUser.isEmpty()) return false
        val email = "$cleanUser@khoj.com"
        val fullName = cleanUser.replaceFirstChar { it.uppercase() }
        val user = KMailUser(username = cleanUser, fullName = fullName, email = email, isGuest = false)
        _currentUser.value = user
        prefs.edit().apply {
            putString("kmail_username", cleanUser)
            putString("kmail_fullname", fullName)
            putString("kmail_email", email)
            putBoolean("kmail_is_guest", false)
            apply()
        }
        return true
    }

    fun continueAsGuest() {
        val user = KMailUser(username = "guest", fullName = "Guest Explorer", email = "guest@khoj.com", isGuest = true)
        _currentUser.value = user
        prefs.edit().apply {
            putString("kmail_username", "guest")
            putString("kmail_fullname", "Guest Explorer")
            putString("kmail_email", "guest@khoj.com")
            putBoolean("kmail_is_guest", true)
            apply()
        }
    }

    fun logout() {
        _currentUser.value = null
        prefs.edit().apply {
            remove("kmail_username")
            remove("kmail_fullname")
            remove("kmail_email")
            remove("kmail_dob")
            remove("kmail_is_guest")
            apply()
        }
    }

    // ---------------- Bookmarks ----------------
    private fun loadBookmarks(): List<BookmarkItem> {
        val json = prefs.getString("bookmarks_json", null) ?: return listOf(
            BookmarkItem(UUID.randomUUID().toString(), "Wikipedia", "https://en.wikipedia.org"),
            BookmarkItem(UUID.randomUUID().toString(), "Nepal Tourism Board", "https://ntb.gov.np"),
            BookmarkItem(UUID.randomUUID().toString(), "GitHub", "https://github.com"),
            BookmarkItem(UUID.randomUUID().toString(), "Kathmandu Post", "https://kathmandupost.com")
        )
        val list = mutableListOf<BookmarkItem>()
        try {
            val array = JSONArray(json)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    BookmarkItem(
                        id = obj.getString("id"),
                        title = obj.getString("title"),
                        url = obj.getString("url"),
                        folder = obj.optString("folder", "Mobile Bookmarks"),
                        timestamp = obj.optLong("timestamp", System.currentTimeMillis())
                    )
                )
            }
        } catch (_: Exception) {}
        return list
    }

    fun addBookmark(title: String, url: String, folder: String = "Mobile Bookmarks") {
        val current = _bookmarks.value.toMutableList()
        // Prevent duplicate URLs
        current.removeAll { it.url == url }
        current.add(0, BookmarkItem(UUID.randomUUID().toString(), title.ifEmpty { url }, url, folder))
        saveBookmarks(current)
    }

    fun removeBookmark(id: String) {
        val current = _bookmarks.value.filterNot { it.id == id }
        saveBookmarks(current)
    }

    fun isBookmarked(url: String): Boolean {
        return _bookmarks.value.any { it.url == url }
    }

    private fun saveBookmarks(list: List<BookmarkItem>) {
        _bookmarks.value = list
        val array = JSONArray()
        list.forEach {
            val obj = JSONObject().apply {
                put("id", it.id)
                put("title", it.title)
                put("url", it.url)
                put("folder", it.folder)
                put("timestamp", it.timestamp)
            }
            array.put(obj)
        }
        prefs.edit().putString("bookmarks_json", array.toString()).apply()
    }

    // ---------------- History ----------------
    private fun loadHistory(): List<HistoryItem> {
        val json = prefs.getString("history_json", null) ?: return listOf(
            HistoryItem(UUID.randomUUID().toString(), "Nepal Gov Portal", "https://nepal.gov.np", System.currentTimeMillis() - 3600000),
            HistoryItem(UUID.randomUUID().toString(), "Wikipedia - Mount Everest", "https://en.wikipedia.org/wiki/Mount_Everest", System.currentTimeMillis() - 7200000)
        )
        val list = mutableListOf<HistoryItem>()
        try {
            val array = JSONArray(json)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    HistoryItem(
                        id = obj.getString("id"),
                        title = obj.getString("title"),
                        url = obj.getString("url"),
                        timestamp = obj.optLong("timestamp", System.currentTimeMillis())
                    )
                )
            }
        } catch (_: Exception) {}
        return list
    }

    fun addHistory(title: String, url: String, isIncognito: Boolean) {
        if (isIncognito || url.startsWith("about:") || url.isEmpty()) return
        val current = _history.value.toMutableList()
        current.removeAll { it.url == url }
        current.add(0, HistoryItem(UUID.randomUUID().toString(), title.ifEmpty { url }, url, System.currentTimeMillis()))
        if (current.size > 200) current.removeAt(current.size - 1)
        saveHistory(current)
    }

    fun deleteHistoryItem(id: String) {
        val current = _history.value.filterNot { it.id == id }
        saveHistory(current)
    }

    fun clearAllHistory() {
        saveHistory(emptyList())
    }

    private fun saveHistory(list: List<HistoryItem>) {
        _history.value = list
        val array = JSONArray()
        list.forEach {
            val obj = JSONObject().apply {
                put("id", it.id)
                put("title", it.title)
                put("url", it.url)
                put("timestamp", it.timestamp)
            }
            array.put(obj)
        }
        prefs.edit().putString("history_json", array.toString()).apply()
    }

    // ---------------- Quick Shortcuts ----------------
    private fun loadShortcuts(): List<QuickShortcut> {
        val json = prefs.getString("shortcuts_json", null) ?: return listOf(
            QuickShortcut("1", "Wikipedia", "https://en.wikipedia.org", "wikipedia"),
            QuickShortcut("2", "YouTube", "https://youtube.com", "video"),
            QuickShortcut("3", "GitHub", "https://github.com", "code"),
            QuickShortcut("4", "Google", "https://google.com", "search"),
            QuickShortcut("5", "Kathmandu Post", "https://kathmandupost.com", "news"),
            QuickShortcut("6", "Reddit", "https://reddit.com", "community")
        )
        val list = mutableListOf<QuickShortcut>()
        try {
            val array = JSONArray(json)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(QuickShortcut(obj.getString("id"), obj.getString("name"), obj.getString("url"), obj.optString("category", "web")))
            }
        } catch (_: Exception) {}
        return list
    }

    fun addShortcut(name: String, url: String) {
        val current = _shortcuts.value.toMutableList()
        current.add(QuickShortcut(UUID.randomUUID().toString(), name, url, "web"))
        saveShortcuts(current)
    }

    fun removeShortcut(id: String) {
        val current = _shortcuts.value.filterNot { it.id == id }
        saveShortcuts(current)
    }

    private fun saveShortcuts(list: List<QuickShortcut>) {
        _shortcuts.value = list
        val array = JSONArray()
        list.forEach {
            val obj = JSONObject().apply {
                put("id", it.id)
                put("name", it.name)
                put("url", it.url)
                put("category", it.iconCategory)
            }
            array.put(obj)
        }
        prefs.edit().putString("shortcuts_json", array.toString()).apply()
    }

    // ---------------- Downloads ----------------
    private fun loadDownloads(): List<DownloadItem> {
        val json = prefs.getString("downloads_json", null) ?: return listOf(
            DownloadItem("d1", "Khoj_Browser_Setup_Guide.pdf", "https://khoj.com/guide.pdf", 2400000, 2400000, DownloadStatus.COMPLETED, System.currentTimeMillis() - 86400000),
            DownloadItem("d2", "Everest_HighRes_Wallpaper.png", "https://khoj.com/wallpaper.png", 5200000, 5200000, DownloadStatus.COMPLETED, System.currentTimeMillis() - 43200000)
        )
        val list = mutableListOf<DownloadItem>()
        try {
            val array = JSONArray(json)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    DownloadItem(
                        id = obj.getString("id"),
                        fileName = obj.getString("fileName"),
                        url = obj.getString("url"),
                        totalBytes = obj.getLong("totalBytes"),
                        downloadedBytes = obj.getLong("downloadedBytes"),
                        status = DownloadStatus.valueOf(obj.getString("status")),
                        timestamp = obj.optLong("timestamp", System.currentTimeMillis())
                    )
                )
            }
        } catch (_: Exception) {}
        return list
    }

    fun addDownload(fileName: String, url: String, totalBytes: Long = 1048576L) {
        val current = _downloads.value.toMutableList()
        val item = DownloadItem(UUID.randomUUID().toString(), fileName, url, totalBytes, totalBytes, DownloadStatus.COMPLETED)
        current.add(0, item)
        saveDownloads(current)
    }

    fun removeDownload(id: String) {
        val current = _downloads.value.filterNot { it.id == id }
        saveDownloads(current)
    }

    private fun saveDownloads(list: List<DownloadItem>) {
        _downloads.value = list
        val array = JSONArray()
        list.forEach {
            val obj = JSONObject().apply {
                put("id", it.id)
                put("fileName", it.fileName)
                put("url", it.url)
                put("totalBytes", it.totalBytes)
                put("downloadedBytes", it.downloadedBytes)
                put("status", it.status.name)
                put("timestamp", it.timestamp)
            }
            array.put(obj)
        }
        prefs.edit().putString("downloads_json", array.toString()).apply()
    }

    // ---------------- Searches ----------------
    private fun loadRecentSearches(): List<String> {
        val json = prefs.getString("recent_searches_json", null) ?: return listOf(
            "Mount Everest expedition history",
            "Nepal tech startups 2026",
            "Kotlin Jetpack Compose Android 15",
            "Kathmandu weather forecast"
        )
        val list = mutableListOf<String>()
        try {
            val array = JSONArray(json)
            for (i in 0 until array.length()) {
                list.add(array.getString(i))
            }
        } catch (_: Exception) {}
        return list
    }

    fun addRecentSearch(query: String) {
        val clean = query.trim()
        if (clean.isEmpty()) return
        val current = _recentSearches.value.toMutableList()
        current.remove(clean)
        current.add(0, clean)
        if (current.size > 15) current.removeAt(current.size - 1)
        _recentSearches.value = current
        val array = JSONArray(current)
        prefs.edit().putString("recent_searches_json", array.toString()).apply()
    }

    fun clearRecentSearches() {
        _recentSearches.value = emptyList()
        prefs.edit().remove("recent_searches_json").apply()
    }

    // ---------------- Announcements ----------------
    fun getAnnouncements(): List<Announcement> {
        return listOf(
            Announcement(
                id = "a1",
                title = "KHOJ Browser 1.0 Global Launch",
                description = "Born in Nepal, built for the world! Today we officially unveil KHOJ: ultra-fast browsing, private tabs, customizable themes, and integrated KMail identity.",
                date = "Nov 05, 2026",
                category = "Release",
                readMoreUrl = "https://khoj.com/releases/v1"
            ),
            Announcement(
                id = "a2",
                title = "Next-Gen Tracking Protection Enabled",
                description = "Your privacy is our priority. KHOJ now blocks third-party cross-site trackers by default without degrading web compatibility.",
                date = "Nov 02, 2026",
                category = "Security",
                readMoreUrl = "https://khoj.com/privacy-update"
            ),
            Announcement(
                id = "a3",
                title = "KHOJ Himalayan Cloud Data Centers",
                description = "Ensuring lightning fast edge search latency across South Asia and worldwide CDN nodes powered by renewable Himalayan hydro-energy.",
                date = "Oct 28, 2026",
                category = "Infrastructure",
                readMoreUrl = "https://khoj.com/green-tech"
            )
        )
    }
}
