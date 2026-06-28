package com.bpkpad.siarsip.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PlaylistAddCheck
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bpkpad.siarsip.ui.theme.CardWhite
import com.bpkpad.siarsip.ui.theme.GreenLight
import com.bpkpad.siarsip.ui.theme.GreenPrimary
import com.bpkpad.siarsip.ui.theme.TextHint

private data class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
)

@Composable
fun PemusnahanBottomBar(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    val items = listOf(
        BottomNavItem(
            route = DrawerRoutes.DAFTAR_ARSIP,
            icon  = Icons.Filled.Folder,
            label = "Arsip"
        ),
        BottomNavItem(
            route = DrawerRoutes.DAFTAR_USUL_MUSNAH,
            icon  = Icons.Filled.PlaylistAddCheck,
            label = "Usul Musnah"
        ),
        BottomNavItem(
            route = DrawerRoutes.TRACKING,
            icon  = Icons.Filled.Timeline,
            label = "Tracking"
        ),
        BottomNavItem(
            route = DrawerRoutes.BERITA_ACARA,
            icon  = Icons.Filled.AssignmentTurnedIn,
            label = "B.A. Musnah"
        ),
        BottomNavItem(
            route = DrawerRoutes.LOG_RIWAYAT,
            icon  = Icons.Filled.History,
            label = "Log"
        )
    )

    NavigationBar(
        containerColor = CardWhite,
        tonalElevation = 8.dp
    ) {
        items.forEach { item ->
            val isSelected = currentRoute == item.route
            NavigationBarItem(
                selected = isSelected,
                onClick  = { onNavigate(item.route) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        modifier = Modifier.size(20.dp),
                        tint = if (isSelected) GreenPrimary else TextHint
                    )
                },
                label = {
                    Text(
                        text       = item.label,
                        fontSize   = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color      = if (isSelected) GreenPrimary else TextHint
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = GreenLight
                )
            )
        }
    }
}
