package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.MainViewModel

@Composable
fun ReportScreen(viewModel: MainViewModel) {
    val foods by viewModel.allFoods.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val targetCalories = userProfile?.calorieTarget ?: 1800
    
    val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
    val today = java.util.Date()
    val calendar = java.util.Calendar.getInstance()
    calendar.time = today
    calendar.add(java.util.Calendar.DAY_OF_YEAR, -6)
    val sevenDaysAgo = calendar.time

    val foodsLast7Days = foods.filter { 
        val foodDate = dateFormat.parse(it.dateString) ?: today
        !foodDate.before(sevenDaysAgo)
    }

    // Simple grouped stats
    val foodsByDate = foodsLast7Days.groupBy { it.dateString }
    val avgCalories = if (foodsByDate.isNotEmpty()) {
        foodsLast7Days.sumOf { it.calories } / foodsByDate.size
    } else 0
    
    val totalBreakfast = foodsLast7Days.filter { it.category == "Makan Pagi" }.sumOf { it.calories }
    val totalLunch = foodsLast7Days.filter { it.category == "Makan Siang" }.sumOf { it.calories }
    val totalDinner = foodsLast7Days.filter { it.category == "Makan Malam" }.sumOf { it.calories }
    val totalSnack = foodsLast7Days.filter { it.category == "Camilan/Lainnya" }.sumOf { it.calories }
    
    val dailyTotals = foodsLast7Days.groupBy { it.dateString }.mapValues { entry ->
        entry.value.sumOf { it.calories }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Laporan Kalori", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Text("Rata-rata Harian", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("$avgCalories kkal", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Target", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("$targetCalories kkal", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                Text("Rincian Kategori (7 Hari Terakhir)", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                
                CategoryStatRow("Makan Pagi", totalBreakfast, Color(0xFFFFA726))
                CategoryStatRow("Makan Siang", totalLunch, Color(0xFF29B6F6))
                CategoryStatRow("Makan Malam", totalDinner, Color(0xFFEF5350))
                CategoryStatRow("Camilan/Lainnya", totalSnack, Color(0xFFAB47BC))
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        Text("Grafik 7 Hari Terakhir", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth().height(150.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                val maxCal = dailyTotals.values.maxOrNull()?.coerceAtLeast(1) ?: 1
                val last7DaysStrings = (0..6).map { i ->
                    val c = java.util.Calendar.getInstance()
                    c.time = today
                    c.add(java.util.Calendar.DAY_OF_YEAR, -6 + i)
                    dateFormat.format(c.time)
                }
                
                last7DaysStrings.forEach { dateStr ->
                    val cals = dailyTotals[dateStr] ?: 0
                    val heightRatio = cals.toFloat() / maxCal.toFloat()
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.6f)
                                .fillMaxHeight(heightRatio.coerceAtLeast(0.05f))
                                .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(dateStr.takeLast(2), style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        Text("Makanan 7 Hari Terakhir", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth().weight(1f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(foodsLast7Days) { food ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(food.name, fontWeight = FontWeight.Bold)
                            Text("${food.dateString} · ${food.category}", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
                        }
                        Text("${food.calories} kkal", fontWeight = FontWeight.SemiBold)
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                }
            }
        }
    }
}

@Composable
fun CategoryStatRow(category: String, calories: Int, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(12.dp).background(color, shape = RoundedCornerShape(2.dp)))
            Spacer(modifier = Modifier.width(8.dp))
            Text(category)
        }
        Text("$calories kkal", fontWeight = FontWeight.SemiBold)
    }
}
