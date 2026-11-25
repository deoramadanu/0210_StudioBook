package com.example.studiobook.data.repository

import com.example.studiobook.data.model.Booking
import com.example.studiobook.data.model.Equipment
import com.example.studiobook.data.model.Studio
import com.example.studiobook.data.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class StudioRepository private constructor() {

    companion object {
        @Volatile
        private var instance: StudioRepository? = null

        fun getInstance(): StudioRepository {
            return instance ?: synchronized(this) {
                instance ?: StudioRepository().also { instance = it }
            }
        }
    }

    private val _studios = MutableStateFlow(
        listOf(
            Studio(
                id = "st-1",
                name = "Studio A - Live Band",
                capacity = 6,
                pricePerHour = 85000,
                facilities = "Drum Akustik, Amp Gitar & Bass, Mixer, 3 Mic",
                isAvailable = true
            ),
            Studio(
                id = "st-2",
                name = "Studio B - Acoustic & Recording",
                capacity = 4,
                pricePerHour = 65000,
                facilities = "Keyboard, Condenser Mic, Audio Interface, Monitor",
                isAvailable = true
            )
        )
    )
    val studios: StateFlow<List<Studio>> = _studios.asStateFlow()

    private val _equipments = MutableStateFlow(
        listOf(
            Equipment(
                id = "eq-1",
                name = "Electric Guitar + Cable",
                pricePerSession = 25000,
                stock = 3,
                isAvailable = true
            ),
            Equipment(
                id = "eq-2",
                name = "Double Pedal Drum",
                pricePerSession = 20000,
                stock = 2,
                isAvailable = true
            ),
            Equipment(
                id = "eq-3",
                name = "Wireless Vocal Mic",
                pricePerSession = 15000,
                stock = 4,
                isAvailable = true
            )
        )
    )
    val equipments: StateFlow<List<Equipment>> = _equipments.asStateFlow()

    private val _bookings = MutableStateFlow<List<Booking>>(emptyList())
    val bookings: StateFlow<List<Booking>> = _bookings.asStateFlow()

    val demoUser = User(
        id = "usr-1",
        name = "Alfilla Deo",
        email = "alfilla@studiobook.id",
        role = "USER"
    )

    private fun normalizeValidDate(rawDate: String): String? {
        val trimmed = rawDate.trim()
        if (!trimmed.matches(Regex("""\d{4}-\d{2}-\d{2}"""))) return null
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
                isLenient = false
            }
            val parsed = sdf.parse(trimmed) ?: return null
            sdf.format(parsed)
        } catch (_: Exception) {
            null
        }
    }

    // Slot bentrok hanya jika studioId, tanggal, dan timeSlot SAMA PERSIS
    fun isSlotBooked(studioId: String, date: String, timeSlot: String): Boolean {
        val cleanDate = date.trim()
        val cleanSlot = timeSlot.trim()
        return _bookings.value.any { booking ->
            booking.studioId == studioId &&
                    booking.date.trim() == cleanDate &&
                    booking.timeSlot.trim() == cleanSlot
        }
    }

    fun createBooking(
        studio: Studio,
        equipment: Equipment?,
        bandName: String,
        bookerName: String,
        date: String,
        timeSlot: String,
        durationHours: Int = 2,
        userEmail: String = demoUser.email
    ): Result<Booking> {
        val cleanBand = bandName.trim()
        val cleanBooker = bookerName.trim()
        val cleanSlot = timeSlot.trim()

        if (cleanBand.isEmpty()) {
            return Result.failure(IllegalArgumentException("Nama band wajib diisi!"))
        }
        if (cleanBooker.isEmpty()) {
            return Result.failure(IllegalArgumentException("Nama pemesan wajib diisi!"))
        }
        if (cleanSlot.isEmpty()) {
            return Result.failure(IllegalArgumentException("Jam / slot waktu wajib dipilih!"))
        }
        if (date.isBlank()) {
            return Result.failure(IllegalArgumentException("Tanggal main wajib diisi!"))
        }

        val normalizedDate = normalizeValidDate(date)
            ?: return Result.failure(
                IllegalArgumentException("Format tanggal harus YYYY-MM-DD (contoh: 2026-11-25)!")
            )

        purgeExpiredBookings()

        if (isSlotBooked(studio.id, normalizedDate, cleanSlot)) {
            return Result.failure(
                IllegalStateException("Slot $cleanSlot pada $normalizedDate untuk ${studio.name} sudah terbooking!")
            )
        }

        val studioCost = studio.pricePerHour * durationHours
        val equipmentCost = equipment?.pricePerSession ?: 0
        val booking = Booking(
            id = "bk-${System.currentTimeMillis()}-${(_bookings.value.size + 1)}",
            userId = userEmail.trim().ifBlank { demoUser.id },
            bandName = cleanBand,
            bookerName = cleanBooker,
            studioId = studio.id,
            studioName = studio.name,
            equipmentId = equipment?.id,
            equipmentName = equipment?.name,
            date = normalizedDate,
            timeSlot = cleanSlot,
            totalPrice = studioCost + equipmentCost,
            status = "Pending"
        )
        _bookings.value = listOf(booking) + _bookings.value
        return Result.success(booking)
    }

    fun confirmBooking(bookingId: String) {
        _bookings.value = _bookings.value.map { item ->
            if (item.id == bookingId) item.copy(status = "Confirmed") else item
        }
    }

    fun deleteBooking(bookingId: String) {
        _bookings.value = _bookings.value.filterNot { it.id == bookingId }
    }

    fun purgeExpiredBookings() {
        val currentList = _bookings.value
        if (currentList.isEmpty()) return

        val nowMillis = System.currentTimeMillis()
        val updated = currentList.filterNot { booking ->
            isBookingExpired(booking, nowMillis)
        }
        if (updated.size != currentList.size) {
            _bookings.value = updated
        }
    }

    private fun isBookingExpired(booking: Booking, nowMillis: Long): Boolean {
        val validDate = normalizeValidDate(booking.date) ?: return false
        val endTimeRaw = booking.timeSlot.substringAfter("-").trim()
        val parts = endTimeRaw.split(":")
        if (parts.size != 2) return false

        val hour = parts[0].toIntOrNull() ?: return false
        val minute = parts[1].toIntOrNull() ?: return false
        if (hour !in 0..23 || minute !in 0..59) return false

        val dateParts = validDate.split("-")
        if (dateParts.size != 3) return false
        val year = dateParts[0].toIntOrNull() ?: return false
        val month = dateParts[1].toIntOrNull() ?: return false
        val day = dateParts[2].toIntOrNull() ?: return false

        return try {
            val endCal = Calendar.getInstance().apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month - 1)
                set(Calendar.DAY_OF_MONTH, day)
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            nowMillis > endCal.timeInMillis
        } catch (_: Exception) {
            false
        }
    }

    fun addStudio(name: String, capacity: Int, pricePerHour: Int, facilities: String) {
        val newStudio = Studio(
            id = "st-${System.currentTimeMillis()}",
            name = name.trim(),
            capacity = capacity,
            pricePerHour = pricePerHour,
            facilities = facilities.trim(),
            isAvailable = true
        )
        _studios.value = listOf(newStudio) + _studios.value
    }

    fun toggleStudioAvailability(studioId: String) {
        _studios.value = _studios.value.map { studio ->
            if (studio.id == studioId) studio.copy(isAvailable = !studio.isAvailable) else studio
        }
    }

    fun deleteStudio(studioId: String) {
        _studios.value = _studios.value.filterNot { it.id == studioId }
    }

    fun addEquipment(name: String, pricePerSession: Int, stock: Int) {
        val newEquipment = Equipment(
            id = "eq-${System.currentTimeMillis()}",
            name = name.trim(),
            pricePerSession = pricePerSession,
            stock = stock,
            isAvailable = stock > 0
        )
        _equipments.value = listOf(newEquipment) + _equipments.value
    }

    fun deleteEquipment(equipmentId: String) {
        _equipments.value = _equipments.value.filterNot { it.id == equipmentId }
    }
}