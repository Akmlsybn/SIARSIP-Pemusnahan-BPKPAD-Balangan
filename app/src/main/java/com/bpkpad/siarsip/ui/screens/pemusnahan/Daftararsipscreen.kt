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

data class TableColumn(
    val key: String,
    val label: String,
    val width: Dp,
    val sticky: Boolean = false
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

data class FilterCondition(
    val id: Int,
    val field: String,
    val value: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DaftarArsipScreen(
    beritaAcaraId: String? = null,
    onNavigate: (String) -> Unit = {},
    viewModel: DaftarArsipViewModel = hiltViewModel()
) {
    LaunchedEffect(beritaAcaraId) {
        viewModel.setBeritaAcaraFilter(beritaAcaraId)
    }

    val archivesUiState by viewModel.archivesUiState.collectAsState()
    val totalCount by viewModel.totalCount.collectAsState()
    val currentPage by viewModel.currentPage.collectAsState()
    val pageSize by viewModel.pageSize.collectAsState()

    val searchQuery by viewModel.searchQuery.collectAsState()
    val activeModul by viewModel.sumberFilter.collectAsState()
    val activeYear by viewModel.tahunFilter.collectAsState()

    val archivesList = (archivesUiState as? ResultState.Success)?.data ?: emptyList()

    val visibleColumnKeys = remember { mutableStateListOf<String>().apply {
        addAll(defaultColumns.map { it.key })
    } }
    val advancedFilters = remember { mutableStateListOf<FilterCondition>() }

    var showColumnSheet by remember { mutableStateOf(false) }
    var showFilterSheet by remember { mutableStateOf(false) }

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val moduls = listOf("Semua", "Keuangan", "Non-Keuangan", "Peminjaman")
    val years = listOf("Semua", "2026", "2025", "2024", "2023", "2022", "2021", "2020", "2019", "2018", "2017", "2016", "2015")

    val totalPages = if (totalCount == 0) 1 else (totalCount + pageSize - 1) / pageSize

    val visibleColumns = defaultColumns.filter { it.key in visibleColumnKeys }
    val totalWidth = visibleColumns.sumOf { it.width.value.toDouble() }.dp

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.fillMaxWidth(0.86f),
                drawerContainerColor = CardWhite,
                drawerShape = RoundedCornerShape(0.dp)
            ) {
                PemusnahanDrawerContent(
                    currentRoute = DrawerRoutes.DAFTAR_ARSIP,
                    onNavigate = { route ->
                        scope.launch { drawerState.close() }
                        onNavigate(route)
                    },
                    onLogout = {
                        scope.launch { drawerState.close() }
                        onNavigate("login")
                    }
                )
            }
        }
    ) {
        Scaffold(
            containerColor = BgDashboard,
            topBar = {
                DaftarArsipTopBar(
                    onOpenDrawer = { scope.launch { drawerState.open() } }
                )
            },
            bottomBar = {
                PemusnahanBottomBar(
                    currentRoute = DrawerRoutes.DAFTAR_ARSIP,
                    onNavigate = onNavigate
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // Toolbar Pencarian & Filter Top Bar
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CardWhite)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Row 1: Search bar + Kolom + Filter tombol
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { viewModel.setSearchQuery(it) },
                            placeholder = { Text("Cari kode atau nama arsip...", fontSize = 12.sp, color = TextHint) },
                            leadingIcon = { Icon(Icons.Filled.Search, null, tint = TextHint, modifier = Modifier.size(18.dp)) },
                            trailingIcon = if (searchQuery.isNotEmpty()) {
                                {
                                    IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                        Icon(Icons.Filled.Close, null, tint = TextHint, modifier = Modifier.size(16.dp))
                                    }
                                }
                            } else null,
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            colors = fieldColors(),
                            modifier = Modifier.weight(1f).height(44.dp)
                        )

                        // Tombol Atur Kolom
                        OutlinedButton(
                            onClick = { showColumnSheet = true },
                            modifier = Modifier.height(44.dp),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, BorderGray),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (visibleColumnKeys.size < defaultColumns.size) GreenLight else CardWhite,
                                contentColor = if (visibleColumnKeys.size < defaultColumns.size) GreenPrimary else TextBody
                            ),
                            contentPadding = PaddingValues(horizontal = 10.dp)
                        ) {
                            Icon(Icons.Filled.ViewColumn, null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Kolom", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }

                        // Tombol Filter Lanjutan
                        OutlinedButton(
                            onClick = { showFilterSheet = true },
                            modifier = Modifier.height(44.dp),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, BorderGray),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (advancedFilters.isNotEmpty()) GreenLight else CardWhite,
                                contentColor = if (advancedFilters.isNotEmpty()) GreenPrimary else TextBody
                            ),
                            contentPadding = PaddingValues(horizontal = 10.dp)
                        ) {
                            Icon(Icons.Filled.FilterList, null, modifier = Modifier.size(16.dp))
                            if (advancedFilters.isNotEmpty()) {
                                Spacer(Modifier.width(4.dp))
                                Box(
                                    modifier = Modifier.size(16.dp).background(GreenPrimary, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("${advancedFilters.size}", fontSize = 9.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    // Row 2: Filter Modul (Sumber) & Filter Tahun
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Filter Modul
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(moduls) { modul ->
                                FilterPill(
                                    label = modul,
                                    isActive = activeModul == modul,
                                    onClick = { viewModel.setSumberFilter(modul) }
                                )
                            }
                        }
                    }

                    // Row 3: Filter Tahun
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("TAHUN:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextHint)
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(years) { year ->
                                FilterPill(
                                    label = year,
                                    isActive = activeYear == year,
                                    onClick = { viewModel.setTahunFilter(year) }
                                )
                            }
                        }
                    }
                }

                HorizontalDivider(thickness = 0.5.dp, color = BorderGray)

                // Info bar — jumlah data + kolom aktif
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF5F7F5))
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Menampilkan ${archivesList.size} dari total $totalCount arsip",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextBody
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Filled.ViewColumn, null, tint = TextHint, modifier = Modifier.size(12.dp))
                        Text(
                            "${visibleColumns.size} / ${defaultColumns.size} kolom",
                            fontSize = 10.sp,
                            color = TextHint
                        )
                    }
                }

                // Table / Loading / Error View
                when (val state = archivesUiState) {
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
                            columns = visibleColumns,
                            data = archivesList,
                            totalWidth = totalWidth,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Pagination Controls Footer (25/50 Data Per Halaman)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CardWhite)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Toggle Ukuran Per Halaman (25 vs 50)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text("Tampil:", fontSize = 11.sp, color = TextHint)
                        listOf(25, 50, 100).forEach { size ->
                            val isSel = size == pageSize
                            Box(
                                modifier = Modifier
                                    .background(
                                        if (isSel) GreenPrimary else Color(0xFFF0F0F0),
                                        RoundedCornerShape(6.dp)
                                    )
                                    .clickable { viewModel.setPageSize(size) }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    "$size",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSel) Color.White else TextBody
                                )
                            }
                        }
                    }

                    // Halaman X dari Y
                    Text(
                        "Halaman ${currentPage + 1} dari $totalPages",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextBody
                    )

                    // Tombol Navigasi Halaman
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PageButton(
                            icon = Icons.Filled.ChevronLeft,
                            enabled = currentPage > 0,
                            onClick = { viewModel.previousPage() }
                        )
                        PageButton(
                            icon = Icons.Filled.ChevronRight,
                            enabled = currentPage < totalPages - 1,
                            onClick = { viewModel.nextPage(totalPages) }
                        )
                    }
                }
            }
        }

        // Column Selector Sheet
        if (showColumnSheet) {
            ColumnSelectorSheet(
                visibleKeys = visibleColumnKeys,
                onToggle = { key ->
                    if (key in visibleColumnKeys) visibleColumnKeys.remove(key)
                    else visibleColumnKeys.add(key)
                },
                onReset = {
                    visibleColumnKeys.clear()
                    visibleColumnKeys.addAll(defaultColumns.map { it.key })
                },
                onDismiss = { showColumnSheet = false }
            )
        }

        // Filter Lanjutan Sheet
        if (showFilterSheet) {
            FilterLanjutanSheet(
                filters = advancedFilters,
                onAddFilter = {
                    val newId = (advancedFilters.maxOfOrNull { it.id } ?: 0) + 1
                    advancedFilters.add(FilterCondition(newId, "kode", ""))
                },
                onRemoveFilter = { id -> advancedFilters.removeAll { it.id == id } },
                onUpdateFilter = { updated ->
                    val idx = advancedFilters.indexOfFirst { it.id == updated.id }
                    if (idx != -1) advancedFilters[idx] = updated
                },
                onReset = { advancedFilters.clear() },
                onDismiss = { showFilterSheet = false }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DaftarArsipTopBar(onOpenDrawer: () -> Unit) {
    TopAppBar(
        title = {
            Column {
                Text("Daftar Berkas Arsip", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text("Database Kearsipan BPKPAD Balangan", fontSize = 10.sp, color = Color.White.copy(alpha = 0.6f))
            }
        },
        navigationIcon = {
            IconButton(onClick = onOpenDrawer) {
                Icon(Icons.Filled.Menu, contentDescription = "Menu", tint = Color.White)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = GreenPrimary)
    )
}

@Composable
private fun FilterPill(
    label: String,
    isActive: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .background(
                if (isActive) GreenPrimary else Color(0xFFF0F2F0),
                RoundedCornerShape(9999.dp)
            )
            .border(
                1.dp,
                if (isActive) GreenPrimary else BorderGray,
                RoundedCornerShape(9999.dp)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 12.dp, vertical = 5.dp)
    ) {
        Text(
            label,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (isActive) Color.White else TextBody
        )
    }
}

@Composable
private fun PageButton(
    icon: ImageVector,
    enabled: Boolean = true,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .background(if (enabled) CardWhite else Color(0xFFE0E0E0), RoundedCornerShape(8.dp))
            .border(1.dp, if (enabled) BorderGray else Color.LightGray, RoundedCornerShape(8.dp))
            .clickable(
                enabled = enabled,
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = if (enabled) TextBody else Color.Gray,
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
private fun ExcelTable(
    columns: List<TableColumn>,
    data: List<Arsip>,
    totalWidth: Dp,
    modifier: Modifier = Modifier
) {
    val horizontalScrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(horizontalScrollState)
    ) {
        // Table Header
        Row(
            modifier = Modifier
                .width(totalWidth)
                .background(Color(0xFFE8EFE8))
                .border(BorderStroke(0.5.dp, BorderGray))
        ) {
            columns.forEach { col ->
                Box(
                    modifier = Modifier
                        .width(col.width)
                        .height(38.dp)
                        .border(BorderStroke(0.5.dp, BorderGray))
                        .padding(horizontal = 8.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        col.label,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextHead,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        // Table Rows
        if (data.isEmpty()) {
            Box(
                modifier = Modifier
                    .width(totalWidth)
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Tidak ada data arsip yang cocok", fontSize = 12.sp, color = TextHint)
            }
        } else {
            LazyColumn(modifier = Modifier.width(totalWidth)) {
                items(data, key = { it.id }) { item ->
                    val index = data.indexOf(item)
                    Row(
                        modifier = Modifier
                            .width(totalWidth)
                            .background(if (index % 2 == 0) CardWhite else Color(0xFFFAFCFA))
                            .border(BorderStroke(0.5.dp, BorderGray))
                    ) {
                        columns.forEach { col ->
                            val cellText = when (col.key) {
                                "no" -> "${index + 1}"
                                "kode" -> item.fullKode
                                "deskripsi" -> item.deskripsi
                                "tahun" -> item.tahun
                                "tingkat" -> item.tingkat
                                "volume" -> item.volume
                                "retensiAktif" -> item.retensiAktif
                                "retensiInaktif" -> item.retensiInaktif
                                "keterangan" -> item.keterangan
                                "sumber" -> item.sumber
                                else -> ""
                            }
                            Box(
                                modifier = Modifier
                                    .width(col.width)
                                    .padding(horizontal = 8.dp, vertical = 8.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Text(
                                    cellText,
                                    fontSize = 11.sp,
                                    color = TextHead,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun fieldColors(): TextFieldColors {
    return OutlinedTextFieldDefaults.colors(
        focusedBorderColor = GreenPrimary,
        unfocusedBorderColor = BorderGray,
        focusedContainerColor = CardWhite,
        unfocusedContainerColor = CardWhite
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ColumnSelectorSheet(
    visibleKeys: List<String>,
    onToggle: (String) -> Unit,
    onReset: () -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Atur Tampilan Kolom", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextHead)
                TextButton(onClick = onReset) {
                    Text("Reset Default", fontSize = 12.sp, color = GreenPrimary)
                }
            }

            defaultColumns.forEach { col ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(col.label, fontSize = 13.sp, color = TextHead)
                    Switch(
                        checked = col.key in visibleKeys,
                        onCheckedChange = { onToggle(col.key) },
                        enabled = !col.sticky
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterLanjutanSheet(
    filters: List<FilterCondition>,
    onAddFilter: () -> Unit,
    onRemoveFilter: (Int) -> Unit,
    onUpdateFilter: (FilterCondition) -> Unit,
    onReset: () -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Filter Lanjutan", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextHead)
                TextButton(onClick = onReset) {
                    Text("Reset Filter", fontSize = 12.sp, color = DangerText)
                }
            }

            if (filters.isEmpty()) {
                Text("Belum ada filter tambahan.", fontSize = 12.sp, color = TextHint)
            } else {
                filters.forEach { cond ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = cond.value,
                            onValueChange = { onUpdateFilter(cond.copy(value = it)) },
                            placeholder = { Text("Nilai filter...", fontSize = 12.sp) },
                            modifier = Modifier.weight(1f).height(44.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = fieldColors()
                        )
                        IconButton(onClick = { onRemoveFilter(cond.id) }) {
                            Icon(Icons.Filled.Delete, null, tint = DangerText)
                        }
                    }
                }
            }

            Button(
                onClick = onAddFilter,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
            ) {
                Icon(Icons.Filled.Add, null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("Tambah Filter Baru", fontSize = 12.sp)
            }
        }
    }
}