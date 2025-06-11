package com.example.ewaste.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import com.example.ewaste.data.remote.ApiService
import com.example.ewaste.data.remote.model.*
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val apiService: ApiService,
    @ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val TOKEN_KEY = "auth_token"
        private const val USER_EMAIL_KEY = "user_email"
        private const val USER_NAME_KEY = "user_name"
        private const val USER_ID_KEY = "user_id"
        private const val USER_PHONE_KEY = "user_phone"
        private const val USER_ADDRESS_KEY = "user_address"
        private const val USER_BIRTH_DATE_KEY = "user_birth_date"
        private const val USER_PHOTO_URL_KEY = "user_photo_url"
    }

    // Save authentication data
    private fun saveAuthData(token: String, userId: Int, email: String, username: String) {
        prefs.edit()
            .putString(TOKEN_KEY, token)
            .putInt(USER_ID_KEY, userId)
            .putString(USER_EMAIL_KEY, email)
            .putString(USER_NAME_KEY, username)
            .apply()
    }

    // Save user profile data
    private fun saveUserProfile(userData: UserData) {
        prefs.edit()
            .putString(USER_NAME_KEY, userData.name)
            .putString(USER_PHONE_KEY, userData.phone_number)
            .putString(USER_ADDRESS_KEY, userData.address)
            .putString(USER_BIRTH_DATE_KEY, userData.birth_date)
            .putString(USER_PHOTO_URL_KEY, userData.photo_url)
            .apply()
    }

    // Get token
    fun getToken(): String? = prefs.getString(TOKEN_KEY, null)

    // Get user data
    fun getUserEmail(): String? = prefs.getString(USER_EMAIL_KEY, null)
    fun getUserName(): String? = prefs.getString(USER_NAME_KEY, null)
    fun getUserId(): Int = prefs.getInt(USER_ID_KEY, -1)
    fun getUserPhone(): String? = prefs.getString(USER_PHONE_KEY, null)
    fun getUserAddress(): String? = prefs.getString(USER_ADDRESS_KEY, null)
    fun getUserBirthDate(): String? = prefs.getString(USER_BIRTH_DATE_KEY, null)
    fun getUserPhotoUrl(): String? = prefs.getString(USER_PHOTO_URL_KEY, null)

    // Check if user is logged in
    fun isLoggedIn(): Boolean = getToken() != null

    // Clear authentication data
    fun clearAuthData() {
        prefs.edit().clear().apply()
    }

    // Get user profile from API
    suspend fun getUserProfile(): Result<UserData> {
        return try {
            val token = getToken()
            if (token == null) {
                return Result.failure(Exception("No authentication token"))
            }

            val response = apiService.getProfile("Bearer $token")
            if (response.isSuccessful && response.body()?.success == true) {
                val userData = response.body()!!.data
                saveUserProfile(userData)
                Result.success(userData)
            } else {
                val errorMessage = response.body()?.message ?: "Failed to get profile"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Network error: ${e.message}"))
        }
    }

    // Upload profile with photo
    suspend fun updateProfileWithPhoto(
        name: String?,
        address: String?,
        birthDate: String?,
        phoneNumber: String?,
        photoUri: Uri?
    ): Result<String> {
        return try {
            val token = getToken()
            if (token == null) {
                return Result.failure(Exception("No authentication token"))
            }

            val nameBody = name?.toRequestBody("text/plain".toMediaTypeOrNull())
            val addressBody = address?.toRequestBody("text/plain".toMediaTypeOrNull())
            val birthDateBody = birthDate?.toRequestBody("text/plain".toMediaTypeOrNull())
            val phoneBody = phoneNumber?.toRequestBody("text/plain".toMediaTypeOrNull())

            var photoPart: MultipartBody.Part? = null
            photoUri?.let { uri ->
                val file = uriToFile(uri)
                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                photoPart = MultipartBody.Part.createFormData("photo", file.name, requestFile)
            }

            val response = apiService.updateProfile(
                "Bearer $token",
                nameBody,
                addressBody,
                birthDateBody,
                phoneBody,
                photoPart
            )

            if (response.isSuccessful && response.body()?.success == true) {
                // Refresh profile data after update
                getUserProfile()
                Result.success(response.body()?.message ?: "Profile updated successfully")
            } else {
                val errorMessage = response.body()?.message ?: "Profile update failed"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Network error: ${e.message}"))
        }
    }

    private fun uriToFile(uri: Uri): File {
        val inputStream = context.contentResolver.openInputStream(uri)
        val tempFile = File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
        val outputStream = FileOutputStream(tempFile)

        inputStream?.copyTo(outputStream)
        inputStream?.close()
        outputStream.close()

        return tempFile
    }

    // Login
    suspend fun login(request: LoginRequest): Result<String> {
        return try {
            val response = apiService.login(request)
            if (response.isSuccessful && response.body()?.success == true) {
                val userResponse = response.body()!!
                val userData = userResponse.data
                val token = userData.token ?: ""

                saveAuthData(token, userData.id, userData.email, userData.username)
                saveUserProfile(userData)
                Result.success("Login successful")
            } else {
                val errorMessage = response.body()?.message ?: "Login failed"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Network error: ${e.message}"))
        }
    }

    // Register
    suspend fun register(request: RegisterRequest): Result<String> {
        return try {
            val response = apiService.register(request)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()?.message ?: "Registration successful")
            } else {
                val errorMessage = response.body()?.message ?: "Registration failed"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Network error: ${e.message}"))
        }
    }

    // Verify OTP
    suspend fun verifyOtp(request: OtpRequest): Result<String> {
        return try {
            val response = apiService.verifyOtp(request)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()?.message ?: "OTP verified successfully")
            } else {
                val errorMessage = response.body()?.message ?: "Invalid OTP"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Network error: ${e.message}"))
        }
    }

    // Forgot password
    suspend fun forgotPassword(email: String): Result<String> {
        return try {
            val response = apiService.forgotPassword(ForgotPasswordRequest(email))
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()?.message ?: "OTP sent successfully")
            } else {
                val errorMessage = response.body()?.message ?: "Email not found"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Network error: ${e.message}"))
        }
    }

    // Reset password
    suspend fun resetPassword(email: String, otpCode: String, newPassword: String): Result<String> {
        return try {
            val request = ResetPasswordRequest(email, otpCode, newPassword)
            val response = apiService.resetPassword(request)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()?.message ?: "Password reset successful")
            } else {
                val errorMessage = response.body()?.message ?: "Password reset failed"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Network error: ${e.message}"))
        }
    }

    // Update password
    suspend fun updatePassword(request: UpdatePasswordRequest): Result<String> {
        return try {
            val token = getToken()
            if (token == null) {
                return Result.failure(Exception("No authentication token"))
            }

            val response = apiService.updatePassword("Bearer $token", request)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()?.message ?: "Password updated successfully")
            } else {
                val errorMessage = response.body()?.message ?: "Password update failed"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Network error: ${e.message}"))
        }
    }

    // Logout
    suspend fun logout(): Result<String> {
        return try {
            val token = getToken()
            if (token != null) {
                apiService.logout("Bearer $token")
            }
            clearAuthData()
            Result.success("Logged out successfully")
        } catch (e: Exception) {
            clearAuthData() // Clear local data even if API call fails
            Result.success("Logged out successfully")
        }
    }
}