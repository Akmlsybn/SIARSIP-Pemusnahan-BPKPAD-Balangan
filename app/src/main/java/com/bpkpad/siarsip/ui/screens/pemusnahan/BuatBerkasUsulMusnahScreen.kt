package com.bpkpad.siarsip.ui.screens.pemusnahan

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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

// ─────────────────────────────────────────────────────────────
//  Screen Utama
// ─────────────────────────────────────────────────────────────
@Composable
fun BuatBerkasUsulMusnahScreen(
    onBack: () -> Unit = {},
    onSimpan: () -> Unit = {},
    viewModel: BuatBerkasUsulMusnahViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val availableArchivesState by viewModel.availableArchives.collectAsState()
    val saveState by viewModel.saveState.collectAsState()
    val nextNomor by viewModel.nextProposalNumber.collectAsState()

    val availableList = (availableArchivesState as? ResultState.Success)?.data ?: emptyList()

    var selectedModul  by remember { mutableStateOf("Keuangan") }
    var perihal        by remember { mutableStateOf("") }
    var pickerSearch   by remember { mutableStateOf("") }
    var pickerFilter   by remember { mutableStateOf("Semua") }
    var pickerYear     by remember { mutableStateOf("Semua") }
    val selectedIds    = remember { mutableStateListOf<String>() }

    val modulList     = listOf("Keuangan", "Non-Keuangan", "Peminjaman")
    val pickerFilters = listOf("Semua", "Musnah", "Permanen")
    val pickerYears   = listOf("Semua", "2026", "2025", "2019", "2018", "2017", "2016")

    val filteredPicker = availableList.filter { item ->
        val matchModul  = item.sumber == selectedModul
        val matchFilter = pickerFilter == "Semua" || item.keterangan == pickerFilter
        val matchYear   = pickerYear == "Semua" || item.tahun == pickerYear
        val matchSearch = pickerSearch.isBlank() ||
                item.kode.contains(pickerSearch, ignoreCase = true) ||
                item.deskripsi.contains(pickerSearch, ignoreCase = true)
        matchModul && matchFilter && matchYear && matchSearch
    }
    val selectedItems = availableList.filter { it.id in selectedIds }

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
                isLoading     = isLoading,
                onBatal       = onBack,
                onSimpan      = {
                    if (perihal.isBlank()) {
                        Toast.makeText(context, "Perihal tidak boleh kosong", Toast.LENGTH_SHORT).show()
                    } else if (selectedIds.isEmpty()) {
                        Toast.makeText(context, "Pilih minimal 1 arsip", Toast.LENGTH_SHORT).show()
                    } else {
                        viewModel.createProposal(
                            tanggal = todayDate,
                            unitPengolah = "BPKPAD Balangan",
                            sumberModul = selectedModul,
                            perihal = perihal,
                            archiveIds = selectedIds,
                            onSuccess = onSimpan
                        )
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier       = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(
                start  = 16.dp,
                end    = 16.dp,
                top    = 14.dp,
                bottom = 8.dp
            ),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            // ── Section 1: Detail Berkas ──────────────────────
            item {
                SectionHeader(
                    icon     = Icons.Filled.Description,
                    title    = "Detail berkas",
                    subtitle = "Informasi identitas berkas usul musnah"
                )
                Spacer(Modifier.height(8.dp))
                FormCard {
                    AutoNomorField(nextNomor)
                    Spacer(Modifier.height(12.dp))
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        FormField(
                            label        = "Tanggal",
                            value        = todayDate,
                            required     = true,
                            trailingIcon = Icons.Filled.CalendarToday,
                            modifier     = Modifier.weight(1f)
                        )
                        FormField(
                            label    = "Unit pengolah",
                            value    = "BPKPAD Balangan",
                            required = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    ModulTabSelector(
                        modulList     = modulList,
                        selectedModul = selectedModul,
                        onSelect      = { selectedModul = it }
                    )
                    Spacer(Modifier.height(12.dp))
                    FormLabelRow(label = "Perihal", required = false)
                    OutlinedTextField(
                        value         = perihal,
                        onValueChange = { perihal = it },
                        placeholder   = {
                            Text(
                                "Contoh: Pemusnahan arsip keuangan tahun 2016" +
                                        " yang telah habis masa retensinya...",
                                fontSize   = 12.sp,
                                color      = TextHint,
                                lineHeight = 18.sp
                            )
                        },
                        shape    = RoundedCornerShape(8.dp),
                        colors   = fieldColors(),
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // ── Section 2: Pilih Arsip ────────────────────────
            item {
                SectionHeader(
                    icon     = Icons.Filled.PlaylistAddCheck,
                    title    = "Pilih arsip",
                    subtitle = "Centang arsip yang akan diusulkan musnah"
                )
                Spacer(Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape    = RoundedCornerShape(16.dp),
                    colors   = CardDefaults.cardColors(containerColor = CardWhite),
                    border   = BorderStroke(1.dp, BorderGray)
                ) {
                    // Picker header
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF5F7F5))
                            .padding(12.dp)
                    ) {
                        // Search bar
                        OutlinedTextField(
                            value         = pickerSearch,
                            onValueChange = { pickerSearch = it },
                            placeholder   = {
                                Text("Cari kode atau nama arsip...",
                                    fontSize = 12.sp, color = TextHint)
                            },
                            leadingIcon = {
                                Icon(Icons.Filled.Search, null,
                                    tint     = TextHint,
                                    modifier = Modifier.size(18.dp))
                            },
                            trailingIcon = if (pickerSearch.isNotEmpty()) {
                                {
                                    IconButton(onClick = { pickerSearch = "" }) {
                                        Icon(Icons.Filled.Close, null,
                                            tint     = TextHint,
                                            modifier = Modifier.size(16.dp))
                                    }
                                }
                            } else null,
                            singleLine = true,
                            shape      = RoundedCornerShape(10.dp),
                            colors     = fieldColors(),
                            modifier   = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                        )

                        // Filter STATUS
                        Text(
                            "STATUS",
                            fontSize      = 9.sp,
                            fontWeight    = FontWeight.Bold,
                            color         = TextHint,
                            letterSpacing = 0.7.sp,
                            modifier      = Modifier.padding(top = 10.dp, bottom = 5.dp, start = 2.dp)
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            pickerFilters.forEach { f ->
                                SmallFilterPill(
                                    label    = f,
                                    isActive = pickerFilter == f,
                                    onClick  = { pickerFilter = f }
                                )
                            }
                        }

                        // Filter TAHUN
                        Text(
                            "TAHUN",
                            fontSize      = 9.sp,
                            fontWeight    = FontWeight.Bold,
                            color         = TextHint,
                            letterSpacing = 0.7.sp,
                            modifier      = Modifier.padding(top = 10.dp, bottom = 5.dp, start = 2.dp)
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            pickerYears.forEach { y ->
                                SmallFilterPill(
                                    label    = y,
                                    isActive = pickerYear == y,
                                    onClick  = { pickerYear = y }
                                )
                            }
                        }
                    }

                    // Count bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            "${filteredPicker.size} arsip tersedia",
                            fontSize = 11.sp,
                            color    = TextHint
                        )
                    }
                    HorizontalDivider(thickness = 0.5.dp, color = BorderGray)

                    // Arsip rows
                    if (filteredPicker.isEmpty()) {
                        Column(
                            modifier              = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment   = Alignment.CenterHorizontally,
                            verticalArrangement   = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Filled.SearchOff, null,
                                tint = TextHint, modifier = Modifier.size(32.dp))
                            Text("Tidak ada arsip ditemukan",
                                fontSize   = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color      = TextBody)
                            Text("Coba ubah kata kunci atau filter",
                                fontSize = 10.sp, color = TextHint)
                        }
                    } else {
                        filteredPicker.forEachIndexed { index, item ->
                            val isSelected = item.id in selectedIds
                            PickerRow(
                                item       = item,
                                isSelected = isSelected,
                                isLast     = index == filteredPicker.lastIndex,
                                onClick    = {
                                    if (isSelected) selectedIds.remove(item.id)
                                    else selectedIds.add(item.id)
                                }
                            )
                        }
                    }

                    // Selection bar — muncul jika ada yang dipilih
                    if (selectedIds.isNotEmpty()) {
                        HorizontalDivider(thickness = 1.dp, color = GreenMid)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF1F8F1))
                                .padding(horizontal = 16.dp, vertical = 11.dp),
                            verticalAlignment     = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment     = Alignment.CenterVertically,
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
                                        fontSize   = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color      = Color.White
                                    )
                                }
                                Text(
                                    "arsip dipilih",
                                    fontSize   = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color      = GreenPrimary
                                )
                            }
                            Text(
                                "Hapus semua",
                                fontSize   = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color      = DangerText,
                                modifier   = Modifier.clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication        = null
                                ) { selectedIds.clear() }
                            )
                        }
                    }
                }
            }

            // ── Section 3: Arsip Terpilih ─────────────────────
            item {
                SectionHeader(
                    icon     = Icons.Filled.CheckCircle,
                    iconBg   = if (selectedItems.isEmpty()) Color(0xFFF0F2F0) else GreenPrimary,
                    iconTint = if (selectedItems.isEmpty()) TextHint else Color.White,
                    title    = "Arsip terpilih",
                    subtitle = if (selectedItems.isEmpty())
                        "Belum ada arsip yang dipilih"
                    else
                        "${selectedItems.size} arsip siap dimasukkan ke berkas"
                )
                Spacer(Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape    = RoundedCornerShape(16.dp),
                    colors   = CardDefaults.cardColors(containerColor = CardWhite),
                    border   = BorderStroke(1.dp, BorderGray)
                ) {
                    if (selectedItems.isEmpty()) {
                        Column(
                            modifier            = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Filled.Inbox,
                                contentDescription = null,
                                tint     = GreenMid,
                                modifier = Modifier.size(32.dp)
                            )
                            Text(
                                "Centang arsip di atas untuk\nmenambahkannya ke berkas ini",
                                fontSize  = 12.sp,
                                color     = TextHint,
                                lineHeight = 18.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        selectedItems.forEachIndexed { index, item ->
                            SelectedItemRow(
                                number   = index + 1,
                                item     = item,
                                isLast   = index == selectedItems.lastIndex,
                                onRemove = { selectedIds.remove(item.id) }
                            )
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(8.dp)) }
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  Top Bar — back button saja
// ─────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BuatBerkasTopBar(onBack: () -> Unit) {
    TopAppBar(
        title = {
            Column {
                Text(
                    "Buat Berkas Usul Musnah",
                    fontSize   = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color      = Color.White
                )
                Text(
                    "Isi detail dan pilih arsip",
                    fontSize = 10.sp,
                    color    = Color.White.copy(alpha = 0.6f)
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.Filled.ArrowBack,
                    contentDescription = "Kembali",
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
//  Bottom Bar
// ─────────────────────────────────────────────────────────────
@Composable
private fun BuatBerkasBottomBar(
    selectedCount: Int,
    isLoading: Boolean,
    onBatal: () -> Unit,
    onSimpan: () -> Unit
) {
    Surface(
        modifier        = Modifier.fillMaxWidth(),
        color           = CardWhite,
        shadowElevation = 0.dp
    ) {
        HorizontalDivider(thickness = 0.5.dp, color = BorderGray)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 18.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick  = onBatal,
                enabled  = !isLoading,
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                shape  = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, BorderGray),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextBody)
            ) {
                Icon(Icons.Filled.Close, null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("Batal", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }

            Button(
                onClick  = onSimpan,
                enabled  = selectedCount > 0 && !isLoading,
                modifier = Modifier
                    .weight(2f)
                    .height(50.dp),
                shape  = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor         = GreenPrimary,
                    contentColor           = Color.White,
                    disabledContainerColor = Color(0xFFB0BEC5),
                    disabledContentColor   = Color.White
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(Icons.Filled.Save, null, modifier = Modifier.size(16.dp))
                }
                Spacer(Modifier.width(6.dp))
                Text(
                    if (isLoading) "Menyimpan..."
                    else if (selectedCount > 0) "Simpan Berkas ($selectedCount arsip)"
                    else "Simpan Berkas",
                    fontSize   = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  Section Header
// ─────────────────────────────────────────────────────────────
@Composable
private fun SectionHeader(
    icon: ImageVector,
    title: String,
    subtitle: String,
    iconBg: Color   = GreenLight,
    iconTint: Color = GreenPrimary
) {
    Row(
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(iconBg, RoundedCornerShape(11.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = iconTint, modifier = Modifier.size(18.dp))
        }
        Column {
            Text(title,
                fontSize   = 14.sp,
                fontWeight = FontWeight.Bold,
                color      = TextHead)
            Text(subtitle,
                fontSize = 11.sp,
                color    = TextBody,
                modifier = Modifier.padding(top = 2.dp))
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  Form Card wrapper
// ─────────────────────────────────────────────────────────────
@Composable
private fun FormCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(16.dp),
        colors   = CardDefaults.cardColors(containerColor = CardWhite),
        border   = BorderStroke(1.dp, BorderGray)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            content  = content
        )
    }
}

// ─────────────────────────────────────────────────────────────
//  Auto Nomor Field (read-only)
// ─────────────────────────────────────────────────────────────
@Composable
private fun AutoNomorField(nomor: String) {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("Nomor berkas",
            fontSize   = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color      = TextBody)
        Box(
            modifier = Modifier
                .background(GreenLight, RoundedCornerShape(9999.dp))
                .padding(horizontal = 8.dp, vertical = 2.dp)
        ) {
            Text("OTOMATIS",
                fontSize      = 9.sp,
                fontWeight    = FontWeight.Bold,
                color         = GreenPrimary,
                letterSpacing = 0.5.sp)
        }
    }
    Spacer(Modifier.height(5.dp))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF0F9F0), RoundedCornerShape(8.dp))
            .border(1.5.dp, GreenMid, RoundedCornerShape(8.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(9.dp)
    ) {
        Icon(Icons.Filled.Lock, null,
            tint     = GreenPrimary,
            modifier = Modifier.size(16.dp))
        Text(nomor,
            fontSize   = 14.sp,
            fontWeight = FontWeight.Bold,
            color      = GreenPrimary)
    }
}

// ─────────────────────────────────────────────────────────────
//  Form Field
// ─────────────────────────────────────────────────────────────
@Composable
private fun FormField(
    label: String,
    value: String,
    required: Boolean = false,
    trailingIcon: ImageVector? = null,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        FormLabelRow(label = label, required = required)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp)
                .background(BgDashboard, RoundedCornerShape(8.dp))
                .border(1.dp, BorderGray, RoundedCornerShape(8.dp))
                .padding(horizontal = 12.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text(value,
                    fontSize = 13.sp,
                    color    = TextHead,
                    modifier = Modifier.weight(1f))
                if (trailingIcon != null) {
                    Icon(trailingIcon, null,
                        tint     = TextHint,
                        modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  Form Label Row
// ─────────────────────────────────────────────────────────────
@Composable
private fun FormLabelRow(label: String, required: Boolean) {
    Row(
        modifier              = Modifier.padding(bottom = 5.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Text(label,
            fontSize   = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color      = TextBody)
        if (required) {
            Text("*",
                fontSize   = 11.sp,
                color      = DangerText,
                fontWeight = FontWeight.Bold)
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  Modul Tab Selector
// ─────────────────────────────────────────────────────────────
@Composable
private fun ModulTabSelector(
    modulList: List<String>,
    selectedModul: String,
    onSelect: (String) -> Unit
) {
    FormLabelRow(label = "Sumber modul", required = true)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF3F5F3), RoundedCornerShape(10.dp))
            .border(1.dp, BorderGray, RoundedCornerShape(10.dp))
            .padding(3.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        modulList.forEach { modul ->
            val isActive = modul == selectedModul
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(36.dp)
                    .background(
                        if (isActive) GreenPrimary else Color.Transparent,
                        RoundedCornerShape(8.dp)
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication        = null
                    ) { onSelect(modul) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    modul,
                    fontSize   = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = if (isActive) Color.White else TextHint
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  Picker Row (selectable)
// ─────────────────────────────────────────────────────────────
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
                .background(if (isSelected) Color(0xFFEDF7EE) else CardWhite)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication        = null,
                    onClick           = onClick
                )
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment     = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(11.dp)
        ) {
            // Checkbox
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .background(
                        if (isSelected) GreenPrimary else CardWhite,
                        RoundedCornerShape(7.dp)
                    )
                    .border(
                        1.5.dp,
                        if (isSelected) GreenPrimary else BorderGray,
                        RoundedCornerShape(7.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Icon(Icons.Filled.Check, null,
                        tint     = Color.White,
                        modifier = Modifier.size(14.dp))
                }
            }

            // Content
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment     = Alignment.CenterVertically,
                    modifier              = Modifier.padding(bottom = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(GreenLight, RoundedCornerShape(9999.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(item.kode,
                            fontSize      = 10.sp,
                            fontWeight    = FontWeight.Bold,
                            color         = GreenPrimary,
                            letterSpacing = 0.2.sp)
                    }
                    val badgeBg  = if (item.keterangan == "Musnah") DangerBg   else GreenLight
                    val badgeTxt = if (item.keterangan == "Musnah") DangerText else GreenPrimary
                    Box(
                        modifier = Modifier
                            .background(badgeBg, RoundedCornerShape(9999.dp))
                            .padding(horizontal = 7.dp, vertical = 2.dp)
                    ) {
                        Text(item.keterangan,
                            fontSize   = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color      = badgeTxt)
                    }
                }
                Text(item.deskripsi,
                    fontSize   = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color      = TextHead,
                    maxLines   = 2,
                    overflow   = TextOverflow.Ellipsis,
                    modifier   = Modifier.padding(bottom = 5.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    PickerMetaChip(item.tahun)
                    PickerMetaChip(item.tingkat)
                    PickerMetaChip(item.volume)
                }
            }
        }
        if (!isLast) {
            HorizontalDivider(thickness = 0.5.dp, color = BorderGray)
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  Selected Item Row
// ─────────────────────────────────────────────────────────────
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
                .padding(horizontal = 14.dp, vertical = 11.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .background(GreenPrimary, RoundedCornerShape(7.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("$number",
                    fontSize   = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color      = Color.White)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "${item.kode} • ${item.tahun} • ${item.tingkat} • ${item.volume}",
                    fontSize      = 10.sp,
                    fontWeight    = FontWeight.Bold,
                    color         = GreenPrimary,
                    letterSpacing = 0.2.sp
                )
                Text(item.deskripsi,
                    fontSize   = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color      = TextHead,
                    maxLines   = 1,
                    overflow   = TextOverflow.Ellipsis,
                    modifier   = Modifier.padding(top = 2.dp))
            }
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(CardWhite, RoundedCornerShape(8.dp))
                    .border(1.dp, BorderGray, RoundedCornerShape(8.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication        = null,
                        onClick           = onRemove
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Close, null,
                    tint     = TextHint,
                    modifier = Modifier.size(14.dp))
            }
        }
        if (!isLast) {
            HorizontalDivider(thickness = 0.5.dp, color = GreenMid)
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  Komponen kecil
// ─────────────────────────────────────────────────────────────
@Composable
private fun SmallFilterPill(
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
            .padding(horizontal = 12.dp, vertical = 5.dp)
    ) {
        Text(label,
            fontSize   = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color      = if (isActive) Color.White else TextBody)
    }
}

@Composable
private fun PickerMetaChip(text: String) {
    Box(
        modifier = Modifier
            .background(Color(0xFFF3F5F3), RoundedCornerShape(9999.dp))
            .border(0.5.dp, BorderGray, RoundedCornerShape(9999.dp))
            .padding(horizontal = 7.dp, vertical = 2.dp)
    ) {
        Text(text, fontSize = 10.sp, color = TextBody)
    }
}

@Composable
private fun fieldColors() = OutlinedTextFieldDefaults.colors(
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
fun BuatBerkasPreview() {
    BuatBerkasUsulMusnahScreen()
}