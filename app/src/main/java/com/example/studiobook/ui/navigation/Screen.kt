package com.example.studiobook.ui.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Studios : Screen("studios")
    data object Bookings : Screen("bookings")
    data object Profile : Screen("profile")
}