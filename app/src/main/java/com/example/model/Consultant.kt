package com.example.model

data class Consultant(
    val id: String,
    val name: String,
    val title: String,
    val specializationSwahili: String,
    val specializationEnglish: String,
    val hospitalAffiliation: String,
    val location: String, // e.g. "Dar es Salaam, Tanzania"
    val consultationFee: String, // e.g. "TZS 30,000 / KES 1,700"
    val rating: Double,
    val reviewsCount: Int,
    val yearsExperience: Int,
    val availableToday: Boolean,
    val bioSwahili: String,
    val bioEnglish: String,
    val availableTimes: List<String> = listOf("10:00 AM", "02:30 PM", "04:00 PM", "06:15 PM")
)

data class ExpertAnswer(
    val questionId: String,
    val parentQuestion: String,
    val childAge: String,
    val doctorName: String,
    val answerSwahili: String,
    val answerEnglish: String,
    val category: String,
    val date: String
)
