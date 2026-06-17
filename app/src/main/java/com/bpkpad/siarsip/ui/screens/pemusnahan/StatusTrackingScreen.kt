package com.bpkpad.siarsip.ui.screens.pemusnahan

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bpkpad.siarsip.ui.components.DrawerRoutes
import com.bpkpad.siarsip.ui.components.PemusnahanDrawerContent
import com.bpkpad.siarsip.ui.theme.*
import kotlinx.coroutines.launch

// ─────────────────────────────────────────────────────────────
//  Data Model — Tahapan tracking
// ─────────────────────────────────────────────────────────────
enum class StageStatus { DONE, ACTIVE, PENDING, REJECTED }

data class TrackingStage(
    val name: String,
    val description: String,
    val person: String,
    val date: String,
    val status: StageStatus
)

data class TrackingBerkas(
    val nomor: String,
    val perihal: String,
    val sumber: String,
    val arsipCount: Int,
    val stages: List<TrackingStage>
) {
    val currentStageIndex: Int
        get() = stages.indexOfFirst { it.status == StageStatus.ACTIVE }
            .takeIf { it >= 0 } ?: stages.indexOfLast { it.status == StageStatus.DONE }

    val overallStatus: String
        get() = when {
            stages.any { it.status == StageStatus.REJECTED } -> "Ditolak"
            stages.all { it.status == StageStatus.DONE }     -> "Selesai"
            stages.any { it.status == StageStatus.ACTIVE }   -> "Diproses"
            else                                              -> "Diajukan"
        }
}

// ─────────────────────────────────────────────────────────────
//  Dummy Data
// ─────────────────────────────────────────────────────────────
val dummyTrackingList = listOf(
    TrackingBerkas(
        nomor      = "BUM-2025-003",
        perihal    = "Pemusnahan arsip keuangan tahun 2016 yang telah habis retensi",
        sumber     = "Keuangan",
        arsipCount = 6,
        stages = listOf(
            TrackingStage("Berkas Dibuat", "Draft berkas usul musnah diinput",
                "Ahmad Fauzi", "12 Mei 2025", StageStatus.DONE),
            TrackingStage("Diajukan ke Tim Penilai", "Berkas dikirim untuk dinilai",
                "Ahmad Fauzi", "13 Mei 2025", StageStatus.DONE),
            TrackingStage("Penilaian Tim", "Sedang dinilai oleh Tim Penilai",
                "Tim Penilai BPKPAD", "15 Mei 2025", StageStatus.ACTIVE),
            TrackingStage("Pengiriman ke Kepala Daerah", "Disetujui dan dikirim",
                "—", "—", StageStatus.PENDING),
            TrackingStage("Pemusnahan & Berita Acara", "Pelaksanaan pemusnahan arsip",
                "—", "—", StageStatus.PENDING)
        )
    ),
    TrackingBerkas(
        nomor      = "BUM-2025-002",
        perihal    = "Pemusnahan SK kepegawaian tahun 2018",
        sumber     = "Non-Keuangan",
        arsipCount = 12,
        stages = listOf(
            TrackingStage("Berkas Dibuat", "Draft berkas usul musnah diinput",
                "Ahmad Fauzi", "8 Mei 2025", StageStatus.DONE),
            TrackingStage("Diajukan ke Tim Penilai", "Berkas dikirim untuk dinilai",
                "Ahmad Fauzi", "9 Mei 2025", StageStatus.DONE),
            TrackingStage("Penilaian Tim", "Tim Penilai setuju usul musnah",
                "Tim Penilai BPKPAD", "11 Mei 2025", StageStatus.DONE),
            TrackingStage("Pengiriman ke Kepala Daerah", "Menunggu persetujuan Bupati",
                "Bupati Balangan", "12 Mei 2025", StageStatus.ACTIVE),
            TrackingStage("Pemusnahan & Berita Acara", "Pelaksanaan pemusnahan arsip",
                "—", "—", StageStatus.PENDING)
        )
    ),
    TrackingBerkas(
        nomor      = "BUM-2025-001",
        perihal    = "Pemusnahan dokumen peminjaman 2017",
        sumber     = "Peminjaman",
        arsipCount = 4,
        stages = listOf(
            TrackingStage("Berkas Dibuat", "Draft berkas usul musnah diinput",
                "Ahmad Fauzi", "1 Apr 2025", StageStatus.DONE),
            TrackingStage("Diajukan ke Tim Penilai", "Berkas dikirim untuk dinilai",
                "Ahmad Fauzi", "2 Apr 2025", StageStatus.DONE),
            TrackingStage("Penilaian Tim", "Tim Penilai setuju usul musnah",
                "Tim Penilai BPKPAD", "5 Apr 2025", StageStatus.DONE),
            TrackingStage("Pengiriman ke Kepala Daerah", "Disetujui oleh Bupati",
                "Bupati Balangan", "10 Apr 2025", StageStatus.DONE),
            TrackingStage("Pemusnahan & Berita Acara", "Pemusnahan selesai dilaksanakan",
                "Tim Pemusnahan", "15 Apr 2025", StageStatus.DONE)
        )
    )
)

// ─────────────────────────────────────────────────────────────
//  Screen Utama
// ─────────────────────────────────────────────────────────────
@Composable
fun StatusTrackingScreen(
    onNavigate: (String) -> Unit = {},
    onCatatBalasan: (TrackingBerkas) -> Unit = {}
) {
    var activeFilter   by remember { mutableStateOf("Semua") }
    var expandedNomor  by remember { mutableStateOf<String?>(null) }

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope       = rememberCoroutineScope()

    val filters = listOf("Semua", "Diproses", "Selesai", "Ditolak")

    val filteredList = dummyTrackingList.filter { berkas ->
        activeFilter == "Semua" || berkas.overallStatus == activeFilter
    }
    val countDiproses = dummyTrackingList.count { it.overallStatus == "Diproses" }
    val countSelesai  = dummyTrackingList.count { it.overallStatus == "Selesai" }

    // ── Drawer wrapper ────────────────────────────────────────
    ModalNavigationDrawer(
        drawerState   = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier             = Modifier.fillMaxWidth(0.86f),
                drawerContainerColor = CardWhite,
                drawerShape          = RoundedCornerShape(0.dp)
            ) {
                PemusnahanDrawerContent(
                    currentRoute = DrawerRoutes.TRACKING,
                    onNavigate   = { route ->
                        scope.launch { drawerState.close() }
                        onNavigate(route)
                    },
                    onLogout = { scope.launch { drawerState.close() } }
                )
            }
        }
    ) {
        Scaffold(
            containerColor = BgDashboard,
            topBar = {
                TrackingTopBar(
                    onMenuClick = { scope.launch { drawerState.open() } }
                )
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(
                    start  = 16.dp,
                    end    = 16.dp,
                    top    = 14.dp,
                    bottom = 16.dp
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                // ── Stats summary ────────────────────────────
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        TrackingStatBox(
                            value    = "${dummyTrackingList.size}",
                            label    = "Total Berkas",
                            bgColor  = CardWhite,
                            color    = TextHead,
                            modifier = Modifier.weight(1f)
                        )
                        TrackingStatBox(
                            value    = "$countDiproses",
                            label    = "Sedang Diproses",
                            bgColor  = AmberBg,
                            color    = AmberText,
                            modifier = Modifier.weight(1f)
                        )
                        TrackingStatBox(
                            value    = "$countSelesai",
                            label    = "Selesai",
                            bgColor  = GreenLight,
                            color    = GreenPrimary,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // ── Filter chips ──────────────────────────────
                item {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                        items(filters) { filter ->
                            TrackingFilterChip(
                                label    = filter,
                                isActive = activeFilter == filter,
                                onClick  = {
                                    activeFilter  = filter
                                    expandedNomor = null
                                }
                            )
                        }
                    }
                }

                // ── List of tracking berkas ───────────────────
                items(filteredList, key = { it.nomor }) { berkas ->
                    val isExpanded = expandedNomor == berkas.nomor
                    TrackingCard(
                        berkas         = berkas,
                        isExpanded     = isExpanded,
                        onToggle       = {
                            expandedNomor = if (isExpanded) null else berkas.nomor
                        },
                        onCatatBalasan = { onCatatBalasan(berkas) }
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  Top Bar
// ─────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TrackingTopBar(onMenuClick: () -> Unit) {
    TopAppBar(
        title = {
            Column {
                Text("Status & Tracking",
                    fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text("Pantau proses berkas usul musnah",
                    fontSize = 10.sp, color = Color.White.copy(alpha = 0.6f))
            }
        },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(Icons.Filled.Menu, "Buka menu", tint = Color.White)
            }
        },
        actions = {
            IconButton(onClick = {}) {
                Icon(Icons.Filled.Search, "Cari", tint = Color.White)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = GreenPrimary)
    )
}

// ─────────────────────────────────────────────────────────────
//  Stat Box
// ─────────────────────────────────────────────────────────────
@Composable
private fun TrackingStatBox(
    value: String,
    label: String,
    bgColor: Color,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(bgColor, RoundedCornerShape(12.dp))
            .border(1.dp, BorderGray, RoundedCornerShape(12.dp))
            .padding(vertical = 12.dp, horizontal = 10.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()) {
            Text(value,
                fontSize   = 18.sp,
                fontWeight = FontWeight.Bold,
                color      = color)
            Text(label,
                fontSize   = 9.sp,
                fontWeight = FontWeight.SemiBold,
                color      = TextHint,
                letterSpacing = 0.3.sp,
                modifier   = Modifier.padding(top = 2.dp))
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  Filter Chip
// ─────────────────────────────────────────────────────────────
@Composable
private fun TrackingFilterChip(
    label: String,
    isActive: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .background(
                if (isActive) GreenPrimary else CardWhite,
                RoundedCornerShape(9999.dp)
            )
            .border(
                1.5.dp,
                if (isActive) GreenPrimary else BorderGray,
                RoundedCornerShape(9999.dp)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication        = null,
                onClick           = onClick
            )
            .padding(horizontal = 15.dp, vertical = 7.dp)
    ) {
        Text(label,
            fontSize   = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color      = if (isActive) Color.White else TextBody)
    }
}

// ─────────────────────────────────────────────────────────────
//  Tracking Card — header + horizontal progress + expandable timeline
// ─────────────────────────────────────────────────────────────
@Composable
private fun TrackingCard(
    berkas: TrackingBerkas,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    onCatatBalasan: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(16.dp),
        colors   = CardDefaults.cardColors(containerColor = CardWhite),
        border   = BorderStroke(1.dp, BorderGray)
    ) {
        Column {
            // ── Header ───────────────────────────────────────
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .background(GreenLight, RoundedCornerShape(9999.dp))
                            .padding(horizontal = 9.dp, vertical = 3.dp)
                    ) {
                        Text(berkas.nomor,
                            fontSize   = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color      = GreenPrimary,
                            letterSpacing = 0.3.sp)
                    }
                    OverallStatusBadge(status = berkas.overallStatus)
                }

                Spacer(Modifier.height(8.dp))

                Text(berkas.perihal,
                    fontSize   = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = TextHead,
                    lineHeight = 18.sp,
                    maxLines   = 2,
                    overflow   = TextOverflow.Ellipsis)

                Spacer(Modifier.height(7.dp))

                // Meta info
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MiniMetaChip(Icons.Filled.Category, berkas.sumber)
                    MiniMetaChip(Icons.Filled.Inventory2, "${berkas.arsipCount} arsip")
                }
            }

            HorizontalDivider(thickness = 0.5.dp, color = BorderGray)

            // ── Mini horizontal progress ─────────────────────
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "TAHAPAN PROSES",
                    fontSize      = 9.sp,
                    fontWeight    = FontWeight.Bold,
                    color         = TextHint,
                    letterSpacing = 0.8.sp,
                    modifier      = Modifier.padding(bottom = 10.dp)
                )

                MiniProgressBar(stages = berkas.stages)

                Spacer(Modifier.height(12.dp))

                // Current stage label
                val currentStage = berkas.stages.getOrNull(berkas.currentStageIndex)
                if (currentStage != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = when (currentStage.status) {
                                    StageStatus.ACTIVE   -> AmberBg
                                    StageStatus.DONE     -> GreenLight
                                    StageStatus.REJECTED -> DangerBg
                                    else                 -> Color(0xFFF3F5F3)
                                },
                                shape = RoundedCornerShape(10.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 9.dp),
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            when (currentStage.status) {
                                StageStatus.DONE     -> Icons.Filled.CheckCircle
                                StageStatus.ACTIVE   -> Icons.Filled.HourglassEmpty
                                StageStatus.REJECTED -> Icons.Filled.Cancel
                                else                 -> Icons.Filled.MoreHoriz
                            },
                            null,
                            tint = when (currentStage.status) {
                                StageStatus.DONE     -> GreenPrimary
                                StageStatus.ACTIVE   -> AmberText
                                StageStatus.REJECTED -> DangerText
                                else                 -> TextHint
                            },
                            modifier = Modifier.size(16.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Tahap ${berkas.currentStageIndex + 1} dari ${berkas.stages.size}",
                                fontSize   = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color      = TextHint,
                                letterSpacing = 0.3.sp
                            )
                            Text(currentStage.name,
                                fontSize   = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color      = TextHead,
                                modifier   = Modifier.padding(top = 1.dp))
                        }
                    }
                }
            }

            // ── Expand toggle row ────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (isExpanded) Color(0xFFF1F8F1) else CardWhite)
                    .border(0.5.dp, BorderGray)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication        = null,
                        onClick           = onToggle
                    )
                    .padding(horizontal = 16.dp, vertical = 11.dp),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    Icon(Icons.Filled.Timeline, null,
                        tint     = if (isExpanded) GreenPrimary else TextHint,
                        modifier = Modifier.size(15.dp))
                    Text(
                        if (isExpanded) "Sembunyikan timeline" else "Lihat detail timeline",
                        fontSize   = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color      = if (isExpanded) GreenPrimary else TextBody
                    )
                }
                Icon(
                    if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    null,
                    tint     = if (isExpanded) GreenPrimary else TextHint,
                    modifier = Modifier.size(20.dp)
                )
            }

            // ── Expandable vertical timeline ─────────────────
            AnimatedVisibility(
                visible = isExpanded,
                enter   = expandVertically() + fadeIn(),
                exit    = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF1F8F1))
                        .padding(16.dp)
                ) {
                    berkas.stages.forEachIndexed { index, stage ->
                        VerticalTimelineItem(
                            stage     = stage,
                            isLast    = index == berkas.stages.lastIndex
                        )
                    }

                    // Tombol catat balasan
                    if (berkas.stages.any { it.status == StageStatus.ACTIVE }) {
                        Spacer(Modifier.height(8.dp))
                        Button(
                            onClick  = onCatatBalasan,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp),
                            shape  = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = GreenPrimary,
                                contentColor   = Color.White
                            )
                        ) {
                            Icon(Icons.Filled.Edit, null, modifier = Modifier.size(15.dp))
                            Spacer(Modifier.width(7.dp))
                            Text("Catat Balasan / Update Status",
                                fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  Mini Progress Bar (5 dots horizontal)
// ─────────────────────────────────────────────────────────────
@Composable
private fun MiniProgressBar(stages: List<TrackingStage>) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        stages.forEachIndexed { index, stage ->
            // Dot
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(
                        color = when (stage.status) {
                            StageStatus.DONE     -> GreenPrimary
                            StageStatus.ACTIVE   -> AmberText
                            StageStatus.REJECTED -> DangerText
                            StageStatus.PENDING  -> Color(0xFFE0E4E0)
                        },
                        shape = CircleShape
                    )
                    .border(
                        width = if (stage.status == StageStatus.ACTIVE) 3.dp else 0.dp,
                        color = AmberBg,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                when (stage.status) {
                    StageStatus.DONE -> Icon(
                        Icons.Filled.Check, null,
                        tint = Color.White, modifier = Modifier.size(14.dp))
                    StageStatus.ACTIVE -> Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(Color.White, CircleShape))
                    StageStatus.REJECTED -> Icon(
                        Icons.Filled.Close, null,
                        tint = Color.White, modifier = Modifier.size(14.dp))
                    StageStatus.PENDING -> Text("${index + 1}",
                        fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextHint)
                }
            }

            // Connector line (kecuali terakhir)
            if (index != stages.lastIndex) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(2.dp)
                        .background(
                            when {
                                stage.status == StageStatus.DONE -> GreenPrimary
                                else -> Color(0xFFE0E4E0)
                            }
                        )
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  Vertical Timeline Item (untuk expanded view)
// ─────────────────────────────────────────────────────────────
@Composable
private fun VerticalTimelineItem(
    stage: TrackingStage,
    isLast: Boolean
) {
    Row(modifier = Modifier.height(IntrinsicSize.Min)) {

        // ── Left: dot + connector line ────────────────────────
        Column(
            modifier            = Modifier.width(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .background(
                        color = when (stage.status) {
                            StageStatus.DONE     -> GreenPrimary
                            StageStatus.ACTIVE   -> AmberText
                            StageStatus.REJECTED -> DangerText
                            StageStatus.PENDING  -> CardWhite
                        },
                        shape = CircleShape
                    )
                    .border(
                        width = if (stage.status == StageStatus.PENDING) 1.5.dp else 0.dp,
                        color = BorderGray,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                when (stage.status) {
                    StageStatus.DONE -> Icon(
                        Icons.Filled.Check, null,
                        tint = Color.White, modifier = Modifier.size(13.dp))
                    StageStatus.ACTIVE -> Box(
                        modifier = Modifier
                            .size(7.dp)
                            .background(Color.White, CircleShape))
                    StageStatus.REJECTED -> Icon(
                        Icons.Filled.Close, null,
                        tint = Color.White, modifier = Modifier.size(13.dp))
                    StageStatus.PENDING -> Box(
                        modifier = Modifier
                            .size(7.dp)
                            .background(Color(0xFFE0E4E0), CircleShape))
                }
            }
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .weight(1f)
                        .background(
                            when (stage.status) {
                                StageStatus.DONE -> GreenPrimary
                                else             -> Color(0xFFE0E4E0)
                            }
                        )
                )
            }
        }

        Spacer(Modifier.width(12.dp))

        // ── Right: content ────────────────────────────────────
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = if (isLast) 0.dp else 14.dp)
        ) {
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier              = Modifier.fillMaxWidth()
            ) {
                Text(stage.name,
                    fontSize   = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color      = when (stage.status) {
                        StageStatus.PENDING -> TextHint
                        else                -> TextHead
                    },
                    modifier   = Modifier.weight(1f))
                StageStatusChip(status = stage.status)
            }
            Text(stage.description,
                fontSize   = 11.sp,
                color      = when (stage.status) {
                    StageStatus.PENDING -> TextHint
                    else                -> TextBody
                },
                lineHeight = 16.sp,
                modifier   = Modifier.padding(top = 3.dp))
            if (stage.person != "—") {
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    modifier              = Modifier.padding(top = 5.dp)
                ) {
                    Icon(Icons.Filled.Person, null,
                        tint = TextHint, modifier = Modifier.size(11.dp))
                    Text(stage.person, fontSize = 10.sp, color = TextHint)
                    Text("•", fontSize = 10.sp, color = TextHint)
                    Icon(Icons.Filled.CalendarToday, null,
                        tint = TextHint, modifier = Modifier.size(10.dp))
                    Text(stage.date, fontSize = 10.sp, color = TextHint)
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  Stage Status Chip (kecil, di kanan timeline item)
// ─────────────────────────────────────────────────────────────
@Composable
private fun StageStatusChip(status: StageStatus) {
    val (label, bg, color) = when (status) {
        StageStatus.DONE     -> Triple("Selesai", GreenLight, GreenPrimary)
        StageStatus.ACTIVE   -> Triple("Aktif",   AmberBg,    AmberText)
        StageStatus.REJECTED -> Triple("Ditolak", DangerBg,   DangerText)
        StageStatus.PENDING  -> Triple("Menunggu",Color(0xFFF3F5F3), TextHint)
    }
    Box(
        modifier = Modifier
            .background(bg, RoundedCornerShape(9999.dp))
            .padding(horizontal = 7.dp, vertical = 2.dp)
    ) {
        Text(label,
            fontSize   = 9.sp,
            fontWeight = FontWeight.Bold,
            color      = color)
    }
}

// ─────────────────────────────────────────────────────────────
//  Overall Status Badge (di header card)
// ─────────────────────────────────────────────────────────────
@Composable
private fun OverallStatusBadge(status: String) {
    val (bg, color, icon) = when (status) {
        "Selesai"  -> Triple(GreenLight, GreenPrimary, Icons.Filled.TaskAlt)
        "Diproses" -> Triple(AmberBg,    AmberText,    Icons.Filled.HourglassEmpty)
        "Ditolak"  -> Triple(DangerBg,   DangerText,   Icons.Filled.Cancel)
        else       -> Triple(BlueBg,     BlueText,     Icons.Filled.Send)
    }
    Row(
        modifier = Modifier
            .background(bg, RoundedCornerShape(9999.dp))
            .padding(horizontal = 9.dp, vertical = 4.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(icon, null, tint = color, modifier = Modifier.size(12.dp))
        Text(status,
            fontSize   = 10.sp,
            fontWeight = FontWeight.Bold,
            color      = color)
    }
}

// ─────────────────────────────────────────────────────────────
//  Mini Meta Chip (sumber, jumlah arsip)
// ─────────────────────────────────────────────────────────────
@Composable
private fun MiniMetaChip(icon: ImageVector, text: String) {
    Row(
        modifier = Modifier
            .background(Color(0xFFF3F5F3), RoundedCornerShape(9999.dp))
            .border(0.5.dp, BorderGray, RoundedCornerShape(9999.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(icon, null, tint = TextHint, modifier = Modifier.size(11.dp))
        Text(text, fontSize = 10.sp, color = TextBody, fontWeight = FontWeight.Medium)
    }
}

// ─────────────────────────────────────────────────────────────
//  Preview
// ─────────────────────────────────────────────────────────────
@Preview(showBackground = true, widthDp = 375, heightDp = 812)
@Composable
fun StatusTrackingPreview() {
    StatusTrackingScreen()
}