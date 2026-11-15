package com.example.model

enum class ContentCategory(val swahili: String, val english: String) {
    NEURO("Mfumo wa Fahamu", "Neurodevelopment"),
    NUTRITION("Lishe ya Asili", "Nutrition & Brain Foods"),
    DIGITAL_PARENTING("Malezi ya Kidijitali", "Digital Parenting & Screens"),
    EMOTIONAL("Elimu ya Kihisia", "Emotional & Behavioral"),
    GENERAL_HEALTH("Afya ya Mtoto", "Child Health & Wellness")
}

enum class ContentType {
    ARTICLE,
    VIDEO
}

data class VideoChapter(
    val timestamp: String,
    val titleSwahili: String,
    val titleEnglish: String
)

data class ContentItem(
    val id: String,
    val type: ContentType,
    val category: ContentCategory,
    val titleSwahili: String,
    val titleEnglish: String,
    val summarySwahili: String,
    val summaryEnglish: String,
    val fullBodySwahili: String,
    val fullBodyEnglish: String,
    val authorName: String,
    val authorRole: String,
    val durationText: String, // e.g., "7 min read" or "12 min video"
    val isPro: Boolean = false,
    val viewsCount: Int = 1240,
    val bookmarked: Boolean = false,
    val videoChapters: List<VideoChapter> = emptyList(),
    val keyTakeawaysSwahili: List<String> = emptyList(),
    val keyTakeawaysEnglish: List<String> = emptyList()
)
