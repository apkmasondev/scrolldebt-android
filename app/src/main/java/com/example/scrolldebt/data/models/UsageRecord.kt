package com.example.scrolldebt.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usage_records")
data class UsageRecord(
    @PrimaryKey
    val date: String, // format YYYY-MM-DD
    val totalTimeMs: Long,
    val appBreakdownJson: String
)
