package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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
import com.example.model.*
import com.example.ui.theme.*

@Composable
fun MilestonesScreen(
    child: ChildProfile,
    language: String,
    milestones: List<Milestone>,
    redFlags: List<RedFlag>,
    onToggleMilestone: (String) -> Unit,
    onConsultDoctor: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: Milestones, 1: Red Flags (Tahadhari)
    var selectedAgeGroup by remember { mutableStateOf("1-2 Miaka") }
    var selectedCategory by remember { mutableStateOf<MilestoneCategory?>(null) }

    val ageGroups = listOf("0-3 Miezi", "4-6 Miezi", "7-12 Miezi", "1-2 Miaka", "3-5 Miaka")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("milestones_screen")
    ) {
        // Tab Row (Milestones vs Red Flags)
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (language == "sw") "Hatua za Ukuaji" else "Milestones",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = AlertCoral, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (language == "sw") "Tahadhari za Kitaalamu" else "Clinical Red Flags",
                            color = AlertCoral,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            )
        }

        when (selectedTab) {
            0 -> {
                // Milestones View
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 90.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Age group filter pills
                    item {
                        Column {
                            Text(
                                text = if (language == "sw") "Hatua ya Umri:" else "Age Bracket:",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(ageGroups) { age ->
                                    FilterChip(
                                        selected = selectedAgeGroup == age,
                                        onClick = { selectedAgeGroup = age },
                                        label = { Text(age) }
                                    )
                                }
                            }
                        }
                    }

                    // Domain Categories
                    item {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            item {
                                FilterChip(
                                    selected = selectedCategory == null,
                                    onClick = { selectedCategory = null },
                                    label = { Text(if (language == "sw") "Zote" else "All") }
                                )
                            }
                            items(MilestoneCategory.values()) { cat ->
                                FilterChip(
                                    selected = selectedCategory == cat,
                                    onClick = { selectedCategory = cat },
                                    label = { Text(if (language == "sw") cat.swahiliName else cat.englishName) }
                                )
                            }
                        }
                    }

                    // Summary Count
                    item {
                        val currentList = milestones.filter {
                            (selectedCategory == null || it.category == selectedCategory)
                        }
                        val achieved = currentList.count { it.isAchieved }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (language == "sw")
                                        "Ukuaji wa ${child.name} (${child.ageMonths}m)"
                                    else
                                        "${child.name}'s Progress (${child.ageMonths}m)",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "$achieved / ${currentList.size} zimekamilika",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = GrowthTeal,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }

                    // Milestone Items
                    val filteredMilestones = milestones.filter {
                        (selectedCategory == null || it.category == selectedCategory)
                    }

                    items(filteredMilestones) { milestone ->
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surface,
                            tonalElevation = 2.dp,
                            shadowElevation = 1.dp,
                            border = if (milestone.isAchieved)
                                androidx.compose.foundation.BorderStroke(1.5.dp, GrowthTeal.copy(alpha = 0.5f))
                            else null,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.Top,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = MaterialTheme.colorScheme.primaryContainer,
                                            modifier = Modifier.padding(bottom = 6.dp)
                                        ) {
                                            Text(
                                                text = if (language == "sw") milestone.category.swahiliName else milestone.category.englishName,
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = MaterialTheme.colorScheme.primary,
                                                    fontWeight = FontWeight.Bold
                                                ),
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }

                                        Text(
                                            text = if (language == "sw") milestone.titleSwahili else milestone.titleEnglish,
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                        )
                                    }

                                    // Achievement Checkbox
                                    IconButton(
                                        onClick = { onToggleMilestone(milestone.id) },
                                        modifier = Modifier.testTag("toggle_milestone_${milestone.id}")
                                    ) {
                                        Icon(
                                            imageVector = if (milestone.isAchieved) Icons.Default.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
                                            contentDescription = "Achieved",
                                            tint = if (milestone.isAchieved) GrowthTeal else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = if (language == "sw") milestone.descriptionSwahili else milestone.descriptionEnglish,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp)
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                // Tip for parent
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = WarmAmberContainer,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Lightbulb,
                                            contentDescription = null,
                                            tint = WarmAmber,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = if (language == "sw") milestone.tipSwahili else milestone.tipEnglish,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onTertiaryContainer
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            1 -> {
                // Clinical Red Flags View
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 90.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = AlertCoralContainer,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.ErrorOutline,
                                        contentDescription = null,
                                        tint = AlertCoral,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = if (language == "sw") "Tahadhari za Kimatibabu (Red Flags)" else "Clinical Warning Indicators",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = AlertCoral
                                        )
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = if (language == "sw")
                                        "Dalili hizi zikionekana kwa mtoto, hazimaanishi ugonjwa wa kudumu bali ni ishara kwamba mfumo wa fahamu unahitaji uchunguzi wa kitaalamu wa mapema (Early Clinical Intervention)."
                                    else
                                        "These clinical indicators suggest the central nervous system requires early screening. Early intervention before age 3 radically optimizes developmental trajectory.",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                )
                            }
                        }
                    }

                    items(redFlags) { flag ->
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surface,
                            tonalElevation = 2.dp,
                            border = androidx.compose.foundation.BorderStroke(1.dp, AlertCoral.copy(alpha = 0.3f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = AlertCoralContainer
                                ) {
                                    Text(
                                        text = flag.ageGroup,
                                        color = AlertCoral,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = if (language == "sw") flag.warningSwahili else flag.warningEnglish,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = AlertCoral
                                    )
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = if (language == "sw") flag.reasonSwahili else flag.reasonEnglish,
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp)
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(imageVector = Icons.Default.MedicalServices, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = if (language == "sw") flag.recommendedActionSwahili else flag.recommendedActionEnglish,
                                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                OutlinedButton(
                                    onClick = onConsultDoctor,
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary)
                                ) {
                                    Icon(imageVector = Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (language == "sw") "Wasiliana na Daktari Moja kwa Moja (Pro)" else "Consult Pediatrician Directly (Pro)",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
