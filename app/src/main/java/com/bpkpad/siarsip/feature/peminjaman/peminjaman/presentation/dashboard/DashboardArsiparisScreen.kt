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
fun DashboardArsiparisScreen(
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit,
    viewModel: DashboardArsiparisViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    DashboardArsiparisContent(
        uiState = uiState,
        onNavigate = onNavigate,
        onLogout = onLogout
    )
}

@Composable
fun DashboardArsiparisContent(
    uiState: DashboardArsiparisUiState,
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit
) {
    var showLogoutDialog by remember { mutableStateOf(false) }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Konfirmasi Keluar") },
            text = { Text("Apakah Anda yakin ingin keluar dari sistem Peminjaman Arsip?") },
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
            HeaderSection(
                userName = "Arsiparis BPKPAD",
                onLogoutClick = { showLogoutDialog = true }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { onNavigate("peminjaman_form") },
                containerColor = Color(0xFF207125),
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Buat Permohonan", fontWeight = FontWeight.Bold)
            }
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
                contentPadding = PaddingValues(top = 16.dp, bottom = 88.dp)
            ) {
                item {
                    Text(
                        "Overview Peminjaman",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A1A)
                    )
                }

                item {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        StatCard(
                            title = "Total Dipinjam",
                            count = uiState.totalDipinjam.toString(),
                            icon = Icons.Default.FolderZip,
                            color = Color(0xFF1976D2),
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "Terlambat",
                            count = uiState.totalOverdue.toString(),
                            icon = Icons.Default.Warning,
                            color = Color(0xFFD32F2F),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        QuickMenuButton(
                            title = "Scan QR",
                            icon = Icons.Default.QrCodeScanner,
                            onClick = { onNavigate("peminjaman_scan_qr") },
                            modifier = Modifier.weight(1f)
                        )
                        QuickMenuButton(
                            title = "Daftar Riwayat",
                            icon = Icons.Default.History,
                            onClick = { onNavigate("peminjaman_riwayat") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    Text(
                        "Transaksi Terakhir",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A1A)
                    )
                }

                items(uiState.recentList) { transaksi ->
                    TransaksiCardItem(
                        transaksi = transaksi,
                        onClick = { onNavigate("peminjaman_detail/${transaksi.id}") }
                    )
                }
            }
        }
    }
}

@Composable
private fun HeaderSection(userName: String, onLogoutClick: () -> Unit) {
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
                    Text("BPKPAD Balangan", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A1A))
                    Text(userName, fontSize = 12.sp, color = Color.Gray)
                }
            }
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(0xFFFEE2E2)).clickable(onClick = onLogoutClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.ExitToApp, "Keluar", tint = Color(0xFFDC2626), modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
private fun StatCard(title: String, count: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(color.copy(alpha = 0.15f)), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = color, modifier = Modifier.size(24.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(count, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A1A))
                Text(title, fontSize = 11.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
private fun QuickMenuButton(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit, modifier: Modifier = Modifier) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp)
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(8.dp))
        Text(title, fontSize = 13.sp)
    }
}

@Composable
private fun TransaksiCardItem(transaksi: Transaksi, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(transaksi.namaInstansi, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(transaksi.status.name, fontSize = 11.sp, color = Color(0xFF207125), fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(4.dp))
            Text("PIC: ${transaksi.picNama} • Surat: ${transaksi.nomorSuratPengantar}", fontSize = 12.sp, color = Color.Gray)
        }
    }
}
