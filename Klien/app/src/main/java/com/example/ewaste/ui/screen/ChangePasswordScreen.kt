package com.example.ewaste.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.ewaste.ui.theme.PrimaryGreen
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ewaste.viewmodel.AuthViewModel

@Composable
fun ChangePasswordScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordMismatch by remember { mutableStateOf(false) }

    var oldPasswordVisible by remember { mutableStateOf(false) }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        // Header dengan back button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = PrimaryGreen
                )
            }
            Text(
                "Change Password",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = PrimaryGreen
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Input Password Lama
        OutlinedTextField(
            value = oldPassword,
            onValueChange = { oldPassword = it },
            label = { Text("Password Lama") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (oldPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { oldPasswordVisible = !oldPasswordVisible }) {
                    Icon(
                        imageVector = if (oldPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = null
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Input Password Baru
        OutlinedTextField(
            value = newPassword,
            onValueChange = { newPassword = it },
            label = { Text("Password Baru") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { newPasswordVisible = !newPasswordVisible }) {
                    Icon(
                        imageVector = if (newPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = null
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Input Konfirmasi Password Baru
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Konfirmasi Password Baru") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                    Icon(
                        imageVector = if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = null
                    )
                }
            },
            isError = passwordMismatch
        )

        if (passwordMismatch) {
            Text("Password baru dan konfirmasi password tidak cocok", color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Tombol Update Password
        Button(
            onClick = {
                if (newPassword != confirmPassword) {
                    passwordMismatch = true
                } else {
                    passwordMismatch = false
                    viewModel.updatePassword(oldPassword, newPassword, confirmPassword)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
            enabled = !viewModel.isLoading
        ) {
            if (viewModel.isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
            } else {
                Text("Update Password")
            }
        }

        // Show update message
        viewModel.registerMessage?.let {
            Spacer(modifier = Modifier.height(16.dp))
            Text(it, color = if (it.contains("success")) Color.Green else Color.Red)
        }
    }
}