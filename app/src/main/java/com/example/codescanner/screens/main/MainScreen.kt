package com.example.codescanner.screens.main

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.codescanner.R
import com.example.codescanner.navigation.Route

@Composable
fun MainScreen(
    navController: NavController
) {
    val buttons = listOf(
        Triple(stringResource(R.string.scan_single_code), Icons.Default.QrCode) { navController.navigate(Route.SingleCodeScanRoute) },
        Triple(stringResource(R.string.scan_multiple_codes), Icons.Default.QrCode2) { navController.navigate(Route.MultipleCodesScanRoute) },
        Triple(stringResource(R.string.scan_selected_codes), Icons.Default.TouchApp) { navController.navigate(Route.SelectedCodesScanRoute) },
        Triple(stringResource(R.string.highlight_entered_code), Icons.Default.DocumentScanner) { navController.navigate(Route.HighlightCodeRoute) },
        Triple(stringResource(R.string.scan_history), Icons.Default.Storage) { navController.navigate(Route.ScanHistoryRoute) },
        Triple(stringResource(R.string.settings), Icons.Default.Settings) { navController.navigate(Route.SettingRoute) },
    )

    val configuration = LocalConfiguration.current
    when(configuration.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> {
            MainScreenLandscapeLayout(buttons)
        }
        else -> {
            MainScreenPortraitLayout(buttons)
        }
    }
}

@Composable
fun MainScreenPortraitLayout(
    buttons : List<Triple<String, ImageVector, () -> Unit>>
) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
    ) { innerPadding ->
        Column {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 5.dp)
                    .clip(shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp))
                    .background(color = MaterialTheme.colorScheme.surfaceVariant)
                    .padding(paddingValues = PaddingValues(top = innerPadding.calculateTopPadding()))
                    .defaultMinSize(minHeight = 100.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(R.string.app_name),
                        fontSize = 40.sp,
                    )

                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues = PaddingValues(bottom = innerPadding.calculateBottomPadding())),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

                buttons.forEach { button ->
                    MainScreenButton(modifier = Modifier.weight(1f),button = button)
                }
            }
        }
    }
}

@Composable
fun MainScreenLandscapeLayout(
    buttons: List<Triple<String, ImageVector, () -> Unit>>
) {
    val layoutDirection = LocalLayoutDirection.current
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(
                    paddingValues = PaddingValues(
                        start = innerPadding.calculateStartPadding(layoutDirection),
                        end = innerPadding.calculateEndPadding(layoutDirection),
                        bottom = innerPadding.calculateBottomPadding()
                    )
                )
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 5.dp)
                    .clip(shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp))
                    .background(color = MaterialTheme.colorScheme.surfaceVariant)
                    .padding(top = innerPadding.calculateTopPadding(), bottom = 10.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(R.string.app_name),
                    fontSize = 40.sp,
                )
            }
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()

                ) {
                    buttons.forEachIndexed { index, button ->
                        if(index % 2 == 0) {
                            MainScreenButton(modifier = Modifier.weight(1f),button = button)
                        }
                    }
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()

                ) {
                    buttons.forEachIndexed { index, button ->
                        if(index % 2 == 1) {
                            MainScreenButton(modifier = Modifier.weight(1f),button = button)
                        }
                    }
                }

            }
        }
    }
}

@Composable
fun MainScreenButton(
    modifier: Modifier = Modifier,
    button : Triple<String, ImageVector, () -> Unit>
) {
    Row(
        modifier = modifier
            .padding(horizontal = 5.dp, vertical = 5.dp)
            .clip(shape = RoundedCornerShape(20.dp))
            .background(color = MaterialTheme.colorScheme.surfaceVariant)
            .fillMaxWidth()
            .clickable(onClick = button.third)
            .padding(horizontal = 20.dp, vertical = 15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = button.second,
            contentDescription = button.first
        )
        Spacer(modifier = Modifier.width(15.dp))
        Text(
            text = button.first
        )
    }
}
