package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.FoodEntry
import com.example.data.FoodDictionary
import com.example.data.FoodItem
import com.example.ui.MainViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DiaryScreen(viewModel: MainViewModel) {
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val foods by viewModel.foodsForCurrentDate.collectAsStateWithLifecycle()
    val date by viewModel.currentDate.collectAsStateWithLifecycle()
    
    val targetCalories = userProfile?.calorieTarget ?: 1800
    val consumedCalories = foods.sumOf { it.calories }
    val remainingCalories = targetCalories - consumedCalories

    var showAddDialog by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf("") }
    var foodToEdit by remember { mutableStateOf<FoodEntry?>(null) }
    
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    
    Column(modifier = Modifier.fillMaxSize()) {
        // Date Selector - Interaktif
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.previousDay() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Hari Sebelumnya")
            }
            Text(
                text = "$date",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = { viewModel.nextDay() }) {
                Icon(Icons.Default.ArrowForward, contentDescription = "Hari Berikutnya")
            }
        }
        
        // Summary Card
        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Ringkasan Kalori", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Text("Target", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("$targetCalories", fontWeight = FontWeight.Bold)
                    }
                    Text("-", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Column {
                        Text("Konsumsi", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("$consumedCalories", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                    Text("=", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Sisa", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("$remainingCalories", fontWeight = FontWeight.Bold, color = if(remainingCalories < 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface)
                    }
                }
            }
        }
        
        // Categories
        val categories = listOf("Makan Pagi", "Makan Siang", "Makan Malam", "Camilan/Lainnya")
        
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(categories) { category ->
                val categoryFoods = foods.filter { it.category == category }
                CategorySection(
                    title = category,
                    foods = categoryFoods,
                    onAddClick = {
                        foodToEdit = null
                        selectedCategory = category
                        showAddDialog = true
                    },
                    onEdit = { food ->
                        foodToEdit = food
                        selectedCategory = food.category
                        showAddDialog = true
                    },
                    onDelete = { viewModel.deleteFood(it.id) }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
    
    if (showAddDialog) {
        AddFoodDialog(
            category = selectedCategory,
            editFood = foodToEdit,
            onDismiss = { showAddDialog = false },
            onSave = { id, name, portions, calories, protein, carbs, fat ->
                viewModel.insertFood(id, name, selectedCategory, portions, calories, protein, carbs, fat)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun CategorySection(title: String, foods: List<FoodEntry>, onAddClick: () -> Unit, onEdit: (FoodEntry) -> Unit, onDelete: (FoodEntry) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                IconButton(onClick = onAddClick) {
                    Icon(Icons.Default.Add, contentDescription = "Tambah $title", tint = MaterialTheme.colorScheme.primary)
                }
            }
            if (foods.isEmpty()) {
                Text("Belum ada catatan.", color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(vertical = 8.dp))
            } else {
                foods.forEach { food ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(food.name, fontWeight = FontWeight.Medium)
                            Text("${food.portions} porsi | P:${food.protein} K:${food.carbs} L:${food.fat}", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
                        }
                        Text("${food.calories} kkal", fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(end = 8.dp))
                        Row {
                            IconButton(onClick = { onEdit(food) }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
                            }
                            IconButton(onClick = { onDelete(food) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFoodDialog(category: String, editFood: FoodEntry? = null, onDismiss: () -> Unit, onSave: (Int, String, Float, Int, Float, Float, Float) -> Unit) {
    var name by remember(editFood) { mutableStateOf(editFood?.name ?: "") }
    var portions by remember(editFood) { mutableStateOf(editFood?.portions?.toString() ?: "1") }
    var calories by remember(editFood) { mutableStateOf(editFood?.calories?.toString() ?: "") }
    var protein by remember(editFood) { mutableStateOf(editFood?.protein?.toString() ?: "0") }
    var carbs by remember(editFood) { mutableStateOf(editFood?.carbs?.toString() ?: "0") }
    var fat by remember(editFood) { mutableStateOf(editFood?.fat?.toString() ?: "0") }

    var expanded by remember { mutableStateOf(false) }
    val filteredFoods = FoodDictionary.foods.filter { it.name.contains(name, ignoreCase = true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tambah $category") },
        text = {
            Column {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { 
                            name = it
                            expanded = true
                        },
                        label = { Text("Nama Makanan") },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        filteredFoods.forEach { food ->
                            DropdownMenuItem(
                                text = { Text(food.name) },
                                onClick = {
                                    name = food.name
                                    expanded = false
                                    // Auto-fill macros based on selected food and current portion
                                    val p = portions.toFloatOrNull() ?: 1f
                                    calories = (food.calories * p).toInt().toString()
                                    protein = (food.protein * p).toString()
                                    carbs = (food.carbs * p).toString()
                                    fat = (food.fat * p).toString()
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = portions,
                        onValueChange = { portions = it },
                        label = { Text("Porsi") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = calories,
                        onValueChange = { calories = it },
                        label = { Text("Kalori (kkal)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = protein,
                        onValueChange = { protein = it },
                        label = { Text("Protein (g)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = carbs,
                        onValueChange = { carbs = it },
                        label = { Text("Karbo (g)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = fat,
                        onValueChange = { fat = it },
                        label = { Text("Lemak (g)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val p = portions.toFloatOrNull() ?: 1f
                val c = calories.toIntOrNull() ?: 0
                val pr = protein.toFloatOrNull() ?: 0f
                val cr = carbs.toFloatOrNull() ?: 0f
                val f = fat.toFloatOrNull() ?: 0f
                if (name.isNotBlank()) {
                    onSave(editFood?.id ?: 0, name, p, c, pr, cr, f)
                }
            }) {
                Text("Simpan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    )
}
