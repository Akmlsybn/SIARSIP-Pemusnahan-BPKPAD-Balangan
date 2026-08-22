package com.bpkpad.peminjaman.peminjaman.presentation.pengembalian

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ScanQrScreen(
    onBack: () -> Unit,
    onFound: (Int) -> Unit,
    viewModel: ScanQrViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    LaunchedEffect(uiState.foundTransaksiId) {
        uiState.foundTransaksiId?.let { id -> viewModel.clearFound(); onFound(id) }
    }
    ScanQrContent(
        uiState = uiState,
        onBack = onBack,
        onQrDetected = viewModel::onQrDetected,
        onManualSearch = viewModel::findByToken,
        onClearError = viewModel::clearError
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanQrContent(
    uiState: ScanQrUiState,
    onBack: () -> Unit,
    onQrDetected: (String) -> Unit,
    onManualSearch: (String) -> Unit,
    onClearError: () -> Unit
) {
    var manualToken by remember { mutableStateOf("") }

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            onQrDetected("TRX-DEMO-001")
        }
    }

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            onQrDetected("TRX-DEMO-001")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Scan QR Pengembalian Arsip", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .background(Color(0xFFE2E8F0), RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.QrCodeScanner,
                    contentDescription = null,
                    modifier = Modifier.size(96.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Pemindaian Kode QR Pengembalian",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Arahkan kamera ke QR Code transaksi peminjaman untuk melakukan validasi pengembalian secara cepat.",
                fontSize = 13.sp,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { takePictureLauncher.launch(null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.CameraAlt, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Buka Kamera HP")
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = { pickImageLauncher.launch("image/*") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Image, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Pilih QR dari Galeri Foto")
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = manualToken,
                onValueChange = { manualToken = it },
                label = { Text("Input Token QR Manual") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(onClick = { if (manualToken.isNotBlank()) onManualSearch(manualToken) }) {
                        Icon(Icons.Default.Search, contentDescription = null)
                    }
                }
            )

            if (uiState.isLoading) {
                Spacer(modifier = Modifier.height(16.dp))
                CircularProgressIndicator()
            }
        }
    }
}
