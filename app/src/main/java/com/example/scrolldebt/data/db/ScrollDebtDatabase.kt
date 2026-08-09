package com.example.scrolldebt.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.scrolldebt.data.models.UsageRecord

@Database(entities = [UsageRecord::class], version = 1, exportSchema = true)
abstract class ScrollDebtDatabase : RoomDatabase() {
    abstract fun usageDao(): UsageDao

    companion object {
        @Volatile
        private var INSTANCE: ScrollDebtDatabase? = null

        fun getDatabase(context: Context): ScrollDebtDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ScrollDebtDatabase::class.java,
                    "scroll_debt_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
