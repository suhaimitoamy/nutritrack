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
    
    // Simple grouped stats
    val foodsByDate = foods.groupBy { it.dateString }
    val avgCalories = if (foodsByDate.isNotEmpty()) {
        foods.sumOf { it.calories } / foodsByDate.size
    } else 0
    
    val totalBreakfast = foods.filter { it.category == "Makan Pagi" }.sumOf { it.calories }
    val totalLunch = foods.filter { it.category == "Makan Siang" }.sumOf { it.calories }
    val totalDinner = foods.filter { it.category == "Makan Malam" }.sumOf { it.calories }
    val totalSnack = foods.filter { it.category == "Camilan/Lainnya" }.sumOf { it.calories }
    
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
                Text("Rincian Kategori (Semua Waktu)", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                
                CategoryStatRow("Makan Pagi", totalBreakfast, Color(0xFFFFA726))
                CategoryStatRow("Makan Siang", totalLunch, Color(0xFF29B6F6))
                CategoryStatRow("Makan Malam", totalDinner, Color(0xFFEF5350))
                CategoryStatRow("Camilan/Lainnya", totalSnack, Color(0xFFAB47BC))
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        Text("Semua Makanan Dikonsumsi", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth().weight(1f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(foods) { food ->
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
