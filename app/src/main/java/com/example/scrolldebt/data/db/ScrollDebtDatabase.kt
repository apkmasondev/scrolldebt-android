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
                // Re-check inside the lock: two threads can both pass the outer null test.
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    ScrollDebtDatabase::class.java,
                    "scroll_debt_database"
                )
                    // Deliberately NO fallbackToDestructiveMigration().
                    //
                    // This database is the only copy of the user's history - the app has no
                    // cloud sync by design. With the destructive fallback, the first schema
                    // change would have wiped every record on upgrade, silently, with no way
                    // back. Omitting it means a missing migration fails loudly in development
                    // instead of quietly deleting a year of someone's data in production.
                    //
                    // When the schema changes: bump the @Database version and add a Migration
                    // here. app/schemas/ holds the exported schema to diff against.
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
