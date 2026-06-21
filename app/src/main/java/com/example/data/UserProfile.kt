package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val name: String = "Sobat",
    val email: String = "user@example.com",
    val calorieTarget: Int = 1800
)
