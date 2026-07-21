package com.bpkpad.siarsip.ui.screens.pemusnahan

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bpkpad.siarsip.core.utils.ResultState
import com.bpkpad.siarsip.feature.arsip.domain.model.Arsip
import com.bpkpad.siarsip.feature.arsip.presentation.DetailBerkasUsulMusnahViewModel
import com.bpkpad.siarsip.ui.theme.*

// ─────────────────────────────────────────────────────────────
//  Data Model — Detail Berkas
// ─────────────────────────────────────────────────────────────
data class BerkasDetail(
    val nomor: String,
    val tanggal: String,
    val sumberModul: String,
    val unitPengolah: String,
    val perihal: String,
    val status: String,      // "Draft", "Penilaian", "Disetujui", "Selesai"
    val tahapan: String,     // mis. "Tahap 3: Pengiriman ke Bupati"
    val arsipList: List<Arsip>
)

// ─────────────────────────────────────────────────────────────
//  Dummy Data
// ─────────────────────────────────────────────────────────────
val dummyArsipListLocal = listOf(
    Arsip(
        id = "1", kode = "KN.03.01", fullKode = "00001/SP2D/1.20.11.01/DPPKAD/2016",
        deskripsi = "Gaji dan Tunjangan Pegawai Bulan Januari 2016",
        tahun = "2016", tingkat = "Copy", volume = "1 Berkas",
        retensiAktif = "2 Thn", retensiInaktif = "8 Thn", keterangan = "Musnah",
        sumber = "Keuangan", status = "PROPOSED", nasibAkhir = "MUSNAH"
    ),
    Arsip(
        id = "2", kode = "KN.03.01", fullKode = "00002/SP2D/1.20.05.01/DPPKAD/2016",
        deskripsi = "Pembayaran Gaji Induk Bulan Januari 2016 Pegawai Dinas PPKAD",
        tahun = "2016", tingkat = "Copy", volume = "1 Berkas",
        retensiAktif = "2 Thn", retensiInaktif = "8 Thn", keterangan = "Musnah",
        sumber = "Keuangan", status = "PROPOSED", nasibAkhir = "MUSNAH"
    )
)

val dummyBerkasDetail = BerkasDetail(
    nomor        = "BUM-2025-003",
    tanggal      = "12 Mei 2025",
    sumberModul  = "Keuangan",
    unitPengolah = "BPKPAD Balangan",
    perihal      = "Pemusnahan arsip keuangan tahun 2016 yang telah habis masa retensinya berdasarkan Jadwal Retensi Arsip (JRA)",
    status       = "Draft",
    tahapan      = "Tahap 1 dari 5 — Pembuatan Usulan",
    arsipList    = dummyArsipListLocal
)

// ─────────────────────────────────────────────────────────────
//  Screen Utama
// ─────────────────────────────────────────────────────────────
@Composable
fun DetailBerkasUsulMusnahScreen(
    onBack: () -> Unit = {},
    onExportPdf: () -> Unit = {},
    onLihatTracking: () -> Unit = {},
    viewModel: DetailBerkasUsulMusnahViewModel = hiltViewModel()
) {
    val proposalState by viewModel.proposalState.collectAsState()
    val removeState by viewModel.removeState.collectAsState()
    val context = LocalContext.current

    var expandedItemId by remember { mutableStateOf<String?>(null) }
    var isEditMode by remember { mutableStateOf(false) }

    LaunchedEffect(removeState) {
        if (removeState is ResultState.Error) {
            Toast.makeText(context, (removeState as ResultState.Error).exception.message ?: "Gagal melepaskan arsip", Toast.LENGTH_LONG).show()
            viewModel.resetRemoveState()
        }
    }

    when (val state = proposalState) {
        is ResultState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = GreenPrimary)
            }
        }
        is ResultState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Gagal memuat detail berkas: ${state.exception.message}", color = DangerText)
            }
        }
        is ResultState.Success -> {
            val proposal = state.data
            if (proposal == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Berkas tidak ditemukan", color = DangerText)
                }
            } else {
                val berkasDetail = remember(proposal) {
                    val statusText = when (proposal.status) {
                        "PROPOSED" -> "Draft"
                        "VERIFIED" -> "Penilaian"
                        "APPROVED" -> "Disetujui"
                        "DISPOSED" -> "Selesai"
                        else -> proposal.status
                    }
                    val tahapanText = when (proposal.status) {
                        "PROPOSED" -> "Tahap 1 dari 5 — Pembuatan Usulan"
                        "VERIFIED" -> "Tahap 3 dari 5 — Menunggu Persetujuan Kepala Daerah"
                        "APPROVED" -> "Tahap 4 dari 5 — Menunggu Eksekusi Pemusnahan"
                        "DISPOSED" -> "Tahap 5 dari 5 — Pemusnahan & Berita Acara"
                        else -> "Status: ${proposal.status}"
                    }
                    BerkasDetail(
                        nomor = proposal.nomorBerkas,
                        tanggal = proposal.tanggal,
                        sumberModul = proposal.sumberModul,
                        unitPengolah = proposal.unitPengolah,
                        perihal = proposal.perihal,
                        status = statusText,
                        tahapan = tahapanText,
                        arsipList = proposal.archives
                    )
                }

                // Turn off edit mode if status changes away from PROPOSED
                if (proposal.status != "PROPOSED") {
                    isEditMode = false
                }

                Scaffold(
                    containerColor = BgDashboard,
                    topBar = {
                        DetailBerkasTopBar(
                            onBack       = onBack
                        )
                    },
                    bottomBar = {
                        DetailBerkasBottomBar(
                            showEditButton  = proposal.status == "PROPOSED",
                            isEditMode      = isEditMode,
                            onEditModeToggle = { isEditMode = it }
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
                            bottom = 8.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {

                        // ── Berkas info card ──────────────────────────────
                        item {
                            BerkasInfoCard(berkas = berkasDetail)
                        }

                        // ── Section header: Daftar Arsip ──────────────────
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment     = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment     = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .background(GreenLight, RoundedCornerShape(11.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Filled.FolderOpen,
                                            null,
                                            tint     = GreenPrimary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Column {
                                        Text("Daftar arsip",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextHead)
                                        Text("${berkasDetail.arsipList.size} arsip dalam berkas ini",
                                            fontSize = 11.sp, color = TextBody,
                                            modifier = Modifier.padding(top = 2.dp))
                                    }
                                }
                                // Total badge
                                Box(
                                    modifier = Modifier
                                        .background(GreenPrimary, RoundedCornerShape(9999.dp))
                                        .padding(horizontal = 11.dp, vertical = 5.dp)
                                ) {
                                    Text(
                                        "${berkasDetail.arsipList.size}",
                                        fontSize   = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color      = Color.White
                                    )
                                }
                            }
                        }

                        // ── Daftar arsip card ─────────────────────────────
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape    = RoundedCornerShape(16.dp),
                                colors   = CardDefaults.cardColors(containerColor = CardWhite),
                                border   = BorderStroke(1.dp, BorderGray)
                            ) {
                                Column {
                                    if (berkasDetail.arsipList.isEmpty()) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(24.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text("Belum ada arsip di dalam usulan ini", color = TextHint, fontSize = 13.sp)
                                        }
                                    } else {
                                        berkasDetail.arsipList.forEachIndexed { index, item ->
                                            val isExpanded = expandedItemId == item.id
                                            val isLast     = index == berkasDetail.arsipList.lastIndex
                                            DetailArsipRow(
                                                number     = index + 1,
                                                item       = item,
                                                isExpanded = isExpanded,
                                                isLast     = isLast,
                                                onEyeClick = {
                                                    expandedItemId = if (isExpanded) null else item.id
                                                },
                                                isEditMode = isEditMode,
                                                onRemoveClick = {
                                                    viewModel.removeArchive(proposalId = proposal.id, archiveId = item.id)
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        item { Spacer(Modifier.height(8.dp)) }
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
private fun DetailBerkasTopBar(
    onBack: () -> Unit
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    "Detail Berkas",
                    fontSize   = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color      = Color.White
                )
                Text(
                    "Isi berkas usul musnah",
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
//  Bottom Bar — tombol Aksi Edit / Lihat Tracking
// ─────────────────────────────────────────────────────────────
@Composable
private fun DetailBerkasBottomBar(
    showEditButton: Boolean,
    isEditMode: Boolean,
    onEditModeToggle: (Boolean) -> Unit
) {
    if (!showEditButton) return

    Surface(
        modifier        = Modifier.fillMaxWidth(),
        color           = CardWhite,
        shadowElevation = 0.dp
    ) {
        HorizontalDivider(thickness = 0.5.dp, color = BorderGray)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 18.dp)
        ) {
            Button(
                onClick  = { onEditModeToggle(!isEditMode) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape  = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isEditMode) GreenPrimary else CardWhite,
                    contentColor   = if (isEditMode) Color.White else GreenPrimary
                ),
                border = if (isEditMode) null else BorderStroke(1.5.dp, GreenPrimary)
            ) {
                Icon(
                    imageVector = if (isEditMode) Icons.Filled.Check else Icons.Filled.Edit,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    if (isEditMode) "Selesai Edit" else "Edit Usulan",
                    fontSize   = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  Berkas Info Card
// ─────────────────────────────────────────────────────────────
@Composable
private fun BerkasInfoCard(berkas: BerkasDetail) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(16.dp),
        colors   = CardDefaults.cardColors(containerColor = CardWhite),
        border   = BorderStroke(1.dp, BorderGray)
    ) {
        Column(
            modifier              = Modifier.padding(16.dp),
            verticalArrangement   = Arrangement.spacedBy(12.dp)
        ) {
            // Row 1: Nomor Berkas & Status
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "NOMOR BERKAS",
                        fontSize      = 9.sp,
                        fontWeight    = FontWeight.Bold,
                        color         = TextHint,
                        letterSpacing = 0.7.sp
                    )
                    Text(
                        berkas.nomor,
                        fontSize   = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color      = GreenPrimary,
                        modifier   = Modifier.padding(top = 2.dp)
                    )
                }
                StatusBadge(berkas.status)
            }

            HorizontalDivider(thickness = 0.5.dp, color = BorderGray)

            // Row 2: Perihal
            Column {
                Text(
                    "PERIHAL",
                    fontSize      = 9.sp,
                    fontWeight    = FontWeight.Bold,
                    color         = TextHint,
                    letterSpacing = 0.7.sp
                )
                Text(
                    berkas.perihal,
                    fontSize   = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color      = TextHead,
                    lineHeight = 17.sp,
                    modifier   = Modifier.padding(top = 2.dp)
                )
            }

            HorizontalDivider(thickness = 0.5.dp, color = BorderGray)

            // Row 3: Tanggal & Unit & Sumber
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                DetailField(
                    label    = "TANGGAL BERKAS",
                    value    = berkas.tanggal,
                    modifier = Modifier.weight(1f)
                )
                DetailField(
                    label    = "UNIT PENGOLAH",
                    value    = berkas.unitPengolah,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier              = Modifier.fillMaxWidth()
            ) {
                DetailField(
                    label    = "SUMBER MODUL",
                    value    = berkas.sumberModul,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            HorizontalDivider(thickness = 0.5.dp, color = BorderGray)

            // Row 4: Tahapan progress
            Column {
                Text(
                    "TAHAPAN PROSES",
                    fontSize      = 9.sp,
                    fontWeight    = FontWeight.Bold,
                    color         = TextHint,
                    letterSpacing = 0.7.sp
                )
                Row(
                    modifier              = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(GreenPrimary, CircleShape)
                    )
                    Text(
                        berkas.tahapan,
                        fontSize   = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color      = TextHead
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  Arsip Row (dalam berkas) + accordion detail
// ─────────────────────────────────────────────────────────────
@Composable
private fun DetailArsipRow(
    number: Int,
    item: Arsip,
    isExpanded: Boolean,
    isLast: Boolean,
    onEyeClick: () -> Unit,
    isEditMode: Boolean,
    onRemoveClick: () -> Unit
) {
    Column {
        // ── Main row ──────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(if (isExpanded) Color(0xFFF0F9F0) else CardWhite)
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(11.dp)
        ) {
            // Nomor badge
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(
                        if (isExpanded) GreenPrimary else GreenLight,
                        RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "$number",
                    fontSize   = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color      = if (isExpanded) Color.White else GreenPrimary
                )
            }

            // Info kolom
            Column(modifier = Modifier.weight(1f)) {
                // Baris 1: kode + status
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment     = Alignment.CenterVertically,
                    modifier              = Modifier.padding(bottom = 4.dp)
                ) {
                    DetailKodePill(item.kode)
                    DetailStatusBadge(item.keterangan)
                }
                // Baris 2: deskripsi
                Text(
                    item.deskripsi,
                    fontSize   = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color      = TextHead,
                    maxLines   = 1,
                    overflow   = TextOverflow.Ellipsis,
                    modifier   = Modifier.padding(bottom = 5.dp)
                )
                // Baris 3: meta chips
                Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    DetailMetaChip(Icons.Filled.CalendarToday, item.tahun)
                    DetailMetaChip(Icons.Filled.ContentCopy,   item.tingkat)
                    DetailMetaChip(Icons.Filled.Inventory2,    item.volume)
                }
            }

            if (isEditMode) {
                // Trash/Delete button
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .background(DangerBg, RoundedCornerShape(10.dp))
                        .border(1.5.dp, Color(0xFFFECACA), RoundedCornerShape(10.dp))
                        .clickable(onClick = onRemoveClick),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Hapus",
                        tint = DangerText,
                        modifier = Modifier.size(17.dp)
                    )
                }
            } else {
                // Eye button
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
        }

        // Divider antar baris
        if (!isExpanded && !isLast) {
            HorizontalDivider(thickness = 0.5.dp, color = BorderGray)
        }

        // ── Detail accordion ──────────────────────────────────
        AnimatedVisibility(
            visible = isExpanded,
            enter   = expandVertically() + fadeIn(),
            exit    = shrinkVertically() + fadeOut()
        ) {
            DetailArsipPanel(item = item, onClose = onEyeClick)
        }

        // Divider terakhir setelah accordion ditutup
        if (isExpanded && !isLast) {
            HorizontalDivider(thickness = 0.5.dp, color = BorderGray)
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  Detail Panel (accordion content)
// ─────────────────────────────────────────────────────────────
@Composable
private fun DetailArsipPanel(item: Arsip, onClose: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF1F8F1))
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
                "INFORMASI LENGKAP",
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
                Icon(Icons.Filled.Close, null,
                    tint     = TextHint,
                    modifier = Modifier.size(13.dp))
            }
        }

        // Kode + Kurun Waktu
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            DetailInfoField(
                label      = "KODE KLASIFIKASI",
                value      = item.fullKode,
                valueColor = GreenPrimary,
                modifier   = Modifier.weight(1f)
            )
            DetailInfoField(
                label    = "KURUN WAKTU",
                value    = item.tahun,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(9.dp))
        DetailInfoField("ISI INFORMASI", item.deskripsi)

        Spacer(Modifier.height(9.dp))
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            DetailInfoField("VOLUME", item.volume, modifier = Modifier.weight(1f))
            DetailInfoField("TINGKAT", item.tingkat, modifier = Modifier.weight(1f))
        }

        Spacer(Modifier.height(9.dp))

        // Retensi pills
        Text("RETENSI",
            fontSize      = 9.sp,
            fontWeight    = FontWeight.Bold,
            color         = TextHint,
            letterSpacing = 0.7.sp,
            modifier      = Modifier.padding(bottom = 6.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
            DetailRetensiPill(Icons.Filled.Timer,   "Aktif: ${item.retensiAktif}")
            DetailRetensiPill(Icons.Filled.Archive, "Inaktif: ${item.retensiInaktif}")
        }

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
            Text("KETERANGAN",
                fontSize      = 9.sp,
                fontWeight    = FontWeight.Bold,
                color         = TextHint,
                letterSpacing = 0.7.sp)
            DetailStatusBadge(item.keterangan)
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  Helper components
// ─────────────────────────────────────────────────────────────
@Composable
private fun DetailKodePill(kode: String) {
    Box(
        modifier = Modifier
            .background(GreenLight, RoundedCornerShape(9999.dp))
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(kode,
            fontSize      = 10.sp,
            fontWeight    = FontWeight.Bold,
            color         = GreenPrimary,
            letterSpacing = 0.3.sp)
    }
}

@Composable
private fun DetailStatusBadge(status: String) {
    val bg    = if (status == "Musnah") DangerBg   else GreenLight
    val color = if (status == "Musnah") DangerText else GreenPrimary
    Box(
        modifier = Modifier
            .background(bg, RoundedCornerShape(9999.dp))
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(status,
            fontSize   = 9.sp,
            fontWeight = FontWeight.Bold,
            color      = color)
    }
}

@Composable
private fun DetailMetaChip(icon: ImageVector, text: String) {
    Row(
        modifier = Modifier
            .background(Color(0xFFF3F5F3), RoundedCornerShape(9999.dp))
            .border(0.5.dp, BorderGray, RoundedCornerShape(9999.dp))
            .padding(horizontal = 7.dp, vertical = 2.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Icon(icon, null,
            tint     = TextHint,
            modifier = Modifier.size(10.dp))
        Text(text, fontSize = 10.sp, color = TextBody)
    }
}

@Composable
private fun DetailInfoField(
    label: String,
    value: String,
    valueColor: Color = TextHead,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(label,
            fontSize      = 9.sp,
            fontWeight    = FontWeight.Bold,
            color         = TextHint,
            letterSpacing = 0.7.sp,
            modifier      = Modifier.padding(bottom = 3.dp))
        Text(value,
            fontSize   = 12.sp,
            fontWeight = FontWeight.Medium,
            color      = valueColor,
            lineHeight = 16.sp)
    }
}

@Composable
private fun DetailRetensiPill(icon: ImageVector, text: String) {
    Row(
        modifier = Modifier
            .background(CardWhite, RoundedCornerShape(9999.dp))
            .border(1.dp, GreenMid, RoundedCornerShape(9999.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Icon(icon, null,
            tint     = GreenPrimary,
            modifier = Modifier.size(12.dp))
        Text(text, fontSize = 11.sp, color = TextBody)
    }
}

@Composable
private fun DetailField(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            label,
            fontSize      = 9.sp,
            fontWeight    = FontWeight.Bold,
            color         = TextHint,
            letterSpacing = 0.7.sp
        )
        Text(
            value,
            fontSize   = 12.sp,
            fontWeight = FontWeight.Medium,
            color      = TextHead,
            modifier   = Modifier.padding(top = 2.dp)
        )
    }
}

@Composable
private fun StatusBadge(status: String) {
    val bg = when (status) {
        "Draft" -> Color(0xFFE0F2FE)
        "Penilaian" -> Color(0xFFFEF3C7)
        "Disetujui" -> Color(0xFFD1FAE5)
        "Selesai" -> Color(0xFFECFDF5)
        else -> Color(0xFFF3F4F6)
    }
    val text = when (status) {
        "Draft" -> Color(0xFF0369A1)
        "Penilaian" -> Color(0xFFB45309)
        "Disetujui" -> Color(0xFF047857)
        "Selesai" -> Color(0xFF065F46)
        else -> Color(0xFF4B5563)
    }
    Box(
        modifier = Modifier
            .background(bg, RoundedCornerShape(9999.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            status,
            fontSize   = 10.sp,
            fontWeight = FontWeight.Bold,
            color      = text
        )
    }
}

// ─────────────────────────────────────────────────────────────
//  Preview
// ─────────────────────────────────────────────────────────────
@Preview(showBackground = true, widthDp = 375, heightDp = 812)
@Composable
fun DetailBerkasPreview() {
    DetailBerkasUsulMusnahScreen()
}