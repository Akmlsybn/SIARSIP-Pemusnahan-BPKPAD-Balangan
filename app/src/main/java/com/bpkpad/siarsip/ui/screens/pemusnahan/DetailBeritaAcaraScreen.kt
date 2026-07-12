package com.bpkpad.siarsip.ui.screens.pemusnahan

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bpkpad.siarsip.core.utils.ResultState
import com.bpkpad.siarsip.feature.arsip.domain.model.BeritaAcaraItem
import com.bpkpad.siarsip.feature.arsip.domain.model.Penandatangan
import com.bpkpad.siarsip.feature.arsip.presentation.DetailBeritaAcaraViewModel
import com.bpkpad.siarsip.ui.theme.*
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.bpkpad.siarsip.core.utils.PdfExportManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// ─────────────────────────────────────────────────────────────
//  Screen Utama
// ─────────────────────────────────────────────────────────────
@Composable
fun DetailBeritaAcaraScreen(
    onBack: () -> Unit = {},
    onUnduhPdf: () -> Unit = {},
    onLihatArsip: (String) -> Unit = {},
    onCetak: () -> Unit = {},
    viewModel: DetailBeritaAcaraViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var baToExport by remember { mutableStateOf<BeritaAcaraItem?>(null) }

    val exportPdfLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/pdf"),
        onResult = { uri ->
            uri?.let {
                baToExport?.let { ba ->
                    scope.launch(Dispatchers.IO) {
                        try {
                            context.contentResolver.openOutputStream(uri)?.use { out ->
                                PdfExportManager.exportToPdf(context, ba, out)
                            }
                            scope.launch(Dispatchers.Main) {
                                Toast.makeText(context, "Berita Acara berhasil diekspor ke PDF!", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            scope.launch(Dispatchers.Main) {
                                Toast.makeText(context, "Gagal ekspor PDF: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }
            }
        }
    )

    val baItem = (uiState as? ResultState.Success)?.data

    Scaffold(
        containerColor = BgDashboard,
        topBar = {
            DetailBATopBar(
                onBack       = onBack
            )
        },
        bottomBar = {
            DetailBABottomBar(
                onLihatArsip = {
                    baItem?.let { ba ->
                        onLihatArsip(ba.id)
                    }
                },
                onCetak      = {
                    baItem?.let { ba ->
                        baToExport = ba
                        exportPdfLauncher.launch("Berita_Acara_Pemusnahan_${ba.nomor.replace("/", "_")}.pdf")
                    }
                }
            )
        }
    ) { padding ->
        when (val state = uiState) {
            is ResultState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = GreenPrimary)
                }
            }
            is ResultState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Gagal memuat detail Berita Acara: ${state.exception.message}",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 14.sp
                    )
                }
            }
            is ResultState.Success -> {
                val ba = state.data
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
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {

                    // ── Hero Card — info utama ────────────────────────
                    item { HeroInfoCard(ba) }

                    // ── Section: Detail Pelaksanaan ──────────────────
                    item {
                        DetailSectionHeader(
                            icon  = Icons.Filled.Info,
                            title = "Detail Pelaksanaan"
                        )
                        Spacer(Modifier.height(8.dp))
                        DetailPelaksanaanCard(ba)
                    }

                    // ── Section: Dasar Hukum Pemusnahan ──────────────
                    item {
                        DetailSectionHeader(
                            icon  = Icons.Filled.Security,
                            title = "Dasar Hukum Pemusnahan"
                        )
                        Spacer(Modifier.height(8.dp))
                        DasarHukumCard(ba)
                    }

                    // ── Section: Penandatangan ───────────────────────
                    item {
                        DetailSectionHeader(
                            icon  = Icons.Filled.Groups,
                            title = "Penandatangan",
                            badge = "${ba.penandatangan.size} orang"
                        )
                        Spacer(Modifier.height(8.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape    = RoundedCornerShape(16.dp),
                            colors   = CardDefaults.cardColors(containerColor = CardWhite),
                            border   = BorderStroke(1.dp, BorderGray)
                        ) {
                            Column {
                                ba.penandatangan.forEachIndexed { index, p ->
                                    PenandatanganDetailRow(
                                        number = index + 1,
                                        p      = p,
                                        isLast = index == ba.penandatangan.lastIndex
                                    )
                                }
                            }
                        }
                    }

                    // ── Section: Audit Info ──────────────────────────
                    item {
                        DetailSectionHeader(
                            icon  = Icons.Filled.Security,
                            title = "Audit Trail"
                        )
                        Spacer(Modifier.height(8.dp))
                        AuditTrailCard(ba)
                    }

                    item { Spacer(Modifier.height(8.dp)) }
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
private fun DetailBATopBar(
    onBack: () -> Unit
) {
    TopAppBar(
        title = {
            Column {
                Text("Detail Berita Acara",
                    fontSize   = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color      = Color.White)
                Text("Dokumen pemusnahan arsip",
                    fontSize = 10.sp,
                    color    = Color.White.copy(alpha = 0.6f))
            }
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, "Kembali", tint = Color.White)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = GreenPrimary)
    )
}

// ─────────────────────────────────────────────────────────────
//  Bottom Bar
// ─────────────────────────────────────────────────────────────
@Composable
private fun DetailBABottomBar(
    onLihatArsip: () -> Unit,
    onCetak: () -> Unit
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
                onClick  = onLihatArsip,
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                shape  = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, GreenMid),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor   = GreenPrimary,
                    containerColor = CardWhite
                )
            ) {
                Icon(Icons.Filled.Folder, null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("Lihat Arsip", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }

            Button(
                onClick  = onCetak,
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                shape  = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GreenPrimary,
                    contentColor   = Color.White
                )
            ) {
                Icon(Icons.Filled.Print, null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("Cetak", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  Hero Info Card
// ─────────────────────────────────────────────────────────────
@Composable
private fun HeroInfoCard(ba: BeritaAcaraItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(16.dp),
        colors   = CardDefaults.cardColors(containerColor = CardWhite),
        border   = BorderStroke(1.dp, BorderGray)
    ) {
        Column {
            // Hero header hijau
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(GreenPrimary)
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(bottom = 6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.2f),
                                RoundedCornerShape(9999.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Filled.Verified, null,
                                tint = Color.White,
                                modifier = Modifier.size(11.dp))
                            Text("Tervalidasi",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White)
                        }
                    }
                }
                Text(
                    "NOMOR BERITA ACARA",
                    fontSize      = 9.sp,
                    fontWeight    = FontWeight.Bold,
                    color         = Color.White.copy(alpha = 0.75f),
                    letterSpacing = 0.8.sp
                )
                Text(
                    ba.nomor,
                    fontSize   = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color      = Color.White,
                    modifier   = Modifier.padding(top = 3.dp)
                )
            }

            // Body — perihal + berkas link
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "PERIHAL",
                    fontSize      = 9.sp,
                    fontWeight    = FontWeight.Bold,
                    color         = TextHint,
                    letterSpacing = 0.7.sp,
                    modifier      = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    ba.perihal,
                    fontSize   = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = TextHead,
                    lineHeight = 18.sp
                )

                Spacer(Modifier.height(12.dp))

                // Berkas link card
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(GreenLight, RoundedCornerShape(10.dp))
                        .border(1.dp, GreenMid, RoundedCornerShape(10.dp))
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(9.dp)
                ) {
                    Icon(
                        Icons.Filled.FolderOpen,
                        null,
                        tint     = GreenPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "BERKAS USUL MUSNAH",
                            fontSize      = 9.sp,
                            fontWeight    = FontWeight.Bold,
                            color         = GreenPrimary.copy(alpha = 0.7f),
                            letterSpacing = 0.7.sp
                        )
                        Text(
                            "${ba.berkasNomor}  •  ${ba.jumlahArsip} arsip",
                            fontSize   = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color      = GreenPrimary,
                            modifier   = Modifier.padding(top = 2.dp)
                        )
                    }
                    Icon(
                        Icons.Filled.ChevronRight,
                        null,
                        tint     = GreenPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  Detail Pelaksanaan Card
// ─────────────────────────────────────────────────────────────
@Composable
private fun DetailPelaksanaanCard(ba: BeritaAcaraItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(16.dp),
        colors   = CardDefaults.cardColors(containerColor = CardWhite),
        border   = BorderStroke(1.dp, BorderGray)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            DetailInfoRow(
                icon  = Icons.Filled.Event,
                label = "Tanggal Pemusnahan",
                value = ba.tanggal
            )
            DetailDivider()

            DetailInfoRow(
                icon  = Icons.Filled.LocationOn,
                label = "Lokasi Pemusnahan",
                value = ba.lokasi
            )
            DetailDivider()

            DetailInfoRow(
                icon       = Icons.Filled.LocalFireDepartment,
                label      = "Metode Pemusnahan",
                value      = ba.metode,
                valueColor = AmberText
            )
            DetailDivider()

            DetailInfoRow(
                icon  = Icons.Filled.Category,
                label = "Sumber Modul",
                value = ba.sumber
            )
            DetailDivider()

            // Jumlah arsip — highlighted
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(DangerBg, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.Delete, null,
                            tint = DangerText, modifier = Modifier.size(14.dp))
                    }
                    Column {
                        Text("Jumlah Arsip Dimusnahkan",
                            fontSize   = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color      = TextHead)
                    }
                }
                Box(
                    modifier = Modifier
                        .background(DangerBg, RoundedCornerShape(9999.dp))
                        .padding(horizontal = 11.dp, vertical = 4.dp)
                ) {
                    Text(
                        "${ba.jumlahArsip} arsip",
                        fontSize   = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color      = DangerText
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  Dasar Hukum Pemusnahan Card
// ─────────────────────────────────────────────────────────────
@Composable
private fun DasarHukumCard(ba: BeritaAcaraItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(16.dp),
        colors   = CardDefaults.cardColors(containerColor = CardWhite),
        border   = BorderStroke(1.dp, BorderGray)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            DetailInfoRow(
                icon  = Icons.Filled.Info,
                label = "Surat Pertimbangan Tim Penilai (VERIFIED)",
                value = if (!ba.suratPertimbanganNomor.isNullOrBlank()) {
                    "${ba.suratPertimbanganNomor}  •  ${ba.suratPertimbanganPerihal ?: "-"}"
                } else {
                    "-"
                }
            )
            DetailDivider()

            val otoritas = when (ba.jenisPersetujuanAkhir?.uppercase()) {
                "BUPATI" -> "Bupati Balangan"
                "ANRI" -> "ANRI"
                else -> "Kepala Daerah / ANRI"
            }

            DetailInfoRow(
                icon  = Icons.Filled.Verified,
                label = "Persetujuan Akhir ($otoritas)",
                value = if (!ba.nomorPersetujuanAkhir.isNullOrBlank()) {
                    "${ba.nomorPersetujuanAkhir}  •  ${ba.perihalPersetujuanAkhir ?: "-"}"
                } else {
                    "-"
                }
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  Detail Info Row
// ─────────────────────────────────────────────────────────────
@Composable
private fun DetailInfoRow(
    icon: ImageVector,
    label: String,
    value: String,
    valueColor: Color = TextHead
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .background(GreenLight, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null,
                tint     = GreenPrimary,
                modifier = Modifier.size(14.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(label,
                fontSize = 11.sp,
                color    = TextHint)
            Text(value,
                fontSize   = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color      = valueColor,
                modifier   = Modifier.padding(top = 1.dp))
        }
    }
}

@Composable
private fun DetailDivider() {
    HorizontalDivider(
        modifier  = Modifier.padding(vertical = 8.dp),
        thickness = 0.5.dp,
        color     = BorderGray
    )
}

// ─────────────────────────────────────────────────────────────
//  Penandatangan Detail Row
// ─────────────────────────────────────────────────────────────
@Composable
private fun PenandatanganDetailRow(
    number: Int,
    p: Penandatangan,
    isLast: Boolean
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Avatar besar
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(GreenLight, CircleShape)
                    .border(2.dp, GreenMid, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(p.nama.firstOrNull()?.toString() ?: "?",
                    fontSize   = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color      = GreenPrimary)
            }

            // Info kolom
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier              = Modifier.padding(bottom = 2.dp)
                ) {
                    Text("$number.",
                        fontSize   = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color      = TextHint)
                    Text(p.nama,
                        fontSize   = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color      = TextHead,
                        maxLines   = 1,
                        overflow   = TextOverflow.Ellipsis,
                        modifier   = Modifier.weight(1f))
                }
                Text(p.jabatan,
                    fontSize = 11.sp,
                    color    = TextBody,
                    modifier = Modifier.padding(start = 16.dp))
            }

            // Role badge
            Box(
                modifier = Modifier
                    .background(GreenPrimary, RoundedCornerShape(9999.dp))
                    .padding(horizontal = 9.dp, vertical = 4.dp)
            ) {
                Text(p.role,
                    fontSize   = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color      = Color.White)
            }
        }
        if (!isLast) {
            HorizontalDivider(thickness = 0.5.dp, color = BorderGray)
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  Audit Trail Card
// ─────────────────────────────────────────────────────────────
@Composable
private fun AuditTrailCard(ba: BeritaAcaraItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(16.dp),
        colors   = CardDefaults.cardColors(containerColor = CardWhite),
        border   = BorderStroke(1.dp, BorderGray)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier              = Modifier.padding(bottom = 10.dp)
            ) {
                Icon(Icons.Filled.Fingerprint, null,
                    tint     = GreenPrimary,
                    modifier = Modifier.size(15.dp))
                Text(
                    "DOKUMEN TERVERIFIKASI",
                    fontSize      = 10.sp,
                    fontWeight    = FontWeight.Bold,
                    color         = GreenPrimary,
                    letterSpacing = 0.7.sp
                )
            }
            Text(
                "Berita acara ini telah ditandatangani secara digital " +
                        "oleh ${ba.penandatangan.size} pihak dan tidak dapat diubah. " +
                        "Setiap perubahan akan tercatat dalam log riwayat sistem.",
                fontSize   = 11.sp,
                color      = TextBody,
                lineHeight = 16.sp
            )

            Spacer(Modifier.height(10.dp))

            // Verification info
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF1F8F1), RoundedCornerShape(8.dp))
                    .border(0.5.dp, GreenMid, RoundedCornerShape(8.dp))
                    .padding(horizontal = 11.dp, vertical = 8.dp),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Hash ID",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextHint)
                    Text("BA${ba.id}-PMS-${ba.tahun}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = GreenPrimary)
                }
                Icon(Icons.Filled.CheckCircle, null,
                    tint = GreenPrimary, modifier = Modifier.size(20.dp))
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  Section Header (untuk detail)
// ─────────────────────────────────────────────────────────────
@Composable
private fun DetailSectionHeader(
    icon: ImageVector,
    title: String,
    badge: String? = null
) {
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
                Icon(icon, null,
                    tint     = GreenPrimary,
                    modifier = Modifier.size(18.dp))
            }
            Text(title,
                fontSize   = 14.sp,
                fontWeight = FontWeight.Bold,
                color      = TextHead)
        }
        if (badge != null) {
            Box(
                modifier = Modifier
                    .background(GreenPrimary, RoundedCornerShape(9999.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(badge,
                    fontSize   = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color      = Color.White)
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  Preview
// ─────────────────────────────────────────────────────────────
@Preview(showBackground = true, widthDp = 375, heightDp = 812)
@Composable
fun DetailBeritaAcaraPreview() {
    // DetailBeritaAcaraScreen()
}