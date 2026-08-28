package com.bpkpad.siarsip.ui.screens.pemusnahan

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bpkpad.siarsip.core.utils.ResultState
import com.bpkpad.siarsip.feature.arsip.domain.model.Arsip
import com.bpkpad.siarsip.feature.arsip.presentation.BuatBerkasUsulMusnahViewModel
import com.bpkpad.siarsip.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuatBerkasUsulMusnahScreen(
    onBack: () -> Unit = {},
    onSimpan: () -> Unit = {},
    viewModel: BuatBerkasUsulMusnahViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val availableArchivesState by viewModel.availableArchives.collectAsState()
    val totalCount by viewModel.totalCount.collectAsState()
    val currentPage by viewModel.currentPage.collectAsState()
    val pageSize by viewModel.pageSize.collectAsState()

    val saveState by viewModel.saveState.collectAsState()
    val nextNomor by viewModel.nextProposalNumber.collectAsState()

    val selectedModul by viewModel.sumberFilter.collectAsState()
    val pickerYear by viewModel.tahunFilter.collectAsState()
    val pickerSearch by viewModel.searchQuery.collectAsState()

    val availableList = (availableArchivesState as? ResultState.Success)?.data ?: emptyList()

    var perihal by remember { mutableStateOf("") }
    val selectedIds = remember { mutableStateListOf<String>() }
    val selectedArchivesMap = remember { mutableStateMapOf<String, Arsip>() }

    val modulList = listOf("Keuangan", "Non-Keuangan", "Peminjaman")
    val pickerYears = listOf("Semua", "2026", "2025", "2024", "2023", "2022", "2021", "2020", "2019", "2018", "2017", "2016", "2015")

    val totalPages = if (totalCount == 0) 1 else (totalCount + pageSize - 1) / pageSize
    val selectedItems = selectedArchivesMap.values.toList()

    val todayDate = remember {
        java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.US).format(java.util.Date())
    }

    val isLoading = saveState is ResultState.Loading

    LaunchedEffect(saveState) {
        if (saveState is ResultState.Error) {
            Toast.makeText(context, (saveState as ResultState.Error).exception.message ?: "Gagal menyimpan berkas", Toast.LENGTH_LONG).show()
            viewModel.resetSaveState()
        }
    }

    Scaffold(
        containerColor = BgDashboard,
        topBar = {
            BuatBerkasTopBar(onBack = onBack)
        },
        bottomBar = {
            BuatBerkasBottomBar(
                selectedCount = selectedIds.size,
                isLoading = isLoading,
                onBatal = onBack,
                onSimpan = {
                    val perihalErr = com.bpkpad.siarsip.core.utils.PemusnahanInputRules.validatePerihal(perihal)
                    if (perihalErr != null) {
                        Toast.makeText(context, perihalErr, Toast.LENGTH_SHORT).show()
                    } else if (selectedIds.isEmpty()) {
                        Toast.makeText(context, "Pilih minimal 1 arsip", Toast.LENGTH_SHORT).show()
                    } else {
                        viewModel.createProposal(
                            tanggal = todayDate,
                            unitPengolah = "BPKPAD Balangan",
                            sumberModul = selectedModul,
                            perihal = com.bpkpad.siarsip.core.utils.PemusnahanInputRules.sanitize(perihal),
                            archiveIds = selectedIds,
                            onSuccess = onSimpan
                        )
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 14.dp,
                bottom = 8.dp
            ),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            // Section 1: Detail Berkas
            item {
                SectionHeader(
                    icon = Icons.Filled.Description,
                    title = "Detail berkas",
                    subtitle = "Informasi identitas berkas usul musnah"
                )
                Spacer(Modifier.height(8.dp))
                FormCard {
                    AutoNomorField(nextNomor)
                    Spacer(Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        FormField(
                            label = "TANGGAL USUL",
                            value = todayDate,
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier.weight(1f)
                        )
                        FormField(
                            label = "UNIT PENGOLAH",
                            value = "BPKPAD Balangan",
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(Modifier.height(12.dp))

                    Text("SUMBER MODUL", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextHint)
                    Spacer(Modifier.height(6.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(modulList) { modul ->
                            FilterPill(
                                label = modul,
                                isActive = selectedModul == modul,
                                onClick = { viewModel.setSumberFilter(modul) }
                            )
                        }
                    }

                    Spacer(Modifier.height(12.dp))
                    FormField(
                        label = "PERIHAL",
                        value = perihal,
                        onValueChange = { perihal = it },
                        placeholder = "Masukan perihal usul pemusnahan...",
                        singleLine = false,
                        maxLines = 3
                    )
                }
            }

            // Section 2: Pilih Arsip Yang Akan Dimusnahkan
            item {
                SectionHeader(
                    icon = Icons.Filled.LibraryAddCheck,
                    title = "Pilih arsip yang akan dimusnahkan",
                    subtitle = "Cari dan centang arsip dari database kearsipan BPKPAD"
                )
                Spacer(Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardWhite),
                    border = BorderStroke(1.dp, BorderGray)
                ) {
                    // Picker Header (Filter Search, Tahun, Modul)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF5F7F5))
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Search bar
                        OutlinedTextField(
                            value = pickerSearch,
                            onValueChange = { viewModel.setSearchQuery(it) },
                            placeholder = { Text("Cari kode atau nama arsip...", fontSize = 12.sp, color = TextHint) },
                            leadingIcon = { Icon(Icons.Filled.Search, null, tint = TextHint, modifier = Modifier.size(18.dp)) },
                            trailingIcon = if (pickerSearch.isNotEmpty()) {
                                {
                                    IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                        Icon(Icons.Filled.Close, null, tint = TextHint, modifier = Modifier.size(16.dp))
                                    }
                                }
                            } else null,
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            colors = fieldColors(),
                            modifier = Modifier.fillMaxWidth().height(44.dp)
                        )

                        // Filter TAHUN
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text("TAHUN:", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextHint)
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                items(pickerYears) { y ->
                                    SmallFilterPill(
                                        label = y,
                                        isActive = pickerYear == y,
                                        onClick = { viewModel.setTahunFilter(y) }
                                    )
                                }
                            }
                        }
                    }

                    // Count bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Menampilkan ${availableList.size} dari total $totalCount arsip tersedia",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextBody
                        )
                    }
                    HorizontalDivider(thickness = 0.5.dp, color = BorderGray)

                    // Arsip Rows View
                    when (availableArchivesState) {
                        is ResultState.Loading -> {
                            Box(
                                modifier = Modifier.fillMaxWidth().height(160.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = GreenPrimary)
                            }
                        }
                        is ResultState.Error -> {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = (availableArchivesState as ResultState.Error).exception.message ?: "Gagal memuat arsip",
                                    color = DangerText,
                                    fontSize = 12.sp
                                )
                            }
                        }
                        is ResultState.Success -> {
                            if (availableList.isEmpty()) {
                                Column(
                                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(Icons.Filled.SearchOff, null, tint = TextHint, modifier = Modifier.size(32.dp))
                                    Text("Tidak ada arsip ditemukan", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextBody)
                                    Text("Coba ubah kata kunci atau filter tahun", fontSize = 10.sp, color = TextHint)
                                }
                            } else {
                                availableList.forEachIndexed { index, item ->
                                    val isSelected = item.id in selectedIds
                                    PickerRow(
                                        item = item,
                                        isSelected = isSelected,
                                        isLast = index == availableList.lastIndex,
                                        onClick = {
                                            if (isSelected) {
                                                selectedIds.remove(item.id)
                                                selectedArchivesMap.remove(item.id)
                                            } else {
                                                selectedIds.add(item.id)
                                                selectedArchivesMap[item.id] = item
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Pagination Footer Controls
                    HorizontalDivider(thickness = 0.5.dp, color = BorderGray)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF9FAF9))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Tampil per page
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text("Tampil:", fontSize = 10.sp, color = TextHint)
                            listOf(25, 50, 100).forEach { size ->
                                val isSel = size == pageSize
                                Box(
                                    modifier = Modifier
                                        .background(
                                            if (isSel) GreenPrimary else Color(0xFFE0E0E0),
                                            RoundedCornerShape(6.dp)
                                        )
                                        .clickable { viewModel.setPageSize(size) }
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        "$size",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSel) Color.White else TextBody
                                    )
                                }
                            }
                        }

                        // Page text
                        Text(
                            "Halaman ${currentPage + 1} dari $totalPages",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextBody
                        )

                        // Next/Prev Buttons
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            PageIconButton(
                                icon = Icons.Filled.ChevronLeft,
                                enabled = currentPage > 0,
                                onClick = { viewModel.previousPage() }
                            )
                            PageIconButton(
                                icon = Icons.Filled.ChevronRight,
                                enabled = currentPage < totalPages - 1,
                                onClick = { viewModel.nextPage(totalPages) }
                            )
                        }
                    }

                    // Selection Bar Footer — muncul jika ada yang dipilih
                    if (selectedIds.isNotEmpty()) {
                        HorizontalDivider(thickness = 1.dp, color = GreenMid)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF1F8F1))
                                .padding(horizontal = 16.dp, vertical = 11.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(9.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .defaultMinSize(minWidth = 26.dp)
                                        .height(26.dp)
                                        .background(GreenPrimary, RoundedCornerShape(9999.dp))
                                        .padding(horizontal = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "${selectedIds.size}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                                Text(
                                    "arsip dipilih",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = GreenPrimary
                                )
                            }
                            Text(
                                "Hapus semua",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = DangerText,
                                modifier = Modifier.clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    selectedIds.clear()
                                    selectedArchivesMap.clear()
                                }
                            )
                        }
                    }
                }
            }

            // Section 3: Ringkasan Arsip Terpilih
            item {
                SectionHeader(
                    icon = Icons.Filled.CheckCircle,
                    iconBg = if (selectedItems.isEmpty()) Color(0xFFF0F2F0) else GreenPrimary,
                    iconTint = if (selectedItems.isEmpty()) TextHint else Color.White,
                    title = "Arsip terpilih",
                    subtitle = if (selectedItems.isEmpty())
                        "Belum ada arsip yang dipilih"
                    else
                        "${selectedItems.size} arsip siap dimasukkan ke berkas"
                )
                Spacer(Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardWhite),
                    border = BorderStroke(1.dp, BorderGray)
                ) {
                    if (selectedItems.isEmpty()) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Filled.Inbox, null, tint = GreenMid, modifier = Modifier.size(32.dp))
                            Text(
                                "Centang arsip di atas untuk\nmenambahkannya ke berkas ini",
                                fontSize = 12.sp,
                                color = TextHint,
                                lineHeight = 18.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        selectedItems.forEachIndexed { index, item ->
                            SelectedItemRow(
                                number = index + 1,
                                item = item,
                                isLast = index == selectedItems.lastIndex,
                                onRemove = {
                                    selectedIds.remove(item.id)
                                    selectedArchivesMap.remove(item.id)
                                }
                            )
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(8.dp)) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BuatBerkasTopBar(onBack: () -> Unit) {
    TopAppBar(
        title = {
            Column {
                Text("Buat Berkas Usul Musnah", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text("Isi detail dan pilih arsip", fontSize = 10.sp, color = Color.White.copy(alpha = 0.6f))
            }
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Kembali", tint = Color.White)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = GreenPrimary)
    )
}

@Composable
private fun SectionHeader(
    icon: ImageVector,
    iconBg: Color = GreenPrimary,
    iconTint: Color = Color.White,
    title: String,
    subtitle: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(iconBg, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(16.dp))
        }
        Column {
            Text(title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextHead)
            Text(subtitle, fontSize = 10.sp, color = TextHint)
        }
    }
}

@Composable
private fun FormCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        border = BorderStroke(1.dp, BorderGray)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            content = content
        )
    }
}

@Composable
private fun AutoNomorField(nomor: String) {
    Column {
        Text("NOMOR BERKAS USUL (OTOMATIS)", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextHint, letterSpacing = 0.7.sp)
        Spacer(Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .background(Color(0xFFF1F8F1), RoundedCornerShape(10.dp))
                .border(1.dp, GreenMid, RoundedCornerShape(10.dp))
                .padding(horizontal = 12.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(nomor, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = GreenPrimary)
                Icon(Icons.Filled.Lock, null, tint = GreenPrimary, modifier = Modifier.size(14.dp))
            }
        }
    }
}

@Composable
private fun FormField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    readOnly: Boolean = false,
    singleLine: Boolean = true,
    maxLines: Int = 1,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(label, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextHint, letterSpacing = 0.7.sp)
        Spacer(Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, fontSize = 12.sp, color = TextHint) },
            readOnly = readOnly,
            singleLine = singleLine,
            maxLines = maxLines,
            shape = RoundedCornerShape(10.dp),
            colors = fieldColors(),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun FilterPill(label: String, isActive: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .background(if (isActive) GreenPrimary else Color(0xFFF0F2F0), RoundedCornerShape(9999.dp))
            .border(1.dp, if (isActive) GreenPrimary else BorderGray, RoundedCornerShape(9999.dp))
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = if (isActive) Color.White else TextBody)
    }
}

@Composable
private fun SmallFilterPill(label: String, isActive: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .background(if (isActive) GreenPrimary else Color.White, RoundedCornerShape(9999.dp))
            .border(1.dp, if (isActive) GreenPrimary else BorderGray, RoundedCornerShape(9999.dp))
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(label, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = if (isActive) Color.White else TextBody)
    }
}

@Composable
private fun PageIconButton(icon: ImageVector, enabled: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(26.dp)
            .background(if (enabled) CardWhite else Color(0xFFE0E0E0), RoundedCornerShape(6.dp))
            .border(1.dp, if (enabled) BorderGray else Color.LightGray, RoundedCornerShape(6.dp))
            .clickable(enabled = enabled, interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, null, tint = if (enabled) TextBody else Color.Gray, modifier = Modifier.size(14.dp))
    }
}

@Composable
private fun PickerRow(
    item: Arsip,
    isSelected: Boolean,
    isLast: Boolean,
    onClick: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(if (isSelected) Color(0xFFF1F8F1) else CardWhite)
                .clickable(onClick = onClick)
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Checkbox(
                checked = isSelected,
                onCheckedChange = { onClick() },
                colors = CheckboxDefaults.colors(
                    checkedColor = GreenPrimary,
                    uncheckedColor = TextHint
                ),
                modifier = Modifier.size(20.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(item.fullKode, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextHead)
                    Text(item.tahun, fontSize = 10.sp, color = TextHint)
                }
                Spacer(Modifier.height(2.dp))
                Text(item.deskripsi, fontSize = 11.sp, color = TextBody, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
        }
        if (!isLast) {
            HorizontalDivider(thickness = 0.5.dp, color = BorderGray)
        }
    }
}

@Composable
private fun SelectedItemRow(
    number: Int,
    item: Arsip,
    isLast: Boolean,
    onRemove: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .background(GreenLight, RoundedCornerShape(9999.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("$number", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GreenPrimary)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(item.fullKode, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextHead)
                Text(item.deskripsi, fontSize = 10.sp, color = TextHint, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            IconButton(onClick = onRemove, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Filled.Close, contentDescription = "Hapus", tint = DangerText, modifier = Modifier.size(16.dp))
            }
        }
        if (!isLast) {
            HorizontalDivider(thickness = 0.5.dp, color = BorderGray)
        }
    }
}

@Composable
private fun BuatBerkasBottomBar(
    selectedCount: Int,
    isLoading: Boolean,
    onBatal: () -> Unit,
    onSimpan: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = CardWhite,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = onBatal,
                enabled = !isLoading,
                modifier = Modifier.weight(1f).height(46.dp),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, BorderGray)
            ) {
                Text("Batal", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextBody)
            }
            Button(
                onClick = onSimpan,
                enabled = !isLoading && selectedCount > 0,
                modifier = Modifier.weight(2f).height(46.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GreenPrimary,
                    disabledContainerColor = Color(0xFFC2D6C2)
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Icon(Icons.Filled.Save, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(
                        if (selectedCount > 0) "Simpan Berkas ($selectedCount)" else "Pilih Arsip Dulu",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
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