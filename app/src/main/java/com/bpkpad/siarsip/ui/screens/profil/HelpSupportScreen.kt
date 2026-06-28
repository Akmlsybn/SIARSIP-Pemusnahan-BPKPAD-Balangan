package com.bpkpad.siarsip.ui.screens.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bpkpad.siarsip.ui.theme.*

// ─────────────────────────────────────────────────────────────
//  Dummy data
// ─────────────────────────────────────────────────────────────
private data class HsFaqItem(val question: String, val answer: String)

private data class HsContactItem(
    val icon: ImageVector,
    val iconBg: Color,
    val iconTint: Color,
    val title: String,
    val subtitle: String
)

private val hsDummyFaqs = listOf(
    HsFaqItem(
        question = "Bagaimana cara mengajukan usul musnah arsip?",
        answer   = "Masuk ke menu Pemusnahan → pilih \"Buat Usul Musnah\" → isi data berkas arsip yang akan dimusnahkan → kirim pengajuan kepada atasan untuk persetujuan."
    ),
    HsFaqItem(
        question = "Siapa yang berwenang menyetujui berita acara pemusnahan?",
        answer   = "Berita acara pemusnahan harus disetujui oleh Kepala BPKPAD dan pejabat yang berwenang sesuai ketentuan peraturan perundang-undangan yang berlaku."
    ),
    HsFaqItem(
        question = "Berapa lama proses verifikasi usul musnah?",
        answer   = "Proses verifikasi biasanya memerlukan waktu 3–7 hari kerja tergantung volume arsip dan ketersediaan pejabat yang berwenang."
    ),
    HsFaqItem(
        question = "Apakah arsip yang sudah dimusnahkan bisa dipulihkan?",
        answer   = "Tidak. Setelah berita acara pemusnahan ditandatangani dan arsip dimusnahkan, proses tidak dapat dibatalkan. Pastikan seluruh verifikasi telah dilakukan sebelum eksekusi pemusnahan."
    ),
    HsFaqItem(
        question = "Bagaimana cara melihat riwayat aktivitas saya?",
        answer   = "Buka menu Profil → pilih \"Activity History\" untuk melihat seluruh riwayat aktivitas Anda di SIARSIP."
    )
)

private val hsContactItems = listOf(
    HsContactItem(
        icon     = Icons.Filled.Email,
        iconBg   = GreenLight,
        iconTint = GreenPrimary,
        title    = "Email Dukungan",
        subtitle = "siarsip@bpkpad-balangan.go.id"
    ),
    HsContactItem(
        icon     = Icons.Filled.Phone,
        iconBg   = BlueBg,
        iconTint = BlueText,
        title    = "Telepon Helpdesk",
        subtitle = "(0526) 123-4567 — Senin–Jumat, 08.00–16.00"
    ),
    HsContactItem(
        icon     = Icons.Filled.Forum,
        iconBg   = PurpleBg,
        iconTint = PurpleText,
        title    = "Live Chat",
        subtitle = "Tersedia pada jam kerja"
    )
)

// ─────────────────────────────────────────────────────────────
//  Screen
// ─────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpSupportScreen(
    onBack: () -> Unit = {}
) {
    Scaffold(
        containerColor = BgDashboard,
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text(
                            "Help & Support",
                            fontSize   = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color      = TextHead
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                Icons.Filled.ArrowBack,
                                contentDescription = "Kembali",
                                tint = GreenPrimary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = CardWhite
                    )
                )
                HorizontalDivider(thickness = 0.5.dp, color = BorderGray)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(24.dp))

            // ── Hero banner ───────────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape    = RoundedCornerShape(20.dp),
                colors   = CardDefaults.cardColors(containerColor = GreenPrimary)
            ) {
                Row(
                    modifier              = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(GreenLight, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Filled.SupportAgent,
                            contentDescription = null,
                            tint     = GreenPrimary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text       = "Tim Dukungan SIARSIP",
                            fontSize   = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color      = Color.White
                        )
                        Text(
                            text     = "Kami siap membantu Anda 24/7",
                            fontSize = 12.sp,
                            color    = GreenMid,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // ── Quick actions ─────────────────────────────────────
            HsSectionTitle("Aksi Cepat")
            Spacer(Modifier.height(10.dp))

            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                HsQuickAction(
                    icon     = Icons.Filled.MenuBook,
                    iconBg   = GreenLight,
                    iconTint = GreenPrimary,
                    label    = "Panduan\nPengguna",
                    modifier = Modifier.weight(1f)
                )
                HsQuickAction(
                    icon     = Icons.Filled.PlayCircle,
                    iconBg   = BlueBg,
                    iconTint = BlueText,
                    label    = "Video\nTutorial",
                    modifier = Modifier.weight(1f)
                )
                HsQuickAction(
                    icon     = Icons.Filled.BugReport,
                    iconBg   = DangerBg,
                    iconTint = DangerText,
                    label    = "Laporkan\nMasalah",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(24.dp))

            // ── FAQ ───────────────────────────────────────────────
            HsSectionTitle("Pertanyaan Umum (FAQ)")
            Spacer(Modifier.height(10.dp))

            hsDummyFaqs.forEachIndexed { index, faq ->
                HsFaqCard(faq = faq)
                if (index < hsDummyFaqs.lastIndex) {
                    Spacer(Modifier.height(8.dp))
                }
            }

            Spacer(Modifier.height(24.dp))

            // ── Hubungi Kami ──────────────────────────────────────
            HsSectionTitle("Hubungi Kami")
            Spacer(Modifier.height(10.dp))

            hsContactItems.forEachIndexed { index, item ->
                HsContactCard(item = item)
                if (index < hsContactItems.lastIndex) {
                    Spacer(Modifier.height(10.dp))
                }
            }

            Spacer(Modifier.height(24.dp))

            // ── App version ───────────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape    = RoundedCornerShape(16.dp),
                colors   = CardDefaults.cardColors(containerColor = CardWhite),
                border   = BorderStroke(1.dp, BorderGray)
            ) {
                Row(
                    modifier              = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Text(
                        text       = "Versi Aplikasi",
                        fontSize   = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color      = TextBody
                    )
                    Box(
                        modifier = Modifier
                            .background(GreenLight, RoundedCornerShape(9999.dp))
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text       = "v1.0.0",
                            fontSize   = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color      = GreenPrimary
                        )
                    }
                }
            }

            Spacer(Modifier.height(28.dp))
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  Internal composables
// ─────────────────────────────────────────────────────────────

@Composable
private fun HsSectionTitle(title: String) {
    Text(
        text       = title,
        fontSize   = 16.sp,
        fontWeight = FontWeight.Bold,
        color      = TextHead,
        modifier   = Modifier.fillMaxWidth()
    )
}

@Composable
private fun HsQuickAction(
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    label: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape    = RoundedCornerShape(16.dp),
        colors   = CardDefaults.cardColors(containerColor = CardWhite),
        border   = BorderStroke(1.dp, BorderGray)
    ) {
        Column(
            modifier            = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(iconBg, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint     = iconTint,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text       = label,
                fontSize   = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color      = TextHead,
                lineHeight = 15.sp
            )
        }
    }
}

@Composable
private fun HsFaqCard(faq: HsFaqItem) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication        = null,
                onClick           = { expanded = !expanded }
            ),
        shape    = RoundedCornerShape(16.dp),
        colors   = CardDefaults.cardColors(containerColor = CardWhite),
        border   = BorderStroke(1.dp, BorderGray)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(GreenLight, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text       = "Q",
                        fontSize   = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color      = GreenPrimary
                    )
                }
                Text(
                    text       = faq.question,
                    fontSize   = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = TextHead,
                    modifier   = Modifier.weight(1f)
                )
                Icon(
                    imageVector        = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = null,
                    tint               = TextHint,
                    modifier           = Modifier.size(20.dp)
                )
            }

            if (expanded) {
                HorizontalDivider(
                    thickness = 0.5.dp,
                    color     = BorderGray,
                    modifier  = Modifier.padding(vertical = 10.dp)
                )
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(BlueBg, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text       = "A",
                            fontSize   = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color      = BlueText
                        )
                    }
                    Text(
                        text       = faq.answer,
                        fontSize   = 13.sp,
                        color      = TextBody,
                        lineHeight = 19.sp,
                        modifier   = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun HsContactCard(item: HsContactItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(16.dp),
        colors   = CardDefaults.cardColors(containerColor = CardWhite),
        border   = BorderStroke(1.dp, BorderGray)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(item.iconBg, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    item.icon,
                    contentDescription = null,
                    tint     = item.iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text       = item.title,
                    fontSize   = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = TextHead
                )
                Text(
                    text       = item.subtitle,
                    fontSize   = 11.sp,
                    color      = TextHint,
                    lineHeight = 15.sp,
                    modifier   = Modifier.padding(top = 2.dp)
                )
            }
            Icon(
                Icons.Filled.ChevronRight,
                contentDescription = null,
                tint     = TextHint,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  Preview
// ─────────────────────────────────────────────────────────────
@Preview(showBackground = true, widthDp = 375, heightDp = 812)
@Composable
fun HelpSupportScreenPreview() {
    HelpSupportScreen()
}
