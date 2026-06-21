package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.AppRepository
import com.example.data.FoodEntry
import com.example.data.UserProfile
import com.example.data.WeightEntry
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
        weight: String = "06:00"
    ) {
        viewModelScope.launch {
            repository.saveUserProfile(
                UserProfile(
                    name = name, 
                    email = email, 
                    calorieTarget = target,
                    breakfastTime = breakfast,
                    lunchTime = lunch,
                    dinnerTime = dinner,
                    weightTime = weight
                )
            )
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
