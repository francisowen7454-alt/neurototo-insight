package com.example.model

data class Comment(
    val id: String,
    val author: String,
    val text: String,
    val timestamp: String,
    val isExpert: Boolean = false
)

data class CommunityPost(
    val id: String,
    val authorName: String,
    val authorCity: String,
    val childAgeTag: String,
    val category: String, // e.g. "Ukuaji & Lugha", "Malezi ya Kidijitali", "Lishe"
    val title: String,
    val content: String,
    val timeAgo: String,
    var likes: Int,
    var isLikedByUser: Boolean = false,
    val comments: MutableList<Comment> = mutableListOf()
)
