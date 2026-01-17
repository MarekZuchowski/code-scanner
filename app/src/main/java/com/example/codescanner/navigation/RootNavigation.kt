package com.example.codescanner.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.codescanner.screens.highlightcodescan.HighlightCodeScreen
import com.example.codescanner.screens.main.MainScreen
import com.example.codescanner.screens.multiplecodesscan.MultipleCodesScanScreen
import com.example.codescanner.screens.scandetails.ScanDetailsScreen
import com.example.codescanner.screens.scanhistory.ScanHistoryScreen
import com.example.codescanner.screens.selectedcodesscan.SelectedCodesScanScreen
import com.example.codescanner.screens.settings.AppLanguageSettingsScreen
import com.example.codescanner.screens.settings.CodeFormatsSettingsScreen
import com.example.codescanner.screens.settings.SettingsScreen
import com.example.codescanner.screens.singlecodescan.SingleCodeScanScreen

@Composable
fun RootNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Route.MainRoute
    ) {
        composable<Route.MainRoute> {
            MainScreen(navController)
        }
        composable<Route.SingleCodeScanRoute> {
            SingleCodeScanScreen(navController)
        }
        composable<Route.MultipleCodesScanRoute> {
            MultipleCodesScanScreen(navController)
        }
        composable<Route.SelectedCodesScanRoute> {
            SelectedCodesScanScreen(navController)
        }
        composable<Route.HighlightCodeRoute> {
            HighlightCodeScreen()
        }
        composable<Route.ScanDetailsRoute> { backStackEntry ->
            val scanDetails: Route.ScanDetailsRoute = backStackEntry.toRoute<Route.ScanDetailsRoute>()
            ScanDetailsScreen(
                navController = navController,
                scanDetails = scanDetails
                )
        }
        composable<Route.ScanHistoryRoute> {
            ScanHistoryScreen(navController)
        }
        composable<Route.SettingRoute> {
            SettingsScreen(navController)
        }
        composable<Route.CodeFormatsSettingsRoute> {
            CodeFormatsSettingsScreen(navController)
        }
        composable<Route.AppLanguageSettingsRoute> {
            AppLanguageSettingsScreen(navController)
        }
    }
}
