package com.fadetogo.app.model

data class Booking(
    val bookingId: String = "",
    val customerId: String = "",
    val customerName: String = "",
    val barberId: String = "",
    val serviceType: String = "",
    val serviceDuration: Int = 0, // in minutes
    val travelTime: Int = 0, // in minutes from Google Distance Matrix
    val totalDuration: Int = 0, // serviceDuration + travelTime + 10min buffer
    val basePrice: Double = 0.0,
    val travelSurcharge: Double = 0.0,
    val totalPrice: Double = 0.0,
    val customerLat: Double = 0.0,
    val customerLng: Double = 0.0,
    val customerAddress: String = "",
    val scheduledTime: String = "",
    val status: String = "", // "pending", "accepted", "declined", "completed"
    val timestamp: Long = 0L
)