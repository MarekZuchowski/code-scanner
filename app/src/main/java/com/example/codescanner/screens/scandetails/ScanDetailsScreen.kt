package com.example.codescanner.screens.scandetails

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.codescanner.R
import com.example.codescanner.common.composable.CustomTextField
import com.example.codescanner.data.CodeScannerDatabase
import com.example.codescanner.data.model.Scan
import com.example.codescanner.data.model.ScanWithCodes
import com.example.codescanner.navigation.Route
import java.text.DateFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanDetailsScreen(
    navController: NavController,
    scanDetails: Route.ScanDetailsRoute
) {
    val context = LocalContext.current
    val clipboardManager: ClipboardManager = LocalClipboardManager.current
    val dao = CodeScannerDatabase.getInstance(context).codeScannerDao()
    val viewModel: ScanDetailsViewModel = viewModel {
        ScanDetailsViewModel(dao, scanDetails.scanId)
    }
    val scanWithCodes by viewModel.scan.collectAsStateWithLifecycle(ScanWithCodes(Scan(), emptyList()))

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.scan_details))
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 15.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
            ) {
                item {
                    Text(
                        text = stringResource(R.string.scan_type),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(
                        Modifier.size(15.dp)
                    )
                    Text(
                        text = scanWithCodes.scan.scanType.displayName
                    )
                    Spacer(
                        Modifier.size(30.dp)
                    )
                    Text(
                        text = stringResource(R.string.date),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(
                        Modifier.size(15.dp)
                    )
                    Text(
                        text = DateFormat.getDateTimeInstance().format(scanWithCodes.scan.timestamp)
                    )
                    Spacer(
                        Modifier.size(30.dp)
                    )
                    CustomTextField(
                        value = viewModel.comment,
                        label = {
                            Text(text = stringResource(R.string.comment))
                        },
                        onValueChange = {
                            viewModel.setCommentValue(it)
                        },
                        maxLines = 3,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                    Spacer(
                        Modifier.size(30.dp)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.codes),
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        IconButton(
                            onClick = {
                                val codes = scanWithCodes.codes.joinToString(separator = ", ") { it.value }
                                clipboardManager.setText(AnnotatedString(codes))
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy"
                            )
                        }
                        IconButton(
                            onClick = {
                                val codes = scanWithCodes.codes.joinToString(separator = ", ") { it.value }
                                val sendIntent: Intent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, codes)
                                    type = "text/plain"
                                }
                                val shareIntent = Intent.createChooser(sendIntent, null)
                                context.startActivity(shareIntent)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share"
                            )
                        }
                    }
                    Spacer(
                        Modifier.size(10.dp)
                    )
                }
                if (scanWithCodes.codes.isNotEmpty()) {
                    items(scanWithCodes.codes.size) { index ->
                        Text(
                            text = scanWithCodes.codes[index].value
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        viewModel.delete(scanWithCodes.scan) {
                            navController.popBackStack()
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Text(text = stringResource(R.string.delete))
                }
                Spacer(modifier = Modifier.width(15.dp))
                Button(
                    onClick = {
                        viewModel.update(scanWithCodes.scan)
                    },
                    enabled = scanWithCodes.scan.comment != viewModel.comment,
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Text(text = stringResource(R.string.save))
                }
            }
        }
    }
}
