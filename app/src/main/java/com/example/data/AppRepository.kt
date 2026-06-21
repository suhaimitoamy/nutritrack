package com.example.data

import kotlinx.coroutines.flow.Flow

class AppRepository(private val dao: CalorieDao) {
    val userProfile: Flow<UserProfile?> = dao.getUserProfile()
    val allWeights: Flow<List<WeightEntry>> = dao.getAllWeights()
    val allFoods: Flow<List<FoodEntry>> = dao.getAllFoods()
    
    fun getFoodsForDate(date: String): Flow<List<FoodEntry>> = dao.getFoodsForDate(date)
    
    suspend fun saveUserProfile(profile: UserProfile) = dao.saveUserProfile(profile)
    suspend fun insertFood(food: FoodEntry) = dao.insertFood(food)
    suspend fun deleteFood(id: Int) = dao.deleteFood(id)
    suspend fun insertWeight(weight: WeightEntry) = dao.insertWeight(weight)
    suspend fun deleteWeight(id: Int) = dao.deleteWeight(id)

    fun getWaterForDate(date: String): Flow<List<WaterEntry>> = dao.getWaterForDate(date)
    suspend fun insertWater(water: WaterEntry) = dao.insertWater(water)
    suspend fun deleteWater(id: Int) = dao.deleteWater(id)

    fun getActivitiesForDate(date: String): Flow<List<ActivityEntry>> = dao.getActivitiesForDate(date)
    suspend fun insertActivity(activity: ActivityEntry) = dao.insertActivity(activity)
    suspend fun deleteActivity(id: Int) = dao.deleteActivity(id)
}
