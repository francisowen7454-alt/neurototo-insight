package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.CompletionFilter
import com.example.ui.viewmodel.MilestoneUiState

@Composable
fun MilestonesScreen(
    child: ChildProfile,
    language: String,
    uiState: MilestoneUiState,
    redFlags: List<RedFlag>,
    onSelectAgeGroup: (String?) -> Unit,
    onSelectCategory: (MilestoneCategory?) -> Unit,
    onSelectCompletionFilter: (CompletionFilter) -> Unit,
    onToggleMilestone: (String) -> Unit,
    onAddMilestone: (name: String, ageGroup: String, category: MilestoneCategory, description: String, isCompleted: Boolean) -> Unit,
    onUpdateMilestoneDetails: (id: String, status: MilestoneStatus, notes: String) -> Unit,
    onDeleteMilestone: (String) -> Unit,
    onConsultDoctor: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: Milestones, 1: Red Flags
    var showAddDialog by remember { mutableStateOf(false) }
    var editingMilestone by remember { mutableStateOf<Milestone?>(null) }

    val ageGroups = listOf(
        "0-3 Miezi",
        "4-6 Miezi",
        "7-9 Miezi",
        "10-12 Miezi",
        "1-2 Miaka",
        "2-3 Miaka",
        "3-5 Miaka"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag("milestones_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
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
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 140.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Age group filter pills
                        item {
                            Column {
                                Text(
                                    text = if (language == "sw") "Hatua ya Umri:" else "Age Group:",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    item {
                                        FilterChip(
                                            selected = uiState.selectedAgeGroup == null,
                                            onClick = { onSelectAgeGroup(null) },
                                            label = { Text(if (language == "sw") "Umri Wote" else "All Ages") },
                                            modifier = Modifier.testTag("age_chip_all")
                                        )
                                    }
                                    items(ageGroups) { age ->
                                        FilterChip(
                                            selected = uiState.selectedAgeGroup == age,
                                            onClick = { onSelectAgeGroup(if (uiState.selectedAgeGroup == age) null else age) },
                                            label = { Text(age) },
                                            modifier = Modifier.testTag("age_chip_$age")
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
                                        selected = uiState.selectedCategory == null,
                                        onClick = { onSelectCategory(null) },
                                        label = { Text(if (language == "sw") "Zote" else "All Domains") }
                                    )
                                }
                                items(MilestoneCategory.values()) { cat ->
                                    FilterChip(
                                        selected = uiState.selectedCategory == cat,
                                        onClick = { onSelectCategory(if (uiState.selectedCategory == cat) null else cat) },
                                        label = { Text(if (language == "sw") cat.swahiliName else cat.englishName) }
                                    )
                                }
                            }
                        }

                        // Completion Status Filter
                        item {
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(CompletionFilter.values()) { filter ->
                                    FilterChip(
                                        selected = uiState.completionFilter == filter,
                                        onClick = { onSelectCompletionFilter(filter) },
                                        leadingIcon = if (uiState.completionFilter == filter) {
                                            { Icon(Icons.Default.Done, contentDescription = null, modifier = Modifier.size(16.dp)) }
                                        } else null,
                                        label = { Text(if (language == "sw") filter.swahiliLabel else filter.englishLabel) },
                                        modifier = Modifier.testTag("completion_filter_${filter.name.lowercase()}")
                                    )
                                }
                            }
                        }

                        // Summary Progress Card
                        item {
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
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
                                            text = if (language == "sw")
                                                "${uiState.completedCount} / ${uiState.totalCount} zimekamilika"
                                            else
                                                "${uiState.completedCount} / ${uiState.totalCount} completed",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = GrowthTeal,
                                                fontWeight = FontWeight.Bold
                                            ),
                                            modifier = Modifier.testTag("milestone_progress_summary")
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    LinearProgressIndicator(
                                        progress = { uiState.completionFraction },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(8.dp),
                                        color = GrowthTeal,
                                        trackColor = MaterialTheme.colorScheme.surface
                                    )
                                }
                            }
                        }

                        if (uiState.isLoading) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        } else if (uiState.filteredMilestones.isEmpty()) {
                            item {
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(24.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.TrackChanges,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(36.dp)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = if (language == "sw")
                                                "Hakuna hatua zilizopatikana kwa kichujio hiki."
                                            else
                                                "No milestones match the active filter.",
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        TextButton(onClick = { showAddDialog = true }) {
                                            Text(if (language == "sw") "+ Ongeza Hatua Mpya" else "+ Add Custom Milestone")
                                        }
                                    }
                                }
                            }
                        } else {
                            items(uiState.filteredMilestones, key = { it.id }) { milestone ->
                                MilestoneCard(
                                    milestone = milestone,
                                    language = language,
                                    onToggle = { onToggleMilestone(milestone.id) },
                                    onEditNotes = { editingMilestone = milestone }
                                )
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

                        items(redFlags, key = { it.id }) { flag ->
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
                                        Icon(imageVector = Icons.AutoMirrored.Filled.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
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

        // FAB to add a custom milestone directly to Room DB
        if (selectedTab == 0) {
            ExtendedFloatingActionButton(
                onClick = { showAddDialog = true },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text(if (language == "sw") "Ongeza Hatua" else "Add Milestone") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 92.dp)
                    .testTag("add_milestone_fab")
            )
        }
    }

    if (showAddDialog) {
        AddMilestoneDialog(
            language = language,
            ageGroups = ageGroups,
            initialAgeGroup = uiState.selectedAgeGroup ?: "1-2 Miaka",
            onDismiss = { showAddDialog = false },
            onSave = { name, ageGroup, category, description, isCompleted ->
                onAddMilestone(name, ageGroup, category, description, isCompleted)
                showAddDialog = false
            }
        )
    }

    editingMilestone?.let { milestone ->
        EditMilestoneStatusDialog(
            milestone = milestone,
            language = language,
            onDismiss = { editingMilestone = null },
            onSave = { status, notes ->
                onUpdateMilestoneDetails(milestone.id, status, notes)
                editingMilestone = null
            },
            onDelete = if (milestone.id.startsWith("m_custom_")) {
                {
                    onDeleteMilestone(milestone.id)
                    editingMilestone = null
                }
            } else null
        )
    }
}

@Composable
private fun MilestoneCard(
    milestone: Milestone,
    language: String,
    onToggle: () -> Unit,
    onEditNotes: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        shadowElevation = 1.dp,
        border = if (milestone.isAchieved)
            androidx.compose.foundation.BorderStroke(1.5.dp, GrowthTeal.copy(alpha = 0.5f))
        else null,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("milestone_card_${milestone.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 6.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
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
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer
                        ) {
                            Text(
                                text = milestone.ageGroup,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Text(
                        text = if (language == "sw") milestone.titleSwahili else milestone.titleEnglish,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                IconButton(
                    onClick = onToggle,
                    modifier = Modifier.testTag("toggle_milestone_${milestone.id}")
                ) {
                    Icon(
                        imageVector = if (milestone.isAchieved) Icons.Default.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
                        contentDescription = if (milestone.isAchieved) "Completed" else "Mark completed",
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

            if (milestone.parentNotes.isNotBlank() || milestone.dateLogged != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    milestone.dateLogged?.let { date ->
                        AssistChip(
                            onClick = onEditNotes,
                            label = { Text(date, fontSize = 11.sp) },
                            leadingIcon = {
                                Icon(Icons.Default.EventAvailable, contentDescription = null, modifier = Modifier.size(14.dp), tint = GrowthTeal)
                            }
                        )
                    }
                    if (milestone.parentNotes.isNotBlank()) {
                        Text(
                            text = milestone.parentNotes,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

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
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (language == "sw") milestone.status.swahiliLabel else milestone.status.englishLabel,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (milestone.isAchieved) GrowthTeal else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                TextButton(
                    onClick = onEditNotes,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Icon(Icons.Default.EditNote, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (language == "sw") "Hariri Hali / Maelezo" else "Update Status / Notes",
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun AddMilestoneDialog(
    language: String,
    ageGroups: List<String>,
    initialAgeGroup: String,
    onDismiss: () -> Unit,
    onSave: (name: String, ageGroup: String, category: MilestoneCategory, description: String, isCompleted: Boolean) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var selectedAgeGroup by remember { mutableStateOf(initialAgeGroup) }
    var selectedCategory by remember { mutableStateOf(MilestoneCategory.COGNITIVE) }
    var description by remember { mutableStateOf("") }
    var isCompleted by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (language == "sw") "Ongeza Hatua ya Ukuaji" else "Add Developmental Milestone",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(if (language == "sw") "Jina la Hatua (mf. Kujitaja Jina)" else "Milestone Name") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("milestone_name_input")
                )

                Text(
                    text = if (language == "sw") "Kundi la Umri:" else "Age Group:",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(ageGroups) { age ->
                        FilterChip(
                            selected = selectedAgeGroup == age,
                            onClick = { selectedAgeGroup = age },
                            label = { Text(age, fontSize = 11.sp) }
                        )
                    }
                }

                Text(
                    text = if (language == "sw") "Kipengele:" else "Domain:",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(MilestoneCategory.values()) { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(if (language == "sw") cat.swahiliName else cat.englishName, fontSize = 11.sp) }
                        )
                    }
                }

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(if (language == "sw") "Maelezo mafupi (Hiari)" else "Short Notes (Optional)") },
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (language == "sw") "Je, imeshakamilika?" else "Already completed?",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Switch(
                        checked = isCompleted,
                        onCheckedChange = { isCompleted = it }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(name.trim(), selectedAgeGroup, selectedCategory, description.trim(), isCompleted) },
                enabled = name.isNotBlank(),
                modifier = Modifier.testTag("save_milestone_button")
            ) {
                Text(if (language == "sw") "Hifadhi" else "Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (language == "sw") "Ghairi" else "Cancel")
            }
        }
    )
}

@Composable
private fun EditMilestoneStatusDialog(
    milestone: Milestone,
    language: String,
    onDismiss: () -> Unit,
    onSave: (status: MilestoneStatus, notes: String) -> Unit,
    onDelete: (() -> Unit)?
) {
    var selectedStatus by remember { mutableStateOf(milestone.status) }
    var notes by remember { mutableStateOf(milestone.parentNotes) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (language == "sw") milestone.titleSwahili else milestone.titleEnglish,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = if (language == "sw") "Hali ya Kukamilika:" else "Completion Status:",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                )
                MilestoneStatus.values().forEach { status ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        RadioButton(
                            selected = selectedStatus == status,
                            onClick = { selectedStatus = status }
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (language == "sw") status.swahiliLabel else status.englishLabel,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text(if (language == "sw") "Kumbukumbu ya Mzazi" else "Parent Observation Notes") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )

                if (onDelete != null) {
                    TextButton(
                        onClick = onDelete,
                        colors = ButtonDefaults.textButtonColors(contentColor = AlertCoral)
                    ) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (language == "sw") "Futa Hatua Hii" else "Delete Custom Milestone")
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onSave(selectedStatus, notes) }) {
                Text(if (language == "sw") "Sasisha" else "Update")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (language == "sw") "Funga" else "Close")
            }
        }
    )
}
