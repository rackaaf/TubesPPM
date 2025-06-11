package com.example.ewaste.viewmodel

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ewaste.data.repository.AuthRepository
import com.example.ewaste.data.remote.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    val repository: AuthRepository
) : ViewModel() {

    var isLoading by mutableStateOf(false)
        private set

    var loginSuccess by mutableStateOf(false)
        private set

    var otpSent by mutableStateOf(false)
        private set

    var otpVerified by mutableStateOf(false)
        private set

    var otpVerifiedForForgot by mutableStateOf(false)
        private set

    var passwordResetSuccess by mutableStateOf(false)
        private set

    var registerMessage by mutableStateOf<String?>(null)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    // Profile data states
    private val _userProfile = MutableStateFlow<UserData?>(null)
    val userProfile: StateFlow<UserData?> = _userProfile.asStateFlow()

    fun resetStates() {
        loginSuccess = false
        otpSent = false
        otpVerified = false
        otpVerifiedForForgot = false
        passwordResetSuccess = false
        registerMessage = null
        errorMessage = null
    }

    fun loadUserProfile() {
        viewModelScope.launch {
            isLoading = true
            val result = repository.getUserProfile()
            isLoading = false

            result.onSuccess { userData ->
                _userProfile.value = userData
            }.onFailure { exception ->
                errorMessage = exception.message
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            isLoading = true
            val result = repository.login(LoginRequest(email, password))
            isLoading = false

            result.onSuccess {
                loginSuccess = true
                errorMessage = null
                // Load profile after successful login
                loadUserProfile()
            }.onFailure { exception ->
                errorMessage = exception.message
                loginSuccess = false
            }
        }
    }

    fun register(username: String, email: String, phone: String, password: String) {
        viewModelScope.launch {
            isLoading = true
            val result = repository.register(RegisterRequest(username, email, phone, password))
            isLoading = false

            result.onSuccess { message ->
                registerMessage = message
                otpSent = true
            }.onFailure { exception ->
                registerMessage = exception.message
                otpSent = false
            }
        }
    }

    fun verifyOtp(code: String, purpose: String) {
        viewModelScope.launch {
            isLoading = true
            val result = repository.verifyOtp(OtpRequest(code))
            isLoading = false

            result.onSuccess { message ->
                registerMessage = message
                when (purpose) {
                    "register" -> otpVerified = true
                    "forgot_password" -> otpVerifiedForForgot = true
                }
            }.onFailure { exception ->
                registerMessage = exception.message
            }
        }
    }

    fun forgotPassword(email: String) {
        viewModelScope.launch {
            isLoading = true
            val result = repository.forgotPassword(email)
            isLoading = false

            result.onSuccess { message ->
                registerMessage = message
            }.onFailure { exception ->
                registerMessage = exception.message
            }
        }
    }

    fun resetPassword(email: String, otpCode: String, newPassword: String) {
        viewModelScope.launch {
            isLoading = true
            val result = repository.resetPassword(email, otpCode, newPassword)
            isLoading = false

            result.onSuccess { message ->
                registerMessage = message
                passwordResetSuccess = true
            }.onFailure { exception ->
                registerMessage = exception.message
                passwordResetSuccess = false
            }
        }
    }

    fun updateProfileWithPhoto(
        name: String?,
        address: String?,
        birthDate: String?,
        phoneNumber: String?,
        photoUri: Uri?
    ) {
        viewModelScope.launch {
            isLoading = true
            val result = repository.updateProfileWithPhoto(name, address, birthDate, phoneNumber, photoUri)
            isLoading = false

            result.onSuccess { message ->
                registerMessage = message
                // Reload profile after update
                loadUserProfile()
            }.onFailure { exception ->
                registerMessage = exception.message
            }
        }
    }

    fun updateProfile(name: String, address: String, birthDate: String, phoneNumber: String?) {
        updateProfileWithPhoto(name, address, birthDate, phoneNumber, null)
    }

    fun updatePassword(oldPassword: String, newPassword: String, confirmPassword: String) {
        viewModelScope.launch {
            isLoading = true
            val result = repository.updatePassword(UpdatePasswordRequest(oldPassword, newPassword, confirmPassword))
            isLoading = false

            result.onSuccess { message ->
                registerMessage = message
            }.onFailure { exception ->
                registerMessage = exception.message
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            resetStates()
            _userProfile.value = null
        }
    }
}