package com.example.codescanner.data.model

import androidx.room.Embedded
import androidx.room.Relation

data class ScanWithCodes(
    @Embedded
    val scan : Scan,
    @Relation(
        parentColumn = "id",
        entityColumn = "scanId"
    )
    val codes : List<Code>
)
