package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.ChildProfile
import com.example.ui.theme.GrowthTeal
import com.example.ui.theme.GrowthTealContainer

@Composable
fun ChildProfileDialog(
    child: ChildProfile,
    language: String,
    onDismiss: () -> Unit,
    onSave: (ChildProfile) -> Unit
) {
    var name by remember { mutableStateOf(child.name) }
    var ageMonths by remember { mutableStateOf(child.ageMonths.toString()) }
    var gender by remember { mutableStateOf(child.gender) }
    var weightKg by remember { mutableStateOf(child.weightKg.toString()) }
    var heightCm by remember { mutableStateOf(child.heightCm.toString()) }
    var bloodGroup by remember { mutableStateOf(child.bloodGroup) }
    var notes by remember { mutableStateOf(child.notes) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.85f)
                .clip(RoundedCornerShape(24.dp)),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChildCare,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (language == "sw") "Taarifa za Mtoto (Pasipoti ya Afya)" else "Child Health Passport",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Privacy guarantee banner
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = GrowthTealContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = GrowthTeal,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (language == "sw")
                                "Taarifa zote zinalindwa kwa usalama wa hali ya juu (End-to-End Encryption). Hatushiriki data zako za siri na mtu yeyote."
                            else
                                "Your child's health records are encrypted and protected under healthcare data security standards.",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = GrowthTeal)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text(if (language == "sw") "Jina la Mtoto" else "Child Name") },
                            modifier = Modifier.fillMaxWidth().testTag("child_name_input"),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = ageMonths,
                                onValueChange = { ageMonths = it },
                                label = { Text(if (language == "sw") "Umri (Miezi)" else "Age (Months)") },
                                modifier = Modifier.weight(1f).testTag("child_age_input"),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = bloodGroup,
                                onValueChange = { bloodGroup = it },
                                label = { Text(if (language == "sw") "Kundi la Damu" else "Blood Group") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true
                            )
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = weightKg,
                                onValueChange = { weightKg = it },
                                label = { Text(if (language == "sw") "Uzito (Kg)" else "Weight (Kg)") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = heightCm,
                                onValueChange = { heightCm = it },
                                label = { Text(if (language == "sw") "Urefu (cm)" else "Height (cm)") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true
                            )
                        }
                    }

                    item {
                        Text(
                            text = if (language == "sw") "Jinsia:" else "Gender:",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            listOf("Mvulana" to "Boy", "Msichana" to "Girl").forEach { (sw, en) ->
                                val isSelected = gender == sw || gender == en
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { gender = sw },
                                    label = { Text(if (language == "sw") sw else en) }
                                )
                            }
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = notes,
                            onValueChange = { notes = it },
                            label = { Text(if (language == "sw") "Mambo Anayopenda / Maelezo Maalum" else "Interests & Health Notes") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                            maxLines = 5,
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        val parsedAge = ageMonths.toIntOrNull() ?: child.ageMonths
                        val parsedWeight = weightKg.toDoubleOrNull() ?: child.weightKg
                        val parsedHeight = heightCm.toDoubleOrNull() ?: child.heightCm
                        onSave(
                            child.copy(
                                name = name.ifBlank { child.name },
                                ageMonths = parsedAge,
                                gender = gender,
                                bloodGroup = bloodGroup,
                                weightKg = parsedWeight,
                                heightCm = parsedHeight,
                                notes = notes
                            )
                        )
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("save_child_profile_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (language == "sw") "Hifadhi Taarifa za Mtoto" else "Save Child Details",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
