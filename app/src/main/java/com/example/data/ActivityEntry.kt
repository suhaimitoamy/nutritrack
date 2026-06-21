package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "activity_entries")
data class ActivityEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val activityName: String,
    val durationMinutes: Int,
    val caloriesBurned: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val dateString: String // YYYY-MM-DD
)
