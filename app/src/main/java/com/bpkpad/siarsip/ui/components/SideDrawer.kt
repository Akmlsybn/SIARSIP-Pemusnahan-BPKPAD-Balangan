package com.bpkpad.siarsip.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bpkpad.siarsip.ui.theme.*

// ─────────────────────────────────────────────────────────────
//  Route constants — pakai di semua layar
// ─────────────────────────────────────────────────────────────
object DrawerRoutes {
    const val DASHBOARD           = "dashboard"
    const val DAFTAR_ARSIP        = "daftar_arsip"
    const val DAFTAR_USUL_MUSNAH  = "daftar_usul_musnah"
    const val TRACKING            = "status_tracking"
    const val BERITA_ACARA        = "berita_acara"
    const val LOG_RIWAYAT         = "log_riwayat"
    const val PROFIL              = "profil"
}

// ─────────────────────────────────────────────────────────────
//  Data model menu item
// ─────────────────────────────────────────────────────────────
private data class DrawerItem(
    val route: String,
    val name: String,
    val description: String,
    val icon: ImageVector,
    val badge: String?       = null,
    val badgeBg: Color       = Color.Transparent,
    val badgeTextColor: Color = Color.White
)

// ─────────────────────────────────────────────────────────────
//  Drawer Content — isi dari side drawer
// ─────────────────────────────────────────────────────────────
@Composable
fun PemusnahanDrawerContent(
    currentRoute: String    = DrawerRoutes.DASHBOARD,
    onNavigate: (String) -> Unit = {},
    onLogout: () -> Unit    = {}
) {
    var showLogoutDialog by remember { androidx.compose.runtime.mutableStateOf(false) }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = {
                Text(
                    text = "Konfirmasi Keluar",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = TextHead
                )
            },
            text = {
                Text(
                    text = "Apakah Anda yakin ingin keluar dari aplikasi?",
                    fontSize = 14.sp,
                    color = TextBody
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        onLogout()
                    }
                ) {
                    Text(text = "Keluar", color = DangerText, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text(text = "Batal", color = TextHint)
                }
            },
            containerColor = CardWhite,
            shape = RoundedCornerShape(16.dp)
        )
    }

    val menuItems = listOf(
        DrawerItem(
            route       = DrawerRoutes.DASHBOARD,
            name        = "Beranda",
            description = "Halaman utama dashboard",
            icon        = Icons.Filled.Home
        ),
        DrawerItem(
            route       = DrawerRoutes.PROFIL,
            name        = "Profil Akun",
            description = "Detail profil pengguna",
            icon        = Icons.Filled.AccountCircle
        )
    )

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .background(CardWhite)
    ) {

        // ── Hero Header (FIXED — tidak ikut scroll) ───────────
        DrawerHeroHeader()

        // ── Menu items (SCROLLABLE) ────────────────────────────
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(vertical = 8.dp)
        ) {
            DrawerSectionLabel("MENU UTAMA")
            menuItems.forEach { item ->
                DrawerMenuItem(
                    item        = item,
                    isActive    = currentRoute == item.route,
                    onNavigate  = onNavigate
                )
            }
        }

        // ── Footer: Keluar (FIXED — tidak ikut scroll) ────────
        HorizontalDivider(thickness = 0.5.dp, color = BorderGray)
        DrawerMenuItem(
            item = DrawerItem(
                route       = "logout",
                name        = "Keluar",
                description = "",
                icon        = Icons.Filled.Logout,
            ),
            isActive   = false,
            isLogout   = true,
            onNavigate = { showLogoutDialog = true }
        )
        Text(
            text     = "SiARSIP v1.0 • BPKPAD Balangan",
            fontSize = 9.sp,
            color    = TextHint,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

// ─────────────────────────────────────────────────────────────
//  Hero Header (bagian hijau atas drawer)
// ─────────────────────────────────────────────────────────────
@Composable
private fun DrawerHeroHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(GreenPrimary)
            .padding(start = 18.dp, end = 18.dp, top = 32.dp, bottom = 20.dp)
    ) {
        Column {
            // Avatar
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(CardWhite)
                    .border(3.dp, Color.White.copy(alpha = 0.25f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text  = "AF",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = GreenPrimary
                )
            }

            Spacer(Modifier.height(12.dp))

            Text("Ahmad Fauzi",
                fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text("Admin Arsip • Bidang Pemusnahan",
                fontSize = 11.sp, color = Color.White.copy(alpha = 0.75f),
                modifier = Modifier.padding(top = 3.dp))

            Spacer(Modifier.height(10.dp))

            // ID Badge
            Row(
                modifier = Modifier
                    .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 10.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Icon(Icons.Filled.Badge, contentDescription = null,
                    tint = Color.White.copy(alpha = 0.75f),
                    modifier = Modifier.size(12.dp))
                Text("BAL-4920", fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.9f))
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  Section label
// ─────────────────────────────────────────────────────────────
@Composable
private fun DrawerSectionLabel(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 18.dp, end = 16.dp,
                top = 10.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text       = text,
            fontSize   = 9.sp,
            fontWeight = FontWeight.Bold,
            color      = TextHint,
            letterSpacing = 0.8.sp
        )
        HorizontalDivider(
            modifier  = Modifier.weight(1f),
            thickness = 0.5.dp,
            color     = BorderGray
        )
    }
}

// ─────────────────────────────────────────────────────────────
//  Menu Item Row
// ─────────────────────────────────────────────────────────────
@Composable
private fun DrawerMenuItem(
    item: DrawerItem,
    isActive: Boolean,
    isLogout: Boolean = false,
    onNavigate: (String) -> Unit
) {
    val bgColor   = if (isActive) GreenLight else Color.Transparent
    val nameColor = when {
        isLogout -> DangerText
        isActive -> GreenPrimary
        else     -> TextHead
    }
    val iconBoxBg = when {
        isLogout -> DangerBg
        isActive -> GreenPrimary
        else     -> Color(0xFFF0F2F0)
    }
    val iconTint = when {
        isLogout -> DangerText
        isActive -> Color.White
        else     -> TextBody
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(bgColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication        = null
            ) { onNavigate(item.route) }
            .padding(horizontal = 10.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Icon box
        Box(
            modifier = Modifier
                .size(38.dp)
                .background(iconBoxBg, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(item.icon, contentDescription = null,
                tint = iconTint, modifier = Modifier.size(19.dp))
        }

        // Text
        Column(modifier = Modifier.weight(1f)) {
            Text(item.name, fontSize = 13.sp,
                fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal,
                color = nameColor)
            if (item.description.isNotEmpty()) {
                Text(item.description, fontSize = 10.sp,
                    color = if (isActive) GreenPrimary.copy(alpha = 0.7f) else TextHint,
                    modifier = Modifier.padding(top = 1.dp))
            }
        }

        // Badge atau Chevron
        if (item.badge != null) {
            Box(
                modifier = Modifier
                    .background(item.badgeBg, RoundedCornerShape(20.dp))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(item.badge, fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = item.badgeTextColor)
            }
        } else if (!isLogout) {
            Icon(Icons.Filled.ChevronRight, contentDescription = null,
                tint = if (isActive) GreenPrimary else TextHint,
                modifier = Modifier.size(16.dp))
        }
    }
}