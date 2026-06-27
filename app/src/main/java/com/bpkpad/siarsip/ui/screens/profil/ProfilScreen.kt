package com.bpkpad.siarsip.ui.screens.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
//  Data Model
// ─────────────────────────────────────────────────────────────
data class UserProfile(
    val nama: String,
    val role: String,        // "Arsiparis BPKPAD"
    val idPegawai: String,   // "BAL-4920"
    val avatarUrl: String? = null
)

val dummyUserProfile = UserProfile(
    nama      = "Lorem Ipsum",
    role      = "Arsiparis BPKPAD",
    idPegawai = "BAL-4920"
)

// ─────────────────────────────────────────────────────────────
//  Screen Utama
// ─────────────────────────────────────────────────────────────
@Composable
fun ProfileScreen(
    user: UserProfile = dummyUserProfile,
    onBack: () -> Unit = {},
    onEditPhoto: () -> Unit = {},
    onProfileSettings: () -> Unit = {},
    onActivityHistory: () -> Unit = {},
    onHelpSupport: () -> Unit = {},
    onLogout: () -> Unit = {},
    onNavigateDashboard: () -> Unit = {},
    onNavigateArsip: () -> Unit = {},
    onNavigateAktivitas: () -> Unit = {}
) {
    Scaffold(
        containerColor = BgDashboard,
        topBar = { ProfileTopBar() },
        bottomBar = {
            ProfileBottomBar(
                onDashboard = onNavigateDashboard,
                onArsip     = onNavigateArsip,
                onAktivitas = onNavigateAktivitas
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.height(24.dp))

            // ── Avatar dengan tombol edit ─────────────────────
            Box {
                Box(
                    modifier = Modifier
                        .size(112.dp)
                        .clip(CircleShape)
                        .background(GreenLight)
                        .border(3.dp, CardWhite, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (user.avatarUrl != null) {
                        // Kalau ada foto, pakai Coil/AsyncImage:
                        // AsyncImage(model = user.avatarUrl, contentDescription = null,
                        //     modifier = Modifier.fillMaxSize().clip(CircleShape),
                        //     contentScale = ContentScale.Crop)
                    } else {
                        Icon(
                            Icons.Filled.Person,
                            contentDescription = null,
                            tint     = GreenPrimary,
                            modifier = Modifier.size(56.dp)
                        )
                    }
                }
                // Edit button — pojok kanan bawah avatar
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(34.dp)
                        .background(GreenPrimary, CircleShape)
                        .border(2.dp, CardWhite, CircleShape)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication        = null,
                            onClick           = onEditPhoto
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.Edit,
                        contentDescription = "Ubah foto",
                        tint     = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // ── Nama ──────────────────────────────────────────
            Text(
                text       = user.nama,
                fontSize   = 24.sp,
                fontWeight = FontWeight.Bold,
                color      = TextHead
            )

            Spacer(Modifier.height(8.dp))

            // ── Badge role + ID ───────────────────────────────
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .background(GreenLight, RoundedCornerShape(9999.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        user.role,
                        fontSize   = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color      = GreenPrimary
                    )
                }
                Box(
                    modifier = Modifier
                        .background(BlueBg, RoundedCornerShape(9999.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        "ID: ${user.idPegawai}",
                        fontSize   = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color      = BlueText
                    )
                }
            }

            Spacer(Modifier.height(28.dp))

            // ── Section title ─────────────────────────────────
            Text(
                "Account Settings",
                fontSize   = 16.sp,
                fontWeight = FontWeight.Bold,
                color      = TextHead,
                modifier   = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            )

            // ── Menu items ─────────────────────────────────────
            ProfileMenuItem(
                icon     = Icons.Filled.Person,
                iconBg   = GreenLight,
                iconTint = GreenPrimary,
                title    = "Profile Settings",
                subtitle = "Lorem Ipsum dot silor amet salwaliya",
                onClick  = onProfileSettings
            )

            Spacer(Modifier.height(10.dp))

            ProfileMenuItem(
                icon     = Icons.Filled.History,
                iconBg   = BlueBg,
                iconTint = BlueText,
                title    = "Activity History",
                subtitle = "Lorem Ipsum dot silor amet salwaliya",
                onClick  = onActivityHistory
            )

            Spacer(Modifier.height(10.dp))

            ProfileMenuItem(
                icon     = Icons.Filled.HelpOutline,
                iconBg   = GreenLight,
                iconTint = GreenPrimary,
                title    = "Help & Support",
                subtitle = "Lorem Ipsum dot silor amet salwaliya",
                onClick  = onHelpSupport
            )

            Spacer(Modifier.height(10.dp))

            ProfileMenuItem(
                icon       = Icons.Filled.Logout,
                iconBg     = DangerBg,
                iconTint   = DangerText,
                title      = "Logout",
                subtitle   = "Keluar dari akun",
                titleColor = DangerText,
                onClick    = onLogout
            )

            Spacer(Modifier.height(20.dp))
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  Top Bar
// ─────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileTopBar() {
    TopAppBar(
        title = {
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(Color.White, RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("B", color = GreenPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
                Text(
                    "BPKPAD Balangan",
                    fontSize   = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color      = GreenPrimary
                )
            }
        },
        actions = {
            Box(
                modifier = Modifier
                    .padding(end = 12.dp)
                    .size(34.dp)
                    .background(GreenLight, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.Person,
                    contentDescription = null,
                    tint     = GreenPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = CardWhite
        )
    )
    HorizontalDivider(thickness = 0.5.dp, color = BorderGray)
}

// ─────────────────────────────────────────────────────────────
//  Profile Menu Item
// ─────────────────────────────────────────────────────────────
@Composable
private fun ProfileMenuItem(
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    title: String,
    subtitle: String,
    titleColor: Color = TextHead,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication        = null,
                onClick           = onClick
            ),
        shape  = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        border = BorderStroke(1.dp, BorderGray)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Icon box bulat
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

            // Text
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    title,
                    fontSize   = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = titleColor
                )
                Text(
                    subtitle,
                    fontSize   = 11.sp,
                    color      = TextHint,
                    lineHeight = 15.sp,
                    modifier   = Modifier.padding(top = 2.dp)
                )
            }

            // Chevron
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
//  Bottom Navigation Bar — TANPA FAB, "Profil" aktif
// ─────────────────────────────────────────────────────────────
@Composable
private fun ProfileBottomBar(
    onDashboard: () -> Unit,
    onArsip: () -> Unit,
    onAktivitas: () -> Unit
) {
    NavigationBar(
        containerColor = CardWhite,
        tonalElevation = 0.dp,
        modifier = Modifier
            .clip(RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp))
    ) {
        NavigationBarItem(
            selected = false,
            onClick  = onDashboard,
            icon     = { Icon(Icons.Filled.Home, contentDescription = null) },
            label    = { Text("Beranda", fontSize = 10.sp) },
            colors   = NavigationBarItemDefaults.colors(
                unselectedIconColor = TextHint,
                unselectedTextColor = TextHint
            )
        )
        NavigationBarItem(
            selected = false,
            onClick  = onArsip,
            icon     = { Icon(Icons.Filled.Folder, contentDescription = null) },
            label    = { Text("Arsip", fontSize = 10.sp) },
            colors   = NavigationBarItemDefaults.colors(
                unselectedIconColor = TextHint,
                unselectedTextColor = TextHint
            )
        )
        NavigationBarItem(
            selected = false,
            onClick  = onAktivitas,
            icon     = { Icon(Icons.Filled.History, contentDescription = null) },
            label    = { Text("Aktivitas", fontSize = 10.sp) },
            colors   = NavigationBarItemDefaults.colors(
                unselectedIconColor = TextHint,
                unselectedTextColor = TextHint
            )
        )
        NavigationBarItem(
            selected = true,
            onClick  = {},
            icon     = {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .background(GreenPrimary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.Person,
                        contentDescription = null,
                        tint     = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            },
            label    = { Text("Profil", fontSize = 10.sp) },
            colors   = NavigationBarItemDefaults.colors(
                selectedIconColor   = GreenPrimary,
                selectedTextColor   = GreenPrimary,
                indicatorColor      = Color.Transparent,
                unselectedIconColor = TextHint,
                unselectedTextColor = TextHint
            )
        )
    }
}

// ─────────────────────────────────────────────────────────────
//  Preview
// ─────────────────────────────────────────────────────────────
@Preview(showBackground = true, widthDp = 375, heightDp = 812)
@Composable
fun ProfileScreenPreview() {
    ProfileScreen()
}