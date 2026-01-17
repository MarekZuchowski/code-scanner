package com.example.codescanner.navigation

import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable
    data object MainRoute : Route

    @Serializable
    data object SingleCodeScanRoute : Route

    @Serializable
    data object MultipleCodesScanRoute : Route

    @Serializable
    data object SelectedCodesScanRoute : Route

    @Serializable
    data object HighlightCodeRoute : Route

    @Serializable
    data class ScanDetailsRoute(
        val scanId : Long
    ) : Route

    @Serializable
    data object ScanHistoryRoute : Route

    @Serializable
    data object SettingRoute : Route

    @Serializable
    data object CodeFormatsSettingsRoute : Route

    @Serializable
    data object AppLanguageSettingsRoute : Route
}
