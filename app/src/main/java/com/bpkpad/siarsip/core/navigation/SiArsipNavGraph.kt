package com.bpkpad.siarsip.core.navigation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.bpkpad.siarsip.feature.auth.presentation.LoginUiState
import com.bpkpad.siarsip.feature.auth.presentation.LoginViewModel
import com.bpkpad.siarsip.feature.portal.presentation.ModuleSelectionScreen
import com.bpkpad.siarsip.ui.screens.auth.LoginScreen
import com.bpkpad.siarsip.ui.screens.dashboard.DashboardScreen
import com.bpkpad.siarsip.ui.screens.pemusnahan.BeritaAcaraScreen
import com.bpkpad.siarsip.ui.screens.pemusnahan.BuatBerkasUsulMusnahScreen
import com.bpkpad.siarsip.ui.screens.pemusnahan.DaftarArsipScreen
import com.bpkpad.siarsip.ui.screens.pemusnahan.DaftarUsulMusnahScreen
import com.bpkpad.siarsip.ui.screens.pemusnahan.DetailBeritaAcaraScreen
import com.bpkpad.siarsip.ui.screens.pemusnahan.DetailBerkasUsulMusnahScreen
import com.bpkpad.siarsip.ui.screens.pemusnahan.LogRiwayatScreen
import com.bpkpad.siarsip.ui.screens.pemusnahan.StatusTrackingScreen
import com.bpkpad.siarsip.ui.screens.profile.ProfileScreen
import com.example.arsipbpkpad.domain.model.UserRole
import com.example.arsipbpkpad.presentation.components.BottomNavItem
import com.example.arsipbpkpad.ui.theme.ArsipBPKPADTheme

/**
 * Helper navigasi generik dipakai semua drawer (PemusnahanDrawerContent.onNavigate)
 * supaya satu fungsi bisa nge-handle semua DrawerRoutes string.
 */
fun NavHostController.navigateToRoute(route: String, onLogout: () -> Unit = {}) {
    if (currentDestination?.route == route) return

    when (route) {
        "logout" -> {
            onLogout()
            navigate(Screen.Login.route) {
                popUpTo(0) { inclusive = true }
            }
        }
        else -> navigate(route) {
            popUpTo(Screen.ModuleSelection.route) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
}

private fun NavHostController.handleKeuanganBottomNav(item: BottomNavItem) {
    when (item) {
        BottomNavItem.HOME -> navigate(Screen.KeuanganHome.route) {
            popUpTo(Screen.KeuanganHome.route) { inclusive = true }
        }
        BottomNavItem.ARCHIVE -> navigate(Screen.KeuanganArchiveList.createRoute()) { launchSingleTop = true }
        BottomNavItem.ADD -> navigate(Screen.KeuanganStagingBoxList.route) { launchSingleTop = true }
        BottomNavItem.STORAGE -> navigate(Screen.KeuanganBoxManagement.route) { launchSingleTop = true }
        BottomNavItem.ANALYTICS -> navigate(Screen.KeuanganAnalytics.route) { launchSingleTop = true }
    }
}

@Composable
fun SiArsipNavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Login.route,
    onLogout: () -> Unit = {}
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // ── Auth ────────────────────────────────────────────────────────────
        composable(Screen.Login.route) {
            val viewModel: LoginViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsState()
            val context = LocalContext.current

            LaunchedEffect(uiState) {
                when (uiState) {
                    is LoginUiState.Success -> {
                        navController.navigate(Screen.ModuleSelection.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                        viewModel.resetState()
                    }
                    is LoginUiState.Error -> {
                        Toast.makeText(context, (uiState as LoginUiState.Error).message, Toast.LENGTH_SHORT).show()
                        viewModel.resetState()
                    }
                    else -> {}
                }
            }

            LoginScreen(
                onLoginClick = { username, password, remember ->
                    viewModel.login(username, password, remember)
                }
            )
        }

        // ── Portal Hub ──────────────────────────────────────────────────────
        composable(Screen.ModuleSelection.route) {
            ModuleSelectionScreen(
                userName = "Budi Santoso",
                userRole = "Arsiparis BPKPAD",
                onSelectModule = { route -> navController.navigate(route) },
                onLogout = { navController.navigateToRoute("logout", onLogout) }
            )
        }

        // ── Modul 1: Pemusnahan (Main App) ──────────────────────────────────
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onNavigate = { route -> navController.navigateToRoute(route, onLogout) },
                onModuleClick = { moduleKey ->
                    when (moduleKey) {
                        "pemusnahan" -> navController.navigate(Screen.DaftarUsulMusnah.route)
                        "keuangan" -> navController.navigate(Screen.KeuanganHome.route)
                        "non_keuangan" -> navController.navigate(Screen.NonKeuanganHome.route)
                        "peminjaman" -> navController.navigate(Screen.PeminjamanDashboard.route)
                        else -> navController.navigate(Screen.DaftarArsip.route)
                    }
                }
            )
        }

        composable(
            route = Screen.DaftarArsip.route,
            arguments = listOf(
                navArgument("beritaAcaraId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val beritaAcaraId = backStackEntry.arguments?.getString("beritaAcaraId")
            DaftarArsipScreen(
                beritaAcaraId = beritaAcaraId,
                onNavigate = { route -> navController.navigateToRoute(route, onLogout) }
            )
        }

        composable(Screen.DaftarUsulMusnah.route) {
            DaftarUsulMusnahScreen(
                onBuatBerkas = { navController.navigate(Screen.BuatBerkasUsulMusnah.route) },
                onNavigate = { route -> navController.navigateToRoute(route, onLogout) },
                onDetailClick = { berkasNomor ->
                    navController.navigate(Screen.DetailBerkasUsulMusnah.createRoute(berkasNomor))
                }
            )
        }

        composable(Screen.BuatBerkasUsulMusnah.route) {
            BuatBerkasUsulMusnahScreen(
                onBack = { navController.popBackStack() },
                onSimpan = { navController.popBackStack() }
            )
        }

        composable(Screen.StatusTracking.route) {
            StatusTrackingScreen(
                onNavigate = { route -> navController.navigateToRoute(route, onLogout) }
            )
        }

        composable(Screen.BeritaAcara.route) {
            BeritaAcaraScreen(
                onNavigate = { route -> navController.navigateToRoute(route, onLogout) },
                onCardClick = { ba ->
                    navController.navigate(Screen.DetailBeritaAcara.createRoute(ba.id))
                }
            )
        }

        composable(Screen.DetailBeritaAcara.route) {
            DetailBeritaAcaraScreen(
                onBack = { navController.popBackStack() },
                onUnduhPdf = { },
                onLihatArsip = { baId -> navController.navigate(Screen.DaftarArsip.createRoute(baId)) },
                onCetak = { }
            )
        }

        composable(Screen.DetailBerkasUsulMusnah.route) {
            DetailBerkasUsulMusnahScreen(
                onBack = { navController.popBackStack() },
                onExportPdf = { },
                onLihatTracking = { navController.navigate(Screen.StatusTracking.route) }
            )
        }

        composable(Screen.LogRiwayat.route) {
            LogRiwayatScreen(
                onNavigate = { route -> navController.navigateToRoute(route, onLogout) }
            )
        }

        // ── Modul 2: Keuangan ───────────────────────────────────────────────
        composable(Screen.KeuanganHome.route) {
            ArsipBPKPADTheme {
                com.example.arsipbpkpad.presentation.home.screen.HomeScreen(
                    userRole = UserRole.SUPER_ADMIN,
                    onNavigateToArchiveList = { year -> navController.navigate(Screen.KeuanganArchiveList.createRoute(year)) },
                    onNavigateToDetail = { id -> navController.navigate(Screen.KeuanganArchiveDetail.createRoute(id)) },
                    onNavigateToStagingBoxList = { navController.navigate(Screen.KeuanganStagingBoxList.route) },
                    onNavigateToBoxManagement = { navController.navigate(Screen.KeuanganBoxManagement.route) },
                    onNavigateToRapidInput = { sessionId -> navController.navigate(Screen.KeuanganRapidInput.createRoute(sessionId)) },
                    onNavigateToAnalytics = { navController.navigate(Screen.KeuanganAnalytics.route) },
                    onNavigateToScan = { navController.navigate(Screen.KeuanganScan.route) },
                    onLogout = { navController.navigateToRoute("logout", onLogout) }
                )
            }
        }

        composable(
            route = Screen.KeuanganArchiveList.route,
            arguments = listOf(navArgument("year") { type = NavType.IntType; defaultValue = -1 })
        ) { backStackEntry ->
            val year = backStackEntry.arguments?.getInt("year")?.takeIf { it != -1 }
            ArsipBPKPADTheme {
                com.example.arsipbpkpad.presentation.archive.list.ArchiveListScreen(
                    year = year,
                    userRole = UserRole.SUPER_ADMIN,
                    onNavigateToDetail = { id -> navController.navigate(Screen.KeuanganArchiveDetail.createRoute(id)) },
                    onNavigateToRapidInput = { navController.navigate(Screen.KeuanganRapidInput.createRoute()) },
                    onNavigateToScan = { navController.navigate(Screen.KeuanganScan.route) },
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToBottomNav = { item -> navController.handleKeuanganBottomNav(item) }
                )
            }
        }

        composable(Screen.KeuanganArchiveDetail.route) { backStackEntry ->
            val archiveId = backStackEntry.arguments?.getString("archiveId") ?: ""
            ArsipBPKPADTheme {
                com.example.arsipbpkpad.presentation.archive.detail.ArchiveDetailScreen(
                    archiveId = archiveId,
                    userRole = UserRole.SUPER_ADMIN,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToArchive = { id -> navController.navigate(Screen.KeuanganArchiveDetail.createRoute(id)) },
                    onNavigateToEdit = { },
                    onNavigateToBottomNav = { item -> navController.handleKeuanganBottomNav(item) }
                )
            }
        }

        composable(Screen.KeuanganRapidInput.route) { backStackEntry ->
            val sessionId = backStackEntry.arguments?.getString("sessionId") ?: ""
            ArsipBPKPADTheme {
                com.example.arsipbpkpad.presentation.archive.add.manual.RapidInputScreen(
                    sessionId = sessionId,
                    userRole = UserRole.SUPER_ADMIN,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToScan = { navController.navigate(Screen.KeuanganScan.route) },
                    onNavigateToBottomNav = { item -> navController.handleKeuanganBottomNav(item) }
                )
            }
        }

        composable(Screen.KeuanganScan.route) {
            ArsipBPKPADTheme {
                com.example.arsipbpkpad.presentation.scan.ScanScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onResultDispatched = { navController.navigate(Screen.KeuanganRapidInput.createRoute()) }
                )
            }
        }

        composable(Screen.KeuanganBoxManagement.route) {
            ArsipBPKPADTheme {
                com.example.arsipbpkpad.presentation.storage.BoxManagementScreen(
                    userRole = UserRole.SUPER_ADMIN,
                    onNavigateToBottomNav = { item -> navController.handleKeuanganBottomNav(item) }
                )
            }
        }

        composable(Screen.KeuanganStagingBoxList.route) {
            val rapidInputViewModel: com.example.arsipbpkpad.presentation.archive.add.manual.RapidInputViewModel = hiltViewModel()
            ArsipBPKPADTheme {
                com.example.arsipbpkpad.presentation.archive.add.manual.StagingBoxListScreen(
                    viewModel = rapidInputViewModel,
                    userRole = UserRole.SUPER_ADMIN,
                    onNavigateToRapidInput = { sessionId -> navController.navigate(Screen.KeuanganRapidInput.createRoute(sessionId)) },
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToBottomNav = { item -> navController.handleKeuanganBottomNav(item) }
                )
            }
        }

        composable(Screen.KeuanganAnalytics.route) {
            ArsipBPKPADTheme {
                com.example.arsipbpkpad.presentation.analytics.AnalyticsScreen(
                    userRole = UserRole.SUPER_ADMIN,
                    onNavigateToBottomNav = { item -> navController.handleKeuanganBottomNav(item) }
                )
            }
        }

        // ── Modul 3: Non-Keuangan ───────────────────────────────────────────
        composable(Screen.NonKeuanganHome.route) {
            com.bpkpad.arsip.presentation.home.HomeScreen(
                onNavigateToScan = { navController.navigate(Screen.NonKeuanganScan.route) },
                onNavigateToManual = { navController.navigate(Screen.NonKeuanganStaging.route) },
                onNavigateToDetail = { id -> navController.navigate(Screen.NonKeuanganDetail.createRoute(id)) }
            )
        }

        composable(Screen.NonKeuanganDetail.route) { backStackEntry ->
            val documentId = backStackEntry.arguments?.getString("documentId")
            com.bpkpad.arsip.presentation.review.ReviewScreen(
                archiveId = documentId,
                onSaveSuccess = { navController.popBackStack() }
            )
        }

        composable(Screen.NonKeuanganStaging.route) {
            com.bpkpad.arsip.presentation.staging.StagingScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEdit = { id -> navController.navigate(Screen.NonKeuanganEditStaging.createRoute(id)) },
                onNavigateToInput = { navController.navigate(Screen.NonKeuanganInput.route) },
                onNavigateToScan = { navController.navigate(Screen.NonKeuanganScan.route) }
            )
        }

        composable(Screen.NonKeuanganEditStaging.route) { backStackEntry ->
            val stagingId = backStackEntry.arguments?.getString("stagingId")
            com.bpkpad.arsip.presentation.review.ReviewScreen(
                stagingId = stagingId,
                onSaveSuccess = { navController.popBackStack() }
            )
        }

        composable(Screen.NonKeuanganInput.route) {
            com.bpkpad.arsip.presentation.staging.InputScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToStaging = { navController.popBackStack() }
            )
        }

        composable(Screen.NonKeuanganScan.route) {
            com.bpkpad.arsip.presentation.scan.ScanScreen(
                onNavigateToReview = { navController.navigate(Screen.NonKeuanganInput.route) }
            )
        }

        // ── Modul 4: Peminjaman ─────────────────────────────────────────────
        composable(Screen.PeminjamanDashboard.route) {
            com.bpkpad.peminjaman.peminjaman.presentation.dashboard.DashboardArsiparisScreen(
                onNavigate = { route -> navController.navigate(route) },
                onLogout = { navController.navigateToRoute("logout", onLogout) }
            )
        }

        composable(Screen.PeminjamanDashboardKasubag.route) {
            com.bpkpad.peminjaman.peminjaman.presentation.dashboard.DashboardKasubagScreen(
                onNavigate = { route -> navController.navigate(route) },
                onLogout = { navController.navigateToRoute("logout", onLogout) }
            )
        }

        composable(Screen.PeminjamanForm.route) {
            com.bpkpad.peminjaman.peminjaman.presentation.form.FormTransaksiScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.PeminjamanApproval.route) {
            com.bpkpad.peminjaman.peminjaman.presentation.approval.AntreanApprovalScreen(
                onBack = { navController.popBackStack() },
                onNavigateToDetail = { id -> navController.navigate(Screen.PeminjamanDetail.createRoute(id)) }
            )
        }

        composable(Screen.PeminjamanScanQr.route) {
            com.bpkpad.peminjaman.peminjaman.presentation.pengembalian.ScanQrScreen(
                onBack = { navController.popBackStack() },
                onFound = { id -> navController.navigate(Screen.PeminjamanDetail.createRoute(id)) }
            )
        }

        composable(
            route = Screen.PeminjamanDetail.route,
            arguments = listOf(navArgument("transaksiId") { type = NavType.IntType }),
            deepLinks = listOf(navDeepLink { uriPattern = "bpkpad://transaksi/{transaksiId}" })
        ) { back ->
            val id = back.arguments?.getInt("transaksiId") ?: 0
            com.bpkpad.peminjaman.peminjaman.presentation.detail.DetailTransaksiScreen(
                transaksiId = id,
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.PeminjamanRiwayat.route,
            arguments = listOf(
                navArgument("status") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val statusParam = backStackEntry.arguments?.getString("status")
            com.bpkpad.peminjaman.peminjaman.presentation.riwayat.ListRiwayatScreen(
                statusFilter = statusParam,
                onBack = { navController.popBackStack() },
                onNavigateToDetail = { id -> navController.navigate(Screen.PeminjamanDetail.createRoute(id)) }
            )
        }

        composable(Screen.PeminjamanInstansi.route) {
            com.bpkpad.peminjaman.master.presentation.ListInstansiScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.PeminjamanDokumen.route) {
            com.bpkpad.peminjaman.master.presentation.ListDokumenScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.PeminjamanLaporan.route) {
            com.bpkpad.peminjaman.laporan.presentation.LaporanScreen(
                onBack = { navController.popBackStack() }
            )
        }

        // ── Profil ──────────────────────────────────────────────────────────
        composable(Screen.Profil.route) {
            ProfileScreen(
                onNavigate = { route -> navController.navigateToRoute(route, onLogout) },
                onLogout   = { navController.navigateToRoute("logout", onLogout) },
                onBack     = { navController.popBackStack() }
            )
        }
    }
}