package com.bpkpad.siarsip.ui.screens.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bpkpad.siarsip.ui.theme.*
import kotlinx.coroutines.launch
import com.bpkpad.siarsip.ui.components.PemusnahanDrawerContent
import com.bpkpad.siarsip.ui.components.DrawerRoutes
import com.bpkpad.siarsip.ui.components.PemusnahanBottomBar

// ─────────────────────────────────────────────────────────────
//  DashboardScreen
// ──────────────Composable───────────────────────────────────────────────
@Composable

fun DashboardScreen(
    onNavigate: (String) -> Unit = {},
    onModuleClick: (String) -> Unit = {}
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.fillMaxWidth(0.86f),
                drawerContainerColor = CardWhite,
                drawerShape = RoundedCornerShape(0.dp)
            ) {
                PemusnahanDrawerContent(
                    currentRoute = DrawerRoutes.DASHBOARD,
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
                DashboardTopBar(
                    onMenuClick = {
                        scope.launch { drawerState.open() }
                    }
                )
            },
            bottomBar = {
                PemusnahanBottomBar(
                    currentRoute = DrawerRoutes.DASHBOARD,
                    onNavigate   = onNavigate
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
                GreetingCard()
                QuickStatsGrid()
                ModuleSection(onModuleClick)
                AlertSection()
                ActivitySection()
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  Top Bar
// ─────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DashboardTopBar(onMenuClick: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = "BPKPAD Balangan",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(Icons.Filled.Menu, contentDescription = "Menu", tint = Color.White)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = GreenPrimary
        )
    )
}

// ─────────────────────────────────────────────────────────────
//  Greeting Card
// ─────────────────────────────────────────────────────────────
@Composable
private fun GreetingCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        border = BorderStroke(1.dp, BorderGray)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Selamat datang,",
                    fontSize = 12.sp,
                    color = TextHint
                )
                Text(
                    text = "Ahmad Fauzi",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = GreenPrimary
                )
                Spacer(Modifier.height(6.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Badge Admin Arsip
                    Box(
                        modifier = Modifier
                            .background(GreenPrimary, RoundedCornerShape(20.dp))
                            .padding(horizontal = 9.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "Admin Arsip",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Text(
                        text = "• BPKPAD Balangan",
                        fontSize = 11.sp,
                        color = TextBody
                    )
                }
            }
            // Avatar
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(GreenPrimary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "AF",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  Quick Stats — 4 angka ringkasan
// ─────────────────────────────────────────────────────────────
@Composable
private fun QuickStatsGrid() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        StatPill("12.190", "TOTAL ARSIP",    CardWhite,  BorderGray,  TextHead,    Modifier.weight(1f))
        StatPill("238",    "USUL MUSNAH",    DangerBg,   Color(0xFFFECACA), DangerText,  Modifier.weight(1f))
        StatPill("350",    "DIPINJAM",       BlueBg,     Color(0xFFBFDBFE), BlueText,    Modifier.weight(1f))
        StatPill("45",     "PROSES AKTIF",   AmberBg,    Color(0xFFFDE68A), AmberText,   Modifier.weight(1f))
    }
}

@Composable
private fun StatPill(
    value: String,
    label: String,
    bgColor: Color,
    borderColor: Color,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = valueColor
            )
            Text(
                text = label,
                fontSize = 8.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextHint,
                letterSpacing = 0.3.sp
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  4 Module Cards
// ─────────────────────────────────────────────────────────────
@Composable
private fun ModuleSection(onModuleClick: (String) -> Unit) {
    SectionHeader(title = "Modul Arsip", linkText = "Kelola semua")

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ModuleCard(
                name       = "Dokumen Keuangan",
                icon       = Icons.Filled.AttachMoney,
                iconBg     = GreenLight,
                iconColor  = GreenPrimary,
                bigNumber  = "4.210",
                bigLabel   = "berkas",
                stat1Label = "Usul musnah",
                stat1Value = "142",
                stat1Color = DangerText,
                stat2Label = "Tambah bulan ini",
                stat2Value = "+28",
                stat2Color = GreenPrimary,
                onClick    = { onModuleClick("keuangan") }
            )
            ModuleCard(
                name       = "Pemusnahan Arsip",
                icon       = Icons.Filled.LocalFireDepartment,
                iconBg     = AmberBg,
                iconColor  = AmberText,
                bigNumber  = "238",
                bigLabel   = "menunggu",
                stat1Label = "Proses aktif",
                stat1Value = "12",
                stat1Color = AmberText,
                stat2Label = "Menunggu persetujuan",
                stat2Value = "5",
                stat2Color = AmberText,
                showAlert  = true,
                alertColor = AmberText,
                onClick    = { onModuleClick("pemusnahan") }
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ModuleCard(
                name       = "Dok. Non-Keuangan",
                icon       = Icons.Filled.Description,
                iconBg     = GreenLight,
                iconColor  = GreenPrimary,
                bigNumber  = "7.980",
                bigLabel   = "berkas",
                stat1Label = "Usul musnah",
                stat1Value = "96",
                stat1Color = DangerText,
                stat2Label = "Tambah bulan ini",
                stat2Value = "+45",
                stat2Color = GreenPrimary,
                onClick    = { onModuleClick("non_keuangan") }
            )
            ModuleCard(
                name       = "Peminjaman Arsip",
                icon       = Icons.Filled.AssignmentReturn,
                iconBg     = BlueBg,
                iconColor  = BlueText,
                bigNumber  = "350",
                bigLabel   = "aktif",
                stat1Label = "Terlambat",
                stat1Value = "8",
                stat1Color = DangerText,
                stat2Label = "Jatuh tempo",
                stat2Value = "12",
                stat2Color = AmberText,
                showAlert  = true,
                alertColor = DangerText,
                onClick    = { onModuleClick("peminjaman") }
            )
        }
    }
}

@Composable
private fun ModuleCard(
    name: String,
    icon: ImageVector,
    iconBg: Color,
    iconColor: Color,
    bigNumber: String,
    bigLabel: String,
    stat1Label: String,
    stat1Value: String,
    stat1Color: Color,
    stat2Label: String,
    stat2Value: String,
    stat2Color: Color,
    showAlert: Boolean = false,
    alertColor: Color  = DangerText,
    onClick: () -> Unit = {}
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderGray)
    ) {
        Box {
            Column(modifier = Modifier.padding(14.dp)) {
                // Header: icon + nama + panah
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .background(iconBg, RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(icon, contentDescription = null,
                            tint = iconColor, modifier = Modifier.size(18.dp))
                    }
                    Text(
                        text = name,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextHead,
                        modifier = Modifier.weight(1f),
                        lineHeight = 16.sp
                    )
                    Icon(
                        Icons.Filled.ChevronRight,
                        contentDescription = null,
                        tint = TextHint,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Spacer(Modifier.height(10.dp))

                // Angka besar
                Text(bigNumber, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextHead)
                Text(bigLabel, fontSize = 10.sp, color = TextHint)

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 9.dp),
                    thickness = 0.5.dp, color = BorderGray
                )

                // 2 statistik kecil
                StatRow(stat1Label, stat1Value, stat1Color)
                Spacer(Modifier.height(5.dp))
                StatRow(stat2Label, stat2Value, stat2Color)
            }

            // Alert dot
            if (showAlert) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .align(Alignment.TopEnd)
                        .offset(x = (-10).dp, y = 10.dp)
                        .background(alertColor, CircleShape)
                )
            }
        }
    }
}

@Composable
private fun StatRow(label: String, value: String, valueColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 10.sp, color = TextHint)
        Text(value, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = valueColor)
    }
}

// ─────────────────────────────────────────────────────────────
//  Perlu Perhatian
// ─────────────────────────────────────────────────────────────
@Composable
private fun AlertSection() {
    SectionHeader(title = "Perlu Perhatian", linkText = "Lihat semua")
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderGray)
    ) {
        Column {
            AlertRow(
                iconBg    = DangerBg,
                iconColor = DangerText,
                icon      = Icons.Filled.Block,
                title     = "SP2D Gaji Dinas Pendidikan 2018",
                subtitle  = "Peminjaman oleh BPK - 2 hari lewat",
                badgeText = "Terlambat",
                badgeBg   = DangerBg,
                badgeText2Color = DangerText,
                showDivider = true
            )
            AlertRow(
                iconBg    = AmberBg,
                iconColor = AmberText,
                icon      = Icons.Filled.Timer,
                title     = "Usulan Pemusnahan Batch #042",
                subtitle  = "Menunggu TTD Kepala Dinas",
                badgeText = "Menunggu",
                badgeBg   = AmberBg,
                badgeText2Color = AmberText,
                showDivider = false
            )
        }
    }
}

@Composable
private fun AlertRow(
    iconBg: Color,
    iconColor: Color,
    icon: ImageVector,
    title: String,
    subtitle: String,
    badgeText: String,
    badgeBg: Color,
    badgeText2Color: Color,
    showDivider: Boolean
) {
    Column {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .background(iconBg, RoundedCornerShape(9.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null,
                    tint = iconColor, modifier = Modifier.size(16.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextHead)
                Text(subtitle, fontSize = 11.sp, color = TextBody, modifier = Modifier.padding(top = 2.dp))
            }
            Box(
                modifier = Modifier
                    .background(badgeBg, RoundedCornerShape(20.dp))
                    .padding(horizontal = 9.dp, vertical = 3.dp)
            ) {
                Text(badgeText, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = badgeText2Color)
            }
        }
        if (showDivider) HorizontalDivider(thickness = 0.5.dp, color = BorderGray)
    }
}

// ─────────────────────────────────────────────────────────────
//  Aktivitas Terbaru
// ─────────────────────────────────────────────────────────────
@Composable
private fun ActivitySection() {
    SectionHeader(title = "Aktivitas terbaru", linkText = "Lihat semua")
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderGray)
    ) {
        Column {
            ActivityRow("Laporan Keuangan Q1 2025 ditambahkan",  "10 menit lalu", GreenPrimary, "Keuangan",     GreenLight,  GreenPrimary, true)
            ActivityRow("Berkas BUM-2025-003 dikirim ke Bupati", "1 jam lalu",    AmberText,   "Pemusnahan",   AmberBg,     AmberText,    true)
            ActivityRow("SK Kepala Bidang 2019 jatuh tempo",     "2 jam lalu",    DangerText,  "Peminjaman",   PurpleBg,    PurpleText,   true)
            ActivityRow("Perda No. 3 Tahun 2020 dipinjam",       "Kemarin, 14.30",BlueText,    "Non-Keuangan", BlueBg,      BlueText,     false)
        }
    }
}

@Composable
private fun ActivityRow(
    title: String,
    time: String,
    dotColor: Color,
    tagText: String,
    tagBg: Color,
    tagColor: Color,
    showDivider: Boolean
) {
    Column {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(dotColor, CircleShape)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    color = TextHead,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(time, fontSize = 10.sp, color = TextHint, modifier = Modifier.padding(top = 1.dp))
            }
            Box(
                modifier = Modifier
                    .background(tagBg, RoundedCornerShape(20.dp))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(tagText, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = tagColor)
            }
        }
        if (showDivider) HorizontalDivider(thickness = 0.5.dp, color = BorderGray)
    }
}

// ─────────────────────────────────────────────────────────────
//  Bottom Navigation Bar
// ─────────────────────────────────────────────────────────────
@Composable
private fun DashboardBottomBar() {
    NavigationBar(
        containerColor = CardWhite,
        tonalElevation = 0.dp,
        modifier = Modifier
            .clip(RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp))
    ) {
        NavigationBarItem(
            selected = true,
            onClick = {},
            icon = { Icon(Icons.Filled.Home, contentDescription = null) },
            label = { Text("Beranda", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor   = GreenPrimary,
                selectedTextColor   = GreenPrimary,
                indicatorColor      = GreenLight,
                unselectedIconColor = TextHint,
                unselectedTextColor = TextHint
            )
        )
        NavigationBarItem(
            selected = false,
            onClick = {},
            icon = { Icon(Icons.Filled.Folder, contentDescription = null) },
            label = { Text("Arsip", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor   = GreenPrimary,
                unselectedIconColor = TextHint,
                unselectedTextColor = TextHint
            )
        )

        NavigationBarItem(
            selected = false,
            onClick = {},
            icon = { Icon(Icons.Filled.History, contentDescription = null) },
            label = { Text("Aktivitas", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = TextHint,
                unselectedTextColor = TextHint
            )
        )
        NavigationBarItem(
            selected = false,
            onClick = {},
            icon = { Icon(Icons.Filled.Person, contentDescription = null) },
            label = { Text("Profil", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = TextHint,
                unselectedTextColor = TextHint
            )
        )
    }
}

// ─────────────────────────────────────────────────────────────
//  Reusable Section Header
// ─────────────────────────────────────────────────────────────
@Composable
private fun SectionHeader(title: String, linkText: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextHead)
        Text(linkText, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = GreenPrimary)
    }
}

// ─────────────────────────────────────────────────────────────
//  Preview
// ─────────────────────────────────────────────────────────────
@Preview(showBackground = true, widthDp = 375, heightDp = 812)
@Composable
fun DashboardPreview() {
    DashboardScreen()
}