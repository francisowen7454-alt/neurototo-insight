package com.example.model

enum class MilestoneCategory(val swahiliName: String, val englishName: String) {
    MOTOR("Mwendo & Misuli", "Gross & Fine Motor"),
    COGNITIVE("Utambuzi & Akili", "Cognitive & Brain"),
    SPEECH("Lugha & Mawasiliano", "Speech & Language"),
    EMOTIONAL("Kihisia & Kijamii", "Social & Emotional"),
    SENSORY("Milango ya Fahamu", "Sensory & Reflexes")
}

enum class MilestoneStatus(val swahiliLabel: String, val englishLabel: String) {
    NOT_YET("Bado", "Not Yet"),
    EMERGING("Inachipukia / Anajaribu", "Emerging / Practicing"),
    ACHIEVED("Imekamilika", "Achieved")
}

data class Milestone(
    val id: String,
    val ageGroup: String, // e.g. "0-3 Miezi", "4-6 Miezi", "7-9 Miezi", "10-12 Miezi", "1-2 Miaka", "3-5 Miaka"
    val category: MilestoneCategory,
    val titleSwahili: String,
    val titleEnglish: String,
    val descriptionSwahili: String,
    val descriptionEnglish: String,
    val tipSwahili: String,
    val tipEnglish: String,
    var isAchieved: Boolean = false,
    val status: MilestoneStatus = if (isAchieved) MilestoneStatus.ACHIEVED else MilestoneStatus.NOT_YET,
    val dateLogged: String? = null,
    val parentNotes: String = "",
    val expectedAgeMonths: String = "",
    val pediatricGuideline: String = "WHO & CDC Pediatric Milestone Surveillance",
    val brainStimulationTipSwahili: String = "",
    val brainStimulationTipEnglish: String = ""
)

data class MilestoneObservation(
    val id: String,
    val milestoneId: String? = null,
    val milestoneTitle: String,
    val childAgeMonths: Int,
    val date: String,
    val category: MilestoneCategory,
    val observationNote: String,
    val status: MilestoneStatus = MilestoneStatus.ACHIEVED,
    val celebrationBadge: String = "🌟"
)

data class RedFlag(
    val id: String,
    val ageGroup: String,
    val warningSwahili: String,
    val warningEnglish: String,
    val reasonSwahili: String,
    val reasonEnglish: String,
    val recommendedActionSwahili: String,
    val recommendedActionEnglish: String
)

data class PediatricGuidelineInfo(
    val titleSwahili: String,
    val titleEnglish: String,
    val summarySwahili: String,
    val summaryEnglish: String,
    val sourceOrganization: String = "AAP & CDC Milestone Moments / WHO Standards",
    val keySurveillanceCriteriaSwahili: List<String>,
    val keySurveillanceCriteriaEnglish: List<String>
)
