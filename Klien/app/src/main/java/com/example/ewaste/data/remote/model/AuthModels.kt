package com.example.ewaste.data.remote.model

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val username: String,
    val email: String,
    val phoneNumber: String,
    val password: String
)

data class OtpRequest(
    val code: String
)

data class ForgotPasswordRequest(
    val email: String
)

data class ResetPasswordRequest(
    val email: String,
    val otpCode: String,
    val newPassword: String
)

data class ProfileRequest(
    val name: String,
    val address: String,
    val birthDate: String,
    val photoUrl: String? = null
)

data class UpdatePasswordRequest(
    val oldPassword: String,
    val newPassword: String,
    val confirmPassword: String
)