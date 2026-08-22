package com.bpkpad.arsip.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.bpkpad.arsip.core.domain.repository.AuthRepository
import com.bpkpad.arsip.presentation.home.HomeScreen
import com.bpkpad.arsip.presentation.review.ReviewScreen
import com.bpkpad.arsip.presentation.scan.ScanScreen
import com.bpkpad.arsip.presentation.staging.InputScreen
import com.bpkpad.arsip.presentation.staging.StagingScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    authRepository: AuthRepository
) {
    NavHost(
        navController = navController,
        startDestination = Routes.DASHBOARD
    ) {
        composable(Routes.DASHBOARD) {
            HomeScreen(
                onNavigateToScan = { navController.navigate(Routes.CAMERA) },
                onNavigateToManual = { navController.navigate(Routes.STAGING) },
                onNavigateToDetail = { id ->
                    navController.navigate(Routes.DETAIL.replace("{documentId}", id))
                }
            )
        }

        composable(Routes.DETAIL) { backStackEntry ->
            val documentId = backStackEntry.arguments?.getString("documentId")
            ReviewScreen(
                archiveId = documentId,
                onSaveSuccess = { navController.popBackStack() }
            )
        }

        composable(Routes.STAGING) {
            StagingScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEdit = { id ->
                    navController.navigate(Routes.EDIT_STAGING.replace("{stagingId}", id))
                },
                onNavigateToInput = { navController.navigate(Routes.INPUT) },
                onNavigateToScan = { navController.navigate(Routes.CAMERA) }
            )
        }

        composable(Routes.EDIT_STAGING) { backStackEntry ->
            val stagingId = backStackEntry.arguments?.getString("stagingId")
            ReviewScreen(
                stagingId = stagingId,
                onSaveSuccess = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.INPUT) {
            InputScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToStaging = { 
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.CAMERA) {
            ScanScreen(
                onNavigateToReview = { document ->
                    navController.navigate(Routes.INPUT)
                }
            )
        }
    }
}