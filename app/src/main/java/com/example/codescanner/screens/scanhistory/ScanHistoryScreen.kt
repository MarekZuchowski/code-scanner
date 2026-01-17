package com.example.codescanner.screens.scanhistory

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.DeveloperMode
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.example.codescanner.R
import com.example.codescanner.data.CodeScannerDatabase
import com.example.codescanner.data.model.Scan
import com.example.codescanner.navigation.Route
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanHistoryScreen(
    navController : NavController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val dao = CodeScannerDatabase.getInstance(context).codeScannerDao()
    val viewModel : ScanHistoryViewModel = viewModel {
        ScanHistoryViewModel(dao)
    }
    val scansLazyPagingItems = viewModel.pagedScans.collectAsLazyPagingItems()

    Scaffold (
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.scan_history))
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.navigateUp()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 5.dp)

        ) {
            if(scansLazyPagingItems.itemCount > 0) {
                LazyColumn(
                    modifier = Modifier,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    items(
                        count = scansLazyPagingItems.itemCount,
                        key = scansLazyPagingItems.itemKey { scan ->
                            scan.id
                        },
                        contentType = scansLazyPagingItems.itemContentType{ "Scans" }
                    ) { index ->
                        val scan = scansLazyPagingItems[index]
                        if(scan != null) {
                            ScanHistoryListItem(
                                scan = scan,
                                navigateToScanDetails = {
                                    navController.navigate(Route.ScanDetailsRoute(scan.id))
                                }
                            )
                        }
                    }
                }
            }
            else {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center,

                ) {
                    Text(
                        text = stringResource(R.string.scan_history_is_empty)
                    )
                }
            }
        }
    }
}

@Composable
fun ScanHistoryListItem(
    scan : Scan,
    navigateToScanDetails : () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormat = SimpleDateFormat(stringResource(R.string.scan_history_date_format), Locale.getDefault())

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { navigateToScanDetails() },
        shape = RoundedCornerShape(corner = CornerSize(16.dp))
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)

        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                Icon(
                    imageVector = Icons.Default.DeveloperMode,
                    contentDescription = stringResource(R.string.scan_type)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = scan.scanType.displayName
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = dateFormat.format(scan.timestamp),
                    fontWeight = FontWeight.Thin
                )
            }
            Spacer(Modifier.height(10.dp))
            Row() {
                Icon(
                    imageVector = Icons.Default.DocumentScanner,
                    contentDescription = stringResource(R.string.scanned_codes)

                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = scan.shortenValue
                )
            }
            if(scan.comment.isNotEmpty()) {
                Spacer(Modifier.height(10.dp))
                Row() {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Message,
                        contentDescription = stringResource(R.string.comment)

                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = scan.comment
                    )
                }
            }
        }
    }
}
