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
//  Data Model
// ─────────────────────────────────────────────────────────────
data class ArsipItem(
    val id: Int,
    val kode: String,
    val fullKode: String,
    val deskripsi: String,
    val tahun: String,
    val tingkat: String,
    val volume: String,
    val retensiAktif: String,
    val retensiInaktif: String,
    val keterangan: String,  // "Musnah" atau "Permanen"
    val sumber: String       // "Keuangan", "Non-Keuangan", "Peminjaman"
)

// ─────────────────────────────────────────────────────────────
//  Dummy Data
// ─────────────────────────────────────────────────────────────
val dummyArsipList = listOf(
    ArsipItem(1,  "KN.03.01", "00001/SP2D/1.20.11.01/DPPKAD/2016",
        "Gaji dan Tunjangan Pegawai Bulan Januari 2016",
        "2016", "Copy", "1 Berkas", "2 Thn", "8 Thn", "Musnah", "Keuangan"),
    ArsipItem(2,  "KN.03.01", "00002/SP2D/1.20.05.01/DPPKAD/2016",
        "Pembayaran Gaji Induk Bulan Januari 2016 Pegawai Dinas PPKAD",
        "2016", "Copy", "1 Berkas", "2 Thn", "8 Thn", "Musnah", "Keuangan"),
    ArsipItem(3,  "KN.03.02", "00003/SP2D/1.20.05.01/DPPKAD/2016",
        "Pembayaran Kekurangan Gaji Bulan Januari 2016",
        "2016", "Copy", "1 Berkas", "2 Thn", "8 Thn", "Musnah", "Keuangan"),
    ArsipItem(4,  "KN.04.01", "00004/SP2D/1.07.01.01/DPPKAD/2016",
        "Pembayaran Gaji dan Tunjangan PNS Dishubkominfo Kab. Balangan",
        "2016", "Asli", "2 Berkas", "10 Thn", "Permanen", "Permanen", "Keuangan"),
    ArsipItem(5,  "KN.01.02", "00005/KN.01.02/DAU/DPPKAD/2019",
        "Dana Alokasi Umum Tahun 2019",
        "2019", "Copy", "1 Berkas", "5 Thn", "8 Thn", "Musnah", "Keuangan"),
    ArsipItem(6,  "NK.02.01", "00006/NK.02.01/PERDA/DPPKAD/2018",
        "Peraturan Daerah No. 5 Tahun 2018",
        "2018", "Asli", "1 Berkas", "10 Thn", "Permanen", "Permanen", "Non-Keuangan"),
    ArsipItem(7,  "NK.05.10", "00007/NK.05.10/SRD/DPPKAD/2017",
        "Surat Dinas Pendidikan Kabupaten Balangan 2017",
        "2017", "Copy", "1 Berkas", "3 Thn", "5 Thn", "Musnah", "Non-Keuangan"),
    ArsipItem(8,  "NK.03.05", "00008/NK.03.05/SK/DPPKAD/2018",
        "SK Pegawai Bidang PPKAD Tahun 2018",
        "2018", "Copy", "1 Berkas", "3 Thn", "5 Thn", "Musnah", "Non-Keuangan"),
    ArsipItem(9,  "PM.01.03", "00009/PM.01.03/PJM/DPPKAD/2018",
        "Arsip Peminjaman Dokumen 2018",
        "2018", "Copy", "1 Berkas", "5 Thn", "8 Thn", "Musnah", "Peminjaman"),
    ArsipItem(10, "PM.02.01", "00010/PM.02.01/DPM/DPPKAD/2019",
        "Data Peminjaman Arsip Tahun 2019",
        "2019", "Copy", "1 Berkas", "2 Thn", "3 Thn", "Musnah", "Peminjaman"),
)

// ─────────────────────────────────────────────────────────────
//  Screen Utama
// ─────────────────────────────────────────────────────────────
@Composable
fun DaftarUsulMusnahScreen(
    onBuatBerkas: () -> Unit = {},
    onNavigate: (String) -> Unit = {}
) {
    var searchQuery    by remember { mutableStateOf("") }
    var activeFilter   by remember { mutableStateOf("Semua") }
    var activeYear     by remember { mutableStateOf("Semua") }
    var expandedItemId by remember { mutableStateOf<Int?>(null) }

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope       = rememberCoroutineScope()
    val filters = listOf("Semua", "Keuangan", "Non-Keuangan", "Peminjaman")
    val years   = listOf("Semua", "2019", "2018", "2017", "2016")

    val filteredList = dummyArsipList.filter { item ->
        val matchFilter = activeFilter == "Semua" || item.sumber == activeFilter
        val matchYear   = activeYear == "Semua" || item.tahun == activeYear
        val matchSearch = searchQuery.isBlank() ||
                item.kode.contains(searchQuery, ignoreCase = true) ||
                item.deskripsi.contains(searchQuery, ignoreCase = true)
        matchFilter && matchYear && matchSearch
    }

    val countMusnah   = filteredList.count { it.keterangan == "Musnah" }
    val countPermanen = filteredList.count { it.keterangan == "Permanen" }

    // ── Drawer wrapper ────────────────────────────────────────
    ModalNavigationDrawer(
        drawerState   = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier             = Modifier.fillMaxWidth(0.86f),
                drawerContainerColor = CardWhite,
                drawerShape          = RoundedCornerShape(
                    topStart    = 0.dp,
                    bottomStart = 0.dp,
                    topEnd      = 0.dp,
                    bottomEnd   = 0.dp
                )
            ) {
                PemusnahanDrawerContent(
                    currentRoute = DrawerRoutes.DAFTAR_USUL_MUSNAH,
                    onNavigate   = { route ->
                        scope.launch { drawerState.close() }
                        onNavigate(route)
                    },
                    onLogout = {
                        scope.launch { drawerState.close() }
                    }
                )
            }
        }
    ) {
        // ── Scaffold ──────────────────────────────────────────
        Scaffold(
            containerColor = BgDashboard,
            topBar = {
                DaftarTopBar(
                    onMenuClick = { scope.launch { drawerState.open() } }
                )
            },
            bottomBar = {
                BuatBerkasButton(onClick = onBuatBerkas)
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
                    bottom = 8.dp
                ),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {

                // ── Search bar ────────────────────────────────
                item {
                    SearchBar(
                        query         = searchQuery,
                        onQueryChange = {
                            searchQuery    = it
                            expandedItemId = null
                        }
                    )
                    Spacer(Modifier.height(10.dp))
                }

                // Filter modul
                item {
                    Column {
                        Text(
                            "MODUL",
                            fontSize      = 9.sp,
                            fontWeight    = FontWeight.Bold,
                            color         = TextHint,
                            letterSpacing = 0.7.sp,
                            modifier      = Modifier.padding(bottom = 6.dp, start = 2.dp)
                        )
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                            items(filters) { filter ->
                                FilterChipItem(
                                    label    = filter,
                                    isActive = activeFilter == filter,
                                    onClick  = {
                                        activeFilter   = filter
                                        expandedItemId = null
                                    }
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                }

                // Filter tahun
                item {
                    Column {
                        Text(
                            "TAHUN",
                            fontSize      = 9.sp,
                            fontWeight    = FontWeight.Bold,
                            color         = TextHint,
                            letterSpacing = 0.7.sp,
                            modifier      = Modifier.padding(bottom = 6.dp, start = 2.dp)
                        )
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                            items(years) { year ->
                                FilterChipItem(
                                    label    = year,
                                    isActive = activeYear == year,
                                    onClick  = {
                                        activeYear     = year
                                        expandedItemId = null
                                    }
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                }

                // ── Stat chips ────────────────────────────────
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                        StatChip(
                            "${filteredList.size} Total",
                            CardWhite, BorderGray, TextBody
                        )
                        StatChip(
                            "$countMusnah Musnah",
                            DangerBg, Color(0xFFFECACA), DangerText
                        )
                        StatChip(
                            "$countPermanen Permanen",
                            GreenLight, GreenMid, GreenPrimary
                        )
                    }
                    Spacer(Modifier.height(10.dp))
                }

                // ── Table header ──────────────────────────────
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape    = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                        colors   = CardDefaults.cardColors(
                            containerColor = Color(0xFFF5F7F5)
                        ),
                        border = BorderStroke(1.dp, BorderGray)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 9.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment     = Alignment.CenterVertically
                        ) {
                            Text(
                                "ARSIP",
                                fontSize      = 9.5.sp,
                                fontWeight    = FontWeight.Bold,
                                color         = TextHint,
                                letterSpacing = 0.9.sp
                            )
                            Text(
                                "DETAIL",
                                fontSize      = 9.5.sp,
                                fontWeight    = FontWeight.Bold,
                                color         = TextHint,
                                letterSpacing = 0.9.sp
                            )
                        }
                    }
                }

                // ── Archive rows ──────────────────────────────
                items(filteredList, key = { it.id }) { item ->
                    val isExpanded = expandedItemId == item.id
                    val isLast     = filteredList.last().id == item.id

                    ArchiveRowCard(
                        item       = item,
                        isExpanded = isExpanded,
                        isLast     = isLast,
                        onEyeClick = {
                            expandedItemId = if (isExpanded) null else item.id
                        }
                    )
                }

                // ── Pagination ────────────────────────────────
                item {
                    Spacer(Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment     = Alignment.CenterVertically
                    ) {
                        Text(
                            "1–${filteredList.size} dari ${filteredList.size}",
                            fontSize = 11.sp,
                            color    = TextHint
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment     = Alignment.CenterVertically
                        ) {
                            PaginationButton(icon = Icons.Filled.ChevronLeft)
                            Text(
                                "1 / 1",
                                fontSize   = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color      = TextHead
                            )
                            PaginationButton(icon = Icons.Filled.ChevronRight)
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  Top Bar — hamburger kiri, filter kanan
// ─────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DaftarTopBar(onMenuClick: () -> Unit) {
    TopAppBar(
        title = {
            Column {
                Text(
                    "Daftar Usul Musnah",
                    fontSize   = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color      = Color.White
                )
                Text(
                    "Berdasarkan Jadwal Retensi Arsip (JRA)",
                    fontSize = 10.sp,
                    color    = Color.White.copy(alpha = 0.6f)
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(
                    Icons.Filled.Menu,
                    contentDescription = "Buka menu",
                    tint = Color.White
                )
            }
        },
        actions = {
            IconButton(onClick = {}) {
                Icon(
                    Icons.Filled.FilterList,
                    contentDescription = "Filter",
                    tint = Color.White
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = GreenPrimary
        )
    )
}

// ─────────────────────────────────────────────────────────────
//  Search Bar
// ─────────────────────────────────────────────────────────────
@Composable
private fun SearchBar(query: String, onQueryChange: (String) -> Unit) {
    OutlinedTextField(
        value          = query,
        onValueChange  = onQueryChange,
        placeholder    = {
            Text(
                "Cari kode klasifikasi atau nama arsip...",
                fontSize = 13.sp,
                color    = TextHint
            )
        },
        leadingIcon = {
            Icon(
                Icons.Filled.Search,
                contentDescription = null,
                tint     = TextHint,
                modifier = Modifier.size(20.dp)
            )
        },
        trailingIcon = if (query.isNotEmpty()) {
            {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        Icons.Filled.Close,
                        contentDescription = "Hapus",
                        tint     = TextHint,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        } else null,
        singleLine = true,
        shape      = RoundedCornerShape(12.dp),
        colors     = OutlinedTextFieldDefaults.colors(
            focusedBorderColor      = GreenPrimary,
            unfocusedBorderColor    = BorderGray,
            focusedContainerColor   = CardWhite,
            unfocusedContainerColor = CardWhite,
            focusedTextColor        = TextHead,
            unfocusedTextColor      = TextHead,
            cursorColor             = GreenPrimary
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
    )
}

// ─────────────────────────────────────────────────────────────
//  Filter Chip
// ─────────────────────────────────────────────────────────────
@Composable
private fun FilterChipItem(
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
        Text(
            text       = label,
            fontSize   = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color      = if (isActive) Color.White else TextBody
        )
    }
}

// ─────────────────────────────────────────────────────────────
//  Stat Chip
// ─────────────────────────────────────────────────────────────
@Composable
private fun StatChip(
    text: String,
    bgColor: Color,
    borderColor: Color,
    textColor: Color
) {
    Box(
        modifier = Modifier
            .background(bgColor, RoundedCornerShape(9999.dp))
            .border(1.dp, borderColor, RoundedCornerShape(9999.dp))
            .padding(horizontal = 11.dp, vertical = 5.dp)
    ) {
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(textColor, CircleShape)
            )
            Text(
                text,
                fontSize   = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color      = textColor
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  Archive Row Card — row + accordion detail
// ─────────────────────────────────────────────────────────────
@Composable
private fun ArchiveRowCard(
    item: ArsipItem,
    isExpanded: Boolean,
    isLast: Boolean,
    onEyeClick: () -> Unit
) {
    val bottomShape = if (isLast && !isExpanded)
        RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
    else
        RoundedCornerShape(0.dp)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardWhite)
            .border(
                width = 0.5.dp,
                color = if (isExpanded) GreenMid else BorderGray,
                shape = bottomShape
            )
    ) {
        // ── Main row ──────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    if (isExpanded) Color(0xFFF0F9F0) else CardWhite
                )
                .padding(horizontal = 16.dp, vertical = 13.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Kiri: info arsip
            Column(modifier = Modifier.weight(1f)) {
                // Baris 1: kode pill + status badge
                Row(
                    horizontalArrangement = Arrangement.spacedBy(7.dp),
                    verticalAlignment     = Alignment.CenterVertically,
                    modifier              = Modifier.padding(bottom = 5.dp)
                ) {
                    KodePill(item.kode)
                    StatusBadge(item.keterangan)
                }
                // Baris 2: deskripsi
                Text(
                    text       = item.deskripsi,
                    fontSize   = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color      = TextHead,
                    maxLines   = 1,
                    overflow   = TextOverflow.Ellipsis,
                    modifier   = Modifier.padding(bottom = 6.dp)
                )
                // Baris 3: chip meta
                Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    MetaChip(Icons.Filled.CalendarToday, item.tahun)
                    MetaChip(Icons.Filled.ContentCopy,   item.tingkat)
                    MetaChip(Icons.Filled.Inventory2,    item.volume)
                }
            }

            // Kanan: tombol mata
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .background(
                        if (isExpanded) GreenPrimary else CardWhite,
                        RoundedCornerShape(10.dp)
                    )
                    .border(
                        1.5.dp,
                        if (isExpanded) GreenPrimary else BorderGray,
                        RoundedCornerShape(10.dp)
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication        = null,
                        onClick           = onEyeClick
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isExpanded)
                        Icons.Filled.VisibilityOff
                    else
                        Icons.Filled.Visibility,
                    contentDescription = "Detail",
                    tint     = if (isExpanded) Color.White else TextHint,
                    modifier = Modifier.size(17.dp)
                )
            }
        }

        // Divider tipis antar baris
        if (!isExpanded) {
            HorizontalDivider(thickness = 0.5.dp, color = BorderGray)
        }

        // ── Detail accordion ──────────────────────────────────
        AnimatedVisibility(
            visible = isExpanded,
            enter   = expandVertically() + fadeIn(),
            exit    = shrinkVertically() + fadeOut()
        ) {
            DetailPanel(item = item, onClose = onEyeClick)
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  Detail Panel (accordion content)
// ─────────────────────────────────────────────────────────────
@Composable
private fun DetailPanel(item: ArsipItem, onClose: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF1F8F1))
            .border(
                width = 1.5.dp,
                color = GreenMid,
                shape = RoundedCornerShape(
                    bottomStart = 16.dp,
                    bottomEnd   = 16.dp
                )
            )
            .padding(14.dp)
    ) {
        // Header detail
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier              = Modifier.padding(bottom = 12.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(16.dp)
                    .background(GreenPrimary, RoundedCornerShape(9999.dp))
            )
            Text(
                "INFORMASI DETAIL",
                fontSize      = 11.sp,
                fontWeight    = FontWeight.Bold,
                color         = GreenPrimary,
                letterSpacing = 0.7.sp,
                modifier      = Modifier.weight(1f)
            )
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(CardWhite, RoundedCornerShape(7.dp))
                    .border(1.dp, GreenMid, RoundedCornerShape(7.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication        = null,
                        onClick           = onClose
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.Close,
                    contentDescription = "Tutup",
                    tint     = TextHint,
                    modifier = Modifier.size(13.dp)
                )
            }
        }

        // Kode + Kurun Waktu
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            DetailField(
                label      = "KODE KLASIFIKASI",
                value      = item.fullKode,
                valueColor = GreenPrimary,
                modifier   = Modifier.weight(1f)
            )
            DetailField(
                label    = "KURUN WAKTU",
                value    = item.tahun,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(9.dp))
        DetailField("ISI INFORMASI", item.deskripsi)

        Spacer(Modifier.height(9.dp))
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            DetailField("VOLUME", item.volume,   modifier = Modifier.weight(1f))
            DetailField("TINGKAT PERKEMBANGAN", item.tingkat, modifier = Modifier.weight(1f))
        }

        Spacer(Modifier.height(9.dp))

        // Retensi
        Text(
            "RETENSI",
            fontSize      = 9.sp,
            fontWeight    = FontWeight.Bold,
            color         = TextHint,
            letterSpacing = 0.7.sp,
            modifier      = Modifier.padding(bottom = 6.dp)
        )
        Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
            RetensiPill(Icons.Filled.Timer,   "Aktif: ${item.retensiAktif}")
            RetensiPill(Icons.Filled.Archive, "Inaktif: ${item.retensiInaktif}")
        }

        // Separator
        HorizontalDivider(
            modifier  = Modifier.padding(vertical = 10.dp),
            thickness = 1.dp,
            color     = GreenMid
        )

        // Keterangan
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Text(
                "KETERANGAN",
                fontSize      = 9.sp,
                fontWeight    = FontWeight.Bold,
                color         = TextHint,
                letterSpacing = 0.7.sp
            )
            StatusBadge(item.keterangan)
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  CTA Button bawah
// ─────────────────────────────────────────────────────────────
@Composable
private fun BuatBerkasButton(onClick: () -> Unit) {
    Surface(
        modifier        = Modifier.fillMaxWidth(),
        color           = CardWhite,
        shadowElevation = 0.dp
    ) {
        Box(
            modifier = Modifier.padding(
                start  = 16.dp,
                end    = 16.dp,
                top    = 10.dp,
                bottom = 18.dp
            )
        ) {
            Button(
                onClick  = onClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape  = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GreenPrimary,
                    contentColor   = Color.White
                )
            ) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    "Buat Berkas Usul Musnah",
                    fontSize      = 14.sp,
                    fontWeight    = FontWeight.Bold,
                    letterSpacing = 0.2.sp
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  Komponen kecil reusable
// ─────────────────────────────────────────────────────────────
@Composable
private fun KodePill(kode: String) {
    Box(
        modifier = Modifier
            .background(GreenLight, RoundedCornerShape(9999.dp))
            .padding(horizontal = 9.dp, vertical = 3.dp)
    ) {
        Text(
            kode,
            fontSize      = 10.sp,
            fontWeight    = FontWeight.Bold,
            color         = GreenPrimary,
            letterSpacing = 0.3.sp
        )
    }
}

@Composable
private fun StatusBadge(status: String) {
    val bg    = if (status == "Musnah") DangerBg   else GreenLight
    val color = if (status == "Musnah") DangerText else GreenPrimary
    Box(
        modifier = Modifier
            .background(bg, RoundedCornerShape(9999.dp))
            .padding(horizontal = 9.dp, vertical = 3.dp)
    ) {
        Text(status, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = color)
    }
}

@Composable
private fun MetaChip(icon: ImageVector, text: String) {
    Row(
        modifier = Modifier
            .background(Color(0xFFF3F5F3), RoundedCornerShape(9999.dp))
            .border(0.5.dp, BorderGray, RoundedCornerShape(9999.dp))
            .padding(horizontal = 8.dp, vertical = 2.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Icon(icon, contentDescription = null,
            tint = TextHint, modifier = Modifier.size(10.dp))
        Text(text, fontSize = 10.sp, color = TextBody)
    }
}

@Composable
private fun DetailField(
    label: String,
    value: String,
    valueColor: Color = TextHead,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            label,
            fontSize      = 9.sp,
            fontWeight    = FontWeight.Bold,
            color         = TextHint,
            letterSpacing = 0.7.sp,
            modifier      = Modifier.padding(bottom = 3.dp)
        )
        Text(
            value,
            fontSize   = 12.sp,
            fontWeight = FontWeight.Medium,
            color      = valueColor,
            lineHeight = 16.sp
        )
    }
}

@Composable
private fun RetensiPill(icon: ImageVector, text: String) {
    Row(
        modifier = Modifier
            .background(CardWhite, RoundedCornerShape(9999.dp))
            .border(1.dp, GreenMid, RoundedCornerShape(9999.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Icon(icon, contentDescription = null,
            tint = GreenPrimary, modifier = Modifier.size(12.dp))
        Text(text, fontSize = 11.sp, color = TextBody)
    }
}

@Composable
private fun PaginationButton(icon: ImageVector) {
    Box(
        modifier = Modifier
            .size(30.dp)
            .background(CardWhite, RoundedCornerShape(9.dp))
            .border(1.5.dp, BorderGray, RoundedCornerShape(9.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication        = null,
                onClick           = {}
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null,
            tint = TextBody, modifier = Modifier.size(16.dp))
    }
}

// ─────────────────────────────────────────────────────────────
//  Preview
// ─────────────────────────────────────────────────────────────
@Preview(showBackground = true, widthDp = 375, heightDp = 812)
@Composable
fun DaftarUsulMusnahPreview() {
    DaftarUsulMusnahScreen()
}