package com.example.ewaste.data.remote.model

data class BaseResponse(
    val success: Boolean,
    val message: String
)

data class UserResponse(
    val success: Boolean,
    val message: String? = null,
    val data: UserData
)

data class UserData(
    val id: Int,
    val username: String,
    val email: String,
    val token: String? = null,
    val name: String? = null,
    val phone_number: String? = null,
    val address: String? = null,
    val birth_date: String? = null,
    val photo_url: String? = null
)

data class KategoriResponse(
    val id: Int,
    val nama: String,
    val deskripsi: String? = null
)

data class JenisResponse(
    val id: Int,
    val namaJenis: String,
    val kategoriId: Int
)

data class ProfileResponse(
    val success: Boolean,
    val message: String,
    val data: UserData
)

