package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.AppRepository
import com.example.data.FoodEntry
import com.example.data.UserProfile
import com.example.data.WeightEntry
import com.example.data.WaterEntry
import com.example.data.ActivityEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainViewModel(private val repository: AppRepository) : ViewModel() {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val _currentDate = MutableStateFlow(dateFormat.format(Date()))
    val currentDate: StateFlow<String> = _currentDate.asStateFlow()
    
    val userProfile: StateFlow<UserProfile?> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
        
    val foodsForCurrentDate: StateFlow<List<FoodEntry>> = _currentDate
        .flatMapLatest { date -> repository.getFoodsForDate(date) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allWeights: StateFlow<List<WeightEntry>> = repository.allWeights
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        
    val allFoods: StateFlow<List<FoodEntry>> = repository.allFoods
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val waterForCurrentDate: StateFlow<List<WaterEntry>> = _currentDate
        .flatMapLatest { date -> repository.getWaterForDate(date) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activitiesForCurrentDate: StateFlow<List<ActivityEntry>> = _currentDate
        .flatMapLatest { date -> repository.getActivitiesForDate(date) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            if (repository.userProfile.first() == null) {
                repository.saveUserProfile(UserProfile())
            }
        }
    }

    fun setDate(date: String) {
        _currentDate.value = date
    }
    
    fun saveUserProfile(
        name: String, 
        email: String, 
        target: Int, 
        breakfast: String = "07:00", 
        lunch: String = "12:00", 
        dinner: String = "19:00", 
        weight: String = "06:00",
        heightCm: Int = 165,
        age: Int = 25,
        gender: String = "Pria",
        dietGoal: String = "Bertahan"
    ) {
        viewModelScope.launch {
            val currentProfile = repository.userProfile.first()
            repository.saveUserProfile(
                UserProfile(
                    id = currentProfile?.id ?: 1,
                    name = name, 
                    email = email, 
                    calorieTarget = target,
                    breakfastTime = breakfast,
                    lunchTime = lunch,
                    dinnerTime = dinner,
                    weightTime = weight,
                    heightCm = heightCm,
                    age = age,
                    gender = gender,
                    dietGoal = dietGoal,
                    isFasting = currentProfile?.isFasting ?: false,
                    fastingStartTime = currentProfile?.fastingStartTime ?: 0L
                )
            )
        }
    }
    
    fun toggleFasting() {
        viewModelScope.launch {
            val currentProfile = repository.userProfile.first() ?: return@launch
            val newIsFasting = !currentProfile.isFasting
            val newTime = if(newIsFasting) System.currentTimeMillis() else 0L
            repository.saveUserProfile(currentProfile.copy(isFasting = newIsFasting, fastingStartTime = newTime))
        }
    }

    fun insertFood(id: Int = 0, name: String, category: String, portions: Float, calories: Int, protein: Float, carbs: Float, fat: Float) {
        viewModelScope.launch {
            repository.insertFood(
                FoodEntry(
                    id = id,
                    name = name,
                    category = category,
                    portions = portions,
                    calories = calories,
                    protein = protein,
                    carbs = carbs,
                    fat = fat,
                    dateString = _currentDate.value
                )
            )
        }
    }

    fun previousDay() {
        val cal = java.util.Calendar.getInstance()
        cal.time = dateFormat.parse(_currentDate.value) ?: Date()
        cal.add(java.util.Calendar.DAY_OF_YEAR, -1)
        _currentDate.value = dateFormat.format(cal.time)
    }

    fun nextDay() {
        val cal = java.util.Calendar.getInstance()
        cal.time = dateFormat.parse(_currentDate.value) ?: Date()
        cal.add(java.util.Calendar.DAY_OF_YEAR, 1)
        _currentDate.value = dateFormat.format(cal.time)
    }

    fun deleteFood(id: Int) {
        viewModelScope.launch {
            repository.deleteFood(id)
        }
    }

    fun insertWeight(weightKg: Float) {
        viewModelScope.launch {
            repository.insertWeight(
                WeightEntry(
                    weightKg = weightKg,
                    dateString = dateFormat.format(Date())
                )
            )
        }
    }

    fun deleteWeight(id: Int) {
        viewModelScope.launch {
            repository.deleteWeight(id)
        }
    }

    fun insertWater(amountMl: Int) {
        viewModelScope.launch {
            repository.insertWater(
                WaterEntry(
                    amountMl = amountMl,
                    dateString = _currentDate.value
                )
            )
        }
    }

    fun deleteWater(id: Int) {
        viewModelScope.launch {
            repository.deleteWater(id)
        }
    }

    fun insertActivity(activityName: String, durationMinutes: Int, caloriesBurned: Int) {
        viewModelScope.launch {
            repository.insertActivity(
                ActivityEntry(
                    activityName = activityName,
                    durationMinutes = durationMinutes,
                    caloriesBurned = caloriesBurned,
                    dateString = _currentDate.value
                )
            )
        }
    }

    fun deleteActivity(id: Int) {
        viewModelScope.launch {
            repository.deleteActivity(id)
        }
    }
}

class MainViewModelFactory(private val repository: AppRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
