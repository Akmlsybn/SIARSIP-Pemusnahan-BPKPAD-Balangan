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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bpkpad.siarsip.ui.components.DrawerRoutes
import com.bpkpad.siarsip.ui.components.PemusnahanDrawerContent
import com.bpkpad.siarsip.ui.theme.*
import kotlinx.coroutines.launch

// ─────────────────────────────────────────────────────────────
//  Data Model
// ─────────────────────────────────────────────────────────────
enum class LogCategory(
    val label: String,
    val color: Color,
    val bgColor: Color,
    val icon: ImageVector
) {
    BERKAS(    "Berkas",    BlueText,    BlueBg,    Icons.Filled.FolderOpen),
    PENILAIAN( "Penilaian", AmberText,   AmberBg,   Icons.Filled.RateReview),
    DISETUJUI( "Disetujui", GreenPrimary, GreenLight, Icons.Filled.CheckCircle),
    DITOLAK(   "Ditolak",   DangerText,  DangerBg,  Icons.Filled.Cancel),
    PEMUSNAHAN("Pemusnahan",AmberText,   AmberBg,   Icons.Filled.LocalFireDepartment),
    BERITA_ACARA("Berita Acara", GreenPrimary, GreenLight, Icons.Filled.Description),
    EKSPOR(    "Ekspor",    Color(0xFF6B21A8), Color(0xFFF3E8FF), Icons.Filled.FileDownload),
    SISTEM(    "Sistem",    Color(0xFF454D47), Color(0xFFF3F5F3), Icons.Filled.Settings)
}

data class LogEntry(
    val id: Int,
    val time: String,            // "14:30"
    val dateGroup: String,       // "Hari Ini", "Kemarin", tanggal
    val sortKey: Int,            // untuk grouping
    val category: LogCategory,
    val title: String,
    val description: String,
    val relatedBerkas: String?,
    val person: String,
    val role: String,
    val ipAddress: String        // untuk audit
)

// ─────────────────────────────────────────────────────────────
//  Dummy Data
// ─────────────────────────────────────────────────────────────
val dummyLogList = listOf(
    LogEntry(1, "14:30", "Hari Ini", 0, LogCategory.BERKAS,
        "Berkas usul musnah diajukan",
        "Berkas BUM-2025-003 dikirim ke Tim Penilai untuk dinilai",
        "BUM-2025-003", "Ahmad Fauzi", "Admin Arsip", "192.168.1.45"),
    LogEntry(2, "13:15", "Hari Ini", 0, LogCategory.PENILAIAN,
        "Tim Penilai menyetujui usul",
        "Tim Penilai menyetujui usul pemusnahan BUM-2025-002",
        "BUM-2025-002", "Drs. Bambang S.", "Ketua Tim Penilai", "192.168.1.20"),
    LogEntry(3, "11:00", "Hari Ini", 0, LogCategory.BERITA_ACARA,
        "Berita Acara dibuat",
        "BA/PMS/2025/003 berhasil dibuat untuk berkas BUM-2025-001",
        "BUM-2025-001", "Ahmad Fauzi", "Admin Arsip", "192.168.1.45"),
    LogEntry(4, "09:15", "Hari Ini", 0, LogCategory.EKSPOR,
        "PDF diunduh",
        "Berita Acara BA/PMS/2025/001 diunduh sebagai PDF",
        "BUM-2024-015", "Hj. Budi R.", "Kabid Aset", "192.168.1.32"),
    LogEntry(5, "08:30", "Hari Ini", 0, LogCategory.SISTEM,
        "Login ke sistem",
        "User berhasil login ke aplikasi SiARSIP",
        null, "Ahmad Fauzi", "Admin Arsip", "192.168.1.45"),

    LogEntry(6, "16:45", "Kemarin", 1, LogCategory.BERKAS,
        "Berkas baru dibuat",
        "Berkas BUM-2025-003 dibuat dengan 6 arsip dari modul Keuangan",
        "BUM-2025-003", "Ahmad Fauzi", "Admin Arsip", "192.168.1.45"),
    LogEntry(7, "15:20", "Kemarin", 1, LogCategory.DISETUJUI,
        "Bupati menyetujui pemusnahan",
        "Bupati Balangan menyetujui usul pemusnahan BUM-2024-015",
        "BUM-2024-015", "H. Abdul Hadi", "Bupati Balangan", "10.0.0.5"),
    LogEntry(8, "10:00", "Kemarin", 1, LogCategory.PEMUSNAHAN,
        "Pemusnahan arsip dilaksanakan",
        "Tim Pemusnah melaksanakan pemusnahan 24 arsip dengan metode Pembakaran",
        "BUM-2024-015", "Dedi Kurniawan", "Tim Pemusnah", "192.168.1.50"),

    LogEntry(9, "14:00", "10 Mei 2025", 2, LogCategory.PENILAIAN,
        "Tim Penilai meminta revisi",
        "Berkas BUM-2025-002 dikembalikan ke admin untuk perbaikan deskripsi",
        "BUM-2025-002", "Drs. Bambang S.", "Ketua Tim Penilai", "192.168.1.20"),
    LogEntry(10, "11:30", "10 Mei 2025", 2, LogCategory.BERKAS,
        "Arsip ditambahkan ke berkas",
        "3 arsip baru ditambahkan ke berkas BUM-2025-002",
        "BUM-2025-002", "Ahmad Fauzi", "Admin Arsip", "192.168.1.45"),

    LogEntry(11, "09:45", "8 Mei 2025", 3, LogCategory.DITOLAK,
        "Tim Penilai menolak usul",
        "Usul pemusnahan BUM-2025-004 ditolak karena retensi belum habis",
        "BUM-2025-004", "Hj. Budi R.", "Anggota Tim Penilai", "192.168.1.32"),
)

// ─────────────────────────────────────────────────────────────
//  Screen Utama
// ─────────────────────────────────────────────────────────────
@Composable
fun LogRiwayatScreen(
    onNavigate: (String) -> Unit = {}
) {
    var searchQuery   by remember { mutableStateOf("") }
    var activeFilter  by remember { mutableStateOf("Semua") }
    var expandedId    by remember { mutableStateOf<Int?>(null) }

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope       = rememberCoroutineScope()

    val filters = listOf("Semua", "Berkas", "Penilaian", "Pemusnahan", "Berita Acara", "Sistem")

    val filteredList = dummyLogList.filter { log ->
        val matchFilter = activeFilter == "Semua" || log.category.label == activeFilter
        val matchSearch = searchQuery.isBlank() ||
                log.title.contains(searchQuery, ignoreCase = true) ||
                log.description.contains(searchQuery, ignoreCase = true) ||
                log.person.contains(searchQuery, ignoreCase = true) ||
                (log.relatedBerkas?.contains(searchQuery, ignoreCase = true) ?: false)
        matchFilter && matchSearch
    }

    // Group by date
    val grouped = filteredList.groupBy { it.dateGroup }
        .toList()
        .sortedBy { it.second.firstOrNull()?.sortKey ?: 99 }

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
                    currentRoute = DrawerRoutes.LOG_RIWAYAT,
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
                LogTopBar(
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
                        LogStatBox(
                            value    = "${dummyLogList.size}",
                            label    = "Total Aktivitas",
                            icon     = Icons.Filled.History,
                            bgColor  = CardWhite,
                            color    = TextHead,
                            modifier = Modifier.weight(1f)
                        )
                        LogStatBox(
                            value    = "${dummyLogList.map { it.person }.distinct().size}",
                            label    = "Pengguna",
                            icon     = Icons.Filled.Group,
                            bgColor  = BlueBg,
                            color    = BlueText,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // ── Search bar ────────────────────────────────
                item {
                    OutlinedTextField(
                        value         = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder   = {
                            Text("Cari aktivitas, berkas, atau pengguna...",
                                fontSize = 13.sp, color = TextHint)
                        },
                        leadingIcon = {
                            Icon(Icons.Filled.Search, null,
                                tint     = TextHint,
                                modifier = Modifier.size(20.dp))
                        },
                        trailingIcon = if (searchQuery.isNotEmpty()) {
                            {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Filled.Close, null,
                                        tint = TextHint, modifier = Modifier.size(18.dp))
                                }
                            }
                        } else null,
                        singleLine = true,
                        shape      = RoundedCornerShape(12.dp),
                        colors     = logFieldColors(),
                        modifier   = Modifier.fillMaxWidth().height(48.dp)
                    )
                }

                // ── Filter chips ──────────────────────────────
                item {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                        items(filters) { filter ->
                            LogFilterChip(
                                label    = filter,
                                isActive = activeFilter == filter,
                                onClick  = {
                                    activeFilter = filter
                                    expandedId   = null
                                }
                            )
                        }
                    }
                }

                // ── Empty state ──────────────────────────────
                if (grouped.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape    = RoundedCornerShape(16.dp),
                            colors   = CardDefaults.cardColors(containerColor = CardWhite),
                            border   = BorderStroke(1.dp, BorderGray)
                        ) {
                            Column(
                                modifier              = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                horizontalAlignment   = Alignment.CenterHorizontally,
                                verticalArrangement   = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Filled.SearchOff, null,
                                    tint = TextHint, modifier = Modifier.size(40.dp))
                                Text("Tidak ada aktivitas ditemukan",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextBody)
                                Text("Coba ubah kata kunci atau filter",
                                    fontSize = 11.sp, color = TextHint)
                            }
                        }
                    }
                }

                // ── Grouped log entries ──────────────────────
                grouped.forEach { (groupName, entries) ->
                    item {
                        DateGroupHeader(label = groupName, count = entries.size)
                    }
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape    = RoundedCornerShape(16.dp),
                            colors   = CardDefaults.cardColors(containerColor = CardWhite),
                            border   = BorderStroke(1.dp, BorderGray)
                        ) {
                            Column {
                                entries.forEachIndexed { index, log ->
                                    val isExpanded = expandedId == log.id
                                    val isLast     = index == entries.lastIndex
                                    LogEntryRow(
                                        log        = log,
                                        isExpanded = isExpanded,
                                        isLast     = isLast,
                                        onToggle   = {
                                            expandedId = if (isExpanded) null else log.id
                                        }
                                    )
                                }
                            }
                        }
                    }
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
private fun LogTopBar(onMenuClick: () -> Unit) {
    TopAppBar(
        title = {
            Column {
                Text("Log Riwayat",
                    fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text("Dokumentasi seluruh tahapan proses",
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
                Icon(Icons.Filled.FileDownload, "Ekspor log", tint = Color.White)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = GreenPrimary)
    )
}

// ─────────────────────────────────────────────────────────────
//  Stat Box
// ─────────────────────────────────────────────────────────────
@Composable
private fun LogStatBox(
    value: String,
    label: String,
    icon: ImageVector,
    bgColor: Color,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(bgColor, RoundedCornerShape(12.dp))
            .border(1.dp, BorderGray, RoundedCornerShape(12.dp))
            .padding(vertical = 12.dp, horizontal = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, null, tint = color, modifier = Modifier.size(18.dp))
        Spacer(Modifier.height(5.dp))
        Text(value,
            fontSize   = 17.sp,
            fontWeight = FontWeight.Bold,
            color      = color)
        Text(label,
            fontSize      = 9.sp,
            fontWeight    = FontWeight.SemiBold,
            color         = TextHint,
            letterSpacing = 0.3.sp,
            modifier      = Modifier.padding(top = 2.dp))
    }
}

// ─────────────────────────────────────────────────────────────
//  Filter Chip
// ─────────────────────────────────────────────────────────────
@Composable
private fun LogFilterChip(
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
//  Date Group Header
// ─────────────────────────────────────────────────────────────
@Composable
private fun DateGroupHeader(label: String, count: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 4.dp, end = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(label,
            fontSize      = 11.sp,
            fontWeight    = FontWeight.Bold,
            color         = GreenPrimary,
            letterSpacing = 0.5.sp)
        Box(
            modifier = Modifier
                .background(GreenLight, RoundedCornerShape(9999.dp))
                .padding(horizontal = 7.dp, vertical = 1.dp)
        ) {
            Text("$count",
                fontSize   = 9.sp,
                fontWeight = FontWeight.Bold,
                color      = GreenPrimary)
        }
        HorizontalDivider(
            modifier  = Modifier.weight(1f),
            thickness = 0.5.dp,
            color     = BorderGray
        )
    }
}

// ─────────────────────────────────────────────────────────────
//  Log Entry Row
// ─────────────────────────────────────────────────────────────
@Composable
private fun LogEntryRow(
    log: LogEntry,
    isExpanded: Boolean,
    isLast: Boolean,
    onToggle: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(if (isExpanded) Color(0xFFF1F8F1) else CardWhite)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication        = null,
                    onClick           = onToggle
                )
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment     = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(11.dp)
        ) {
            // ── Left: time + colored icon ─────────────────────
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier            = Modifier.width(38.dp)
            ) {
                Text(log.time,
                    fontSize   = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color      = TextHint)
                Spacer(Modifier.height(5.dp))
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(log.category.bgColor, CircleShape)
                        .border(1.5.dp, log.category.color.copy(alpha = 0.3f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        log.category.icon, null,
                        tint     = log.category.color,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }

            // ── Right: content ────────────────────────────────
            Column(modifier = Modifier.weight(1f)) {
                // Title + category badge
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier              = Modifier.padding(bottom = 3.dp)
                ) {
                    Text(log.title,
                        fontSize   = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color      = TextHead,
                        modifier   = Modifier.weight(1f))
                    Box(
                        modifier = Modifier
                            .background(log.category.bgColor, RoundedCornerShape(9999.dp))
                            .padding(horizontal = 6.dp, vertical = 1.dp)
                    ) {
                        Text(log.category.label,
                            fontSize   = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color      = log.category.color)
                    }
                }

                // Description
                Text(log.description,
                    fontSize   = 11.sp,
                    color      = TextBody,
                    lineHeight = 16.sp)

                // Meta row
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    modifier              = Modifier.padding(top = 5.dp)
                ) {
                    Icon(Icons.Filled.Person, null,
                        tint = TextHint, modifier = Modifier.size(11.dp))
                    Text(log.person,
                        fontSize   = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color      = TextHint)
                    if (log.relatedBerkas != null) {
                        Text("•", fontSize = 10.sp, color = TextHint)
                        Icon(Icons.Filled.Link, null,
                            tint = GreenPrimary, modifier = Modifier.size(10.dp))
                        Text(log.relatedBerkas,
                            fontSize   = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color      = GreenPrimary)
                    }
                }

                // Expand chevron
                Icon(
                    if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    null,
                    tint     = TextHint,
                    modifier = Modifier
                        .padding(top = 2.dp)
                        .size(16.dp)
                        .align(Alignment.End)
                )
            }
        }

        // Divider antar entry
        if (!isLast && !isExpanded) {
            HorizontalDivider(
                modifier  = Modifier.padding(start = 64.dp),
                thickness = 0.5.dp,
                color     = BorderGray
            )
        }

        // Expandable detail
        AnimatedVisibility(
            visible = isExpanded,
            enter   = expandVertically() + fadeIn(),
            exit    = shrinkVertically() + fadeOut()
        ) {
            LogDetailPanel(log = log, isLast = isLast)
        }

        if (isExpanded && !isLast) {
            HorizontalDivider(thickness = 0.5.dp, color = BorderGray)
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  Log Detail Panel (expanded view)
// ─────────────────────────────────────────────────────────────
@Composable
private fun LogDetailPanel(log: LogEntry, isLast: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF1F8F1))
            .padding(14.dp)
    ) {
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier              = Modifier.padding(bottom = 12.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(14.dp)
                    .background(GreenPrimary, RoundedCornerShape(9999.dp))
            )
            Text("INFORMASI LENGKAP",
                fontSize      = 10.sp,
                fontWeight    = FontWeight.Bold,
                color         = GreenPrimary,
                letterSpacing = 0.7.sp)
        }

        // Detail grid
        LogDetailField(
            icon  = Icons.Filled.AccessTime,
            label = "WAKTU",
            value = "${log.dateGroup} • ${log.time} WITA"
        )
        Spacer(Modifier.height(8.dp))

        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            LogDetailField(
                icon  = Icons.Filled.Person,
                label = "PENGGUNA",
                value = log.person,
                modifier = Modifier.weight(1f)
            )
            LogDetailField(
                icon  = Icons.Filled.Badge,
                label = "JABATAN",
                value = log.role,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(8.dp))

        if (log.relatedBerkas != null) {
            LogDetailField(
                icon  = Icons.Filled.Link,
                label = "BERKAS TERKAIT",
                value = log.relatedBerkas,
                valueColor = GreenPrimary
            )
            Spacer(Modifier.height(8.dp))
        }

        LogDetailField(
            icon  = Icons.Filled.Notes,
            label = "DESKRIPSI",
            value = log.description
        )

        HorizontalDivider(
            modifier  = Modifier.padding(vertical = 10.dp),
            thickness = 1.dp,
            color     = GreenMid
        )

        // Audit info
        Row(
            modifier              = Modifier.fillMaxWidth(),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Icon(Icons.Filled.Security, null,
                tint     = TextHint,
                modifier = Modifier.size(11.dp))
            Text("AUDIT •",
                fontSize      = 9.sp,
                fontWeight    = FontWeight.Bold,
                color         = TextHint,
                letterSpacing = 0.7.sp)
            Text("IP ${log.ipAddress}",
                fontSize  = 10.sp,
                color     = TextBody,
                fontWeight = FontWeight.Medium)
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  Log Detail Field
// ─────────────────────────────────────────────────────────────
@Composable
private fun LogDetailField(
    icon: ImageVector,
    label: String,
    value: String,
    valueColor: Color = TextHead,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier              = Modifier.padding(bottom = 3.dp)
        ) {
            Icon(icon, null, tint = TextHint, modifier = Modifier.size(11.dp))
            Text(label,
                fontSize      = 9.sp,
                fontWeight    = FontWeight.Bold,
                color         = TextHint,
                letterSpacing = 0.7.sp)
        }
        Text(value,
            fontSize   = 12.sp,
            fontWeight = FontWeight.Medium,
            color      = valueColor,
            lineHeight = 16.sp)
    }
}

// ─────────────────────────────────────────────────────────────
//  Field Colors
// ─────────────────────────────────────────────────────────────
@Composable
private fun logFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor      = GreenPrimary,
    unfocusedBorderColor    = BorderGray,
    focusedContainerColor   = CardWhite,
    unfocusedContainerColor = CardWhite,
    focusedTextColor        = TextHead,
    unfocusedTextColor      = TextHead,
    cursorColor             = GreenPrimary
)

// ─────────────────────────────────────────────────────────────
//  Preview
// ─────────────────────────────────────────────────────────────
@Preview(showBackground = true, widthDp = 375, heightDp = 812)
@Composable
fun LogRiwayatPreview() {
    LogRiwayatScreen()
}