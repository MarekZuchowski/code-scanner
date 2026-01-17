package com.example.codescanner.screens.multiplecodesscan

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.codescanner.R
import com.example.codescanner.data.model.Code

@Composable
fun MultipleCodesScanBottomSheetContent(
    codes : List<Code>,
    onRemove : (Int) -> Unit,
    onSave : () -> Unit,
    modifier: Modifier = Modifier
) {

    if(codes.isEmpty()) {
        Box(
            modifier = modifier
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(stringResource(R.string.no_codes_yet))
        }
        Spacer(Modifier.height(50.dp))

    }
    else {
        LazyColumn(
            modifier = modifier
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(
                items = codes,

                ) { index, code ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = code.value,
                        modifier = Modifier
                            .weight(1f)
                    )
                    IconButton(
                        onClick = {
                            onRemove(index)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.RemoveCircleOutline,
                            contentDescription = "Remove"
                        )

                    }
                }

            }
        }
        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            Button(
                onClick = onSave,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier,
            ) {
                Text(text = stringResource(R.string.save))
            }
        }
        Spacer(Modifier.height(50.dp))

    }
}
