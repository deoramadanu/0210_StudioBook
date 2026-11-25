package com.example.studiobook.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.studiobook.data.model.Booking
import com.example.studiobook.data.model.Equipment
import com.example.studiobook.data.model.Studio
import com.example.studiobook.data.repository.StudioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class StudioViewModel(
    private val repository: StudioRepository = StudioRepository.getInstance()
) : ViewModel() {

    val studios: StateFlow<List<Studio>> = repository.studios
    val equipments: StateFlow<List<Equipment>> = repository.equipments
    val bookings: StateFlow<List<Booking>> = repository.bookings
    val currentUser = repository.demoUser

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _hasSeenWelcome = MutableStateFlow(false)
    val hasSeenWelcome: StateFlow<Boolean> = _hasSeenWelcome.asStateFlow()

    private val _isAdmin = MutableStateFlow(false)
    val isAdmin: StateFlow<Boolean> = _isAdmin.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _userEmail = MutableStateFlow(repository.demoUser.email)
    val userEmail: StateFlow<String> = _userEmail.asStateFlow()

    private val adminCredentials = mapOf(
        "admin@studiobook.id" to "admindeo2026",
        "admin.studiobook@gmail.com" to "admindeo2026"
    )

    init {
        refreshAndPurgeExpiredBookings()
    }

    fun continueFromWelcome() {
        _hasSeenWelcome.value = true
    }

    fun backToWelcome() {
        _hasSeenWelcome.value = false
    }

    fun login(email: String, password: String): Result<Unit> {
        val cleanEmail = email.trim().lowercase()
        if (cleanEmail.isBlank() || password.isBlank()) {
            return Result.failure(IllegalArgumentException("Email dan password wajib diisi!"))
        }

        val expectedAdminPassword = adminCredentials[cleanEmail]
        val isAdminLogin = if (expectedAdminPassword != null) {
            if (password != expectedAdminPassword) {
                return Result.failure(IllegalArgumentException("Password akun admin salah!"))
            }
            true
        } else {
            false
        }

        _userEmail.value = email.trim()
        _isAdmin.value = isAdminLogin
        _isLoggedIn.value = true
        refreshAndPurgeExpiredBookings()
        return Result.success(Unit)
    }

    fun logout() {
        _isLoggedIn.value = false
        _isAdmin.value = false
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun refreshAndPurgeExpiredBookings() {
        repository.purgeExpiredBookings()
    }

    fun createBooking(
        studio: Studio,
        equipment: Equipment?,
        bandName: String,
        bookerName: String,
        date: String,
        timeSlot: String,
        durationHours: Int = 2
    ): Result<Booking> {
        return repository.createBooking(
            studio = studio,
            equipment = equipment,
            bandName = bandName,
            bookerName = bookerName,
            date = date,
            timeSlot = timeSlot,
            durationHours = durationHours,
            userEmail = _userEmail.value
        )
    }

    fun confirmBooking(bookingId: String) {
        repository.confirmBooking(bookingId)
    }

    fun deleteBooking(bookingId: String) {
        repository.deleteBooking(bookingId)
    }

    fun addStudio(name: String, capacity: Int, pricePerHour: Int, facilities: String) {
        repository.addStudio(name, capacity, pricePerHour, facilities)
    }

    fun toggleStudioAvailability(studioId: String) {
        repository.toggleStudioAvailability(studioId)
    }

    fun deleteStudio(studioId: String) {
        repository.deleteStudio(studioId)
    }

    fun addEquipment(name: String, pricePerSession: Int, stock: Int) {
        repository.addEquipment(name, pricePerSession, stock)
    }

    fun deleteEquipment(equipmentId: String) {
        repository.deleteEquipment(equipmentId)
    }
}