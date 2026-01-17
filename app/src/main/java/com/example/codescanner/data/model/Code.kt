package com.example.codescanner.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "code",
    foreignKeys = [
        ForeignKey(entity = Scan::class, parentColumns = ["id"], childColumns = ["scanId"], onDelete = CASCADE, onUpdate = CASCADE),
    ]
)
data class Code(
    @PrimaryKey(autoGenerate = true)
    val id : Long = 0,
    var scanId : Long = 0,
    val timestamp : Date = Date(),
    val value : String,
)
