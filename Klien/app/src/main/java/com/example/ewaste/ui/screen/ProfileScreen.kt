package com.example.ewaste.ui.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.ewaste.ui.theme.PrimaryGreen
import com.example.ewaste.viewmodel.AuthViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.collectAsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
        uri?.let {
            // Upload photo immediately when selected
            viewModel.updateProfileWithPhoto(
                name = name.ifEmpty { null },
                address = address.ifEmpty { null },
                birthDate = birthDate.ifEmpty { null },
                phoneNumber = phone.ifEmpty { null },
                photoUri = it
            )
        }
    }

    // Load user profile on first load
    LaunchedEffect(Unit) {
        viewModel.loadUserProfile()
    }

    // Update fields when profile loads
    LaunchedEffect(userProfile) {
        userProfile?.let { profile ->
            name = profile.name ?: ""
            email = profile.email
            phone = profile.phone_number ?: ""
            address = profile.address ?: ""
            // Fix birth date formatting - remove time part
            birthDate = profile.birth_date?.split("T")?.get(0) ?: ""
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
                    "Profile",
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
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = PrimaryGreen
            )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(24.dp)
        ) {
            // Profile Picture Section with AsyncImage
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box {
                    // Profile Picture
                    val imageToShow = selectedImageUri ?: userProfile?.photo_url

                    if (imageToShow != null) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(imageToShow)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Profile Picture",
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        // Default avatar with initial
                        Surface(
                            modifier = Modifier.size(120.dp),
                            shape = CircleShape,
                            color = PrimaryGreen.copy(alpha = 0.2f)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = name.take(1).uppercase().ifEmpty { "U" },
                                    style = MaterialTheme.typography.headlineLarge,
                                    color = PrimaryGreen
                                )
                            }
                        }
                    }

                    // Edit button
                    FloatingActionButton(
                        onClick = { imagePickerLauncher.launch("image/*") },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(36.dp),
                        containerColor = PrimaryGreen
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Change Picture",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            // Profile Fields
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nama Lengkap") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryGreen,
                    focusedLabelColor = PrimaryGreen
                )
            )

            OutlinedTextField(
                value = email,
                onValueChange = { /* Email tidak bisa diubah */ },
                label = { Text("Email") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                enabled = false, // Email tidak bisa diubah
                colors = OutlinedTextFieldDefaults.colors(
                    disabledBorderColor = Color.Gray,
                    disabledLabelColor = Color.Gray
                )
            )

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Nomor Telepon") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryGreen,
                    focusedLabelColor = PrimaryGreen
                )
            )

            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Alamat") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                maxLines = 3,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryGreen,
                    focusedLabelColor = PrimaryGreen
                )
            )

            OutlinedTextField(
                value = birthDate,
                onValueChange = { birthDate = it },
                label = { Text("Tanggal Lahir (YYYY-MM-DD)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryGreen,
                    focusedLabelColor = PrimaryGreen
                ),
                placeholder = { Text("Contoh: 1995-06-09") }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Update Profile Button
            Button(
                onClick = {
                    viewModel.updateProfile(name, address, birthDate, phone)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                enabled = !viewModel.isLoading
            ) {
                if (viewModel.isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text(
                        "Update Profile",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Change Password Button
            OutlinedButton(
                onClick = { navController.navigate("change-password") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = PrimaryGreen
                )
            ) {
                Text(
                    "Ganti Password",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            // Show update message
            viewModel.registerMessage?.let { message ->
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (message.contains("success", ignoreCase = true))
                            Color.Green.copy(alpha = 0.1f)
                        else
                            Color.Red.copy(alpha = 0.1f)
                    )
                ) {
                    Text(
                        text = message,
                        modifier = Modifier.padding(16.dp),
                        color = if (message.contains("success", ignoreCase = true))
                            Color.Green.copy(alpha = 0.8f)
                        else
                            Color.Red.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

