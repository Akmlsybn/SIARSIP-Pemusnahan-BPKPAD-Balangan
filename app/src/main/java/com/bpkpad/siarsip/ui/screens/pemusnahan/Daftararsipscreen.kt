package com.bpkpad.siarsip.ui.screens.pemusnahan

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bpkpad.siarsip.core.utils.ResultState
import com.bpkpad.siarsip.feature.arsip.domain.model.Arsip
import com.bpkpad.siarsip.feature.arsip.presentation.DaftarArsipViewModel
import com.bpkpad.siarsip.ui.components.DrawerRoutes
import com.bpkpad.siarsip.ui.components.PemusnahanDrawerContent
import com.bpkpad.siarsip.ui.components.PemusnahanBottomBar
import com.bpkpad.siarsip.ui.theme.*
import kotlinx.coroutines.launch

// ─────────────────────────────────────────────────────────────
//  Data Model — Column definitions
// ─────────────────────────────────────────────────────────────
data class TableColumn(
    val key: String,
    val label: String,
    val width: Dp,
    val sticky: Boolean = false   // kolom yang tidak bisa di-hide
)

val defaultColumns = listOf(
    TableColumn("no",              "No",                 50.dp, sticky = true),
    TableColumn("kode",            "Kode Klasifikasi",  120.dp, sticky = true),
    TableColumn("deskripsi",       "Isi Informasi",     260.dp),
    TableColumn("tahun",           "Kurun Waktu",        85.dp),
    TableColumn("tingkat",         "Tingkat",            85.dp),
    TableColumn("volume",          "Volume",             90.dp),
    TableColumn("retensiAktif",    "Retensi Aktif",     105.dp),
    TableColumn("retensiInaktif",  "Retensi Inaktif",   110.dp),
    TableColumn("keterangan",      "Keterangan",        100.dp),
    TableColumn("sumber",          "Sumber Modul",      120.dp)
)

// ─────────────────────────────────────────────────────────────
//  Filter Condition Model
// ─────────────────────────────────────────────────────────────
data class FilterCondition(
    val id: Int,
    val field: String,    // "kode", "tahun", "tingkat", dll
    val value: String
)

// ─────────────────────────────────────────────────────────────
//  Screen Utama
// ─────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DaftarArsipScreen(
    onNavigate: (String) -> Unit = {},
    viewModel: DaftarArsipViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val archivesList = (uiState as? ResultState.Success)?.data ?: emptyList()

    var searchQuery       by remember { mutableStateOf("") }
    var activeModul       by remember { mutableStateOf("Semua") }
    var activeYear        by remember { mutableStateOf("Semua") }
    val visibleColumnKeys = remember { mutableStateListOf<String>().apply {
        addAll(defaultColumns.map { it.key })
    } }
    val advancedFilters   = remember { mutableStateListOf<FilterCondition>() }

    var showColumnSheet   by remember { mutableStateOf(false) }
    var showFilterSheet   by remember { mutableStateOf(false) }

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope       = rememberCoroutineScope()

    val moduls = listOf("Semua", "Keuangan", "Non-Keuangan", "Peminjaman")
    val years  = listOf("Semua", "2019", "2018", "2017", "2016")

    // Filter logic
    val filteredList = archivesList.filter { item ->
        val matchModul  = activeModul == "Semua" || item.sumber == activeModul
        val matchYear   = activeYear == "Semua" || item.tahun == activeYear
        val matchSearch = searchQuery.isBlank() ||
                item.kode.contains(searchQuery, ignoreCase = true) ||
                item.deskripsi.contains(searchQuery, ignoreCase = true)
        val matchAdvanced = advancedFilters.all { cond ->
            if (cond.value.isBlank()) true
            else when (cond.field) {
                "kode"       -> item.kode.contains(cond.value, ignoreCase = true)
                "deskripsi"  -> item.deskripsi.contains(cond.value, ignoreCase = true)
                "tahun"      -> item.tahun == cond.value
                "tingkat"    -> item.tingkat.equals(cond.value, ignoreCase = true)
                "keterangan" -> item.keterangan.equals(cond.value, ignoreCase = true)
                "sumber"     -> item.sumber.equals(cond.value, ignoreCase = true)
                else         -> true
            }
        }
        matchModul && matchYear && matchSearch && matchAdvanced
    }

    val visibleColumns = defaultColumns.filter { it.key in visibleColumnKeys }
    val totalWidth     = visibleColumns.sumOf { it.width.value.toDouble() }.dp

    ModalNavigationDrawer(
        drawerState   = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier             = Modifier.fillMaxWidth(0.86f),
                drawerContainerColor = CardWhite,
                drawerShape          = RoundedCornerShape(0.dp)
            ) {
                PemusnahanDrawerContent(
                    currentRoute = DrawerRoutes.DAFTAR_ARSIP,
                    onNavigate   = { route ->
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
            bottomBar = {
                PemusnahanBottomBar(
                    currentRoute = DrawerRoutes.DAFTAR_ARSIP,
                    onNavigate   = onNavigate
                )
            },
            topBar = {
                ArsipTopBar(
                    onMenuClick      = { scope.launch { drawerState.open() } },
                    onColumnsClick   = { showColumnSheet = true },
                    onFilterClick    = { showFilterSheet = true },
                    activeFilterCount = advancedFilters.count { it.value.isNotBlank() }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // ── Search + Quick Filters ──────────────────────
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CardWhite)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Search bar
                    OutlinedTextField(
                        value         = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder   = {
                            Text("Cari kode atau deskripsi arsip...",
                                fontSize = 13.sp, color = TextHint)
                        },
                        leadingIcon = {
                            Icon(Icons.Filled.Search, null,
                                tint = TextHint, modifier = Modifier.size(20.dp))
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
                        colors     = arsipFieldColors(),
                        modifier   = Modifier.fillMaxWidth().height(46.dp)
                    )

                    // Modul filter
                    Column {
                        Text("MODUL",
                            fontSize = 9.sp, fontWeight = FontWeight.Bold,
                            color = TextHint, letterSpacing = 0.7.sp,
                            modifier = Modifier.padding(bottom = 5.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(moduls) { modul ->
                                QuickFilterPill(
                                    label    = modul,
                                    isActive = activeModul == modul,
                                    onClick  = { activeModul = modul }
                                )
                            }
                        }
                    }

                    // Tahun filter
                    Column {
                        Text("TAHUN",
                            fontSize = 9.sp, fontWeight = FontWeight.Bold,
                            color = TextHint, letterSpacing = 0.7.sp,
                            modifier = Modifier.padding(bottom = 5.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(years) { year ->
                                QuickFilterPill(
                                    label    = year,
                                    isActive = activeYear == year,
                                    onClick  = { activeYear = year }
                                )
                            }
                        }
                    }
                }

                HorizontalDivider(thickness = 0.5.dp, color = BorderGray)

                // ── Info bar — jumlah data + kolom aktif ─────
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF5F7F5))
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Text(
                        "${filteredList.size} dari ${archivesList.size} arsip",
                        fontSize   = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color      = TextBody
                    )
                    Row(
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Filled.ViewColumn, null,
                            tint = TextHint, modifier = Modifier.size(12.dp))
                        Text(
                            "${visibleColumns.size} / ${defaultColumns.size} kolom",
                            fontSize = 10.sp,
                            color    = TextHint
                        )
                    }
                }

                // ── Excel Table / Loading / Error ────────────
                when (val state = uiState) {
                    is ResultState.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxWidth().weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = GreenPrimary)
                        }
                    }
                    is ResultState.Error -> {
                        Box(
                            modifier = Modifier.fillMaxWidth().weight(1f).padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = state.exception.message ?: "Terjadi kesalahan loading data",
                                color = DangerText,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    is ResultState.Success -> {
                        ExcelTable(
                            columns     = visibleColumns,
                            data        = filteredList,
                            totalWidth  = totalWidth,
                            modifier    = Modifier.weight(1f)
                        )
                    }
                }

                // ── Pagination bar ──────────────────────────
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CardWhite)
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Text("Halaman 1 dari 1",
                        fontSize = 11.sp, color = TextHint)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PageButton(Icons.Filled.ChevronLeft)
                        Text("1", fontSize = 12.sp,
                            fontWeight = FontWeight.Bold, color = TextHead)
                        PageButton(Icons.Filled.ChevronRight)
                    }
                }
            }
        }

        // ── Column Selector Bottom Sheet ─────────────────────
        if (showColumnSheet) {
            ColumnSelectorSheet(
                visibleKeys = visibleColumnKeys,
                onToggle    = { key ->
                    if (key in visibleColumnKeys) visibleColumnKeys.remove(key)
                    else visibleColumnKeys.add(key)
                },
                onReset     = {
                    visibleColumnKeys.clear()
                    visibleColumnKeys.addAll(defaultColumns.map { it.key })
                },
                onDismiss   = { showColumnSheet = false }
            )
        }

        // ── Filter Advanced Bottom Sheet ─────────────────────
        if (showFilterSheet) {
            FilterAdvancedSheet(
                filters    = advancedFilters,
                onAdd      = {
                    val newId = (advancedFilters.maxOfOrNull { it.id } ?: 0) + 1
                    advancedFilters.add(FilterCondition(newId, "kode", ""))
                },
                onRemove   = { id ->
                    advancedFilters.removeAll { it.id == id }
                },
                onUpdate   = { id, field, value ->
                    val index = advancedFilters.indexOfFirst { it.id == id }
                    if (index >= 0) {
                        advancedFilters[index] = advancedFilters[index].copy(
                            field = field, value = value
                        )
                    }
                },
                onReset    = { advancedFilters.clear() },
                onDismiss  = { showFilterSheet = false }
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  Top Bar
// ─────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ArsipTopBar(
    onMenuClick: () -> Unit,
    onColumnsClick: () -> Unit,
    onFilterClick: () -> Unit,
    activeFilterCount: Int
) {
    TopAppBar(
        title = {
            Column {
                Text("Daftar Arsip",
                    fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text("Database arsip BPKPAD",
                    fontSize = 10.sp, color = Color.White.copy(alpha = 0.6f))
            }
        },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(Icons.Filled.Menu, "Buka menu", tint = Color.White)
            }
        },
        actions = {
            // Column selector button
            IconButton(onClick = onColumnsClick) {
                Icon(Icons.Filled.ViewColumn, "Kolom", tint = Color.White)
            }
            // Filter button with badge
            Box {
                IconButton(onClick = onFilterClick) {
                    Icon(Icons.Filled.FilterAlt, "Filter Lanjutan", tint = Color.White)
                }
                if (activeFilterCount > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 6.dp, end = 6.dp)
                            .size(16.dp)
                            .background(AmberText, CircleShape)
                            .border(1.dp, GreenPrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("$activeFilterCount",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White)
                    }
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = GreenPrimary)
    )
}

// ─────────────────────────────────────────────────────────────
//  Excel Table — sticky header + horizontal scroll
// ─────────────────────────────────────────────────────────────
@Composable
private fun ExcelTable(
    columns: List<TableColumn>,
    data: List<Arsip>,
    totalWidth: Dp,
    modifier: Modifier = Modifier
) {
    val horizontalScroll = rememberScrollState()

    Column(modifier = modifier.fillMaxWidth()) {
        // ── Sticky Header ─────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(GreenPrimary)
                .horizontalScroll(horizontalScroll)
                .width(totalWidth)
        ) {
            columns.forEach { col ->
                Box(
                    modifier = Modifier
                        .width(col.width)
                        .height(44.dp)
                        .border(0.5.dp, Color.White.copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        col.label.uppercase(),
                        fontSize      = 10.sp,
                        fontWeight    = FontWeight.Bold,
                        color         = Color.White,
                        letterSpacing = 0.4.sp,
                        maxLines      = 1,
                        overflow      = TextOverflow.Ellipsis
                    )
                }
            }
        }

        // ── Data rows ────────────────────────────────────────
        if (data.isEmpty()) {
            Column(
                modifier              = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(32.dp),
                horizontalAlignment   = Alignment.CenterHorizontally,
                verticalArrangement   = Arrangement.Center
            ) {
                Icon(Icons.Filled.SearchOff, null,
                    tint = TextHint, modifier = Modifier.size(48.dp))
                Spacer(Modifier.height(10.dp))
                Text("Tidak ada arsip ditemukan",
                    fontSize   = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = TextBody)
                Text("Coba ubah filter atau pencarian",
                    fontSize = 11.sp, color = TextHint,
                    modifier = Modifier.padding(top = 4.dp))
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                items(data.size) { index ->
                    val item   = data[index]
                    val isEven = index % 2 == 0
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(if (isEven) CardWhite else Color(0xFFFAFCFA))
                            .horizontalScroll(horizontalScroll)
                            .width(totalWidth)
                            .border(0.5.dp, BorderGray)
                    ) {
                        columns.forEach { col ->
                            ExcelCell(
                                column = col,
                                item   = item,
                                number = index + 1
                            )
                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  Excel Cell — render value based on column key
// ─────────────────────────────────────────────────────────────
@Composable
private fun ExcelCell(
    column: TableColumn,
    item: Arsip,
    number: Int
) {
    Box(
        modifier = Modifier
            .width(column.width)
            .height(48.dp)
            .border(0.5.dp, BorderGray)
            .padding(horizontal = 10.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        when (column.key) {
            "no" -> Text(
                "$number",
                fontSize   = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color      = TextHint
            )
            "kode" -> Box(
                modifier = Modifier
                    .background(GreenLight, RoundedCornerShape(9999.dp))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(item.kode,
                    fontSize   = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color      = GreenPrimary)
            }
            "deskripsi" -> Text(
                item.deskripsi,
                fontSize   = 11.sp,
                color      = TextHead,
                maxLines   = 2,
                overflow   = TextOverflow.Ellipsis,
                lineHeight = 14.sp
            )
            "keterangan" -> {
                val bg    = if (item.keterangan == "Musnah") DangerBg   else GreenLight
                val color = if (item.keterangan == "Musnah") DangerText else GreenPrimary
                Box(
                    modifier = Modifier
                        .background(bg, RoundedCornerShape(9999.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(item.keterangan,
                        fontSize   = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color      = color)
                }
            }
            "sumber" -> {
                val (bg, color) = when (item.sumber) {
                    "Keuangan"     -> GreenLight to GreenPrimary
                    "Non-Keuangan" -> BlueBg     to BlueText
                    "Peminjaman"   -> Color(0xFFF3E8FF) to Color(0xFF6B21A8)
                    else           -> Color(0xFFF3F5F3) to TextBody
                }
                Box(
                    modifier = Modifier
                        .background(bg, RoundedCornerShape(9999.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(item.sumber,
                        fontSize   = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color      = color)
                }
            }
            else -> Text(
                when (column.key) {
                    "tahun"          -> item.tahun
                    "tingkat"        -> item.tingkat
                    "volume"         -> item.volume
                    "retensiAktif"   -> item.retensiAktif
                    "retensiInaktif" -> item.retensiInaktif
                    else             -> ""
                },
                fontSize = 11.sp,
                color    = TextBody,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  Quick Filter Pill
// ─────────────────────────────────────────────────────────────
@Composable
private fun QuickFilterPill(
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
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(label,
            fontSize   = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color      = if (isActive) Color.White else TextBody)
    }
}

@Composable
private fun PageButton(icon: ImageVector) {
    Box(
        modifier = Modifier
            .size(28.dp)
            .background(CardWhite, RoundedCornerShape(8.dp))
            .border(1.dp, BorderGray, RoundedCornerShape(8.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication        = null,
                onClick           = {}
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, null, tint = TextBody, modifier = Modifier.size(14.dp))
    }
}

// ═════════════════════════════════════════════════════════════
//  COLUMN SELECTOR BOTTOM SHEET
// ═════════════════════════════════════════════════════════════
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ColumnSelectorSheet(
    visibleKeys: List<String>,
    onToggle: (String) -> Unit,
    onReset: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState       = sheetState,
        containerColor   = CardWhite,
        dragHandle       = { BottomSheetDefaults.DragHandle(color = BorderGray) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Pilih Kolom",
                        fontSize = 17.sp, fontWeight = FontWeight.Bold,
                        color = TextHead)
                    Text("Centang kolom yang ingin ditampilkan",
                        fontSize = 11.sp, color = TextBody,
                        modifier = Modifier.padding(top = 2.dp))
                }
                Text("Reset",
                    fontSize = 12.sp, fontWeight = FontWeight.SemiBold,
                    color = GreenPrimary,
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onReset
                    ))
            }

            Spacer(Modifier.height(16.dp))

            // List of columns with checkboxes
            Column(
                modifier = Modifier
                    .border(1.dp, BorderGray, RoundedCornerShape(12.dp))
                    .background(CardWhite, RoundedCornerShape(12.dp))
            ) {
                defaultColumns.forEachIndexed { index, col ->
                    val isChecked = col.key in visibleKeys
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(
                                enabled = !col.sticky,
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = { if (!col.sticky) onToggle(col.key) }
                            )
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(11.dp)
                    ) {
                        // Checkbox
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .background(
                                    if (isChecked) GreenPrimary else CardWhite,
                                    RoundedCornerShape(7.dp)
                                )
                                .border(
                                    1.5.dp,
                                    if (isChecked) GreenPrimary else BorderGray,
                                    RoundedCornerShape(7.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isChecked) {
                                Icon(Icons.Filled.Check, null,
                                    tint     = Color.White,
                                    modifier = Modifier.size(14.dp))
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(col.label,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (col.sticky) TextHint else TextHead)
                            if (col.sticky) {
                                Text("Wajib ditampilkan",
                                    fontSize = 10.sp,
                                    color = TextHint,
                                    modifier = Modifier.padding(top = 1.dp))
                            }
                        }

                        if (col.sticky) {
                            Icon(Icons.Filled.Lock, null,
                                tint = TextHint, modifier = Modifier.size(14.dp))
                        }
                    }
                    if (index != defaultColumns.lastIndex) {
                        HorizontalDivider(thickness = 0.5.dp, color = BorderGray)
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // Apply button
            Button(
                onClick  = onDismiss,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape    = RoundedCornerShape(12.dp),
                colors   = ButtonDefaults.buttonColors(
                    containerColor = GreenPrimary,
                    contentColor   = Color.White
                )
            ) {
                Icon(Icons.Filled.Check, null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("Terapkan", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ═════════════════════════════════════════════════════════════
//  FILTER ADVANCED BOTTOM SHEET
// ═════════════════════════════════════════════════════════════
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterAdvancedSheet(
    filters: List<FilterCondition>,
    onAdd: () -> Unit,
    onRemove: (Int) -> Unit,
    onUpdate: (Int, String, String) -> Unit,
    onReset: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scroll     = rememberScrollState()

    val fieldOptions = listOf(
        "kode"       to "Kode Klasifikasi",
        "deskripsi"  to "Deskripsi",
        "tahun"      to "Tahun",
        "tingkat"    to "Tingkat",
        "keterangan" to "Keterangan",
        "sumber"     to "Sumber Modul"
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState       = sheetState,
        containerColor   = CardWhite,
        dragHandle       = { BottomSheetDefaults.DragHandle(color = BorderGray) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 20.dp)
                .verticalScroll(scroll)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Filter Lanjutan",
                        fontSize = 17.sp, fontWeight = FontWeight.Bold, color = TextHead)
                    Text("Tambahkan kondisi untuk pencarian spesifik",
                        fontSize = 11.sp, color = TextBody,
                        modifier = Modifier.padding(top = 2.dp))
                }
                if (filters.isNotEmpty()) {
                    Text("Reset",
                        fontSize = 12.sp, fontWeight = FontWeight.SemiBold,
                        color = DangerText,
                        modifier = Modifier.clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onReset
                        ))
                }
            }

            Spacer(Modifier.height(16.dp))

            // Filter conditions
            if (filters.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF5F7F5), RoundedCornerShape(12.dp))
                        .border(1.dp, BorderGray, RoundedCornerShape(12.dp))
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Filled.FilterAlt, null,
                        tint = TextHint, modifier = Modifier.size(28.dp))
                    Text("Belum ada filter",
                        fontSize = 12.sp, fontWeight = FontWeight.SemiBold,
                        color = TextBody)
                    Text("Tap tombol di bawah untuk menambah kondisi",
                        fontSize = 10.sp, color = TextHint)
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    filters.forEachIndexed { index, cond ->
                        FilterConditionRow(
                            number       = index + 1,
                            condition    = cond,
                            fieldOptions = fieldOptions,
                            onUpdate     = onUpdate,
                            onRemove     = { onRemove(cond.id) }
                        )
                        if (index != filters.lastIndex) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .background(GreenLight, RoundedCornerShape(9999.dp))
                                        .padding(horizontal = 9.dp, vertical = 2.dp)
                                ) {
                                    Text("DAN",
                                        fontSize = 9.sp, fontWeight = FontWeight.Bold,
                                        color = GreenPrimary, letterSpacing = 0.5.sp)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // Add filter button
            OutlinedButton(
                onClick  = onAdd,
                modifier = Modifier.fillMaxWidth().height(44.dp),
                shape    = RoundedCornerShape(10.dp),
                border   = BorderStroke(1.5.dp, GreenMid),
                colors   = ButtonDefaults.outlinedButtonColors(
                    contentColor   = GreenPrimary,
                    containerColor = GreenLight.copy(alpha = 0.3f)
                )
            ) {
                Icon(Icons.Filled.Add, null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("Tambah Kondisi", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(Modifier.height(20.dp))

            // Apply button
            Button(
                onClick  = onDismiss,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape    = RoundedCornerShape(12.dp),
                colors   = ButtonDefaults.buttonColors(
                    containerColor = GreenPrimary,
                    contentColor   = Color.White
                )
            ) {
                Icon(Icons.Filled.Search, null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("Terapkan Filter", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  Filter Condition Row
// ─────────────────────────────────────────────────────────────
@Composable
private fun FilterConditionRow(
    number: Int,
    condition: FilterCondition,
    fieldOptions: List<Pair<String, String>>,
    onUpdate: (Int, String, String) -> Unit,
    onRemove: () -> Unit
) {
    var fieldDropdown by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF1F8F1), RoundedCornerShape(12.dp))
            .border(1.dp, GreenMid, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        // Header row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .background(GreenPrimary, RoundedCornerShape(7.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("$number", fontSize = 10.sp,
                        fontWeight = FontWeight.Bold, color = Color.White)
                }
                Text("Kondisi #$number",
                    fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
                    color = GreenPrimary)
            }
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .background(CardWhite, RoundedCornerShape(8.dp))
                    .border(1.dp, BorderGray, RoundedCornerShape(8.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onRemove
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Delete, null,
                    tint = DangerText, modifier = Modifier.size(14.dp))
            }
        }

        Spacer(Modifier.height(10.dp))

        // Field selector (dropdown style)
        Box {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CardWhite, RoundedCornerShape(8.dp))
                    .border(1.dp, BorderGray, RoundedCornerShape(8.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { fieldDropdown = true }
                    )
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("KOLOM", fontSize = 8.sp, fontWeight = FontWeight.Bold,
                        color = TextHint, letterSpacing = 0.6.sp)
                    Text(
                        fieldOptions.firstOrNull { it.first == condition.field }?.second
                            ?: "Pilih kolom",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextHead,
                        modifier = Modifier.padding(top = 1.dp)
                    )
                }
                Icon(Icons.Filled.ArrowDropDown, null,
                    tint = TextHint, modifier = Modifier.size(20.dp))
            }
            DropdownMenu(
                expanded         = fieldDropdown,
                onDismissRequest = { fieldDropdown = false },
                modifier         = Modifier.background(CardWhite)
            ) {
                fieldOptions.forEach { (key, label) ->
                    DropdownMenuItem(
                        text = {
                            Text(label, fontSize = 13.sp,
                                fontWeight = if (key == condition.field)
                                    FontWeight.Bold else FontWeight.Normal,
                                color = if (key == condition.field)
                                    GreenPrimary else TextHead)
                        },
                        onClick = {
                            onUpdate(condition.id, key, condition.value)
                            fieldDropdown = false
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        // Operator + Value
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Operator (fixed = for now)
            Box(
                modifier = Modifier
                    .background(GreenPrimary, RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 13.dp)
            ) {
                Text("BERISI",
                    fontSize = 10.sp, fontWeight = FontWeight.Bold,
                    color = Color.White, letterSpacing = 0.5.sp)
            }

            // Value input
            OutlinedTextField(
                value         = condition.value,
                onValueChange = { onUpdate(condition.id, condition.field, it) },
                placeholder   = {
                    Text("Masukkan nilai...",
                        fontSize = 12.sp, color = TextHint)
                },
                singleLine = true,
                shape      = RoundedCornerShape(8.dp),
                colors     = arsipFieldColors(),
                modifier   = Modifier.weight(1f).height(48.dp)
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  Field Colors
// ─────────────────────────────────────────────────────────────
@Composable
private fun arsipFieldColors() = OutlinedTextFieldDefaults.colors(
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
fun DaftarArsipPreview() {
    DaftarArsipScreen()
}