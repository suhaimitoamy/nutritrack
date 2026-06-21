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
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.FitnessCenter
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
    val activities by viewModel.activitiesForCurrentDate.collectAsStateWithLifecycle()
    val waterEntries by viewModel.waterForCurrentDate.collectAsStateWithLifecycle()
    val date by viewModel.currentDate.collectAsStateWithLifecycle()
    
    val targetCalories = userProfile?.calorieTarget ?: 1800
    val consumedCalories = foods.sumOf { it.calories }
    val burnedCalories = activities.sumOf { it.caloriesBurned }
    val remainingCalories = targetCalories + burnedCalories - consumedCalories
    
    val totalProtein = foods.sumOf { it.protein.toDouble() }.toFloat()
    val totalCarbs = foods.sumOf { it.carbs.toDouble() }.toFloat()
    val totalFat = foods.sumOf { it.fat.toDouble() }.toFloat()
    val totalWater = waterEntries.sumOf { it.amountMl }

    var showAddDialog by remember { mutableStateOf(false) }
    var showActivityDialog by remember { mutableStateOf(false) }
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
        
        // Fasting Banner
        if (userProfile?.isFasting == true) {
            val startTime = userProfile?.fastingStartTime ?: 0L
            val elapsedMs = System.currentTimeMillis() - startTime
            val hours = elapsedMs / (1000 * 60 * 60)
            val minutes = (elapsedMs / (1000 * 60)) % 60
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Timer, contentDescription = null, tint = MaterialTheme.colorScheme.onTertiaryContainer)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("Puasa Sedang Berjalan", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onTertiaryContainer)
                        Text("Durasi: $hours jam $minutes menit", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onTertiaryContainer)
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Button(onClick = { viewModel.toggleFasting() }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)) {
                        Text("Akhiri")
                    }
                }
            }
        } else {
            Button(onClick = { viewModel.toggleFasting() }, modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)) {
                Text("Mulai Puasa (16:8)")
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
                    Text("+", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Column {
                        Text("Olahraga", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("$burnedCalories", fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
                    }
                    Text("=", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Sisa", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("$remainingCalories", fontWeight = FontWeight.Bold, color = if(remainingCalories < 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("P: ${String.format(Locale.US, "%.1f", totalProtein)}g", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
                    Text("K: ${String.format(Locale.US, "%.1f", totalCarbs)}g", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
                    Text("L: ${String.format(Locale.US, "%.1f", totalFat)}g", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
                }
            }
        }
        
        // Categories
        val categories = listOf("Makan Pagi", "Makan Siang", "Makan Malam", "Camilan/Lainnya")
        
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            item {
                // Water Tracker
                Card(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                    Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocalDrink, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Air Minum", fontWeight = FontWeight.Bold)
                            Text("$totalWater ml / 2000 ml", style = MaterialTheme.typography.bodySmall)
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        val context = LocalContext.current
                        Button(onClick = { 
                            viewModel.insertWater(250)
                            android.widget.Toast.makeText(context, "+250ml Air Ditambahkan!", android.widget.Toast.LENGTH_SHORT).show()
                        }) {
                            Text("+250ml")
                        }
                    }
                }
            }

            item {
                // Activity Tracker
                Card(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.FitnessCenter, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Olahraga", fontWeight = FontWeight.Bold)
                            }
                            IconButton(onClick = { showActivityDialog = true }) {
                                Icon(Icons.Default.Add, contentDescription = "Tambah Olahraga")
                            }
                        }
                        if (activities.isEmpty()) {
                            Text("Belum ada olahraga hari ini.", style = MaterialTheme.typography.bodySmall)
                        } else {
                            activities.forEach { act ->
                                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Column {
                                        Text(act.activityName, fontWeight = FontWeight.Medium)
                                        Text("${act.durationMinutes} menit", style = MaterialTheme.typography.bodySmall)
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("${act.caloriesBurned} kkal", fontWeight = FontWeight.SemiBold, color = Color(0xFF4CAF50))
                                        IconButton(onClick = { viewModel.deleteActivity(act.id) }) {
                                            Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

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

    if (showActivityDialog) {
        AddActivityDialog(
            onDismiss = { showActivityDialog = false },
            onSave = { name, duration, calories ->
                viewModel.insertActivity(name, duration, calories)
                showActivityDialog = false
            }
        )
    }
}

@Composable
fun AddActivityDialog(onDismiss: () -> Unit, onSave: (String, Int, Int) -> Unit) {
    var name by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }
    var calories by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tambah Olahraga") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nama Aktivitas (misal: Lari)") })
                OutlinedTextField(value = duration, onValueChange = { duration = it }, label = { Text("Durasi (menit)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                OutlinedTextField(value = calories, onValueChange = { calories = it }, label = { Text("Kalori Terbakar") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            }
        },
        confirmButton = {
            Button(onClick = {
                if(name.isNotBlank()) {
                    onSave(name, duration.toIntOrNull() ?: 0, calories.toIntOrNull() ?: 0)
                }
            }) { Text("Simpan") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    )
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
