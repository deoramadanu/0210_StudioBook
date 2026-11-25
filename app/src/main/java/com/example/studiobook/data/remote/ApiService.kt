package com.example.studiobook.data.remote

import com.example.studiobook.data.model.AuthSession
import com.example.studiobook.data.model.Booking
import com.example.studiobook.data.model.Equipment
import com.example.studiobook.data.model.Studio
import com.example.studiobook.data.model.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.Query

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)

data class RefreshTokenRequest(
    val refreshToken: String
)

data class UpdateStatusRequest(
    val status: String
)

interface ApiService {

    // --- Auth & Users ---
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthSession>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthSession>

    @POST("auth/refresh")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): Response<AuthSession>

    @GET("users/me")
    suspend fun getCurrentUser(): Response<User>

    // --- Studios (CRUDS) ---
    @GET("studios")
    suspend fun getStudios(@Query("q") search: String? = null): Response<List<Studio>>

    @POST("studios")
    suspend fun createStudio(@Body studio: Studio): Response<Studio>

    @PUT("studios/{id}")
    suspend fun updateStudio(
        @Path("id") id: String,
        @Body studio: Studio
    ): Response<Studio>

    @DELETE("studios/{id}")
    suspend fun deleteStudio(@Path("id") id: String): Response<Unit>

    // --- Equipment (CRUDS) ---
    @GET("equipment")
    suspend fun getEquipment(@Query("q") search: String? = null): Response<List<Equipment>>

    @POST("equipment")
    suspend fun createEquipment(@Body equipment: Equipment): Response<Equipment>

    @PUT("equipment/{id}")
    suspend fun updateEquipment(
        @Path("id") id: String,
        @Body equipment: Equipment
    ): Response<Equipment>

    @DELETE("equipment/{id}")
    suspend fun deleteEquipment(@Path("id") id: String): Response<Unit>

    // --- Bookings (CRUDS) ---
    @GET("bookings")
    suspend fun getBookings(@Query("q") search: String? = null): Response<List<Booking>>

    @POST("bookings")
    suspend fun createBooking(@Body booking: Booking): Response<Booking>

    @PATCH("bookings/{id}/status")
    suspend fun updateBookingStatus(
        @Path("id") id: String,
        @Body body: UpdateStatusRequest
    ): Response<Booking>

    @DELETE("bookings/{id}")
    suspend fun deleteBooking(@Path("id") id: String): Response<Unit>
}