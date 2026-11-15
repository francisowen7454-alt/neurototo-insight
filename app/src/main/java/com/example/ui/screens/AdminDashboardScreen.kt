package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.window.Dialog
import com.example.model.AdminAnalytics
import com.example.model.ContentCategory
import com.example.model.ContentItem
import com.example.model.ContentType
import com.example.ui.theme.*

@Composable
fun AdminDashboardScreen(
    language: String,
    analytics: AdminAnalytics,
    adminUser: com.example.model.AdminUser? = null,
    onAddNewContent: (ContentItem) -> Unit,
    onAddSchedule: (com.example.model.ArticleSchedule) -> Unit = {},
    onSendNotification: (String, String) -> Unit = { _, _ -> },
    onLogout: () -> Unit = {},
    onCloseAdmin: () -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var showScheduleDialog by remember { mutableStateOf(false) }
    var showBroadcastDialog by remember { mutableStateOf(false) }
    var notificationSentMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("admin_dashboard_screen")
    ) {
        // Admin Top Banner
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(WarmAmber),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.AdminPanelSettings, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (language == "sw") "Dashibodi ya Mmiliki (Admin Panel)" else "NeuroToto Insight Admin",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = if (adminUser != null)
                                "${adminUser.fullName} • ${adminUser.role}"
                            else if (language == "sw")
                                "Francis Owen • Mmiliki Mkuu (Owner)"
                            else
                                "Francis Owen • Lead Owner",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedButton(
                        onClick = onLogout,
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(text = if (language == "sw") "Ondoka" else "Logout", fontSize = 11.sp)
                    }

                    Button(
                        onClick = onCloseAdmin,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(text = if (language == "sw") "Rudi kwa Mzazi" else "Exit", fontSize = 11.sp)
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Analytics Grid
            item {
                Text(
                    text = if (language == "sw") "Takwimu za Moja kwa Moja (Live Metrics):" else "Live Platform Metrics:",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Total Parents
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surface,
                        tonalElevation = 2.dp,
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Icon(imageVector = Icons.Default.People, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = "18,450", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                            Text(
                                text = if (language == "sw") "Wazazi Waliojiunga" else "Total Parents",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                        }
                    }

                    // Active Subscribers
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surface,
                        tonalElevation = 2.dp,
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Icon(imageVector = Icons.Default.Stars, contentDescription = null, tint = GrowthTeal)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = "7,820", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = GrowthTeal))
                            Text(
                                text = if (language == "sw") "Wasajiliwa wa PRO" else "Active PRO Users",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Mobile Revenue
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surface,
                        tonalElevation = 2.dp,
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Icon(imageVector = Icons.Default.Payments, contentDescription = null, tint = WarmAmber)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = "TZS 117.3M", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = WarmAmber))
                            Text(
                                text = if (language == "sw") "Mapato ya Simu" else "Mobile Money Rev",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                        }
                    }

                    // Video Watches
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surface,
                        tonalElevation = 2.dp,
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Icon(imageVector = Icons.Default.PlayCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = "45,910", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                            Text(
                                text = if (language == "sw") "Video Zilizotazamwa" else "Video Plays",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                        }
                    }
                }
            }

            // Quick Actions: Content & Notification Management
            item {
                Text(
                    text = if (language == "sw") "Usimamizi wa Maudhui, Ratiba & Arifa za Wazazi:" else "Content, Schedule & Notification Controls:",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(10.dp))

                // Action 1: Add New Video / Article
                Button(
                    onClick = { showAddDialog = true },
                    modifier = Modifier.fillMaxWidth().height(48.dp).testTag("admin_add_content_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(imageVector = Icons.Default.PostAdd, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (language == "sw") "Chapisha Somo / Video Mpya Moja kwa Moja" else "Publish New Lesson / Video Directly",
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Action 2: Schedule an Upcoming Lesson
                OutlinedButton(
                    onClick = { showScheduleDialog = true },
                    modifier = Modifier.fillMaxWidth().height(48.dp).testTag("admin_schedule_lesson_button"),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (language == "sw") "Panga Ratiba ya Somo Linalokuja (Schedule)" else "Schedule Upcoming Lesson",
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Action 3: Broadcast Push Notification to Parents
                Button(
                    onClick = { showBroadcastDialog = true },
                    modifier = Modifier.fillMaxWidth().height(48.dp).testTag("admin_broadcast_notification_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = WarmAmber)
                ) {
                    Icon(imageVector = Icons.Default.Campaign, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (language == "sw") "Tuma Push Notification kwa Wazazi Wote" else "Broadcast Push Notification to All Parents",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                if (notificationSentMessage != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = GrowthTealContainer,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = notificationSentMessage!!,
                            color = GrowthTeal,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            }
        }
    }

    // Add Content Dialog
    if (showAddDialog) {
        var newTitle by remember { mutableStateOf("") }
        var newSummary by remember { mutableStateOf("") }
        var newBody by remember { mutableStateOf("") }
        var newAuthor by remember { mutableStateOf("Dkt. Amani Mwenda") }
        var isVideo by remember { mutableStateOf(true) }
        var selectedCat by remember { mutableStateOf(ContentCategory.NEURO) }

        Dialog(onDismissRequest = { showAddDialog = false }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .fillMaxHeight(0.85f)
                    .clip(RoundedCornerShape(20.dp)),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(modifier = Modifier.fillMaxSize().padding(18.dp)) {
                    Text(
                        text = if (language == "sw") "Ongeza Maudhui Mapya ya Afya & Malezi" else "Publish New Child Content",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    LazyColumn(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                FilterChip(
                                    selected = isVideo,
                                    onClick = { isVideo = true },
                                    label = { Text("Video ya App") }
                                )
                                FilterChip(
                                    selected = !isVideo,
                                    onClick = { isVideo = false },
                                    label = { Text("Makala ya Kusoma") }
                                )
                            }
                        }

                        item {
                            OutlinedTextField(
                                value = newTitle,
                                onValueChange = { newTitle = it },
                                label = { Text("Kichwa cha Somo") },
                                modifier = Modifier.fillMaxWidth().testTag("admin_content_title_input"),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true
                            )
                        }

                        item {
                            OutlinedTextField(
                                value = newSummary,
                                onValueChange = { newSummary = it },
                                label = { Text("Muhtasari Mfupi") },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 2,
                                shape = RoundedCornerShape(12.dp)
                            )
                        }

                        item {
                            OutlinedTextField(
                                value = newBody,
                                onValueChange = { newBody = it },
                                label = { Text("Maelezo Kamili ya Somo") },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 4,
                                maxLines = 8,
                                shape = RoundedCornerShape(12.dp)
                            )
                        }

                        item {
                            OutlinedTextField(
                                value = newAuthor,
                                onValueChange = { newAuthor = it },
                                label = { Text("Jina la Daktari / Mwandishi") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            if (newTitle.isNotBlank()) {
                                onAddNewContent(
                                    ContentItem(
                                        id = "custom_${System.currentTimeMillis()}",
                                        type = if (isVideo) ContentType.VIDEO else ContentType.ARTICLE,
                                        category = selectedCat,
                                        titleSwahili = newTitle,
                                        titleEnglish = newTitle,
                                        summarySwahili = newSummary.ifBlank { "Somo jipya la afya ya mtoto lililochapishwa na NeuroToto Insight." },
                                        summaryEnglish = newSummary.ifBlank { "New pediatric neurodevelopment lesson." },
                                        fullBodySwahili = newBody.ifBlank { "Maelezo ya kina ya somo hili yatapatikana hivi karibuni." },
                                        fullBodyEnglish = newBody.ifBlank { "Detailed clinical notes available shortly." },
                                        authorName = newAuthor,
                                        authorRole = "Daktari Mtaalamu",
                                        durationText = if (isVideo) "Dakika 10 • Video ya Ndani ya App" else "Dakika 5 • Makala ya Kusoma",
                                        isPro = false
                                    )
                                )
                                showAddDialog = false
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp).testTag("admin_submit_content_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Chapisha Maudhui Haya")
                    }
                }
            }
        }
    }

    // Schedule Dialog
    if (showScheduleDialog) {
        var schedTitle by remember { mutableStateOf("") }
        var schedDate by remember { mutableStateOf("Kesho saa 2:00 Asubuhi") }
        var schedDoctor by remember { mutableStateOf("Dkt. Joyce Massawe (Pediatric Neurologist)") }
        var isSchedVideo by remember { mutableStateOf(true) }

        Dialog(onDismissRequest = { showScheduleDialog = false }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .clip(RoundedCornerShape(20.dp)),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = if (language == "sw") "Panga Ratiba ya Somo Linalokuja" else "Schedule Upcoming Lesson",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = isSchedVideo,
                            onClick = { isSchedVideo = true },
                            label = { Text("Video") }
                        )
                        FilterChip(
                            selected = !isSchedVideo,
                            onClick = { isSchedVideo = false },
                            label = { Text("Makala") }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = schedTitle,
                        onValueChange = { schedTitle = it },
                        label = { Text("Kichwa cha Somo Linalokuja") },
                        modifier = Modifier.fillMaxWidth().testTag("schedule_title_input"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = schedDate,
                        onValueChange = { schedDate = it },
                        label = { Text("Tarehe & Saa ya Kurushwa") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = schedDoctor,
                        onValueChange = { schedDoctor = it },
                        label = { Text("Daktari Mwasilishaji") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (schedTitle.isNotBlank()) {
                                onAddSchedule(
                                    com.example.model.ArticleSchedule(
                                        id = "sched_${System.currentTimeMillis()}",
                                        titleSwahili = schedTitle,
                                        titleEnglish = schedTitle,
                                        category = ContentCategory.NEURO,
                                        type = if (isSchedVideo) ContentType.VIDEO else ContentType.ARTICLE,
                                        scheduledDate = schedDate,
                                        scheduledTime = "09:00 Asubuhi",
                                        instructorName = schedDoctor,
                                        summarySwahili = "Somo maalum la video kuhusu afya na ukuaji wa mtoto.",
                                        summaryEnglish = "Special neurodevelopment lesson scheduled for parents.",
                                        isReminderSet = true
                                    )
                                )
                                notificationSentMessage = if (language == "sw")
                                    "Ratiba imepangwa na Push Notification imetumwa kwa wazazi!"
                                else
                                    "Schedule added and push notification sent to parents!"
                                showScheduleDialog = false
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp).testTag("submit_schedule_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(if (language == "sw") "Hifadhi & Tangaza Ratiba" else "Save & Broadcast Schedule")
                    }
                }
            }
        }
    }

    // Broadcast Push Notification Dialog
    if (showBroadcastDialog) {
        var notifTitle by remember { mutableStateOf("Somo Jipya la Video Limewasili!") }
        var notifBody by remember { mutableStateOf("Wazazi, somo jipya kuhusu afya ya mfumo wa fahamu na mazoezi ya mtoto limewekwa kwenye App!") }

        Dialog(onDismissRequest = { showBroadcastDialog = false }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .clip(RoundedCornerShape(20.dp)),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Campaign, contentDescription = null, tint = WarmAmber)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (language == "sw") "Tuma Push Notification kwa Wazazi" else "Broadcast Push Notification",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = if (language == "sw")
                            "Arifa hii itaonekana papo hapo juu ya skrini kwa wazazi wote wanaotumia NeuroToto Insight."
                        else
                            "This notification will pop up immediately for all parents on NeuroToto Insight.",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = notifTitle,
                        onValueChange = { notifTitle = it },
                        label = { Text("Kichwa cha Arifa (Notification Title)") },
                        modifier = Modifier.fillMaxWidth().testTag("push_title_input"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = notifBody,
                        onValueChange = { notifBody = it },
                        label = { Text("Ujumbe wa Arifa (Notification Body)") },
                        modifier = Modifier.fillMaxWidth().testTag("push_body_input"),
                        minLines = 3,
                        maxLines = 5,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            if (notifTitle.isNotBlank() && notifBody.isNotBlank()) {
                                onSendNotification(notifTitle, notifBody)
                                notificationSentMessage = if (language == "sw")
                                    "Push Notification imetumwa kwa mafanikio kwa wazazi wote!"
                                else
                                    "Push notification dispatched successfully to all parents!"
                                showBroadcastDialog = false
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp).testTag("confirm_broadcast_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = WarmAmber)
                    ) {
                        Text(
                            text = if (language == "sw") "Tuma Sasa kwa Wazazi Wote" else "Send Now to All Parents",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
