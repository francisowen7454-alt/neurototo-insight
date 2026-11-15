package com.example.model

data class AdminUser(
    val id: String,
    val fullName: String,
    val emailOrPhone: String,
    val passwordHash: String,
    val passwordSalt: String,
    val role: String = "Mmiliki Mkuu (Owner)",
    val registeredDate: String = "Oktoba 2026"
)

data class AdminAuthResult(
    val success: Boolean,
    val errorMessage: String? = null,
    val isLockedOut: Boolean = false,
    val remainingLockoutSeconds: Long = 0L
)

data class AdminRegistrationResult(
    val success: Boolean,
    val errorMessage: String? = null
)

data class ArticleSchedule(
    val id: String,
    val titleSwahili: String,
    val titleEnglish: String,
    val category: ContentCategory,
    val type: ContentType,
    val scheduledDate: String, // e.g. "Jumanne, 28 Oktoba"
    val scheduledTime: String, // e.g. "09:00 Asubuhi"
    val instructorName: String,
    val summarySwahili: String,
    val summaryEnglish: String,
    var isReminderSet: Boolean = false
)

data class FaqItem(
    val id: String,
    val category: String, // e.g. "Mfumo wa Fahamu", "Malezi ya Kidijitali", "Lishe", "Malipo ya Pro", "Mawasiliano"
    val questionSwahili: String,
    val questionEnglish: String,
    val answerSwahili: String,
    val answerEnglish: String
)

data class PushNotificationItem(
    val id: String,
    val title: String,
    val body: String,
    val timestamp: String,
    val contentId: String? = null,
    var isRead: Boolean = false
)
