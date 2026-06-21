package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.MainViewModel
import com.example.util.NotificationHelper

@Composable
fun ProfileScreen(viewModel: MainViewModel) {
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val weights by viewModel.allWeights.collectAsStateWithLifecycle()
    
    val currentWeightStr = weights.maxByOrNull { it.timestamp }?.weightKg?.toString() ?: "-"
    
    var isEditing by remember { mutableStateOf(false) }
    
    var target by remember { mutableStateOf(userProfile?.calorieTarget?.toString() ?: "1800") }
    var breakfastTime by remember { mutableStateOf(userProfile?.breakfastTime ?: "07:00") }
    var lunchTime by remember { mutableStateOf(userProfile?.lunchTime ?: "12:00") }
    var dinnerTime by remember { mutableStateOf(userProfile?.dinnerTime ?: "19:00") }
    var weightTime by remember { mutableStateOf(userProfile?.weightTime ?: "06:00") }
    
    var heightCm by remember { mutableStateOf(userProfile?.heightCm?.toString() ?: "165") }
    var age by remember { mutableStateOf(userProfile?.age?.toString() ?: "25") }
    var gender by remember { mutableStateOf(userProfile?.gender ?: "Pria") }
    var dietGoal by remember { mutableStateOf(userProfile?.dietGoal ?: "Bertahan") }
    
    val context = LocalContext.current
    
    LaunchedEffect(userProfile) {
        if (!isEditing && userProfile != null) {
            name = userProfile!!.name
            email = userProfile!!.email
            target = userProfile!!.calorieTarget.toString()
            breakfastTime = userProfile!!.breakfastTime
            lunchTime = userProfile!!.lunchTime
            dinnerTime = userProfile!!.dinnerTime
            weightTime = userProfile!!.weightTime
            heightCm = userProfile!!.heightCm.toString()
            age = userProfile!!.age.toString()
            gender = userProfile!!.gender
            dietGoal = userProfile!!.dietGoal
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), RoundedCornerShape(40.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, contentDescription = "Profil", modifier = Modifier.size(40.dp), tint = MaterialTheme.colorScheme.primary)
                }
                Spacer(modifier = Modifier.height(16.dp))
                
                if (isEditing) {
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nama") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = heightCm, onValueChange = { heightCm = it }, label = { Text("Tinggi (cm)") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                        OutlinedTextField(value = age, onValueChange = { age = it }, label = { Text("Usia") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Simple Gender toggle
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
                        Text("Gender:")
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = gender == "Pria", onClick = { gender = "Pria" })
                            Text("Pria")
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = gender == "Wanita", onClick = { gender = "Wanita" })
                            Text("Wanita")
                        }
                    }
                    
                    // Simple Goal toggle
                    Text("Tujuan Diet:", style = MaterialTheme.typography.bodySmall)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
                        listOf("Turun", "Bertahan", "Naik").forEach { goal ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(selected = dietGoal == goal, onClick = { dietGoal = goal })
                                Text(goal, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = {
                        val w = currentWeightStr.toFloatOrNull() ?: 60f
                        val h = heightCm.toIntOrNull() ?: 165
                        val a = age.toIntOrNull() ?: 25
                        val bmr = if(gender == "Pria") (10 * w) + (6.25 * h) - (5 * a) + 5 else (10 * w) + (6.25 * h) - (5 * a) - 161
                        val tdee = bmr * 1.375
                        val idealCal = when(dietGoal) {
                            "Turun" -> tdee - 500
                            "Naik" -> tdee + 500
                            else -> tdee
                        }
                        target = idealCal.toInt().toString()
                    }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)) {
                        Text("Hitung Kalori Ideal")
                    }

                    OutlinedTextField(
                        value = target,
                        onValueChange = { target = it },
                        label = { Text("Target Kalori (kkal)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Pengingat (Format HH:MM)", style = MaterialTheme.typography.titleSmall, modifier = Modifier.align(Alignment.Start))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = breakfastTime, onValueChange = { breakfastTime = it }, label = { Text("Sarapan") }, modifier = Modifier.weight(1f))
                        OutlinedTextField(value = lunchTime, onValueChange = { lunchTime = it }, label = { Text("Makan Siang") }, modifier = Modifier.weight(1f))
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = dinnerTime, onValueChange = { dinnerTime = it }, label = { Text("Makan Malam") }, modifier = Modifier.weight(1f))
                        OutlinedTextField(value = weightTime, onValueChange = { weightTime = it }, label = { Text("Timbang Berat") }, modifier = Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                viewModel.saveUserProfile(
                                    name, email, target.toIntOrNull() ?: 1800, 
                                    breakfastTime, lunchTime, dinnerTime, weightTime,
                                    heightCm.toIntOrNull() ?: 165, age.toIntOrNull() ?: 25, gender, dietGoal
                                )
                                isEditing = false
                                
                                // Schedule Alarms
                                fun schedule(time: String, reqCode: Int, title: String, msg: String) {
                                    val parts = time.split(":")
                                    if(parts.size == 2) {
                                        val h = parts[0].toIntOrNull() ?: return
                                        val m = parts[1].toIntOrNull() ?: return
                                        NotificationHelper.scheduleDailyReminder(context, reqCode, title, msg, h, m)
                                    }
                                }
                                schedule(breakfastTime, 101, "Waktunya Sarapan!", "Jangan lewatkan sarapan sehatmu.")
                                schedule(lunchTime, 102, "Waktunya Makan Siang!", "Isi energimu untuk beraktivitas.")
                                schedule(dinnerTime, 103, "Waktunya Makan Malam!", "Pilih menu ringan untuk malam ini.")
                                schedule(weightTime, 104, "Waktunya Timbang!", "Catat berat badanmu hari ini.")
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Simpan")
                        }
                        OutlinedButton(onClick = { isEditing = false }, modifier = Modifier.weight(1f)) {
                            Text("Batal")
                        }
                    }
                } else {
                    Text(userProfile?.name ?: "Sobat", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text(userProfile?.email ?: "-", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Target Kalori", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
                            Text("${userProfile?.calorieTarget ?: 1800} kkal", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Berat Saat Ini", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
                            Text("$currentWeightStr kg", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        }
                    }
                    
                    val w = currentWeightStr.toFloatOrNull() ?: 0f
                    val h = userProfile?.heightCm?.toFloat() ?: 165f
                    val bmi = if(w > 0 && h > 0) w / ((h/100) * (h/100)) else 0f
                    val bmiCategory = when {
                        bmi < 18.5 -> "Kurus"
                        bmi < 25 -> "Normal"
                        bmi < 30 -> "Gemuk"
                        else -> "Obesitas"
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
                        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text("BMI Saat Ini", style = MaterialTheme.typography.labelMedium)
                                Text(if(bmi > 0) String.format(java.util.Locale.US, "%.1f", bmi) else "-", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                                Text(if(bmi > 0) bmiCategory else "-", color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.SemiBold)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Tujuan", style = MaterialTheme.typography.labelMedium)
                                Text(userProfile?.dietGoal ?: "-", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(onClick = { isEditing = true }, modifier = Modifier.fillMaxWidth()) {
                        Text("Edit Profil & Target")
                    }
                }
            }
        }
    }
}
