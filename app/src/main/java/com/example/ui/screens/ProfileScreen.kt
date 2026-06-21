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
    
    var name by remember { mutableStateOf(userProfile?.name ?: "") }
    var email by remember { mutableStateOf(userProfile?.email ?: "") }
    var target by remember { mutableStateOf(userProfile?.calorieTarget?.toString() ?: "1800") }
    var breakfastTime by remember { mutableStateOf(userProfile?.breakfastTime ?: "07:00") }
    var lunchTime by remember { mutableStateOf(userProfile?.lunchTime ?: "12:00") }
    var dinnerTime by remember { mutableStateOf(userProfile?.dinnerTime ?: "19:00") }
    var weightTime by remember { mutableStateOf(userProfile?.weightTime ?: "06:00") }
    
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
                                viewModel.saveUserProfile(name, email, target.toIntOrNull() ?: 1800, breakfastTime, lunchTime, dinnerTime, weightTime)
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
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(onClick = { isEditing = true }, modifier = Modifier.fillMaxWidth()) {
                        Text("Edit Profil & Target")
                    }
                }
            }
        }
    }
}
