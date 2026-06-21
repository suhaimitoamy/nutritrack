package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CalorieDao {
    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getUserProfile(): Flow<UserProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserProfile(profile: UserProfile)

    @Query("SELECT * FROM food_entries WHERE dateString = :date ORDER BY timestamp DESC")
    fun getFoodsForDate(date: String): Flow<List<FoodEntry>>
    
    @Query("SELECT * FROM food_entries ORDER BY timestamp DESC")
    fun getAllFoods(): Flow<List<FoodEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFood(food: FoodEntry)

    @Query("DELETE FROM food_entries WHERE id = :id")
    suspend fun deleteFood(id: Int)

    @Query("SELECT * FROM weight_entries ORDER BY timestamp DESC")
    fun getAllWeights(): Flow<List<WeightEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeight(weight: WeightEntry)
    
    @Query("DELETE FROM weight_entries WHERE id = :id")
    suspend fun deleteWeight(id: Int)
}
