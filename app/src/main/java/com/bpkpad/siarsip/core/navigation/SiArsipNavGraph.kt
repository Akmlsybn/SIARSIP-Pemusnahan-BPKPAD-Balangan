package com.bpkpad.siarsip.core.navigation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bpkpad.siarsip.feature.auth.presentation.LoginViewModel
import com.bpkpad.siarsip.feature.auth.presentation.LoginUiState
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
import com.bpkpad.siarsip.ui.screens.pemusnahan.dummyBerkasDetail
import com.bpkpad.siarsip.ui.screens.profile.ProfileScreen

/**
 * Helper navigasi generik dipakai semua drawer (PemusnahanDrawerContent.onNavigate)
 * supaya satu fungsi bisa nge-handle semua DrawerRoutes string.
 */
private fun NavHostController.navigateToRoute(route: String, onLogout: () -> Unit) {
    if (currentDestination?.route == route) return

    when (route) {
        "logout" -> {
            onLogout()
            navigate(Screen.Login.route) {
                popUpTo(0) { inclusive = true }
            }
        }
        else -> navigate(route) {
            popUpTo(Screen.Dashboard.route) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
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
        composable(Screen.Login.route) {
            val viewModel: LoginViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsState()
            val context = LocalContext.current

            LaunchedEffect(uiState) {
                when (uiState) {
                    is LoginUiState.Success -> {
                        navController.navigate(Screen.Dashboard.route) {
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

        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onNavigate = { route -> navController.navigateToRoute(route, onLogout) },
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
                onNavigate = { route -> navController.navigateToRoute(route, onLogout) }
            )
        }

        composable(Screen.DaftarUsulMusnah.route) {
            DaftarUsulMusnahScreen(
                onBuatBerkas = { navController.navigate(Screen.BuatBerkasUsulMusnah.route) },
                onNavigate = { route -> navController.navigateToRoute(route, onLogout) }
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
                onNavigate = { route -> navController.navigateToRoute(route, onLogout) }
            )
        }

        // Profil
        composable(Screen.Profil.route) {
            ProfileScreen(
                onNavigate = { route -> navController.navigateToRoute(route, onLogout) },
                onLogout   = { navController.navigateToRoute("logout", onLogout) },
                onBack     = { navController.popBackStack() }
            )
        }
    }
}