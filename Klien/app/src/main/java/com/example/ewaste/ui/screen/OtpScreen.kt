package com.example.ewaste.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.delay

@Composable
fun OtpScreen(
    navController: NavController,
    purpose: String,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var code by remember { mutableStateOf("") }
    var redirecting by remember { mutableStateOf(false) }

    // Efek samping untuk redirect setelah verifikasi berhasil
    LaunchedEffect(viewModel.otpVerified, viewModel.otpVerifiedForForgot) {
        if (viewModel.otpVerified || viewModel.otpVerifiedForForgot) {
            redirecting = true
            delay(2000)

            when (purpose) {
                "register" -> {
                    navController.navigate("login") {
                        popUpTo("register") { inclusive = true }
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Verifikasi OTP",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Input Kode OTP
        OutlinedTextField(
            value = code,
            onValueChange = { code = it },
            label = { Text("Kode OTP") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Tombol Verifikasi
        Button(
            onClick = {
                viewModel.verifyOtp(code, purpose)
            },
            enabled = !redirecting && !viewModel.isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (viewModel.isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
            } else {
                Text("Verifikasi")
            }
        }

        if (redirecting) {
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(8.dp))
            Text("Verifikasi berhasil. Mengarahkan...")
        }

        viewModel.registerMessage?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(it)
        }

        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = { navController.popBackStack() }) {
            Text("Kembali", color = MaterialTheme.colorScheme.primary)
        }
    }
}