package com.example.ewaste.data.remote.model

// Data classes untuk keperluan lokal jika diperlukan
data class UserProfile(
    val id: Int,
    val username: String,
    val email: String,
    val name: String? = null,
    val address: String? = null,
    val phoneNumber: String? = null
)

