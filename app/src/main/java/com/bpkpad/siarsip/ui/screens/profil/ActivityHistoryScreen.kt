package com.bpkpad.siarsip.ui.screens.profile

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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bpkpad.siarsip.ui.theme.*

// ─────────────────────────────────────────────────────────────
//  Dummy data
// ─────────────────────────────────────────────────────────────
private data class AhActivityItem(
    val icon: ImageVector,
    val iconBg: Color,
    val iconTint: Color,
    val title: String,
    val description: String,
    val timestamp: String,
    val badgeText: String,
    val badgeBg: Color,
    val badgeTextColor: Color
)

private val ahDummyList = listOf(
    AhActivityItem(
        icon          = Icons.Filled.UploadFile,
        iconBg        = GreenLight,
        iconTint      = GreenPrimary,
        title         = "Pengajuan Usul Musnah",
        description   = "Berkas arsip periode 2019 diajukan untuk pemusnahan",
        timestamp     = "Hari ini, 09:42",
        badgeText     = "Diajukan",
        badgeBg       = GreenLight,
        badgeTextColor = GreenPrimary
    ),
    AhActivityItem(
        icon          = Icons.Filled.CheckCircle,
        iconBg        = GreenLight,
        iconTint      = GreenPrimary,
        title         = "Persetujuan Berita Acara",
        description   = "BA Pemusnahan No. BA-2024-011 telah disetujui",
        timestamp     = "Kemarin, 14:15",
        badgeText     = "Disetujui",
        badgeBg       = GreenLight,
        badgeTextColor = GreenPrimary
    ),
    AhActivityItem(
        icon          = Icons.Filled.HourglassTop,
        iconBg        = AmberBg,
        iconTint      = AmberText,
        title         = "Menunggu Verifikasi",
        description   = "Daftar arsip periode 2018 menunggu persetujuan atasan",
        timestamp     = "28 Jun 2026, 10:00",
        badgeText     = "Pending",
        badgeBg       = AmberBg,
        badgeTextColor = AmberText
    ),
    AhActivityItem(
        icon          = Icons.Filled.Delete,
        iconBg        = DangerBg,
        iconTint      = DangerText,
        title         = "Pemusnahan Arsip",
        description   = "12 berkas arsip periode 2017 telah dimusnahkan",
        timestamp     = "27 Jun 2026, 11:30",
        badgeText     = "Selesai",
        badgeBg       = GreenLight,
        badgeTextColor = GreenPrimary
    ),
    AhActivityItem(
        icon          = Icons.Filled.Search,
        iconBg        = BlueBg,
        iconTint      = BlueText,
        title         = "Pencarian Arsip",
        description   = "Melakukan pencarian arsip keuangan 2016",
        timestamp     = "26 Jun 2026, 08:55",
        badgeText     = "Ditemukan",
        badgeBg       = BlueBg,
        badgeTextColor = BlueText
    ),
    AhActivityItem(
        icon          = Icons.Filled.Description,
        iconBg        = PurpleBg,
        iconTint      = PurpleText,
        title         = "Cetak Daftar Arsip",
        description   = "Laporan daftar arsip periode Q2 2026 dicetak",
        timestamp     = "25 Jun 2026, 15:20",
        badgeText     = "Dicetak",
        badgeBg       = PurpleBg,
        badgeTextColor = PurpleText
    ),
    AhActivityItem(
        icon          = Icons.Filled.Login,
        iconBg        = GreenLight,
        iconTint      = GreenPrimary,
        title         = "Login ke Sistem",
        description   = "Masuk ke SIARSIP melalui perangkat mobile",
        timestamp     = "25 Jun 2026, 07:48",
        badgeText     = "Berhasil",
        badgeBg       = GreenLight,
        badgeTextColor = GreenPrimary
    )
)

private val ahFilterTabs = listOf("Semua", "Arsip", "Pemusnahan", "Lainnya")

// ─────────────────────────────────────────────────────────────
//  Screen
// ─────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityHistoryScreen(
    onBack: () -> Unit = {}
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        containerColor = BgDashboard,
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text(
                            "Activity History",
                            fontSize   = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color      = TextHead
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                Icons.Filled.ArrowBack,
                                contentDescription = "Kembali",
                                tint = GreenPrimary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = CardWhite
                    )
                )
                HorizontalDivider(thickness = 0.5.dp, color = BorderGray)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // ── Summary stats ─────────────────────────────────────
            Row(
                modifier              = Modifier
                    .fillMaxWidth()
                    .background(CardWhite)
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AhStatCard(
                    label     = "Total Aktivitas",
                    value     = "47",
                    bg        = GreenLight,
                    textColor = GreenPrimary,
                    modifier  = Modifier.weight(1f)
                )
                AhStatCard(
                    label     = "Bulan Ini",
                    value     = "12",
                    bg        = BlueBg,
                    textColor = BlueText,
                    modifier  = Modifier.weight(1f)
                )
                AhStatCard(
                    label     = "Pending",
                    value     = "3",
                    bg        = AmberBg,
                    textColor = AmberText,
                    modifier  = Modifier.weight(1f)
                )
            }
            HorizontalDivider(thickness = 0.5.dp, color = BorderGray)

            // ── Filter chips ──────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CardWhite)
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ahFilterTabs.forEachIndexed { index, label ->
                    AhFilterChip(
                        label    = label,
                        selected = selectedTab == index,
                        onClick  = { selectedTab = index }
                    )
                }
            }
            HorizontalDivider(thickness = 0.5.dp, color = BorderGray)

            // ── Activity list ─────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(Modifier.height(16.dp))

                Text(
                    text       = "Riwayat Terbaru",
                    fontSize   = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color      = TextHead,
                    modifier   = Modifier.padding(bottom = 12.dp)
                )

                ahDummyList.forEachIndexed { index, item ->
                    AhActivityCard(item = item)
                    if (index < ahDummyList.lastIndex) {
                        Spacer(Modifier.height(10.dp))
                    }
                }

                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  Internal composables
// ─────────────────────────────────────────────────────────────

@Composable
private fun AhStatCard(
    label: String,
    value: String,
    bg: Color,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape    = RoundedCornerShape(12.dp),
        colors   = CardDefaults.cardColors(containerColor = bg),
        border   = BorderStroke(1.dp, BorderGray)
    ) {
        Column(
            modifier            = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text       = value,
                fontSize   = 22.sp,
                fontWeight = FontWeight.Bold,
                color      = textColor
            )
            Text(
                text       = label,
                fontSize   = 10.sp,
                color      = textColor,
                lineHeight = 13.sp
            )
        }
    }
}

@Composable
private fun AhFilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick  = onClick,
        label    = {
            Text(
                text       = label,
                fontSize   = 12.sp,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
            )
        },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = GreenPrimary,
            selectedLabelColor     = Color.White,
            containerColor         = CardWhite,
            labelColor             = TextHint
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled             = true,
            selected            = selected,
            borderColor         = BorderGray,
            selectedBorderColor = GreenPrimary,
            borderWidth         = 1.dp,
            selectedBorderWidth = 1.dp
        ),
        shape = RoundedCornerShape(9999.dp)
    )
}

@Composable
private fun AhActivityCard(item: AhActivityItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(16.dp),
        colors   = CardDefaults.cardColors(containerColor = CardWhite),
        border   = BorderStroke(1.dp, BorderGray)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment     = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(item.iconBg, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    item.icon,
                    contentDescription = null,
                    tint     = item.iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Content
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Text(
                        text       = item.title,
                        fontSize   = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color      = TextHead,
                        modifier   = Modifier.weight(1f, fill = false)
                    )
                    Spacer(Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .background(item.badgeBg, RoundedCornerShape(9999.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text       = item.badgeText,
                            fontSize   = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color      = item.badgeTextColor
                        )
                    }
                }
                Text(
                    text       = item.description,
                    fontSize   = 11.sp,
                    color      = TextBody,
                    lineHeight = 15.sp,
                    modifier   = Modifier.padding(top = 4.dp)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier          = Modifier.padding(top = 6.dp)
                ) {
                    Icon(
                        Icons.Filled.AccessTime,
                        contentDescription = null,
                        tint     = TextHint,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text     = item.timestamp,
                        fontSize = 11.sp,
                        color    = TextHint
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  Preview
// ─────────────────────────────────────────────────────────────
@Preview(showBackground = true, widthDp = 375, heightDp = 812)
@Composable
fun ActivityHistoryScreenPreview() {
    ActivityHistoryScreen()
}
