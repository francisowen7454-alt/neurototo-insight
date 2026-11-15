package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.AiAssistantService
import com.example.data.NeuroRepository
import com.example.model.ChatMessage
import com.example.model.ChildProfile
import com.example.model.MilestoneCategory
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun AiConsultantScreen(
    child: ChildProfile,
    language: String,
    onConsultDoctor: () -> Unit,
    onNavigateToMilestones: () -> Unit = {}
) {
    val context = LocalContext.current
    var inputText by remember { mutableStateOf("") }
    var isThinking by remember { mutableStateOf(false) }
    var showClearConfirmDialog by remember { mutableStateOf(false) }
    var selectedCategoryFilter by remember { mutableStateOf<MilestoneCategory?>(null) }
    var milestoneCelebrationText by remember { mutableStateOf<String?>(null) }

    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    // Initialize persistence and collect persistent history
    LaunchedEffect(Unit) {
        NeuroRepository.initChatPersistence(context)
    }

    val allMessages by NeuroRepository.chatMessages.collectAsStateWithLifecycle()
    val milestones by NeuroRepository.milestones.collectAsStateWithLifecycle()

    // Filter messages if category filter is active
    val displayedMessages = remember(allMessages, selectedCategoryFilter) {
        if (selectedCategoryFilter == null) {
            allMessages
        } else {
            allMessages.filter { it.relatedMilestoneCategory == selectedCategoryFilter || it.sender == "user" }
        }
    }

    // Milestone stats for the child
    val achievedCount = remember(milestones) { milestones.count { it.isAchieved } }
    val totalCount = remember(milestones) { milestones.size }

    // Quick developmental inquiry prompts grouped by topic
    val quickTopics = remember(language) {
        if (language == "sw") listOf(
            Triple(MilestoneCategory.SPEECH, "Lugha & Usemi", "Mtoto kuchelewa kuongea na mbinu za lugha"),
            Triple(MilestoneCategory.MOTOR, "Mwendo & Misuli", "Kutembea, kusimama na kukimbia kwa mtoto"),
            Triple(MilestoneCategory.COGNITIVE, "Utambuzi & Neva", "Kuelekeza kidole (Joint Attention) na kuitika jina"),
            Triple(MilestoneCategory.EMOTIONAL, "Hisia & Usingizi", "Kutuliza hasira za mtoto (Tantrums) na usingizi mnono"),
            Triple(MilestoneCategory.SENSORY, "Skrini & Simu", "Mbinu za kuondoa simu na TV bila kulia"),
            Triple(MilestoneCategory.COGNITIVE, "Lishe ya Ubongo", "Vyakula vya asili vya kujenga ubongo (Dagaa na mayai)")
        ) else listOf(
            Triple(MilestoneCategory.SPEECH, "Speech & Words", "Speech and language delay interventions"),
            Triple(MilestoneCategory.MOTOR, "Gross Motor", "Independent walking and motor milestones"),
            Triple(MilestoneCategory.COGNITIVE, "Joint Attention", "Pointing to share interest and responding to name"),
            Triple(MilestoneCategory.EMOTIONAL, "Emotions & Sleep", "De-escalating toddler emotional tantrums and sleep"),
            Triple(MilestoneCategory.SENSORY, "Digital Detox", "Healthy screen-free parenting strategies"),
            Triple(MilestoneCategory.COGNITIVE, "Brain Foods", "Indigenous brain nutrition (Lake fish and choline)")
        )
    }

    // Auto-scroll on new message
    LaunchedEffect(displayedMessages.size) {
        if (displayedMessages.isNotEmpty()) {
            listState.animateScrollToItem(displayedMessages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("ai_consultant_screen")
    ) {
        // 1. Top Developmental Profile Header
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "TotoAI Mshauri wa Ukuaji",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = GrowthTealContainer
                                ) {
                                    Text(
                                        text = "GROWTH AI",
                                        color = GrowthTeal,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = if (language == "sw")
                                    "${child.name} • Miezi ${child.ageMonths} (Hatua $achievedCount/$totalCount)"
                                else
                                    "${child.name} • ${child.ageMonths}m ($achievedCount/$totalCount Milestones)",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Clear History Action Button
                        IconButton(
                            onClick = { showClearConfirmDialog = true },
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.DeleteSweep,
                                contentDescription = "Futa Historia",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // View Milestones Link
                        OutlinedButton(
                            onClick = onNavigateToMilestones,
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.TrackChanges,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (language == "sw") "Hatua" else "Tracker",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                // Persistence & Clinical Notice Chip
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.HistoryEdu,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (language == "sw")
                                "Kumbukumbu inahifadhiwa kwenye kifaa. Maswali yameunganishwa na miongozo ya WHO/CDC."
                            else
                                "Chat history saved securely on device. Guidance aligns with WHO/CDC pediatric milestones.",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Celebration Toast Banner if a milestone was just recorded
        AnimatedVisibility(
            visible = milestoneCelebrationText != null,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Surface(
                color = GrowthTealContainer,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🌟", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = milestoneCelebrationText ?: "",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = GrowthTeal
                            )
                        )
                    }
                    IconButton(
                        onClick = { milestoneCelebrationText = null },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = GrowthTeal, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }

        // 2. Developmental Topic Chips (Quick Questions)
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(quickTopics) { (category, title, promptQuery) ->
                val isSelected = selectedCategoryFilter == category
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        inputText = promptQuery
                    },
                    label = {
                        Text(text = title, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = when (category) {
                                MilestoneCategory.SPEECH -> Icons.Default.RecordVoiceOver
                                MilestoneCategory.MOTOR -> Icons.Default.DirectionsRun
                                MilestoneCategory.COGNITIVE -> Icons.Default.Psychology
                                MilestoneCategory.EMOTIONAL -> Icons.Default.Favorite
                                MilestoneCategory.SENSORY -> Icons.Default.Smartphone
                            },
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                    },
                    shape = RoundedCornerShape(20.dp)
                )
            }
        }

        // 3. Persistent Chat Message Feed
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(displayedMessages, key = { it.id }) { msg ->
                val isUser = msg.sender == "user"
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
                ) {
                    Surface(
                        shape = RoundedCornerShape(
                            topStart = 18.dp,
                            topEnd = 18.dp,
                            bottomStart = if (isUser) 18.dp else 4.dp,
                            bottomEnd = if (isUser) 4.dp else 18.dp
                        ),
                        color = if (isUser)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.surfaceVariant,
                        tonalElevation = 2.dp,
                        modifier = Modifier.widthIn(max = 340.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            if (!isUser) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 6.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.SmartToy,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "TotoAI Pediatric Engine",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = MaterialTheme.colorScheme.primary,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp
                                            )
                                        )
                                    }
                                    if (msg.relatedMilestoneCategory != null) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                                        ) {
                                            Text(
                                                text = if (language == "sw") msg.relatedMilestoneCategory.swahiliName else msg.relatedMilestoneCategory.englishName,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            // Message Body
                            Text(
                                text = msg.text,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = if (isUser) Color.White else MaterialTheme.colorScheme.onSurface,
                                    lineHeight = 20.sp,
                                    fontSize = 13.sp
                                )
                            )

                            // 4. Growth Milestone Card Attachment
                            if (!isUser && msg.relatedMilestoneTitle != null) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.surface,
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (msg.isMilestoneLogged) GrowthTeal else MaterialTheme.colorScheme.outlineVariant
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.TrackChanges,
                                                    contentDescription = null,
                                                    tint = if (msg.isMilestoneLogged) GrowthTeal else WarmAmber,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = if (language == "sw") "Hatua ya Ukuaji:" else "Linked Milestone:",
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        fontWeight = FontWeight.Bold,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                )
                                            }
                                            if (msg.isMilestoneLogged) {
                                                Surface(
                                                    shape = RoundedCornerShape(6.dp),
                                                    color = GrowthTealContainer
                                                ) {
                                                    Text(
                                                        text = if (language == "sw") "IMEREKODIWA ✓" else "LOGGED ✓",
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.ExtraBold,
                                                        color = GrowthTeal,
                                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = msg.relatedMilestoneTitle,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp
                                            )
                                        )

                                        if (msg.developmentalTipSwahili != null || msg.developmentalTipEnglish != null) {
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = (if (language == "sw") msg.developmentalTipSwahili else msg.developmentalTipEnglish) ?: "",
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    fontSize = 11.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))

                                        // Milestone logging action button
                                        if (!msg.isMilestoneLogged) {
                                            Button(
                                                onClick = {
                                                    val milestoneId = msg.relatedMilestoneId ?: "m_1_2_cog_point"
                                                    val note = msg.suggestedObservationNote ?: "Imerekodiwa kutoka kwenye ushauri wa TotoAI."
                                                    NeuroRepository.logMilestoneFromChat(
                                                        context = context,
                                                        messageId = msg.id,
                                                        milestoneId = milestoneId,
                                                        note = note
                                                    )
                                                    milestoneCelebrationText = if (language == "sw")
                                                        "Hongera! Hatua ya '${msg.relatedMilestoneTitle}' imerekodiwa kwa mafanikio."
                                                    else
                                                        "Great job! '${msg.relatedMilestoneTitle}' recorded to milestones."
                                                },
                                                shape = RoundedCornerShape(8.dp),
                                                colors = ButtonDefaults.buttonColors(containerColor = GrowthTeal),
                                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                                modifier = Modifier.fillMaxWidth().testTag("log_milestone_chat_btn_${msg.id}")
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.BookmarkAdd,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = if (language == "sw") "Rekodi Kwenye Ukuaji wa Mtoto" else "Record In Child Milestones",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        } else {
                                            OutlinedButton(
                                                onClick = onNavigateToMilestones,
                                                shape = RoundedCornerShape(8.dp),
                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = GrowthTeal, modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = if (language == "sw") "Tazama Kwenye Orodha ya Hatua" else "View In Milestone Tracker",
                                                    fontSize = 11.sp,
                                                    color = GrowthTeal,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            // Doctor consultation referral button if clinical warning
                            if (msg.isWarning) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Button(
                                    onClick = onConsultDoctor,
                                    colors = ButtonDefaults.buttonColors(containerColor = AlertCoral),
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.MedicalServices,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (language == "sw") "Ongea na Daktari Bingwa (Pro)" else "Consult Pediatric Specialist (Pro)",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = msg.timestamp,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            fontSize = 9.sp
                        )
                    )
                }
            }

            if (isThinking) {
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(8.dp)
                            .testTag("ai_thinking_indicator")
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (language == "sw")
                                "TotoAI anachakata majibu ya makuzi na miongozo ya neva..."
                            else
                                "TotoAI analyzing growth milestones & pediatric clinical guidance...",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                }
            }
        }

        // 5. Input Field Bar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 4.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 80.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = {
                        Text(
                            text = if (language == "sw") "Uliza swali la ukuaji, mfumo wa fahamu, au lishe..." else "Ask a developmental or milestone question...",
                            fontSize = 13.sp
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("ai_chat_input"),
                    shape = RoundedCornerShape(22.dp),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        val text = inputText.trim()
                        if (text.isNotEmpty() && !isThinking) {
                            val userMsg = ChatMessage(
                                id = "usr_${System.currentTimeMillis()}",
                                sender = "user",
                                text = text,
                                timestamp = "Sasa hivi",
                                timestampMillis = System.currentTimeMillis()
                            )
                            NeuroRepository.addChatMessage(context, userMsg)
                            inputText = ""
                            isThinking = true

                            coroutineScope.launch {
                                val reply = AiAssistantService.generateResponse(text, child, language)
                                NeuroRepository.addChatMessage(context, reply)
                                isThinking = false
                            }
                        }
                    },
                    enabled = inputText.isNotBlank() && !isThinking,
                    modifier = Modifier
                        .size(46.dp)
                        .background(
                            if (inputText.isNotBlank() && !isThinking) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            CircleShape
                        )
                        .testTag("send_ai_message_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send",
                        tint = if (inputText.isNotBlank() && !isThinking) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }

    // Confirmation Dialog to Clear History
    if (showClearConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showClearConfirmDialog = false },
            title = {
                Text(
                    text = if (language == "sw") "Futa Historia ya Mazungumzo?" else "Clear Chat History?",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = if (language == "sw")
                        "Mazungumzo yote yaliyohifadhiwa yatafutwa kwenye kifaa. Hatua za ukuaji zilizokwisha rekodiwa hazitaathiriwa."
                    else
                        "All saved conversation history will be cleared on this device. Recorded child milestones will remain safe."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        NeuroRepository.clearChatHistory(context, child.name, child.ageMonths, language)
                        showClearConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AlertCoral)
                ) {
                    Text(text = if (language == "sw") "Futa Yote" else "Clear All")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showClearConfirmDialog = false }) {
                    Text(text = if (language == "sw") "Ghairi" else "Cancel")
                }
            }
        )
    }
}
