package com.example.model

/**
 * ChatMessage represents a single interaction in the developmental AI consultation.
 * Supports persistent history and direct linkage to child growth milestones.
 */
data class ChatMessage(
    val id: String,
    val sender: String, // "user" or "toto_ai"
    val text: String,
    val timestamp: String,
    val timestampMillis: Long = System.currentTimeMillis(),
    val isWarning: Boolean = false,
    val recommendedDoctorCategory: String? = null,
    val isAiGenerated: Boolean = true,
    val relatedMilestoneCategory: MilestoneCategory? = null,
    val relatedMilestoneId: String? = null,
    val relatedMilestoneTitle: String? = null,
    val developmentalTipSwahili: String? = null,
    val developmentalTipEnglish: String? = null,
    val suggestedObservationNote: String? = null,
    val isMilestoneLogged: Boolean = false
)
