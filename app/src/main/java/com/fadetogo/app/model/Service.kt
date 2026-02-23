package com.fadetogo.app.model

data class Service(
    val serviceId: String = "",
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val durationMinutes: Int = 0
)