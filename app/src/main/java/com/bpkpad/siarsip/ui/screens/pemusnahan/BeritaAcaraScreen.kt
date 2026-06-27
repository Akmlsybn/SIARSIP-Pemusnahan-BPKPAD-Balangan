package com.bpkpad.siarsip.ui.screens.pemusnahan

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.bpkpad.siarsip.core.utils.ResultState
import com.bpkpad.siarsip.feature.arsip.domain.model.BeritaAcaraItem
import com.bpkpad.siarsip.feature.arsip.presentation.BeritaAcaraViewModel
import com.bpkpad.siarsip.ui.components.DrawerRoutes
import com.bpkpad.siarsip.ui.components.PemusnahanDrawerContent
import com.bpkpad.siarsip.ui.theme.*
import kotlinx.coroutines.launch

// ─────────────────────────────────────────────────────────────
//  Screen Utama
// ─────────────────────────────────────────────────────────────
@Composable
fun BeritaAcaraScreen(
    onNavigate: (String) -> Unit = {},
    onCardClick: (BeritaAcaraItem) -> Unit = {},
    viewModel: BeritaAcaraViewModel = hiltViewModel()
) {
    var searchQuery  by remember { mutableStateOf("") }
    var activeFilter by remember { mutableStateOf("Semua") }

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope       = rememberCoroutineScope()

    val filters = listOf("Semua", "2025", "2024", "2023")

    val uiState by viewModel.uiState.collectAsState()

    ModalNavigationDrawer(
        drawerState   = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier             = Modifier.fillMaxWidth(0.86f),
                drawerContainerColor = CardWhite,
                drawerShape          = RoundedCornerShape(0.dp)
            ) {
                PemusnahanDrawerContent(
                    currentRoute = DrawerRoutes.BERITA_ACARA,
                    onNavigate   = { route ->
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
                BATopBar(onMenuClick = { scope.launch { drawerState.open() } })
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
                            text = "Gagal memuat data: ${state.exception.message}",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 14.sp
                        )
                    }
                }
                is ResultState.Success -> {
                    val beritaAcaraList = state.data
                    val filteredList = beritaAcaraList.filter { ba ->
                        val matchFilter = activeFilter == "Semua" || ba.tahun == activeFilter
                        val matchSearch = searchQuery.isBlank() ||
                                ba.nomor.contains(searchQuery, ignoreCase = true) ||
                                ba.berkasNomor.contains(searchQuery, ignoreCase = true) ||
                                ba.perihal.contains(searchQuery, ignoreCase = true)
                        matchFilter && matchSearch
                    }

                    val countTahunIni = beritaAcaraList.count { it.tahun == "2025" }
                    val totalArsip    = beritaAcaraList.sumOf { it.jumlahArsip }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(padding),
                        contentPadding = PaddingValues(
                            start = 16.dp, end = 16.dp, top = 14.dp, bottom = 16.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                BAStatBox("${beritaAcaraList.size}", "Total BA",
                                    Icons.Filled.Description, CardWhite, TextHead, Modifier.weight(1f))
                                BAStatBox("$countTahunIni", "Tahun Ini",
                                    Icons.Filled.CalendarMonth, GreenLight, GreenPrimary, Modifier.weight(1f))
                                BAStatBox("$totalArsip", "Arsip Musnah",
                                    Icons.Filled.LocalFireDepartment, AmberBg, AmberText, Modifier.weight(1f))
                            }
                        }

                        item {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = {
                                    Text("Cari nomor BA, berkas, atau perihal...",
                                        fontSize = 13.sp, color = TextHint)
                                },
                                leadingIcon = {
                                    Icon(Icons.Filled.Search, null,
                                        tint = TextHint, modifier = Modifier.size(20.dp))
                                },
                                trailingIcon = if (searchQuery.isNotEmpty()) {
                                    {
                                        IconButton(onClick = { searchQuery = "" }) {
                                            Icon(Icons.Filled.Close, null,
                                                tint = TextHint, modifier = Modifier.size(18.dp))
                                        }
                                    }
                                } else null,
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                colors = baFieldColors(),
                                modifier = Modifier.fillMaxWidth().height(48.dp)
                            )
                        }

                        item {
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                                items(filters) { filter ->
                                    BAFilterChip(
                                        label = filter,
                                        isActive = activeFilter == filter,
                                        onClick = { activeFilter = filter }
                                    )
                                }
                            }
                        }

                        item {
                            Text("Menampilkan ${filteredList.size} berita acara",
                                fontSize = 11.sp, color = TextHint)
                        }

                        items(filteredList, key = { it.id }) { ba ->
                            BeritaAcaraCard(
                                ba = ba,
                                onClick = { onCardClick(ba) }
                            )
                        }
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
private fun BATopBar(onMenuClick: () -> Unit) {
    TopAppBar(
        title = {
            Column {
                Text("Berita Acara",
                    fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text("Pencatatan setelah pemusnahan",
                    fontSize = 10.sp, color = Color.White.copy(alpha = 0.6f))
            }
        },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(Icons.Filled.Menu, "Buka menu", tint = Color.White)
            }
        },
        actions = {
            IconButton(onClick = {}) {
                Icon(Icons.Filled.FileDownload, "Ekspor semua", tint = Color.White)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = GreenPrimary)
    )
}

// ─────────────────────────────────────────────────────────────
//  Stat Box
// ─────────────────────────────────────────────────────────────
@Composable
private fun BAStatBox(
    value: String,
    label: String,
    icon: ImageVector,
    bgColor: Color,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(bgColor, RoundedCornerShape(12.dp))
            .border(1.dp, BorderGray, RoundedCornerShape(12.dp))
            .padding(vertical = 12.dp, horizontal = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, null, tint = color, modifier = Modifier.size(18.dp))
        Spacer(Modifier.height(5.dp))
        Text(value, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = color)
        Text(label, fontSize = 9.sp, fontWeight = FontWeight.SemiBold,
            color = TextHint, letterSpacing = 0.3.sp, modifier = Modifier.padding(top = 2.dp))
    }
}

// ─────────────────────────────────────────────────────────────
//  Filter Chip
// ─────────────────────────────────────────────────────────────
@Composable
private fun BAFilterChip(
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
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 15.dp, vertical = 7.dp)
    ) {
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold,
            color = if (isActive) Color.White else TextBody)
    }
}

// ─────────────────────────────────────────────────────────────
//  Berita Acara Card — clickable (no accordion)
// ─────────────────────────────────────────────────────────────
@Composable
private fun BeritaAcaraCard(
    ba: BeritaAcaraItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        border = BorderStroke(1.dp, BorderGray)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth().padding(14.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DateBox(dateMultiline = ba.tanggalShort)

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(ba.nomor, fontSize = 13.sp, fontWeight = FontWeight.Bold,
                            color = TextHead, modifier = Modifier.weight(1f))
                        Row(
                            modifier = Modifier
                                .background(GreenLight, RoundedCornerShape(9999.dp))
                                .padding(horizontal = 7.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Icon(Icons.Filled.Verified, null,
                                tint = GreenPrimary, modifier = Modifier.size(10.dp))
                            Text("Tervalidasi", fontSize = 9.sp,
                                fontWeight = FontWeight.Bold, color = GreenPrimary)
                        }
                    }

                    Spacer(Modifier.height(5.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Icon(Icons.Filled.Link, null,
                            tint = TextHint, modifier = Modifier.size(11.dp))
                        Text(ba.berkasNomor, fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold, color = GreenPrimary)
                        Text("•", fontSize = 10.sp, color = TextHint)
                        Text("${ba.jumlahArsip} arsip", fontSize = 10.sp, color = TextBody)
                    }

                    Spacer(Modifier.height(8.dp))

                    Text(ba.perihal, fontSize = 12.sp, fontWeight = FontWeight.Medium,
                        color = TextHead, lineHeight = 17.sp,
                        maxLines = 2, overflow = TextOverflow.Ellipsis)
                }
            }

            HorizontalDivider(thickness = 0.5.dp, color = BorderGray)

            // Footer: clickable hint
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 11.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    Icon(Icons.Filled.Article, null,
                        tint = GreenPrimary, modifier = Modifier.size(15.dp))
                    Text("Lihat detail lengkap", fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold, color = GreenPrimary)
                }
                Icon(Icons.Filled.ChevronRight, null,
                    tint = GreenPrimary, modifier = Modifier.size(20.dp))
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  Date Box
// ─────────────────────────────────────────────────────────────
@Composable
private fun DateBox(dateMultiline: String) {
    val parts = dateMultiline.split("\n")
    Column(
        modifier = Modifier
            .size(60.dp)
            .background(GreenPrimary, RoundedCornerShape(11.dp)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(parts.getOrElse(0) { "" }, fontSize = 19.sp, fontWeight = FontWeight.Bold,
            color = Color.White, lineHeight = 22.sp)
        Text(parts.getOrElse(1) { "" }, fontSize = 9.sp, fontWeight = FontWeight.Bold,
            color = Color.White.copy(alpha = 0.85f), letterSpacing = 0.7.sp)
        Text(parts.getOrElse(2) { "" }, fontSize = 9.sp,
            color = Color.White.copy(alpha = 0.7f))
    }
}

@Composable
private fun baFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = GreenPrimary,
    unfocusedBorderColor = BorderGray,
    focusedContainerColor = CardWhite,
    unfocusedContainerColor = CardWhite,
    focusedTextColor = TextHead,
    unfocusedTextColor = TextHead,
    cursorColor = GreenPrimary
)

@Preview(showBackground = true, widthDp = 375, heightDp = 812)
@Composable
fun BeritaAcaraPreview() {
    BeritaAcaraScreen()
}