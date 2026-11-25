package com.example.studiobook.data.model

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val role: String = "USER" // "USER" atau "ADMIN"
)

data class Studio(
    val id: String = "",
    val name: String = "",
    val capacity: Int = 4,
    val pricePerHour: Int = 0,
    val facilities: String = "",
    val isAvailable: Boolean = true
)

data class Equipment(
    val id: String = "",
    val name: String = "",
    val pricePerSession: Int = 0,
    val stock: Int = 1,
    val isAvailable: Boolean = true
)

data class Booking(
    val id: String = "",
    val userId: String = "",
    val bandName: String = "",
    val bookerName: String = "",
    val studioId: String = "",
    val studioName: String = "",
    val equipmentId: String? = null,
    val equipmentName: String? = null,
    val date: String = "",
    val timeSlot: String = "",
    val totalPrice: Int = 0,
    val status: String = "Pending" // Pending, Confirmed, Completed
)

data class AuthSession(
    val accessToken: String = "",
    val refreshToken: String = "",
    val user: User = User()
)