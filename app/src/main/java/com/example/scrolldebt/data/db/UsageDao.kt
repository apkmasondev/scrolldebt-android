package com.example.scrolldebt.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.scrolldebt.data.models.UsageRecord

/**
 * All methods are `suspend` so Room enforces off-main-thread access at compile time.
 * They were previously blocking and merely called from Dispatchers.IO by convention -
 * one careless call site away from a main-thread database access.
 */
@Dao
interface UsageDao {

    @Query("SELECT * FROM usage_records ORDER BY date DESC")
    suspend fun getAllRecords(): List<UsageRecord>

    @Query("SELECT * FROM usage_records WHERE date = :date LIMIT 1")
    suspend fun getRecordForDate(date: String): UsageRecord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateRecord(record: UsageRecord)

    @Query("SELECT SUM(totalTimeMs) FROM usage_records")
    suspend fun getTotalWastedTimeMs(): Long?
}
