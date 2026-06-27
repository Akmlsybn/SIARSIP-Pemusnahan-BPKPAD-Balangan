package com.bpkpad.siarsip.ui.screens.pengaturan

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bpkpad.siarsip.ui.components.DrawerRoutes
import com.bpkpad.siarsip.ui.components.PemusnahanDrawerContent
import com.bpkpad.siarsip.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PengaturanScreen(
    onNavigate: (String) -> Unit = {}
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var retensiYears by remember { mutableStateOf("10 Tahun") }
    var securityLock by remember { mutableStateOf(true) }
    var notificationSound by remember { mutableStateOf(false) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.fillMaxWidth(0.86f),
                drawerContainerColor = CardWhite,
                drawerShape = RoundedCornerShape(0.dp)
            ) {
                PemusnahanDrawerContent(
                    currentRoute = DrawerRoutes.PENGATURAN,
                    onNavigate = { route ->
                        scope.launch { drawerState.close() }
                        onNavigate(route)
                    },
                    onLogout = {
                        scope.launch { drawerState.close() }
                        onNavigate("logout")
                    }
                )
            }
        }
    ) {
        Scaffold(
            containerColor = BgDashboard,
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text("Pengaturan",
                                fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text("Konfigurasi sistem & pemeliharaan data",
                                fontSize = 10.sp, color = Color.White.copy(alpha = 0.6f))
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Filled.Menu, "Buka menu", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = GreenPrimary)
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // ── Section 1: Retensi Sistem ──
                SettingsSection(title = "Sistem & Retensi") {
                    SettingsRow(
                        title = "Batas Retensi Pemusnahan",
                        subtitle = "Batas minimal usia arsip untuk dimusnahkan",
                        icon = Icons.Filled.CalendarMonth,
                        iconBg = GreenLight,
                        iconTint = GreenPrimary
                    ) {
                        Text(
                            text = retensiYears,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = GreenPrimary,
                            modifier = Modifier.padding(end = 4.dp)
                        )
                    }
                }

                // ── Section 2: Pemeliharaan Database ──
                SettingsSection(title = "Pemeliharaan Basis Data") {
                    SettingsRowAction(
                        title = "Backup Database Room",
                        subtitle = "Ekspor salinan basis data terbaru ke lokal storage",
                        icon = Icons.Filled.Backup,
                        iconBg = BlueBg,
                        iconTint = BlueText
                    )
                    SettingsRowAction(
                        title = "Restore Database",
                        subtitle = "Pulihkan basis data dari file eksternal",
                        icon = Icons.Filled.Restore,
                        iconBg = AmberBg,
                        iconTint = AmberText
                    )
                    SettingsRowAction(
                        title = "Bersihkan Cache Aplikasi",
                        subtitle = "Hapus berkas sementara yang tidak terpakai",
                        icon = Icons.Filled.CleaningServices,
                        iconBg = DangerBg,
                        iconTint = DangerText
                    )
                }

                // ── Section 3: Keamanan & Notifikasi ──
                SettingsSection(title = "Keamanan & Pengingat") {
                    SettingsRow(
                        title = "Kunci Aplikasi Otomatis",
                        subtitle = "Minta autentikasi setiap kali aplikasi dibuka",
                        icon = Icons.Filled.Security,
                        iconBg = GreenLight,
                        iconTint = GreenPrimary
                    ) {
                        Switch(
                            checked = securityLock,
                            onCheckedChange = { securityLock = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = GreenPrimary,
                                uncheckedThumbColor = TextHint,
                                uncheckedTrackColor = BorderGray
                            )
                        )
                    }
                    SettingsRow(
                        title = "Suara Notifikasi Status",
                        subtitle = "Bunyikan alarm saat usulan disetujui",
                        icon = Icons.Filled.VolumeUp,
                        iconBg = BlueBg,
                        iconTint = BlueText
                    ) {
                        Switch(
                            checked = notificationSound,
                            onCheckedChange = { notificationSound = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = GreenPrimary,
                                uncheckedThumbColor = TextHint,
                                uncheckedTrackColor = BorderGray
                            )
                        )
                    }
                }

                // ── Section 4: Informasi Aplikasi ──
                SettingsSection(title = "Tentang Aplikasi") {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        InfoTextRow("Nama Aplikasi", "SIARSIP Pemusnahan")
                        InfoTextRow("Versi Sistem", "1.0.0 - Stable Release")
                        InfoTextRow("Database Engine", "Room SQLite SQLite3")
                        InfoTextRow("Developer", "Bidang Kearsipan BPKPAD Balangan")
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "© 2026 Pemerintah Kabupaten Balangan. Hak Cipta Dilindungi.",
                            fontSize = 10.sp,
                            color = TextHint,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {
        Text(
            text = title.uppercase(),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = TextHint,
            letterSpacing = 0.8.sp,
            modifier = Modifier.padding(start = 12.dp, bottom = 6.dp)
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardWhite),
            border = BorderStroke(1.dp, BorderGray),
            content = content
        )
    }
}

@Composable
private fun SettingsRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    actionContent: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = iconTint, modifier = Modifier.size(18.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextHead)
            Text(subtitle, fontSize = 10.sp, color = TextHint)
        }
        Spacer(Modifier.width(8.dp))
        actionContent()
    }
}

@Composable
private fun SettingsRowAction(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = iconTint, modifier = Modifier.size(18.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextHead)
            Text(subtitle, fontSize = 10.sp, color = TextHint)
        }
        Icon(Icons.Filled.ChevronRight, null, tint = TextHint, modifier = Modifier.size(20.dp))
    }
}

@Composable
private fun InfoTextRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 12.sp, color = TextHint)
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextBody)
    }
}
