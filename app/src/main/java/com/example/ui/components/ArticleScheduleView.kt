package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ArticleSchedule
import com.example.model.ContentType
import com.example.ui.theme.GrowthTeal
import com.example.ui.theme.GrowthTealContainer
import com.example.ui.theme.WarmAmber

@Composable
fun ArticleScheduleView(
    language: String,
    schedules: List<ArticleSchedule>,
    onToggleReminder: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth().testTag("article_schedule_view")) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(38.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = if (language == "sw") "Ratiba ya Masomo na Video Zinazokuja" else "Upcoming Lesson Publishing Schedule",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    )
                    Text(
                        text = if (language == "sw")
                            "Weka kikumbusho kupokea arifa pindi somo linapochapishwa."
                        else
                            "Set alerts to get notified instantly once lessons air.",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            schedules.forEach { item ->
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 2.dp,
                    border = if (item.isReminderSet)
                        androidx.compose.foundation.BorderStroke(1.5.dp, GrowthTeal.copy(alpha = 0.6f))
                    else null,
                    modifier = Modifier.fillMaxWidth().testTag("schedule_item_${item.id}")
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (item.type == ContentType.VIDEO) MaterialTheme.colorScheme.primaryContainer else GrowthTealContainer
                                ) {
                                    Text(
                                        text = if (item.type == ContentType.VIDEO)
                                            (if (language == "sw") "VIDEO YA APP" else "VIDEO")
                                        else
                                            (if (language == "sw") "MAKALA YA KUSOMA" else "ARTICLE"),
                                        color = if (item.type == ContentType.VIDEO) MaterialTheme.colorScheme.primary else GrowthTeal,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant
                                ) {
                                    Text(
                                        text = "${item.scheduledDate} • ${item.scheduledTime}",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            // Reminder toggle button
                            IconButton(
                                onClick = { onToggleReminder(item.id) },
                                modifier = Modifier.size(32.dp).testTag("reminder_btn_${item.id}")
                            ) {
                                Icon(
                                    imageVector = if (item.isReminderSet) Icons.Default.NotificationsActive else Icons.Outlined.NotificationsNone,
                                    contentDescription = "Set Reminder",
                                    tint = if (item.isReminderSet) GrowthTeal else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = if (language == "sw") item.titleSwahili else item.titleEnglish,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = if (language == "sw") item.summarySwahili else item.summaryEnglish,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Mwalimu: ${item.instructorName}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )

                            if (item.isReminderSet) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = GrowthTealContainer
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = GrowthTeal, modifier = Modifier.size(10.dp))
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text(
                                            text = if (language == "sw") "Kikumbusho Kimewashwa" else "Alert Active",
                                            color = GrowthTeal,
                                            fontSize = 9.sp,
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
}
