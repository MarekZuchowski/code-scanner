package com.example.codescanner.data.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.codescanner.data.model.Code
import com.example.codescanner.data.model.Scan
import com.example.codescanner.data.model.ScanWithCodes
import kotlinx.coroutines.flow.Flow

@Dao
interface CodeScannerDao {

    @Insert
    suspend fun insertScan(scan: Scan) : Long

    @Update
    suspend fun updateScan(scan: Scan) : Int

    @Delete
    suspend fun deleteScan(scan: Scan) : Int

    @Query("SELECT * FROM scan WHERE id = :scanId")
    suspend fun getScan(scanId : Long) : Scan

    @Query("SELECT * FROM scan ORDER BY timestamp DESC")
    fun getScans() : Flow<List<Scan>>

    @Query("SELECT * FROM scan ORDER BY timestamp DESC")
    fun getPagedScans() : PagingSource<Int, Scan>

    @Insert
    suspend fun insertCode(code: Code) : Long

    @Insert
    suspend fun insertCodes(codes : List<Code>) : List<Long>

    @Update
    suspend fun updateCode(code: Code) : Int

    @Delete
    suspend fun deleteCode(code: Code) : Int

    @Query("SELECT * FROM code WHERE id = :codeId")
    suspend fun getCode(codeId : Long) : Code

    @Query("SELECT * FROM code")
    suspend fun getCodes() : List<Code>

    @Query("SELECT * FROM scan WHERE id = :scanId")
    fun getScanWithCodes(scanId : Long) : Flow<ScanWithCodes>

}