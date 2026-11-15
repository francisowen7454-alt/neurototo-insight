package com.example.model

data class ChildProfile(
    val id: String,
    val name: String,
    val birthDate: String,
    val ageMonths: Int,
    val gender: String, // "Mvulana" / "Msichana"
    val bloodGroup: String = "O+",
    val weightKg: Double = 11.4,
    val heightCm: Double = 84.0,
    val notes: String = "Anapenda kucheza na vitu vya rangi na kusikiliza nyimbo za watoto.",
    val isSelected: Boolean = true
)
