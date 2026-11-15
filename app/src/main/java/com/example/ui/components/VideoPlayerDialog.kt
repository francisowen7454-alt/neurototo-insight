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
import com.example.model.ContentItem
import com.example.model.ContentType
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun VideoPlayerDialog(
    item: ContentItem,
    language: String,
    onDismiss: () -> Unit,
    onBookmarkToggle: () -> Unit
) {
    var isPlaying by remember { mutableStateOf(true) }
    var playbackProgress by remember { mutableFloatStateOf(0.24f) }
    var playbackSpeed by remember { mutableStateOf("1.0x") }
    var activeChapter by remember { mutableStateOf("03:15") }

    // Simulate playback timer
    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            delay(1000)
            if (playbackProgress < 0.99f) {
                playbackProgress += 0.015f
            } else {
                isPlaying = false
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_video_player")
                    ) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }

                    Text(
                        text = if (item.type == ContentType.VIDEO)
                            (if (language == "sw") "Somo la Video" else "Video Lesson")
                        else
                            (if (language == "sw") "Makala ya Afya" else "Health Article"),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )

                    IconButton(onClick = onBookmarkToggle) {
                        Icon(
                            imageVector = if (item.bookmarked) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = if (item.bookmarked) WarmAmber else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // If it's a video: Show interactive Player Box
                if (item.type == ContentType.VIDEO) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(230.dp)
                            .background(Color.Black),
                        color = Color(0xFF0F172A)
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            // Player Background Art / Indicator
                            Column(
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = Color.White.copy(alpha = 0.2f),
                                    modifier = Modifier.size(54.dp)
                                ) {
                                    IconButton(
                                        onClick = { isPlaying = !isPlaying },
                                        modifier = Modifier.testTag("play_pause_button")
                                    ) {
                                        Icon(
                                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                            contentDescription = "Play/Pause",
                                            tint = Color.White,
                                            modifier = Modifier.size(32.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = if (language == "sw") item.titleSwahili else item.titleEnglish,
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    maxLines = 1,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            // Resolution & Quality Badge
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color.Black.copy(alpha = 0.6f),
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = "1080p HD • NEUROTOTO",
                                    color = GrowthTealLight,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }

                            // Bottom Controls Bar
                            Column(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .fillMaxWidth()
                                    .background(Color.Black.copy(alpha = 0.7f))
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                // Scrubber / Slider
                                Slider(
                                    value = playbackProgress,
                                    onValueChange = { playbackProgress = it },
                                    colors = SliderDefaults.colors(
                                        thumbColor = GrowthTealLight,
                                        activeTrackColor = GrowthTealLight,
                                        inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(18.dp)
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "${(playbackProgress * 14).toInt()}:0${((playbackProgress * 14 % 1) * 60).toInt()} / 14:00",
                                            color = Color.White,
                                            fontSize = 11.sp
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            text = item.authorName,
                                            color = Color.White.copy(alpha = 0.8f),
                                            fontSize = 11.sp
                                        )
                                    }

                                    // Speed toggle button
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color.White.copy(alpha = 0.2f),
                                        modifier = Modifier.clickable {
                                            playbackSpeed = when (playbackSpeed) {
                                                "1.0x" -> "1.25x"
                                                "1.25x" -> "1.5x"
                                                else -> "1.0x"
                                            }
                                        }
                                    ) {
                                        Text(
                                            text = playbackSpeed,
                                            color = Color.White,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Body & Chapter Guide
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    // Title and Author
                    item {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer
                                ) {
                                    Text(
                                        text = if (language == "sw") item.category.swahili else item.category.english,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = item.durationText,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (language == "sw") item.titleSwahili else item.titleEnglish,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "${item.authorName} • ${item.authorRole}",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }

                    // Video Chapters
                    if (item.videoChapters.isNotEmpty()) {
                        item {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(
                                        text = if (language == "sw") "Vipindi vya Video (Chapters):" else "Video Chapters:",
                                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    item.videoChapters.forEach { chapter ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    activeChapter = chapter.timestamp
                                                    playbackProgress = when (chapter.timestamp) {
                                                        "00:00" -> 0.05f
                                                        "03:15" -> 0.25f
                                                        "07:40" -> 0.55f
                                                        else -> 0.85f
                                                    }
                                                }
                                                .padding(vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = if (activeChapter == chapter.timestamp) GrowthTeal else MaterialTheme.colorScheme.surface,
                                                modifier = Modifier.padding(end = 8.dp)
                                            ) {
                                                Text(
                                                    text = chapter.timestamp,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (activeChapter == chapter.timestamp) Color.White else MaterialTheme.colorScheme.onSurface,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                            Text(
                                                text = if (language == "sw") chapter.titleSwahili else chapter.titleEnglish,
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    fontWeight = if (activeChapter == chapter.timestamp) FontWeight.Bold else FontWeight.Normal,
                                                    color = if (activeChapter == chapter.timestamp) GrowthTeal else MaterialTheme.colorScheme.onSurface
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Key Takeaways Checklist
                    val takeaways = if (language == "sw") item.keyTakeawaysSwahili else item.keyTakeawaysEnglish
                    if (takeaways.isNotEmpty()) {
                        item {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = WarmAmberContainer,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Default.Lightbulb, contentDescription = null, tint = WarmAmber)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = if (language == "sw") "Mambo Makuu ya Kuzingatia (Takeaways):" else "Key Actionable Takeaways:",
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onTertiaryContainer
                                            )
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))
                                    takeaways.forEach { takeaway ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp),
                                            verticalAlignment = Alignment.Top
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                tint = WarmAmber,
                                                modifier = Modifier
                                                    .size(16.dp)
                                                    .padding(top = 2.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = takeaway,
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                                                    fontSize = 12.sp
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // In-depth text
                    item {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surface,
                            tonalElevation = 1.dp,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = if (language == "sw") "Maelezo ya Kina ya Somo:" else "Detailed Lesson Notes:",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = if (language == "sw") item.fullBodySwahili else item.fullBodyEnglish,
                                    style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
