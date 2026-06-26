package com.bpkpad.siarsip.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
import com.bpkpad.siarsip.ui.screens.pemusnahan.dummyBeritaAcaraList
import com.bpkpad.siarsip.ui.screens.pemusnahan.dummyBerkasDetail

/**
 * Helper navigasi generik dipakai semua drawer (PemusnahanDrawerContent.onNavigate)
 * supaya satu fungsi bisa nge-handle semua DrawerRoutes string.
 */
private fun NavHostController.navigateToRoute(route: String) {
    when (route) {
        "logout" -> {
            navigate(Screen.Login.route) {
                popUpTo(0) { inclusive = true }
            }
        }
        else -> navigate(route) {
            launchSingleTop = true
        }
    }
}

@Composable
fun SiArsipNavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Login.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginClick = { _, _ ->
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onForgotPasswordClick = { /* TODO */ }
            )
        }

        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onModuleClick = { moduleKey ->
                    when (moduleKey) {
                        "pemusnahan" -> navController.navigate(Screen.DaftarUsulMusnah.route)
                        "keuangan", "non_keuangan", "peminjaman" ->
                            navController.navigate(Screen.DaftarArsip.route)
                    }
                }
            )
        }

        composable(Screen.DaftarArsip.route) {
            DaftarArsipScreen(
                onNavigate = { route -> navController.navigateToRoute(route) }
            )
        }

        composable(Screen.DaftarUsulMusnah.route) {
            DaftarUsulMusnahScreen(
                onBuatBerkas = { navController.navigate(Screen.BuatBerkasUsulMusnah.route) },
                onNavigate = { route -> navController.navigateToRoute(route) }
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
                onNavigate = { route -> navController.navigateToRoute(route) },
                onCatatBalasan = { /* TODO: buka form update status */ }
            )
        }

        composable(Screen.BeritaAcara.route) {
            BeritaAcaraScreen(
                onNavigate = { route -> navController.navigateToRoute(route) },
                onCardClick = { ba ->
                    navController.navigate(Screen.DetailBeritaAcara.createRoute(ba.id))
                }
            )
        }

        composable(Screen.DetailBeritaAcara.route) { backStackEntry ->
            val baId = backStackEntry.arguments
                ?.getString("baId")?.toIntOrNull()
            val ba = dummyBeritaAcaraList.firstOrNull { it.id == baId }
                ?: dummyBeritaAcaraList.first()
            DetailBeritaAcaraScreen(
                ba = ba,
                onBack = { navController.popBackStack() },
                onUnduhPdf = { /* TODO */ },
                onLihatArsip = { navController.navigate(Screen.DaftarArsip.route) },
                onCetak = { /* TODO */ }
            )
        }

        composable(Screen.DetailBerkasUsulMusnah.route) {
            DetailBerkasUsulMusnahScreen(
                berkas = dummyBerkasDetail,
                onBack = { navController.popBackStack() },
                onExportPdf = { /* TODO */ },
                onLihatTracking = { navController.navigate(Screen.StatusTracking.route) }
            )
        }

        composable(Screen.LogRiwayat.route) {
            LogRiwayatScreen(
                onNavigate = { route -> navController.navigateToRoute(route) }
            )
        }

        // Profil & Pengaturan — placeholder sementara, sambungkan ke screen asli nanti
        composable(Screen.Profil.route) {
            PlaceholderScreen(title = "Profil Akun", onBack = { navController.popBackStack() })
        }
        composable(Screen.Pengaturan.route) {
            PlaceholderScreen(title = "Pengaturan", onBack = { navController.popBackStack() })
        }
    }
}