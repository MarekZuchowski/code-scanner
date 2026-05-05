package com.example.codescanner.screens.scandetails

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codescanner.data.dao.CodeScannerDao
import com.example.codescanner.data.model.Scan
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ScanDetailsViewModel(
    private val dao : CodeScannerDao,
    private val scanId : Long,
) : ViewModel() {

    val scan = dao.getScanWithCodes(scanId)
    var comment by mutableStateOf("")
        private set

    init {
        viewModelScope.launch {
            val commentValue = scan.first().scan.comment
            withContext(Dispatchers.Main) {
                comment = commentValue
            }
        }
    }

    fun setCommentValue(commentText : String) {
        comment = commentText
    }

    fun update(scan : Scan) {
        val trimmedComment = comment.trim()
        comment = trimmedComment
        viewModelScope.launch {
            dao.updateScan(scan.copy(comment = trimmedComment))
        }
    }

    fun delete(scan : Scan, goBack : () -> Unit) {
        viewModelScope.launch {
            dao.deleteScan(scan)
            withContext(Dispatchers.Main) {
                goBack()
            }
        }
    }
}
