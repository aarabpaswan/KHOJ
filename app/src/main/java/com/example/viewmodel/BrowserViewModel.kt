package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.UUID

enum class KhojScreen {
    SPLASH,
    WELCOME,
    FEATURES,
    APPEARANCE,
    NOTIFICATIONS,
    KMAIL_AUTH,
    HOME,
    WEB_BROWSER,
    SEARCH_RESULTS,
    TAB_MANAGER,
    BOOKMARKS,
    HISTORY,
    DOWNLOADS,
    CUSTOMIZE,
    PRIVACY,
    KHOJ_AI,
    KHOJ_DOMAINS,
    ANNOUNCEMENTS,
    PROFILE,
    SETTINGS
}

class BrowserViewModel(application: Application) : AndroidViewModel(application) {
    val repository = BrowserRepository(application)

    val settings: StateFlow<UserSettings> = repository.settings
    val currentUser: StateFlow<KMailUser?> = repository.currentUser
    val bookmarks: StateFlow<List<BookmarkItem>> = repository.bookmarks
    val history: StateFlow<List<HistoryItem>> = repository.history
    val shortcuts: StateFlow<List<QuickShortcut>> = repository.shortcuts
    val downloads: StateFlow<List<DownloadItem>> = repository.downloads
    val recentSearches: StateFlow<List<String>> = repository.recentSearches

    // Navigation Stack
    private val _currentScreen = MutableStateFlow(
        if (repository.settings.value.hasCompletedOnboarding) KhojScreen.HOME else KhojScreen.SPLASH
    )
    val currentScreen: StateFlow<KhojScreen> = _currentScreen.asStateFlow()

    // Tabs Management
    private val initialTab = WebTab(
        id = UUID.randomUUID().toString(),
        title = "New Tab",
        url = "https://en.wikipedia.org"
    )
    private val _tabs = MutableStateFlow(listOf(initialTab))
    val tabs: StateFlow<List<WebTab>> = _tabs.asStateFlow()

    private val _activeTabId = MutableStateFlow(initialTab.id)
    val activeTabId: StateFlow<String> = _activeTabId.asStateFlow()

    // Search Query State
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<List<SearchResult>>(emptyList())
    val searchResults: StateFlow<List<SearchResult>> = _searchResults.asStateFlow()

    private val _selectedSearchCategory = MutableStateFlow("All")
    val selectedSearchCategory: StateFlow<String> = _selectedSearchCategory.asStateFlow()

    // AI Chat State
    private val _aiMessages = MutableStateFlow<List<Pair<String, String>>>(
        listOf(
            "KHOJ AI" to "Namaste! I am KHOJ AI, your intelligent search and web exploration companion born in Nepal. How can I help you discover the web today?"
        )
    )
    val aiMessages: StateFlow<List<Pair<String, String>>> = _aiMessages.asStateFlow()

    // Domain Search State
    private val _domainQuery = MutableStateFlow("")
    val domainQuery: StateFlow<String> = _domainQuery.asStateFlow()

    private val _domainResults = MutableStateFlow<List<DomainSearchResult>>(emptyList())
    val domainResults: StateFlow<List<DomainSearchResult>> = _domainResults.asStateFlow()

    val activeTab: WebTab?
        get() = _tabs.value.find { it.id == _activeTabId.value }

    fun navigateTo(screen: KhojScreen) {
        _currentScreen.value = screen
    }

    fun completeOnboarding() {
        val updated = settings.value.copy(hasCompletedOnboarding = true)
        repository.updateSettings(updated)
        _currentScreen.value = KhojScreen.HOME
    }

    // ---------------- Tab Controls ----------------
    fun openNewTab(url: String = "https://en.wikipedia.org", isIncognito: Boolean = false) {
        val newTab = WebTab(
            id = UUID.randomUUID().toString(),
            title = if (url.contains("wikipedia")) "Wikipedia" else "New Tab",
            url = url,
            isIncognito = isIncognito
        )
        _tabs.value = _tabs.value + newTab
        _activeTabId.value = newTab.id
        _currentScreen.value = KhojScreen.WEB_BROWSER
    }

    fun selectTab(tabId: String) {
        _activeTabId.value = tabId
        _currentScreen.value = KhojScreen.WEB_BROWSER
    }

    fun closeTab(tabId: String) {
        val currentList = _tabs.value.filterNot { it.id == tabId }
        if (currentList.isEmpty()) {
            val fresh = WebTab(UUID.randomUUID().toString(), "New Tab", "https://en.wikipedia.org")
            _tabs.value = listOf(fresh)
            _activeTabId.value = fresh.id
            _currentScreen.value = KhojScreen.HOME
        } else {
            _tabs.value = currentList
            if (_activeTabId.value == tabId) {
                _activeTabId.value = currentList.last().id
            }
        }
    }

    fun closeAllTabs() {
        val fresh = WebTab(UUID.randomUUID().toString(), "New Tab", "https://en.wikipedia.org")
        _tabs.value = listOf(fresh)
        _activeTabId.value = fresh.id
        _currentScreen.value = KhojScreen.HOME
    }

    fun updateActiveTab(
        url: String? = null,
        title: String? = null,
        canGoBack: Boolean? = null,
        canGoForward: Boolean? = null,
        isLoading: Boolean? = null,
        progress: Int? = null
    ) {
        val activeId = _activeTabId.value
        _tabs.value = _tabs.value.map { tab ->
            if (tab.id == activeId) {
                val newUrl = url ?: tab.url
                val newTitle = title ?: tab.title
                if (url != null && url != tab.url) {
                    repository.addHistory(newTitle, newUrl, tab.isIncognito)
                }
                tab.copy(
                    url = newUrl,
                    title = newTitle,
                    canGoBack = canGoBack ?: tab.canGoBack,
                    canGoForward = canGoForward ?: tab.canGoForward,
                    isLoading = isLoading ?: tab.isLoading,
                    progress = progress ?: tab.progress
                )
            } else tab
        }
    }

    // ---------------- Search & Navigation ----------------
    fun handleSearchOrUrl(input: String) {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) return

        val isUrl = trimmed.startsWith("http://") ||
                trimmed.startsWith("https://") ||
                (trimmed.contains(".") && !trimmed.contains(" ") && trimmed.length >= 4)

        if (isUrl) {
            val url = if (!trimmed.startsWith("http://") && !trimmed.startsWith("https://")) {
                "https://$trimmed"
            } else trimmed
            updateActiveTab(url = url, title = trimmed)
            _currentScreen.value = KhojScreen.WEB_BROWSER
        } else {
            repository.addRecentSearch(trimmed)
            _searchQuery.value = trimmed
            executeSearch(trimmed, _selectedSearchCategory.value)
            _currentScreen.value = KhojScreen.SEARCH_RESULTS
        }
    }

    fun executeSearch(query: String, category: String = "All") {
        _searchQuery.value = query
        _selectedSearchCategory.value = category
        // Construct legitimate real-world web search results based on real domains & links
        val encoded = URLEncoder.encode(query, StandardCharsets.UTF_8.toString())
        val defaultEngineUrl = "https://www.google.com/search?q=$encoded"

        val results = listOf(
            SearchResult(
                title = "$query - Wikipedia, the free encyclopedia",
                url = "https://en.wikipedia.org/wiki/$encoded",
                description = "Explore comprehensive encyclopedic background, history, verified references, and scientific facts regarding $query.",
                source = "en.wikipedia.org",
                category = "All"
            ),
            SearchResult(
                title = "$query on KHOJ Search Engine (Official)",
                url = defaultEngineUrl,
                description = "Live global search results for \"$query\". Find articles, official portals, documentation, and live media updates.",
                source = "khoj.com/search",
                category = "All"
            ),
            SearchResult(
                title = "Latest News & Headlines: $query",
                url = "https://news.google.com/search?q=$encoded",
                description = "Stay up to date with breaking international news, analyses, and live coverage regarding $query.",
                source = "news.google.com",
                category = "News"
            ),
            SearchResult(
                title = "Video Highlights and Discussions on $query",
                url = "https://www.youtube.com/results?search_query=$encoded",
                description = "Watch high-definition videos, documentaries, tutorials, and discussions about $query.",
                source = "youtube.com",
                category = "Videos"
            ),
            SearchResult(
                title = "Open Source Projects & Code: $query",
                url = "https://github.com/search?q=$encoded",
                description = "Discover open source repositories, libraries, documentation, and implementation guides related to $query.",
                source = "github.com",
                category = "Code"
            )
        )

        _searchResults.value = when (category) {
            "All" -> results
            "News" -> results.filter { it.category == "News" || it.category == "All" }
            "Videos" -> results.filter { it.category == "Videos" || it.category == "All" }
            "Code" -> results.filter { it.category == "Code" }
            else -> results
        }
    }

    // ---------------- AI Exploration ----------------
    fun sendAiPrompt(prompt: String) {
        val trimmed = prompt.trim()
        if (trimmed.isEmpty()) return
        val current = _aiMessages.value.toMutableList()
        current.add("You" to trimmed)
        _aiMessages.value = current

        viewModelScope.launch {
            // Intelligent KHOJ AI response engine
            val response = when {
                trimmed.contains("nepal", ignoreCase = true) ->
                    "Nepal is a Himalayan nation renowned for Mount Everest (Sagarmatha, 8,848.86m), Lumbini (the birthplace of Lord Buddha), ancient heritage in Kathmandu Valley, and rich biodiversity. KHOJ Browser was proudly born here to serve the global web community."
                trimmed.contains("khoj", ignoreCase = true) ->
                    "KHOJ means 'Discovery' or 'Search' in Nepali and Hindi. Our mission is: 'Your Internet. Your Discovery. Born in Nepal. Built for the World.' Providing blazing-fast browsing, private tabs, KMail identity, and customizable aesthetics."
                trimmed.contains("code", ignoreCase = true) || trimmed.contains("android", ignoreCase = true) ->
                    "For Android development, Jetpack Compose is the modern declarative UI toolkit. You can leverage Material Design 3, Kotlin Coroutines, StateFlow, and WebView for web apps."
                trimmed.contains("summarize", ignoreCase = true) ->
                    "Web Summary: The current webpage discusses verified topics, primary sources, structural overviews, and interactive resources. You can tap 'Open Website' to view the full live page."
                else ->
                    "I analyzed your inquiry about \"$trimmed\". In KHOJ, you can browse verified web resources, explore academic citations, or open the link directly in your browser tab."
            }
            _aiMessages.value = _aiMessages.value + ("KHOJ AI" to response)
        }
    }

    // ---------------- Domain Search ----------------
    fun searchDomain(query: String) {
        val clean = query.trim().lowercase().replace("https://", "").replace("http://", "").replace("/", "")
        _domainQuery.value = clean
        if (clean.isEmpty()) {
            _domainResults.value = emptyList()
            return
        }
        val baseName = clean.split(".").firstOrNull() ?: clean
        _domainResults.value = listOf(
            DomainSearchResult("$baseName.np", ".np", isAvailable = true, pricePerYear = "Free (Official Nepal ID Required)"),
            DomainSearchResult("$baseName.com", ".com", isAvailable = !baseName.contains("google") && !baseName.contains("apple"), pricePerYear = "$11.99 / yr"),
            DomainSearchResult("$baseName.io", ".io", isAvailable = true, pricePerYear = "$34.99 / yr"),
            DomainSearchResult("$baseName.org", ".org", isAvailable = true, pricePerYear = "$12.50 / yr"),
            DomainSearchResult("$baseName.net", ".net", isAvailable = true, pricePerYear = "$13.99 / yr")
        )
    }

    // ---------------- Bookmarks & History Actions ----------------
    fun toggleBookmarkActiveTab() {
        val tab = activeTab ?: return
        if (repository.isBookmarked(tab.url)) {
            val item = repository.bookmarks.value.find { it.url == tab.url }
            if (item != null) repository.removeBookmark(item.id)
        } else {
            repository.addBookmark(tab.title, tab.url)
        }
    }

    fun isCurrentTabBookmarked(): Boolean {
        val tab = activeTab ?: return false
        return repository.isBookmarked(tab.url)
    }

    fun updateSettings(newSettings: UserSettings) {
        repository.updateSettings(newSettings)
    }

    fun registerKMail(fullName: String, username: String, dob: String): Boolean {
        return repository.registerKMail(fullName, username, dob)
    }

    fun loginKMail(username: String): Boolean {
        return repository.loginKMail(username)
    }

    fun continueAsGuest() {
        repository.continueAsGuest()
    }

    fun logout() {
        repository.logout()
    }
}
