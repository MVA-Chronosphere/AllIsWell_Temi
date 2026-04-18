package com.example.alliswelltemi.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.alliswelltemi.data.Doctor
import com.example.alliswelltemi.viewmodel.DoctorsViewModel
import com.robotemi.sdk.Robot
import com.robotemi.sdk.TtsRequest

@Composable
fun DoctorsScreen(
    robot: Robot?,
    viewModel: DoctorsViewModel,
    onBackPress: () -> Unit,
    onSelectDoctor: (Doctor) -> Unit
) {
    val doctors by viewModel.doctors
    val isLoading by viewModel.isLoading
    val departments by viewModel.departments
    val selectedDept by viewModel.selectedDepartment
    val error by viewModel.error

    var searchQuery by remember { mutableStateOf("") }

    TemiScreenScaffold(
        title = "Doctors & Departments",
        onBackClick = onBackPress
    ) { contentModifier ->
        Column(modifier = contentModifier) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                placeholder = { Text("Search doctors, specialties...", color = Color.Gray) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFF00D9FF),
                    unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f)
                )
            )

            // Departments Row
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                item {
                    DepartmentChip(
                        name = "All",
                        isSelected = selectedDept == null,
                        onClick = { viewModel.filterByDepartment(null) }
                    )
                }
                items(departments) { dept ->
                    DepartmentChip(
                        name = dept,
                        isSelected = selectedDept == dept,
                        onClick = { viewModel.filterByDepartment(dept) }
                    )
                }
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF00D9FF))
                }
            } else if (error != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = error!!, color = Color.Red)
                        Button(onClick = { viewModel.retry() }) {
                            Text("Retry")
                        }
                    }
                }
            } else {
                val filteredDoctors = viewModel.searchDoctors(searchQuery)
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(filteredDoctors) { doctor ->
                        DoctorCard(
                            doctor = doctor,
                            onClick = {
                                onSelectDoctor(doctor)
                            },
                            onNavigateClick = {
                                val speech = "I'll take you to Dr. ${doctor.name}. Please follow me."
                                robot?.speak(TtsRequest.create(speech, isShowOnConversationLayer = false))
                                // Command: "take me to dr{name}"
                                // Assuming the cabin/location name in Temi matches or we use the cabin field
                                val destination = doctor.cabin.ifBlank { doctor.name }
                                robot?.goTo(destination)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DepartmentChip(
    name: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) Color(0xFF00D9FF) else Color(0xFF1A2332),
        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, Color.Gray.copy(alpha = 0.5f))
    ) {
        Text(
            text = name,
            color = if (isSelected) Color.Black else Color.White,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun DoctorCard(
    doctor: Doctor,
    onClick: () -> Unit,
    onNavigateClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A2332).copy(alpha = 0.6f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile Image
            AsyncImage(
                model = doctor.profileImageUrl,
                contentDescription = doctor.name,
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color.Gray.copy(alpha = 0.2f)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = doctor.name,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = doctor.specialization,
                    color = Color(0xFF00D9FF),
                    fontSize = 14.sp
                )
                Text(
                    text = "${doctor.yearsOfExperience} years experience",
                    color = Color.LightGray,
                    fontSize = 12.sp
                )
                if (doctor.cabin.isNotBlank()) {
                    Text(
                        text = "Location: ${doctor.cabin}",
                        color = Color.LightGray,
                        fontSize = 12.sp
                    )
                }
            }

            // Navigate Button
            Button(
                onClick = onNavigateClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00D9FF)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Navigate", color = Color.Black)
            }
        }
    }
}
