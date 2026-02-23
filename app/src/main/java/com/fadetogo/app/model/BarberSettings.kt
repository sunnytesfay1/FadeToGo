package com.fadetogo.app.model

data class BarberSettings(
    val barberId: String = "",

    // pricing settings the barber controls
    val baseRadiusMiles: Double = 10.0,
    val maxRadiusMiles: Double = 30.0,
    val costPerMile: Double = 1.50,
    val bufferMinutes: Int = 10,

    // working hours stored as a map
    // key is day of week e.g "Monday"
    // value is a WorkingHours object with start and end times
    val workingHours: Map<String, WorkingHours> = emptyMap(),

    // whether the barber is currently accepting bookings
    val isAvailable: Boolean = false
)

data class WorkingHours(
    val isOpen: Boolean = false,
    val startTime: String = "09:00",
    val endTime: String = "17:00"
)