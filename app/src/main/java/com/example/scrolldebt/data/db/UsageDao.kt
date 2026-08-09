package com.example.scrolldebt.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.scrolldebt.data.models.UsageRecord

@Dao
interface UsageDao {
    @Query("SELECT * FROM usage_records ORDER BY date DESC")
    fun getAllRecordsSync(): List<UsageRecord>

    @Query("SELECT * FROM usage_records WHERE date = :date LIMIT 1")
    fun getRecordForDate(date: String): UsageRecord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdateRecord(record: UsageRecord)

    @Query("SELECT SUM(totalTimeMs) FROM usage_records")
    fun getTotalWastedTimeMsSync(): Long?
}
