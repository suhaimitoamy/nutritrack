package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val name: String = "Sobat",
    val email: String = "user@example.com",
    val calorieTarget: Int = 1800,
    val breakfastTime: String = "07:00",
    val lunchTime: String = "12:00",
    val dinnerTime: String = "19:00",
    val weightTime: String = "06:00"
)
