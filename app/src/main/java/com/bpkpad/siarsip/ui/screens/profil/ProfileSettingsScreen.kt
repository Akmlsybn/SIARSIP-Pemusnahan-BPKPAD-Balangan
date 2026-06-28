package com.bpkpad.siarsip.ui.screens.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bpkpad.siarsip.ui.theme.*

// ─────────────────────────────────────────────────────────────
//  Screen
// ─────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSettingsScreen(
    onBack: () -> Unit = {}
) {
    Scaffold(
        containerColor = BgDashboard,
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text(
                            "Profile Settings",
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

            // ── Avatar ───────────────────────────────────────────
            Box(
                modifier         = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .background(GreenLight)
                        .border(3.dp, CardWhite, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.Person,
                        contentDescription = null,
                        tint     = GreenPrimary,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // ── Informasi Pribadi ─────────────────────────────────
            PssSectionTitle("Informasi Pribadi")
            Spacer(Modifier.height(10.dp))

            PssReadOnlyField(
                icon  = Icons.Filled.Person,
                label = "Nama Lengkap",
                value = "Lorem Ipsum"
            )
            Spacer(Modifier.height(10.dp))

            PssReadOnlyField(
                icon  = Icons.Filled.Badge,
                label = "ID Pegawai",
                value = "BAL-4920"
            )
            Spacer(Modifier.height(10.dp))

            PssReadOnlyField(
                icon  = Icons.Filled.Work,
                label = "Jabatan",
                value = "Arsiparis BPKPAD"
            )
            Spacer(Modifier.height(10.dp))

            PssReadOnlyField(
                icon  = Icons.Filled.AccountBalance,
                label = "Unit Kerja",
                value = "BPKPAD Kabupaten Balangan"
            )

            Spacer(Modifier.height(20.dp))

            // ── Kontak ────────────────────────────────────────────
            PssSectionTitle("Kontak")
            Spacer(Modifier.height(10.dp))

            PssReadOnlyField(
                icon  = Icons.Filled.Email,
                label = "Email",
                value = "lorem.ipsum@bpkpad-balangan.go.id"
            )
            Spacer(Modifier.height(10.dp))

            PssReadOnlyField(
                icon  = Icons.Filled.Phone,
                label = "Nomor Telepon",
                value = "+62 812-3456-7890"
            )

            Spacer(Modifier.height(20.dp))

            // ── Keamanan ──────────────────────────────────────────
            PssSectionTitle("Keamanan")
            Spacer(Modifier.height(10.dp))

            PssActionCard(
                icon     = Icons.Filled.Lock,
                iconBg   = GreenLight,
                iconTint = GreenPrimary,
                title    = "Ganti Password",
                subtitle = "Perbarui kata sandi akun Anda"
            )
            Spacer(Modifier.height(10.dp))

            PssToggleCard(
                icon     = Icons.Filled.Security,
                iconBg   = BlueBg,
                iconTint = BlueText,
                title    = "Autentikasi Dua Faktor",
                subtitle = "Tambah lapisan keamanan ekstra"
            )
            Spacer(Modifier.height(10.dp))

            PssActionCard(
                icon     = Icons.Filled.Notifications,
                iconBg   = AmberBg,
                iconTint = AmberText,
                title    = "Notifikasi",
                subtitle = "Atur preferensi pemberitahuan"
            )

            Spacer(Modifier.height(28.dp))
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  Internal composables
// ─────────────────────────────────────────────────────────────

@Composable
private fun PssSectionTitle(title: String) {
    Text(
        text       = title,
        fontSize   = 16.sp,
        fontWeight = FontWeight.Bold,
        color      = TextHead,
        modifier   = Modifier.fillMaxWidth().padding(bottom = 0.dp)
    )
}

@Composable
private fun PssReadOnlyField(
    icon: ImageVector,
    label: String,
    value: String
) {
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
                    .background(GreenLight, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint     = GreenPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text       = label,
                    fontSize   = 11.sp,
                    color      = TextHint,
                    lineHeight = 14.sp
                )
                Text(
                    text       = value,
                    fontSize   = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = TextHead,
                    modifier   = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}

@Composable
private fun PssActionCard(
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    title: String,
    subtitle: String
) {
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
                    .background(iconBg, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint     = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text       = title,
                    fontSize   = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = TextHead
                )
                Text(
                    text       = subtitle,
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

@Composable
private fun PssToggleCard(
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    title: String,
    subtitle: String
) {
    var checked by remember { mutableStateOf(false) }
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
                    .background(iconBg, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint     = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text       = title,
                    fontSize   = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = TextHead
                )
                Text(
                    text       = subtitle,
                    fontSize   = 11.sp,
                    color      = TextHint,
                    lineHeight = 15.sp,
                    modifier   = Modifier.padding(top = 2.dp)
                )
            }
            Switch(
                checked         = checked,
                onCheckedChange = { checked = it },
                colors = SwitchDefaults.colors(
                    checkedThumbColor   = Color.White,
                    checkedTrackColor   = GreenPrimary,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = BorderGray
                )
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  Preview
// ─────────────────────────────────────────────────────────────
@Preview(showBackground = true, widthDp = 375, heightDp = 812)
@Composable
fun ProfileSettingsScreenPreview() {
    ProfileSettingsScreen()
}
