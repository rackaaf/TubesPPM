package com.example.ewaste.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.example.ewaste.ui.screen.*

@Composable
fun EWasteApp() {
    val navController: NavHostController = rememberNavController()

    NavHost(navController, startDestination = "splash") {
        composable("splash") { SplashScreen(navController) }
        composable("register") { RegisterScreen(navController) }
        composable("otp/{purpose}") { backStackEntry ->
            val purpose = backStackEntry.arguments?.getString("purpose") ?: "register"
            OtpScreen(navController, purpose)
        }
        composable("login") { LoginScreen(navController) }
        composable("home") { HomeScreen(navController) }
        composable("forgot") { ForgotPasswordScreen(navController) }
        composable("reset") { ResetPasswordScreen(navController) }
        composable("profile") { ProfileScreen(navController) }
        composable("change-password") { ChangePasswordScreen(navController) }
        composable("kategori") { KategoriScreen(navController) }

        // Route untuk jenis sampah dengan parameter kategoriId
        composable(
            route = "jenis/{kategoriId}",
            arguments = listOf(navArgument("kategoriId") { type = NavType.IntType })
        ) { backStackEntry ->
            val kategoriId = backStackEntry.arguments?.getInt("kategoriId") ?: 0
            JenisScreen(navController, kategoriId)
        }

        // Route untuk semua jenis sampah (tanpa filter)
        composable("jenis") {
            JenisScreen(navController, null) // null = tampilkan semua jenis
        }
    }
}