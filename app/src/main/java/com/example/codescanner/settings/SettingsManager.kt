package com.example.codescanner.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.common.Barcode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsManager(
    context : Context
) {
    private val dataStore = context.dataStore

    suspend fun getScannerOptions(): BarcodeScannerOptions {
        val data = dataStore.data.first()
        val settingsOptions = keyList.mapIndexed { index, key -> SettingsOption(format = barcodeFormats[index], isSelected = (data[key] ?: true)) }
        val selectedFormats = settingsOptions.filter { option -> option.isSelected }.map { option -> option.format }.toIntArray()
        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(selectedFormats[0], *selectedFormats.sliceArray(1 until selectedFormats.size))
            .build()

        return options
    }

    suspend fun getSoundSignalOption(): Boolean {
        val data = dataStore.data.first()
        return data[PreferencesKeys.SOUND_SIGNAL] ?: true
    }

    fun getBarcodeFormatsSettings(): Flow<List<Boolean>> {
        return combine(*keyList.map { key ->
            dataStore.data.map { preferences -> preferences[key] ?: true }
        }.toTypedArray()) { values ->
            values.toList()
        }
    }

    fun getSoundSignalSetting() : Flow<Boolean> {
        return dataStore.data
            .map { preferences ->
                preferences[PreferencesKeys.SOUND_SIGNAL] ?: true
            }
    }

    suspend fun saveBooleanOption(key : Preferences.Key<Boolean>, value : Boolean) {
        dataStore.edit { settings ->
            settings[key] = value
        }
    }

    companion object {
        val formatNames = listOf(
            FormatNames.FORMAT_CODE_128,
            FormatNames.FORMAT_CODE_39,
            FormatNames.FORMAT_CODE_93,
            FormatNames.FORMAT_CODABAR,
            FormatNames.FORMAT_EAN_13,
            FormatNames.FORMAT_EAN_8,
            FormatNames.FORMAT_ITF,
            FormatNames.FORMAT_UPC_A,
            FormatNames.FORMAT_UPC_E,
            FormatNames.FORMAT_QR_CODE,
            FormatNames.FORMAT_PDF417,
            FormatNames.FORMAT_AZTEC,
            FormatNames.FORMAT_DATA_MATRIX,
        )

        val keyList = listOf(
            PreferencesKeys.FORMAT_CODE_128,
            PreferencesKeys.FORMAT_CODE_39,
            PreferencesKeys.FORMAT_CODE_93,
            PreferencesKeys.FORMAT_CODABAR,
            PreferencesKeys.FORMAT_EAN_13,
            PreferencesKeys.FORMAT_EAN_8,
            PreferencesKeys.FORMAT_ITF,
            PreferencesKeys.FORMAT_UPC_A,
            PreferencesKeys.FORMAT_UPC_E,
            PreferencesKeys.FORMAT_QR_CODE,
            PreferencesKeys.FORMAT_PDF417,
            PreferencesKeys.FORMAT_AZTEC,
            PreferencesKeys.FORMAT_DATA_MATRIX,
        )

        val barcodeFormats = listOf(
            Barcode.FORMAT_CODE_128,
            Barcode.FORMAT_CODE_39,
            Barcode.FORMAT_CODE_93,
            Barcode.FORMAT_CODABAR,
            Barcode.FORMAT_EAN_13,
            Barcode.FORMAT_EAN_8,
            Barcode.FORMAT_ITF,
            Barcode.FORMAT_UPC_A,
            Barcode.FORMAT_UPC_E,
            Barcode.FORMAT_QR_CODE,
            Barcode.FORMAT_PDF417,
            Barcode.FORMAT_AZTEC,
            Barcode.FORMAT_DATA_MATRIX,
        )
    }

}
