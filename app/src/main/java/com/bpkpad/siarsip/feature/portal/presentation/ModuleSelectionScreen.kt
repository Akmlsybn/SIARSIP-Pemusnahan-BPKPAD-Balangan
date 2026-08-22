package com.bpkpad.siarsip.feature.portal.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.AssignmentReturn
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bpkpad.siarsip.core.navigation.Screen

data class ModuleCardItem(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val route: String,
    val badgeText: String,
    val gradientColors: List<Color>,
    val iconBgColor: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModuleSelectionScreen(
    userName: String = "Admin SIARSIP",
    userRole: String = "Administrator BPKPAD",
    onSelectModule: (String) -> Unit,
    onLogout: () -> Unit
) {
    val modules = remember {
        listOf(
            ModuleCardItem(
                title = "Arsip Pemusnahan",
                description = "Pengelolaan berkas usul musnah, penilaian retensi, & berita acara pemusnahan",
                icon = Icons.Default.DeleteSweep,
                route = Screen.Dashboard.route,
                badgeText = "Modul Utama",
                gradientColors = listOf(Color(0xFF1E293B), Color(0xFF0F172A)),
                iconBgColor = Color(0xFFEF4444)
            ),
            ModuleCardItem(
                title = "Arsip Keuangan",
                description = "Pencatatan & verifikasi digital SP2D, SPM, SPJ, serta manajemen rak storage",
                icon = Icons.Default.AccountBalanceWallet,
                route = Screen.KeuanganHome.route,
                badgeText = "Micro-App",
                gradientColors = listOf(Color(0xFF1E293B), Color(0xFF0F172A)),
                iconBgColor = Color(0xFF3B82F6)
            ),
            ModuleCardItem(
                title = "Arsip Non-Keuangan",
                description = "Pengarsipan digital Surat Dinas, PERDA, PERBUP, & Keputusan Bupati Balangan",
                icon = Icons.Default.FolderSpecial,
                route = Screen.NonKeuanganHome.route,
                badgeText = "Micro-App",
                gradientColors = listOf(Color(0xFF1E293B), Color(0xFF0F172A)),
                iconBgColor = Color(0xFF10B981)
            ),
            ModuleCardItem(
                title = "Peminjaman Arsip",
                description = "Layanan permohonan pinjam arsip instansi, persetujuan Kasubag, & scan QR pengembalian",
                icon = Icons.AutoMirrored.Filled.AssignmentReturn,
                route = Screen.PeminjamanDashboard.route,
                badgeText = "Micro-App",
                gradientColors = listOf(Color(0xFF1E293B), Color(0xFF0F172A)),
                iconBgColor = Color(0xFF8B5CF6)
            )
        )
    }

    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        isVisible = true
    }

    Scaffold(
        containerColor = Color(0xFF0F172A),
        topBar = {
            TopAppBar(
                title = {},
                actions = {
                    IconButton(
                        onClick = onLogout,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Logout",
                            tint = Color(0xFFF87171)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
        ) {
            // Header Info
            Column(modifier = Modifier.padding(bottom = 24.dp)) {
                Surface(
                    color = Color(0xFF1E293B),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF22C55E))
                        )
                        Text(
                            text = "PORTAL UTAMA SIARSIP",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }

                Text(
                    text = "Selamat Datang,",
                    fontSize = 16.sp,
                    color = Color(0xFF94A3B8)
                )
                Text(
                    text = userName,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = userRole,
                    fontSize = 13.sp,
                    color = Color(0xFF64748B)
                )
            }

            Text(
                text = "Pilih Layanan Modul",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFFCBD5E1),
                modifier = Modifier.padding(bottom = 14.dp)
            )

            // Module Cards Grid
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn() + slideInVertically(initialOffsetY = { 40 })
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(1),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(modules) { item ->
                        ModuleCard(
                            item = item,
                            onClick = { onSelectModule(item.route) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ModuleCard(
    item: ModuleCardItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF1E293B), Color(0xFF1E293B))
                    )
                )
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Module Icon
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(item.iconBgColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        tint = item.iconBgColor,
                        modifier = Modifier.size(28.dp)
                    )
                }

                // Text Content
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = item.title,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = item.description,
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Action Icon
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Buka",
                    tint = Color(0xFF64748B),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
