package com.bpkpad.peminjaman.peminjaman.presentation.detail

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bpkpad.peminjaman.peminjaman.domain.model.Perpanjangan
import com.bpkpad.peminjaman.peminjaman.domain.model.Transaksi
import com.bpkpad.peminjaman.peminjaman.domain.model.enums.TransaksiStatus
import com.bpkpad.peminjaman.qr.QrGenerator

@Composable
fun DetailTransaksiScreen(
    transaksiId: Int,
    onBack: () -> Unit,
    viewModel: DetailTransaksiViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(transaksiId) {
        viewModel.load(transaksiId)
    }

    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearMessages()
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            Toast.makeText(context, "Error: $it", Toast.LENGTH_SHORT).show()
            viewModel.clearMessages()
        }
    }

    DetailTransaksiContent(
        uiState = uiState,
        onBack = onBack,
        onBypass = viewModel::bypassPendingTransaksi,
        onCancel = viewModel::cancelTransaksi
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailTransaksiContent(
    uiState: DetailTransaksiUiState,
    onBack: () -> Unit,
    onBypass: () -> Unit,
    onCancel: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Transaksi Peminjaman", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        if (uiState.isLoading && uiState.transaksi == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else {
            val t = uiState.transaksi
            if (t == null) {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Text("Transaksi tidak ditemukan")
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(t.namaInstansi, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                StatusBadge(t.status)
                            }
                            HorizontalDivider(color = Color(0xFFF1F5F9))
                            DetailRow("PIC Peminjam", "${t.picNama} (${t.picNoHp})")
                            DetailRow("Nomor Surat", t.nomorSuratPengantar)
                            DetailRow("Tanggal Pinjam", t.tanggalPinjam.toString())
                            DetailRow("Rencana Kembali", t.tanggalKembaliRencana.toString())
                            if (t.tanggalKembaliAktual != null) {
                                DetailRow("Tanggal Kembali Aktual", t.tanggalKembaliAktual.toString())
                            }
                        }
                    }

                    val token = t.qrCodeToken
                    if (!token.isNullOrBlank()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(Modifier.fillMaxWidth().background(Color(0xFFDFF5E1).copy(alpha = 0.5f)).padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                                    Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = Color(0xFF207125))
                                    Spacer(Modifier.width(8.dp))
                                    Text("Kode QR Pengembalian Arsip", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = Color(0xFF207125))
                                }
                                Spacer(Modifier.height(8.dp))
                                QrCodeDisplay(token, 200.dp)
                                Spacer(Modifier.height(8.dp))
                                Surface(color = Color.White, shape = RoundedCornerShape(8.dp)) {
                                    Text(token, style = MaterialTheme.typography.labelMedium, color = Color(0xFF207125), fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
                                }
                            }
                        }
                    }

                    if (uiState.perpanjanganList.isNotEmpty()) {
                        Text("Riwayat Perpanjangan (${uiState.perpanjanganList.size})", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        uiState.perpanjanganList.forEach { p ->
                            PerpanjanganItem(p)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 13.sp, color = Color.Gray)
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun StatusBadge(status: TransaksiStatus) {
    Surface(
        color = Color(0xFF207125).copy(alpha = 0.15f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = status.name,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF207125)
        )
    }
}

@Composable
private fun QrCodeDisplay(token: String, size: Dp) {
    val bitmap = remember(token) { QrGenerator.generateQrBitmap(token) }
    if (bitmap != null) {
        androidx.compose.foundation.Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "QR Code",
            modifier = Modifier.size(size)
        )
    }
}

@Composable
private fun PerpanjanganItem(p: Perpanjangan) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Perpanjangan S/D: ${p.tanggalKembaliBaru}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(Modifier.height(4.dp))
            Text("Alasan: ${p.alasan}", fontSize = 12.sp, color = Color.Gray)
        }
    }
}
