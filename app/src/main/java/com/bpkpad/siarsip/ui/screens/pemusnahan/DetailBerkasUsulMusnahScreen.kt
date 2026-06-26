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
import com.bpkpad.siarsip.feature.arsip.domain.model.Arsip
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
    val status: String,      // "Draft", "Diajukan", "Menunggu", "Disetujui", "Selesai"
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
        sumber = "Keuangan", status = "PROPOSED"
    ),
    Arsip(
        id = "2", kode = "KN.03.01", fullKode = "00002/SP2D/1.20.05.01/DPPKAD/2016",
        deskripsi = "Pembayaran Gaji Induk Bulan Januari 2016 Pegawai Dinas PPKAD",
        tahun = "2016", tingkat = "Copy", volume = "1 Berkas",
        retensiAktif = "2 Thn", retensiInaktif = "8 Thn", keterangan = "Musnah",
        sumber = "Keuangan", status = "PROPOSED"
    )
)

val dummyBerkasDetail = BerkasDetail(
    nomor        = "BUM-2025-003",
    tanggal      = "12 Mei 2025",
    sumberModul  = "Keuangan",
    unitPengolah = "BPKPAD Balangan",
    perihal      = "Pemusnahan arsip keuangan tahun 2016 yang telah " +
            "habis masa retensinya berdasarkan Jadwal Retensi Arsip (JRA)",
    status       = "Menunggu",
    tahapan      = "Tahap 3 dari 5 — Pengiriman ke Bupati",
    arsipList    = dummyArsipListLocal
)

// ─────────────────────────────────────────────────────────────
//  Screen Utama
// ─────────────────────────────────────────────────────────────
@Composable
fun DetailBerkasUsulMusnahScreen(
    berkas: BerkasDetail = dummyBerkasDetail,
    onBack: () -> Unit = {},
    onExportPdf: () -> Unit = {},
    onLihatTracking: () -> Unit = {}
) {
    var expandedItemId by remember { mutableStateOf<String?>(null) }

    Scaffold(
        containerColor = BgDashboard,
        topBar = {
            DetailBerkasTopBar(
                onBack       = onBack,
                onExportPdf  = onExportPdf
            )
        },
        bottomBar = {
            DetailBerkasBottomBar(
                arsipCount       = berkas.arsipList.size,
                onLihatTracking  = onLihatTracking
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
                BerkasInfoCard(berkas = berkas)
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
                            Text("${berkas.arsipList.size} arsip dalam berkas ini",
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
                            "${berkas.arsipList.size}",
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
                        berkas.arsipList.forEachIndexed { index, item ->
                            val isExpanded = expandedItemId == item.id
                            val isLast     = index == berkas.arsipList.lastIndex
                            DetailArsipRow(
                                number     = index + 1,
                                item       = item,
                                isExpanded = isExpanded,
                                isLast     = isLast,
                                onEyeClick = {
                                    expandedItemId = if (isExpanded) null else item.id
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

// ─────────────────────────────────────────────────────────────
//  Top Bar
// ─────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DetailBerkasTopBar(
    onBack: () -> Unit,
    onExportPdf: () -> Unit
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
        actions = {
            IconButton(onClick = onExportPdf) {
                Icon(
                    Icons.Filled.PictureAsPdf,
                    contentDescription = "Export PDF",
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
//  Bottom Bar — tombol Lihat Tracking
// ─────────────────────────────────────────────────────────────
@Composable
private fun DetailBerkasBottomBar(
    arsipCount: Int,
    onLihatTracking: () -> Unit
) {
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
                onClick  = onLihatTracking,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape  = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GreenPrimary,
                    contentColor   = Color.White
                )
            ) {
                Icon(Icons.Filled.Timeline, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(
                    "Lihat Status & Tracking",
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
        Column(modifier = Modifier.fillMaxWidth()) {

            // Hero header — nomor + status
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(GreenPrimary)
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "NOMOR BERKAS",
                        fontSize      = 9.sp,
                        fontWeight    = FontWeight.Bold,
                        color         = Color.White.copy(alpha = 0.7f),
                        letterSpacing = 0.8.sp
                    )
                    Text(
                        berkas.nomor,
                        fontSize   = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color      = Color.White,
                        modifier   = Modifier.padding(top = 3.dp)
                    )
                }
                BerkasStatusBadge(status = berkas.status)
            }

            // Body
            Column(modifier = Modifier.padding(16.dp)) {

                // Tahapan progress
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(AmberBg, RoundedCornerShape(10.dp))
                        .border(1.dp, Color(0xFFFDE68A), RoundedCornerShape(10.dp))
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(9.dp)
                ) {
                    Icon(
                        Icons.Filled.HourglassEmpty,
                        null,
                        tint     = AmberText,
                        modifier = Modifier.size(16.dp)
                    )
                    Column {
                        Text(
                            "Sedang Diproses",
                            fontSize   = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color      = AmberText
                        )
                        Text(
                            berkas.tahapan,
                            fontSize = 11.sp,
                            color    = TextBody,
                            modifier = Modifier.padding(top = 1.dp)
                        )
                    }
                }

                Spacer(Modifier.height(14.dp))

                // Info grid — 2 kolom
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    BerkasInfoField(
                        icon     = Icons.Filled.Event,
                        label    = "Tanggal Dibuat",
                        value    = berkas.tanggal,
                        modifier = Modifier.weight(1f)
                    )
                    BerkasInfoField(
                        icon     = Icons.Filled.Category,
                        label    = "Sumber Modul",
                        value    = berkas.sumberModul,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(Modifier.height(12.dp))

                BerkasInfoField(
                    icon  = Icons.Filled.Domain,
                    label = "Unit Pengolah",
                    value = berkas.unitPengolah
                )

                Spacer(Modifier.height(12.dp))

                // Perihal — full row
                Column {
                    Row(
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier              = Modifier.padding(bottom = 5.dp)
                    ) {
                        Icon(
                            Icons.Filled.Notes,
                            null,
                            tint     = TextHint,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            "PERIHAL",
                            fontSize      = 9.sp,
                            fontWeight    = FontWeight.Bold,
                            color         = TextHint,
                            letterSpacing = 0.7.sp
                        )
                    }
                    Text(
                        berkas.perihal,
                        fontSize   = 12.sp,
                        color      = TextBody,
                        lineHeight = 17.sp
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  Status Badge (untuk header berkas)
// ─────────────────────────────────────────────────────────────
@Composable
private fun BerkasStatusBadge(status: String) {
    val (bg, textColor, icon) = when (status) {
        "Draft"     -> Triple(Color.White.copy(alpha = 0.15f), Color.White, Icons.Filled.EditNote)
        "Diajukan"  -> Triple(BlueBg, BlueText, Icons.Filled.Send)
        "Menunggu"  -> Triple(AmberBg, AmberText, Icons.Filled.HourglassEmpty)
        "Disetujui" -> Triple(GreenLight, GreenPrimary, Icons.Filled.CheckCircle)
        "Selesai"   -> Triple(GreenLight, GreenPrimary, Icons.Filled.TaskAlt)
        else        -> Triple(Color.White.copy(alpha = 0.15f), Color.White, Icons.Filled.Info)
    }

    Row(
        modifier = Modifier
            .background(bg, RoundedCornerShape(9999.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Icon(icon, null, tint = textColor, modifier = Modifier.size(13.dp))
        Text(
            status,
            fontSize   = 11.sp,
            fontWeight = FontWeight.Bold,
            color      = textColor
        )
    }
}

// ─────────────────────────────────────────────────────────────
//  Info Field (label kecil + value)
// ─────────────────────────────────────────────────────────────
@Composable
private fun BerkasInfoField(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier              = Modifier.padding(bottom = 4.dp)
        ) {
            Icon(icon, null,
                tint     = TextHint,
                modifier = Modifier.size(13.dp))
            Text(
                label.uppercase(),
                fontSize      = 9.sp,
                fontWeight    = FontWeight.Bold,
                color         = TextHint,
                letterSpacing = 0.7.sp
            )
        }
        Text(
            value,
            fontSize   = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color      = TextHead
        )
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
    onEyeClick: () -> Unit
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

// ─────────────────────────────────────────────────────────────
//  Preview
// ─────────────────────────────────────────────────────────────
@Preview(showBackground = true, widthDp = 375, heightDp = 812)
@Composable
fun DetailBerkasPreview() {
    DetailBerkasUsulMusnahScreen()
}