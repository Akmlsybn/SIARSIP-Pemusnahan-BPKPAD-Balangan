package com.bpkpad.siarsip.ui.screens.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bpkpad.siarsip.core.navigation.Screen
import com.bpkpad.siarsip.ui.components.DrawerRoutes
import com.bpkpad.siarsip.ui.components.PemusnahanBottomBar
import com.bpkpad.siarsip.ui.components.PemusnahanDrawerContent
import com.bpkpad.siarsip.ui.theme.*
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

@Composable
fun DashboardScreen(
    onNavigate: (String) -> Unit = {},
    onModuleClick: (String) -> Unit = {},
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.fillMaxWidth(0.86f),
                drawerContainerColor = CardWhite,
                drawerShape = RoundedCornerShape(0.dp)
            ) {
                PemusnahanDrawerContent(
                    currentRoute = DrawerRoutes.DASHBOARD,
                    onNavigate = { route ->
                        scope.launch { drawerState.close() }
                        onNavigate(route)
                    },
                    onLogout = {
                        scope.launch { drawerState.close() }
                        onNavigate("logout")
                    }
                )
            }
        }
    ) {
        Scaffold(
            containerColor = BgDashboard,
            topBar = {
                DashboardTopBar(
                    onMenuClick = {
                        scope.launch { drawerState.open() }
                    }
                )
            },
            bottomBar = {
                PemusnahanBottomBar(
                    currentRoute = DrawerRoutes.DASHBOARD,
                    onNavigate = onNavigate
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                GreetingCard()
                QuickStatsGrid(uiState)
                PemusnahanWorkflowSection(onNavigate)
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DashboardTopBar(onMenuClick: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = "Overview Pemusnahan Arsip BPKPAD",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(Icons.Filled.Menu, contentDescription = "Menu", tint = Color.White)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = GreenPrimary
        )
    )
}

@Composable
private fun GreetingCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        border = BorderStroke(1.dp, BorderGray)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Selamat datang,",
                    fontSize = 12.sp,
                    color = TextHint
                )
                Text(
                    text = "Admin Arsip",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = GreenPrimary
                )
                Spacer(Modifier.height(6.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(GreenPrimary, RoundedCornerShape(20.dp))
                            .padding(horizontal = 9.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "Tim Pemusnahan Arsip",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Text(
                        text = "• BPKPAD Balangan",
                        fontSize = 11.sp,
                        color = TextBody
                    )
                }
            }
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(GreenPrimary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "AA",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun QuickStatsGrid(uiState: PemusnahanDashboardUiState) {
    val formatter = NumberFormat.getInstance(Locale("id", "ID"))
    val formattedTotal = if (uiState.totalArsip >= 1000) {
        "${formatter.format(uiState.totalArsip)}+"
    } else {
        formatter.format(uiState.totalArsip)
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Ringkasan Data Riil (2015–2024)",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = TextHead
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatPill(formattedTotal, "TOTAL ARSIP", CardWhite, BorderGray, TextHead, Modifier.weight(1f))
            StatPill(formatter.format(uiState.tersedia), "TERSEDIA", GreenLight, BorderGray, GreenPrimary, Modifier.weight(1f))
            StatPill(formatter.format(uiState.usulMusnah), "USUL MUSNAH", AmberBg, Color(0xFFFDE68A), AmberText, Modifier.weight(1f))
            StatPill(formatter.format(uiState.musnah), "DIMUSNAHKAN", DangerBg, Color(0xFFFECACA), DangerText, Modifier.weight(1f))
        }
    }
}

@Composable
private fun StatPill(
    value: String,
    label: String,
    bgColor: Color,
    borderColor: Color,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = valueColor,
                maxLines = 1
            )
            Text(
                text = label,
                fontSize = 8.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextHint,
                letterSpacing = 0.2.sp
            )
        }
    }
}

@Composable
private fun PemusnahanWorkflowSection(onNavigate: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = "Menu Utama Pemusnahan Arsip",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = TextHead
        )

        WorkflowActionCard(
            title = "Daftar Berkas Arsip (2015–2024)",
            subtitle = "Eksplorasi, pencarian & filter 183.000+ data kearsipan riil",
            icon = Icons.Filled.Folder,
            iconBg = GreenLight,
            iconColor = GreenPrimary,
            onClick = { onNavigate(DrawerRoutes.DAFTAR_ARSIP) }
        )

        WorkflowActionCard(
            title = "Buat Berkas Usul Musnah",
            subtitle = "Pilih arsip riil berstatus Musnah lintas tahun & buat proposal BUM",
            icon = Icons.Filled.AddCircle,
            iconBg = AmberBg,
            iconColor = AmberText,
            onClick = { onNavigate(Screen.BuatBerkasUsulMusnah.route) }
        )

        WorkflowActionCard(
            title = "Daftar Berkas Usul Musnah (BUM)",
            subtitle = "Monitoring status verifikasi & persetujuan usul pemusnahan",
            icon = Icons.Filled.Assignment,
            iconBg = BlueBg,
            iconColor = BlueText,
            onClick = { onNavigate(DrawerRoutes.DAFTAR_USUL_MUSNAH) }
        )

        WorkflowActionCard(
            title = "Berita Acara Pemusnahan (BA)",
            subtitle = "Pembuatan Berita Acara resmi & penandatanganan saksi pemusnahan",
            icon = Icons.Filled.Gavel,
            iconBg = PurpleBg,
            iconColor = PurpleText,
            onClick = { onNavigate(DrawerRoutes.BERITA_ACARA) }
        )
    }
}

@Composable
private fun WorkflowActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconBg: Color,
    iconColor: Color,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        border = BorderStroke(1.dp, BorderGray)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(iconBg, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(22.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextHead
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = TextBody,
                    lineHeight = 14.sp
                )
            }
            Icon(
                Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = TextHint,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 375, heightDp = 812)
@Composable
fun PemusnahanDashboardPreview() {
    DashboardScreen()
}