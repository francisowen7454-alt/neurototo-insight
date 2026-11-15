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
import com.example.model.ArticleSchedule
import com.example.model.ContentCategory
import com.example.model.ContentItem
import com.example.model.ContentType
import com.example.model.FaqItem
import com.example.ui.components.ArticleScheduleView
import com.example.ui.components.FaqSection
import com.example.ui.theme.*

@Composable
fun LearningHubScreen(
    language: String,
    contentItems: List<ContentItem>,
    schedules: List<ArticleSchedule>,
    faqs: List<FaqItem>,
    isProUser: Boolean,
    onSelectItem: (ContentItem) -> Unit,
    onToggleBookmark: (String) -> Unit,
    onToggleReminder: (String) -> Unit,
    onProClick: () -> Unit
) {
    var selectedTopTab by remember { mutableStateOf(0) } // 0: Content, 1: Schedule, 2: FAQs
    var searchQuery by remember { mutableStateOf("") }
    var selectedContentType by remember { mutableStateOf<ContentType?>(null) } // null = all, VIDEO, ARTICLE
    var selectedCategory by remember { mutableStateOf<ContentCategory?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("learning_hub_screen")
    ) {
        // Top Main Tabs
        TabRow(
            selectedTabIndex = selectedTopTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            Tab(
                selected = selectedTopTab == 0,
                onClick = { selectedTopTab = 0 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.VideoLibrary, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (language == "sw") "Masomo & Video" else "Lessons & Videos",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            )
            Tab(
                selected = selectedTopTab == 1,
                onClick = { selectedTopTab = 1 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (language == "sw") "Ratiba ya Masomo" else "Schedule",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            )
            Tab(
                selected = selectedTopTab == 2,
                onClick = { selectedTopTab = 2 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Quiz, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "FAQs",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            )
        }

        when (selectedTopTab) {
            0 -> {
                // CATEGORISED ARTICLES & VIDEOS
                Column(modifier = Modifier.fillMaxSize()) {
                    // Search & Filter Header
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = {
                                Text(
                                    text = if (language == "sw") "Tafuta masomo, simu, lishe ya ubongo..." else "Search lessons, screen time, nutrition...",
                                    fontSize = 13.sp
                                )
                            },
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { searchQuery = "" }) {
                                        Icon(imageVector = Icons.Default.Close, contentDescription = "Clear")
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("learning_search_input"),
                            shape = RoundedCornerShape(14.dp),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Format Filter Pills (All / Video / Articles)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                selected = selectedContentType == null,
                                onClick = { selectedContentType = null },
                                label = { Text(if (language == "sw") "Maudhui Yote" else "All Lessons") }
                            )
                            FilterChip(
                                selected = selectedContentType == ContentType.VIDEO,
                                onClick = { selectedContentType = ContentType.VIDEO },
                                leadingIcon = {
                                    Icon(imageVector = Icons.Default.PlayCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                                },
                                label = { Text(if (language == "sw") "Video za App" else "Videos") }
                            )
                            FilterChip(
                                selected = selectedContentType == ContentType.ARTICLE,
                                onClick = { selectedContentType = ContentType.ARTICLE },
                                leadingIcon = {
                                    Icon(imageVector = Icons.Default.Article, contentDescription = null, modifier = Modifier.size(16.dp))
                                },
                                label = { Text(if (language == "sw") "Makala" else "Articles") }
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Category Horizontal Scroll
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            item {
                                FilterChip(
                                    selected = selectedCategory == null,
                                    onClick = { selectedCategory = null },
                                    label = { Text(if (language == "sw") "Mada Zote" else "All Topics") }
                                )
                            }
                            items(ContentCategory.values()) { category ->
                                FilterChip(
                                    selected = selectedCategory == category,
                                    onClick = { selectedCategory = category },
                                    label = { Text(if (language == "sw") category.swahili else category.english) }
                                )
                            }
                        }
                    }

                    // Content List
                    val filteredList = contentItems.filter { item ->
                        val matchesType = selectedContentType == null || item.type == selectedContentType
                        val matchesCat = selectedCategory == null || item.category == selectedCategory
                        val matchesQuery = searchQuery.isBlank() ||
                                item.titleSwahili.contains(searchQuery, ignoreCase = true) ||
                                item.titleEnglish.contains(searchQuery, ignoreCase = true) ||
                                item.summarySwahili.contains(searchQuery, ignoreCase = true)
                        matchesType && matchesCat && matchesQuery
                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 90.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(filteredList) { item ->
                            Surface(
                                shape = RoundedCornerShape(18.dp),
                                color = MaterialTheme.colorScheme.surface,
                                tonalElevation = 2.dp,
                                shadowElevation = 1.dp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        if (item.isPro && !isProUser) {
                                            onProClick()
                                        } else {
                                            onSelectItem(item)
                                        }
                                    }
                                    .testTag("content_item_${item.id}")
                            ) {
                                Column {
                                    // Top media preview for video
                                    if (item.type == ContentType.VIDEO) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(140.dp)
                                                .background(Color(0xFF0F172A))
                                        ) {
                                            Column(
                                                modifier = Modifier
                                                    .align(Alignment.Center)
                                                    .padding(12.dp),
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Surface(
                                                    shape = CircleShape,
                                                    color = Color.White.copy(alpha = 0.25f),
                                                    modifier = Modifier.size(46.dp)
                                                ) {
                                                    Box(contentAlignment = Alignment.Center) {
                                                        Icon(
                                                            imageVector = Icons.Default.PlayArrow,
                                                            contentDescription = "Play",
                                                            tint = Color.White,
                                                            modifier = Modifier.size(30.dp)
                                                        )
                                                    }
                                                }
                                            }

                                            // Badges
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(10.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Surface(
                                                    shape = RoundedCornerShape(6.dp),
                                                    color = GrowthTeal
                                                ) {
                                                    Text(
                                                        text = if (language == "sw") item.category.swahili else item.category.english,
                                                        color = Color.White,
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                    )
                                                }

                                                if (item.isPro) {
                                                    Surface(
                                                        shape = RoundedCornerShape(6.dp),
                                                        color = AlertCoral
                                                    ) {
                                                        Row(
                                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            Icon(
                                                                imageVector = Icons.Default.Lock,
                                                                contentDescription = null,
                                                                tint = Color.White,
                                                                modifier = Modifier.size(10.dp)
                                                            )
                                                            Spacer(modifier = Modifier.width(3.dp))
                                                            Text(
                                                                text = "PRO",
                                                                color = Color.White,
                                                                fontSize = 9.sp,
                                                                fontWeight = FontWeight.Bold
                                                            )
                                                        }
                                                    }
                                                }
                                            }

                                            // Bottom duration
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = Color.Black.copy(alpha = 0.7f),
                                                modifier = Modifier
                                                    .align(Alignment.BottomEnd)
                                                    .padding(8.dp)
                                            ) {
                                                Text(
                                                    text = item.durationText,
                                                    color = Color.White,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                    }

                                    // Text content
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        if (item.type == ContentType.ARTICLE) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
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
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                    )
                                                }

                                                if (item.isPro) {
                                                    Surface(
                                                        shape = RoundedCornerShape(6.dp),
                                                        color = AlertCoralContainer
                                                    ) {
                                                        Text(
                                                            text = "PRO",
                                                            color = AlertCoral,
                                                            fontSize = 9.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                        )
                                                    }
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(6.dp))
                                        }

                                        Text(
                                            text = if (language == "sw") item.titleSwahili else item.titleEnglish,
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                        )

                                        Spacer(modifier = Modifier.height(6.dp))

                                        Text(
                                            text = if (language == "sw") item.summarySwahili else item.summaryEnglish,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                fontSize = 12.sp,
                                                lineHeight = 17.sp
                                            ),
                                            maxLines = 3
                                        )

                                        Spacer(modifier = Modifier.height(10.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "${item.authorName} • ${item.authorRole.split(" ").firstOrNull() ?: ""}",
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    color = MaterialTheme.colorScheme.primary,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Medium
                                                )
                                            )

                                            IconButton(
                                                onClick = { onToggleBookmark(item.id) },
                                                modifier = Modifier.size(24.dp)
                                            ) {
                                                Icon(
                                                    imageVector = if (item.bookmarked) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
                                                    contentDescription = "Bookmark",
                                                    tint = if (item.bookmarked) WarmAmber else MaterialTheme.colorScheme.onSurfaceVariant,
                                                    modifier = Modifier.size(20.dp)
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

            1 -> {
                // PUBLISHING SCHEDULE VIEW
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 90.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        ArticleScheduleView(
                            language = language,
                            schedules = schedules,
                            onToggleReminder = onToggleReminder
                        )
                    }
                }
            }

            2 -> {
                // FAQS VIEW
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 90.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        FaqSection(
                            language = language,
                            faqs = faqs
                        )
                    }
                }
            }
        }
    }
}
