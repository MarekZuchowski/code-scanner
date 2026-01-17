package com.example.codescanner.settings

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object PreferencesKeys {
    val FORMAT_CODE_128 = booleanPreferencesKey("FORMAT_CODE_128")
    val FORMAT_CODE_39 = booleanPreferencesKey("FORMAT_CODE_39")
    val FORMAT_CODE_93 = booleanPreferencesKey("FORMAT_CODE_93")
    val FORMAT_CODABAR = booleanPreferencesKey("FORMAT_CODABAR")
    val FORMAT_EAN_13 = booleanPreferencesKey("FORMAT_EAN_13")
    val FORMAT_EAN_8 = booleanPreferencesKey("FORMAT_EAN_8")
    val FORMAT_ITF = booleanPreferencesKey("FORMAT_ITF")
    val FORMAT_UPC_A = booleanPreferencesKey("FORMAT_UPC_A")
    val FORMAT_UPC_E = booleanPreferencesKey("FORMAT_UPC_E")
    val FORMAT_QR_CODE = booleanPreferencesKey("FORMAT_QR_CODE")
    val FORMAT_PDF417 = booleanPreferencesKey("FORMAT_PDF417")
    val FORMAT_AZTEC = booleanPreferencesKey("FORMAT_AZTEC")
    val FORMAT_DATA_MATRIX = booleanPreferencesKey("FORMAT_DATA_MATRIX")

    val SOUND_SIGNAL = booleanPreferencesKey("SOUND_SIGNAL")
    val APP_LANGUAGE = stringPreferencesKey("APP_LANGUAGE")
}

object FormatNames {
    const val FORMAT_CODE_128 = "Code 128"
    const val FORMAT_CODE_39 = "Code 39"
    const val FORMAT_CODE_93 = "Code 93"
    const val FORMAT_CODABAR = "Codabar"
    const val FORMAT_EAN_13 = "EAN-13"
    const val FORMAT_EAN_8 = "EAN-8"
    const val FORMAT_ITF = "ITF"
    const val FORMAT_UPC_A = "UPC-A"
    const val FORMAT_UPC_E = "UPC-E"
    const val FORMAT_QR_CODE = "QR Code"
    const val FORMAT_PDF417 = "PDF417"
    const val FORMAT_AZTEC = "Aztec"
    const val FORMAT_DATA_MATRIX = "Data Matrix"
}
