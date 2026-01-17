package com.example.codescanner.screens.scanhistory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.codescanner.data.dao.CodeScannerDao
import com.example.codescanner.data.model.Scan
import kotlinx.coroutines.flow.Flow

class ScanHistoryViewModel(
    private val dao : CodeScannerDao
) : ViewModel() {
    val pagedScans = getScans().cachedIn(viewModelScope)

    private fun getScans() : Flow<PagingData<Scan>> = Pager(
        PagingConfig(
            pageSize = 10,
            prefetchDistance = 5,
            initialLoadSize = 10,
        )
    ) {
        dao.getPagedScans()
    }
        .flow

}
