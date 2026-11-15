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
import androidx.compose.ui.window.Dialog
import com.example.model.CommunityPost
import com.example.ui.theme.*

@Composable
fun CommunityScreen(
    language: String,
    posts: List<CommunityPost>,
    onLikePost: (String) -> Unit,
    onAddComment: (String, String) -> Unit,
    onCreatePost: (String, String, String, String) -> Unit
) {
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var showCreateDialog by remember { mutableStateOf(false) }
    var activeCommentingPostId by remember { mutableStateOf<String?>(null) }
    var commentInput by remember { mutableStateOf("") }

    val categories = listOf("Zote", "Malezi ya Kidijitali", "Ukuaji & Lugha", "Lishe ya Asili", "Afya ya Fahamu")

    Box(modifier = Modifier.fillMaxSize().testTag("community_screen")) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Banner
            item {
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
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(imageVector = Icons.Default.Groups, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (language == "sw") "Jumuiya ya Wazazi wa Afrika Mashariki" else "East Africa Parents Community",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            )
                            Text(
                                text = if (language == "sw")
                                    "Badilishana uzoefu wa kulea, kuuliza maswali, na kujifunza pamoja."
                                else
                                    "Exchange real-world childcare stories, advice, and tips.",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp)
                            )
                        }
                    }
                }
            }

            // Categories Filter
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(categories) { cat ->
                        val isSelected = (cat == "Zote" && selectedCategory == null) || selectedCategory == cat
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                selectedCategory = if (cat == "Zote") null else cat
                            },
                            label = { Text(cat, fontSize = 12.sp) }
                        )
                    }
                }
            }

            // Posts List
            val filteredPosts = posts.filter {
                selectedCategory == null || it.category.contains(selectedCategory!!, ignoreCase = true)
            }

            items(filteredPosts) { post ->
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 2.dp,
                    shadowElevation = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
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
                                        .background(GrowthTealContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = post.authorName.firstOrNull()?.toString() ?: "W",
                                        fontWeight = FontWeight.Bold,
                                        color = GrowthTeal
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = post.authorName,
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        text = "${post.authorCity} • ${post.childAgeTag}",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Text(
                                    text = post.category,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = post.title,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = post.content,
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp, lineHeight = 19.sp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Like and Comments row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = { onLikePost(post.id) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = if (post.isLikedByUser) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                                        contentDescription = "Like",
                                        tint = if (post.isLikedByUser) AlertCoral else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Text(
                                    text = "${post.likes}",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp)
                                )

                                Spacer(modifier = Modifier.width(16.dp))

                                IconButton(
                                    onClick = {
                                        activeCommentingPostId = if (activeCommentingPostId == post.id) null else post.id
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.ChatBubbleOutline,
                                        contentDescription = "Comment",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Text(
                                    text = "${post.comments.size}",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp)
                                )
                            }

                            Text(
                                text = post.timeAgo,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 10.sp
                                )
                            )
                        }

                        // Comments expansion
                        if (post.comments.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                    .padding(10.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                post.comments.forEach { comment ->
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = comment.author,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp,
                                                color = if (comment.isExpert) GrowthTeal else MaterialTheme.colorScheme.onSurface
                                            )
                                            if (comment.isExpert) {
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Surface(
                                                    shape = RoundedCornerShape(4.dp),
                                                    color = GrowthTealContainer
                                                ) {
                                                    Text(
                                                        text = "DKT. BINGWA",
                                                        fontSize = 8.sp,
                                                        color = GrowthTeal,
                                                        fontWeight = FontWeight.Bold,
                                                        modifier = Modifier.padding(horizontal = 3.dp, vertical = 1.dp)
                                                    )
                                                }
                                            }
                                        }
                                        Text(text = comment.text, fontSize = 11.sp, lineHeight = 16.sp)
                                    }
                                }
                            }
                        }

                        // Input to add comment
                        if (activeCommentingPostId == post.id) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = commentInput,
                                    onValueChange = { commentInput = it },
                                    placeholder = { Text("Andika jibu lako...", fontSize = 11.sp) },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(14.dp),
                                    singleLine = true
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                IconButton(
                                    onClick = {
                                        if (commentInput.isNotBlank()) {
                                            onAddComment(post.id, commentInput.trim())
                                            commentInput = ""
                                            activeCommentingPostId = null
                                        }
                                    },
                                    modifier = Modifier.size(36.dp).background(MaterialTheme.colorScheme.primary, CircleShape)
                                ) {
                                    Icon(imageVector = Icons.Default.Send, contentDescription = "Post", tint = Color.White, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        }

        // Floating Action Button to create post
        ExtendedFloatingActionButton(
            onClick = { showCreateDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 85.dp)
                .testTag("create_community_post_fab"),
            icon = { Icon(Icons.Default.Add, contentDescription = null) },
            text = { Text(if (language == "sw") "Shiriki Uzoefu" else "Post Story") },
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White
        )
    }

    // Create Post Dialog
    if (showCreateDialog) {
        var title by remember { mutableStateOf("") }
        var content by remember { mutableStateOf("") }
        var category by remember { mutableStateOf("Malezi ya Kidijitali") }
        var childAge by remember { mutableStateOf("Mtoto wa Mwaka 1") }

        Dialog(onDismissRequest = { showCreateDialog = false }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.94f)
                    .clip(RoundedCornerShape(20.dp)),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = if (language == "sw") "Andika Uzoefu au Swali lako" else "Share with Parents",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text(if (language == "sw") "Kichwa cha Habari" else "Title") },
                        modifier = Modifier.fillMaxWidth().testTag("new_post_title_input"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = content,
                        onValueChange = { content = it },
                        label = { Text(if (language == "sw") "Uzoefu wako kwa kina..." else "Your experience...") },
                        modifier = Modifier.fillMaxWidth().testTag("new_post_content_input"),
                        minLines = 4,
                        maxLines = 6,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            if (title.isNotBlank() && content.isNotBlank()) {
                                onCreatePost(title, content, category, childAge)
                                showCreateDialog = false
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp).testTag("publish_post_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(text = if (language == "sw") "Chapisha kwa Wazazi" else "Publish to Community")
                    }
                }
            }
        }
    }
}
