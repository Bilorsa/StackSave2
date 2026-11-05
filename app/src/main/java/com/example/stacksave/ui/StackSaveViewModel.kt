package com.example.stacksave.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.stacksave.model.StoreDeal
import com.example.stacksave.model.UserProfile
import com.example.stacksave.repository.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class StackSaveViewModel(app: Application) : AndroidViewModel(app) {
    private val auth = AuthRepository(app)
    /** Firebase configuration status shown on the login screen. */
    val firebaseStatus: String
        get() = auth.firebaseStatus()
    private val users = UserRepository(app)
    private val api = DealFeedService(app)
    private var searchJob: Job? = null
    private val offline = OfflineRepository(app)

    private val _deals = MutableStateFlow<List<StoreDeal>>(emptyList())
    val deals = _deals.asStateFlow()

    private val _favoriteDeals = MutableStateFlow<List<StoreDeal>>(emptyList())
    val favoriteDeals = _favoriteDeals.asStateFlow()

    private val _liveResultsQuery = MutableStateFlow("")
    val liveResultsQuery = _liveResultsQuery.asStateFlow()

    private val _savedDeals = MutableStateFlow<List<StoreDeal>>(emptyList())
    val savedDeals = _savedDeals.asStateFlow()

    private val _basketQuantities = MutableStateFlow<Map<String, Int>>(emptyMap())
    val basketQuantities = _basketQuantities.asStateFlow()

    private val _selectedDealForDetail = MutableStateFlow<StoreDeal?>(null)
    val selectedDealForDetail = _selectedDealForDetail.asStateFlow()

    private val _selectedLocation = MutableStateFlow("Sandton, Johannesburg")
    val selectedLocation = _selectedLocation.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    private val _lastUpdated = MutableStateFlow(currentTimestamp())
    val lastUpdated = _lastUpdated.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory = _selectedCategory.asStateFlow()

    private val _quickFilter = MutableStateFlow("All") // Home feed quick filter
    val quickFilter = _quickFilter.asStateFlow()

    private val _message = MutableStateFlow("")
    val message = _message.asStateFlow()

    private val _apiStatus = MutableStateFlow("Connecting to live price service…")
    val apiStatus = _apiStatus.asStateFlow()

    private val _loggedIn = MutableStateFlow(auth.currentUid() != null)
    val loggedIn = _loggedIn.asStateFlow()

    private val _profile = MutableStateFlow(UserProfile())
    val profile = _profile.asStateFlow()

    init {
        refreshDeals()
    }

    fun toggleFavoriteDeal(deal: StoreDeal) {
        val current = _favoriteDeals.value.toMutableList()
        if (current.any { it.id == deal.id }) current.removeAll { it.id == deal.id }
        else current.add(deal)
        _favoriteDeals.value = current
    }

    fun searchLivePrices(query: String) {
        _liveResultsQuery.value = query.trim()
        setSearchQuery(query)
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(550)
            _isRefreshing.value = true
            val result = api.getDeals(query)
            val list = result.getOrElse { emptyList() }
            _deals.value = list
            _liveResultsQuery.value = query.trim()
            _apiStatus.value = api.dataSourceMessage
            _lastUpdated.value = currentTimestamp()
            _isRefreshing.value = false
        }
    }

    fun setSelectedCategory(category: String) {
        _selectedCategory.value = category
    }

    fun setQuickFilter(filter: String) {
        _quickFilter.value = filter
    }

    fun setSelectedLocation(location: String) {
        _selectedLocation.value = location
    }

    fun openDealDetail(deal: StoreDeal?) {
        _selectedDealForDetail.value = deal
    }

    fun toggleSavedDeal(deal: StoreDeal) {
        val current = _savedDeals.value.toMutableList()
        if (current.any { it.id == deal.id }) {
            current.removeAll { it.id == deal.id }
            _basketQuantities.value = _basketQuantities.value - deal.id
        } else {
            current.add(deal)
            _basketQuantities.value = _basketQuantities.value + (deal.id to 1)
        }
        _savedDeals.value = current
    }

    fun setBasketQuantity(dealId: String, quantity: Int) {
        if (_savedDeals.value.none { it.id == dealId }) return
        if (quantity <= 0) {
            _savedDeals.value = _savedDeals.value.filterNot { it.id == dealId }
            _basketQuantities.value = _basketQuantities.value - dealId
        } else {
            _basketQuantities.value = _basketQuantities.value + (dealId to quantity.coerceAtMost(99))
        }
    }

    fun basketQuantity(dealId: String): Int = _basketQuantities.value[dealId] ?: 1

    fun isDealSaved(dealId: String): Boolean {
        return _savedDeals.value.any { it.id == dealId }
    }

    fun refreshDeals() {
        viewModelScope.launch {
            _isRefreshing.value = true
            val r = api.getDeals(_searchQuery.value)
            val list = r.getOrElse { emptyList() }
            _deals.value = list
            _liveResultsQuery.value = _searchQuery.value.trim()
            _apiStatus.value = api.dataSourceMessage
            _lastUpdated.value = currentTimestamp()
            _isRefreshing.value = false
        }
    }

    fun login(email: String, password: String, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            auth.login(email, password).onSuccess {
                _loggedIn.value = true
                _profile.value = UserProfile(it, email = email)
                _message.value = if (auth.isFirebaseConfigured()) "Logged in with Firebase" else "Logged in using local demo mode"
                refreshDeals()
                onDone()
            }.onFailure {
                _message.value = it.message ?: "Login failed"
            }
        }
    }

    fun demoLogin() {
        _profile.value = UserProfile(
            uid = "local-demo-user",
            displayName = "StackSave Demo",
            email = "demo@stacksave.local"
        )
        _loggedIn.value = true
        _message.value = "Demo mode: account data is not being saved to Firebase."
    }

    fun register(email: String, password: String, name: String, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            auth.register(email, password, name).onSuccess { uid ->
                _loggedIn.value = true
                _profile.value = UserProfile(uid, name, email)
                users.saveProfile(_profile.value)
                _message.value = if (auth.isFirebaseConfigured()) "Account created with Firebase" else "Account created in local demo mode"
                refreshDeals()
                onDone()
            }.onFailure {
                _message.value = it.message ?: "Registration failed"
            }
        }
    }

    fun updateSettings(language: String, notifications: Boolean, offlineSync: Boolean) {
        _profile.value = _profile.value.copy(
            language = language,
            notificationsEnabled = notifications,
            offlineSyncEnabled = offlineSync
        )
        offline.setOfflineEnabled(offlineSync)
        viewModelScope.launch {
            users.saveProfile(_profile.value).onFailure {
                _message.value = "Settings were changed locally but could not sync: ${it.message ?: "Firebase error"}"
            }
        }
    }

    fun appContext() = getApplication<Application>()

    fun signOut() {
        auth.signOut()
        _loggedIn.value = false
    }

    private fun currentTimestamp(): String {
        return SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
    }
}
