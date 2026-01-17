package com.example.codescanner.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "scan"
)
data class Scan (
    @PrimaryKey(autoGenerate = true)
    val id : Long = 0,
    val timestamp : Date = Date(),
    val scanType : ScanType = ScanType.SINGLE_CODE,
    val shortenValue : String = "",
    val comment : String = "",
)
