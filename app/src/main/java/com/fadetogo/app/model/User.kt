package com.fadetogo.app.model

data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val role: String = "", // "barber" or "customer"
    val profileImageUrl: String = "",
    val isAvailable: Boolean = false // only relevant for barber
)