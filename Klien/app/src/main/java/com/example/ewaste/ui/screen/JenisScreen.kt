package com.example.ewaste.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.ewaste.ui.theme.PrimaryGreen
import com.example.ewaste.ui.theme.SecondaryGreen
import com.example.ewaste.viewmodel.KategoriViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.collectAsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JenisScreen(
    navController: NavController,
    viewModel: KategoriViewModel = hiltViewModel()
) {
    val jenisList by viewModel.jenisList.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Alternative: load all jenis if accessed directly
    LaunchedEffect(Unit) {
        if (jenisList.isEmpty()) {
            viewModel.loadAllJenis()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        // Top App Bar
        TopAppBar(
            title = {
                Text(
                    "Jenis Sampah",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
            },
            actions = {
                IconButton(onClick = { viewModel.loadAllJenis() }) {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = "Refresh",
                        tint = Color.White
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = SecondaryGreen
            )
        )

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = SecondaryGreen)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Memuat data jenis sampah...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SecondaryGreen
                    )
                }
            }
        } else if (jenisList.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "🗂️",
                        style = MaterialTheme.typography.displayMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Tidak ada jenis sampah tersedia",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { viewModel.loadAllJenis() },
                        colors = ButtonDefaults.buttonColors(containerColor = SecondaryGreen)
                    ) {
                        Text("Coba Lagi")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(jenisList) { jenis ->
                    JenisItem(
                        jenis = jenis.namaJenis,
                        onClick = {
                            // Handle jenis click if needed (maybe show details)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun JenisItem(jenis: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon berdasarkan jenis sampah
            Text(
                text = getJenisIcon(jenis),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(end = 16.dp)
            )

            // Nama jenis sampah
            Text(
                text = jenis,
                style = MaterialTheme.typography.titleMedium,
                color = SecondaryGreen,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )

            // Arrow indicator
            Text(
                text = "→",
                style = MaterialTheme.typography.titleLarge,
                color = SecondaryGreen.copy(alpha = 0.7f)
            )
        }
    }
}

private fun getJenisIcon(jenis: String): String {
    return when {
        jenis.contains("baterai", ignoreCase = true) -> "🔋"
        jenis.contains("lampu", ignoreCase = true) -> "💡"
        jenis.contains("cat", ignoreCase = true) -> "🎨"
        jenis.contains("makanan", ignoreCase = true) -> "🍎"
        jenis.contains("daun", ignoreCase = true) -> "🍃"
        jenis.contains("sayuran", ignoreCase = true) -> "🥬"
        jenis.contains("kotoran", ignoreCase = true) -> "💩"
        jenis.contains("botol", ignoreCase = true) -> "🍼"
        jenis.contains("kantong", ignoreCase = true) -> "🛍️"
        jenis.contains("kemasan", ignoreCase = true) -> "📦"
        jenis.contains("popok", ignoreCase = true) -> "👶"
        jenis.contains("pembalut", ignoreCase = true) -> "🩸"
        else -> "♻️"
    }
}