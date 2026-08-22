package com.bpkpad.peminjaman.peminjaman.presentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bpkpad.peminjaman.peminjaman.domain.model.Transaksi

@Composable
fun DashboardKasubagScreen(
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit,
    viewModel: DashboardKasubagViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    DashboardKasubagContent(
        uiState = uiState,
        onNavigate = onNavigate,
        onLogout = onLogout
    )
}

@Composable
fun DashboardKasubagContent(
    uiState: DashboardKasubagUiState,
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit
) {
    var showLogoutDialog by remember { mutableStateOf(false) }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Konfirmasi Keluar") },
            text = { Text("Apakah Anda yakin ingin keluar?") },
            confirmButton = {
                TextButton(onClick = { showLogoutDialog = false; onLogout() }) {
                    Text("Keluar", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) { Text("Batal") }
            }
        )
    }

    Scaffold(
        containerColor = Color(0xFFF7F8FA),
        topBar = {
            KasubagHeader(
                userName = "Kasubag BPKPAD",
                onLogoutClick = { showLogoutDialog = true }
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF207125))
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
            ) {
                item {
                    Text("Dashboard Kasubag", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable { onNavigate("peminjaman_approval") },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF207125))
                    ) {
                        Row(
                            modifier = Modifier.padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Antrean Persetujuan", color = Color.White, fontSize = 14.sp)
                                Text("${uiState.totalMenunggu} Permohonan", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                            }
                            Icon(Icons.Default.ArrowForward, null, tint = Color.White)
                        }
                    }
                }

                item {
                    Text("Permohonan Menunggu Persetujuan", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                items(uiState.pendingTransaksi) { transaksi ->
                    KasubagItemCard(
                        transaksi = transaksi,
                        onClick = { onNavigate("peminjaman_detail/${transaksi.id}") }
                    )
                }
            }
        }
    }
}

@Composable
private fun KasubagHeader(userName: String, onLogoutClick: () -> Unit) {
    Surface(modifier = Modifier.fillMaxWidth(), color = Color.White, shadowElevation = 1.dp) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AccountBalance,
                    contentDescription = null,
                    tint = Color(0xFF207125),
                    modifier = Modifier.size(32.dp).padding(end = 4.dp)
                )
                Spacer(Modifier.width(10.dp))
                Column {
                    Text("Kasubag BPKPAD", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text(userName, fontSize = 12.sp, color = Color.Gray)
                }
            }
            IconButton(onClick = onLogoutClick) {
                Icon(Icons.Default.ExitToApp, "Keluar", tint = Color.Red)
            }
        }
    }
}

@Composable
private fun KasubagItemCard(transaksi: Transaksi, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(transaksi.namaInstansi, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(Modifier.height(4.dp))
            Text("Surat: ${transaksi.nomorSuratPengantar} • PIC: ${transaksi.picNama}", fontSize = 12.sp, color = Color.Gray)
        }
    }
}
