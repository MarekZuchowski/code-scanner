package com.example.codescanner.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.codescanner.data.converter.Converters
import com.example.codescanner.data.dao.CodeScannerDao
import com.example.codescanner.data.model.Code
import com.example.codescanner.data.model.Scan

@Database(
    entities = [
        Scan::class,
        Code::class
    ],
    version = 1
)
@TypeConverters(Converters::class)
abstract class CodeScannerDatabase : RoomDatabase() {
    abstract fun codeScannerDao() : CodeScannerDao

    companion object {
        @Volatile
        private var INSTANCE: CodeScannerDatabase? = null

        fun getInstance(context: Context): CodeScannerDatabase {
            synchronized(this) {
                return INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    CodeScannerDatabase::class.java,
                    "code_scanner.db"
                ).build().also {
                    INSTANCE = it
                }
            }
        }
    }

}