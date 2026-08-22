package com.example.arsipbpkpad.presentation.home.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AddCard
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bpkpad.siarsip.R
import com.example.arsipbpkpad.domain.model.UserRole
import com.example.arsipbpkpad.domain.model.canManageStaging
import com.example.arsipbpkpad.domain.model.canManageStorage
import com.example.arsipbpkpad.domain.model.canViewAnalytics
import com.example.arsipbpkpad.presentation.components.BottomNavItem
import com.example.arsipbpkpad.presentation.components.BpkpadBottomNavigation
import com.example.arsipbpkpad.presentation.components.BpkpadBrandTopAppBar
import com.example.arsipbpkpad.presentation.components.BpkpadExpandableFAB
import com.example.arsipbpkpad.presentation.components.BpkpadLogoutButton
import com.example.arsipbpkpad.presentation.home.HomeUiState
import com.example.arsipbpkpad.presentation.home.HomeViewModel
import com.example.arsipbpkpad.presentation.home.component.HeaderSection
import com.example.arsipbpkpad.presentation.home.component.PrimaryStatCard
import com.example.arsipbpkpad.presentation.home.component.RecentArchiveTable
import com.example.arsipbpkpad.presentation.home.component.SecondaryStatCard
import com.example.arsipbpkpad.presentation.home.component.SectionHeader

@Composable
fun HomeScreen(
    onNavigateToArchiveList: (Int?) -> Unit,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToStagingBoxList: () -> Unit,
    onNavigateToBoxManagement: () -> Unit,
    onNavigateToRapidInput: (String) -> Unit,
    onNavigateToAnalytics: () -> Unit,
    onNavigateToScan: () -> Unit,
    onLogout: () -> Unit,
    userRole: UserRole = UserRole.SUPER_ADMIN,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HomeContent(
        uiState = uiState,
        userRole = userRole,
        onNavigateToArchiveList = onNavigateToArchiveList,
        onNavigateToDetail = onNavigateToDetail,
        onNavigateToStagingBoxList = onNavigateToStagingBoxList,
        onNavigateToBoxManagement = onNavigateToBoxManagement,
        onNavigateToRapidInput = onNavigateToRapidInput,
        onNavigateToAnalytics = onNavigateToAnalytics,
        onNavigateToScan = onNavigateToScan,
        onLogout = onLogout
    )
}

@Composable
fun HomeContent(
    uiState: HomeUiState,
    userRole: UserRole,
    onNavigateToArchiveList: (Int?) -> Unit,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToStagingBoxList: () -> Unit,
    onNavigateToBoxManagement: () -> Unit,
    onNavigateToRapidInput: (String) -> Unit,
    onNavigateToAnalytics: () -> Unit,
    onNavigateToScan: () -> Unit,
    onLogout: () -> Unit
) {
    Scaffold(
        topBar = { 
            BpkpadBrandTopAppBar(
                actions = {
                    BpkpadLogoutButton(
                        onClick = onLogout,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            ) 
        },
        floatingActionButton = {
            BpkpadExpandableFAB(
                onManualInputClick = { onNavigateToStagingBoxList() },
                onOcrScanClick = onNavigateToScan
            )
        },
        bottomBar = {
            HomeBottomNavigation(
                userRole = userRole,
                onNavigateToArchiveList = onNavigateToArchiveList,
                onNavigateToStagingBoxList = onNavigateToStagingBoxList,
                onNavigateToBoxManagement = onNavigateToBoxManagement,
                onNavigateToAnalytics = onNavigateToAnalytics
            )
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) { paddingValues ->
        HomeMainList(
            uiState = uiState,
            userRole = userRole,
            paddingValues = paddingValues,
            onNavigateToArchiveList = onNavigateToArchiveList,
            onNavigateToDetail = onNavigateToDetail,
            onNavigateToStagingBoxList = onNavigateToStagingBoxList,
            onNavigateToBoxManagement = onNavigateToBoxManagement,
            onNavigateToAnalytics = onNavigateToAnalytics,
            onNavigateToScan = onNavigateToScan
        )
    }
}

@Composable
fun HomeBottomNavigation(
    userRole: UserRole,
    onNavigateToArchiveList: (Int?) -> Unit,
    onNavigateToStagingBoxList: () -> Unit,
    onNavigateToBoxManagement: () -> Unit,
    onNavigateToAnalytics: () -> Unit
) {
    BpkpadBottomNavigation(
        currentRoute = BottomNavItem.HOME.route,
        userRole = userRole,
        onNavigate = { item ->
            when (item) {
                BottomNavItem.HOME -> { }
                BottomNavItem.ARCHIVE -> onNavigateToArchiveList(null)
                BottomNavItem.ADD -> onNavigateToStagingBoxList()
                BottomNavItem.STORAGE -> onNavigateToBoxManagement()
                BottomNavItem.ANALYTICS -> onNavigateToAnalytics()
            }
        }
    )
}

@Composable
fun HomeMainList(
    uiState: HomeUiState,
    userRole: UserRole,
    paddingValues: PaddingValues,
    onNavigateToArchiveList: (Int?) -> Unit,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToStagingBoxList: () -> Unit,
    onNavigateToBoxManagement: () -> Unit,
    onNavigateToAnalytics: () -> Unit,
    onNavigateToScan: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            top = paddingValues.calculateTopPadding() + 16.dp,
            bottom = paddingValues.calculateBottomPadding() + 88.dp,
            start = 20.dp,
            end = 20.dp
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { HeaderSection() }

        // ── Grid Pintas Akses Modul Keuangan ─────────────────────────────────
        item {
            HomeQuickActionGrid(
                onNavigateToStagingBoxList = onNavigateToStagingBoxList,
                onNavigateToScan = onNavigateToScan,
                onNavigateToBoxManagement = onNavigateToBoxManagement,
                onNavigateToAnalytics = onNavigateToAnalytics
            )
        }

        if (uiState.activeStagingBoxes.isNotEmpty()) {
            item {
                val totalDocs = uiState.activeStagingBoxes.sumOf { it.itemCount }
                val totalBoxes = uiState.activeStagingBoxes.size
                StagingStatusCard(
                    count = totalDocs,
                    summary = stringResource(R.string.staging_boxes_ready, totalBoxes),
                    onClick = onNavigateToStagingBoxList,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        }

        item { HomePrimaryStats(uiState) }
        item { HomeSecondaryStats(uiState) }

        item {
            SectionHeader(
                title = stringResource(R.string.recently_added),
                actionText = stringResource(R.string.view_all),
                onActionClick = { onNavigateToArchiveList(null) }
            )
        }

        item {
            RecentArchiveTable(
                items = uiState.recentItems,
                onArchiveClick = onNavigateToDetail
            )
        }
    }
}

@Composable
private fun HomeQuickActionGrid(
    onNavigateToStagingBoxList: () -> Unit,
    onNavigateToScan: () -> Unit,
    onNavigateToBoxManagement: () -> Unit,
    onNavigateToAnalytics: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = "PINTAS FITUR UTAMA",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            letterSpacing = 0.8.sp
        )
        val primaryColor = MaterialTheme.colorScheme.primary
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            QuickActionButton(
                modifier = Modifier.weight(1f),
                title = "Rapid Input",
                subtitle = "Input Staging",
                icon = Icons.Default.AddCard,
                color = primaryColor,
                onClick = onNavigateToStagingBoxList
            )
            QuickActionButton(
                modifier = Modifier.weight(1f),
                title = "Scan OCR",
                subtitle = "Pemindaian",
                icon = Icons.Default.CameraAlt,
                color = primaryColor,
                onClick = onNavigateToScan
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            QuickActionButton(
                modifier = Modifier.weight(1f),
                title = "Storage Box",
                subtitle = "Gudang & Rak",
                icon = Icons.Default.Inventory2,
                color = primaryColor,
                onClick = onNavigateToBoxManagement
            )
            QuickActionButton(
                modifier = Modifier.weight(1f),
                title = "Analitik",
                subtitle = "Grafik Laporan",
                icon = Icons.Default.Analytics,
                color = primaryColor,
                onClick = onNavigateToAnalytics
            )
        }
    }
}

@Composable
private fun QuickActionButton(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = color,
                    modifier = Modifier.size(22.dp)
                )
            }
            Column {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = subtitle,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun HomePrimaryStats(uiState: HomeUiState) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        PrimaryStatCard(
            title = stringResource(R.string.total_documents),
            count = uiState.totalDocuments,
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
        PrimaryStatCard(
            title = stringResource(R.string.expired_documents),
            count = uiState.expiredDocuments,
            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f),
            contentColor = MaterialTheme.colorScheme.onErrorContainer
        )
    }
}

@Composable
fun HomeSecondaryStats(uiState: HomeUiState) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SecondaryStatCard(
                modifier = Modifier.weight(1f),
                count = uiState.sp2dCount,
                label = stringResource(R.string.type_sp2d)
            )
            SecondaryStatCard(
                modifier = Modifier.weight(1f),
                count = uiState.spmCount,
                label = stringResource(R.string.type_spm)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SecondaryStatCard(
                modifier = Modifier.weight(1f),
                count = uiState.spjCount,
                label = stringResource(R.string.type_spj)
            )
            SecondaryStatCard(
                modifier = Modifier.weight(1f),
                count = uiState.otherTypeCount,
                label = stringResource(R.string.type_other)
            )
        }
    }
}

@Composable
fun StagingStatusCard(
    count: Int,
    summary: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            StagingCardHeader()
            Spacer(modifier = Modifier.height(16.dp))
            StagingCardContent(count = count, summary = summary)
            Spacer(modifier = Modifier.height(16.dp))
            StagingProgressBar()
        }
    }
}

@Composable
fun StagingCardHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = stringResource(R.string.btn_sync),
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = stringResource(R.string.staging_status_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

@Composable
fun StagingCardContent(count: Int, summary: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(MaterialTheme.colorScheme.primary, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.List,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(24.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column {
            Text(
                text = stringResource(R.string.staging_docs_waiting, count),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Text(
                text = summary,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun StagingProgressBar() {
    LinearProgressIndicator(
        progress = { 1f },
        modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
    )
}
