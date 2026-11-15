package com.example.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.Milestone
import com.example.model.MilestoneCategory
import com.example.model.MilestoneStatus

/**
 * Room database entity representing a child's developmental milestone.
 * Tracks milestone name, age group, category, clinical tips, and completion status.
 */
@Entity(tableName = "developmental_milestones")
data class DevelopmentalMilestoneEntity(
    @PrimaryKey
    val id: String,

    @ColumnInfo(name = "child_id")
    val childId: String = "child_1",

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "name_swahili")
    val nameSwahili: String,

    @ColumnInfo(name = "age_group")
    val ageGroup: String,

    @ColumnInfo(name = "category")
    val category: String = MilestoneCategory.COGNITIVE.name,

    @ColumnInfo(name = "description_english")
    val descriptionEnglish: String = "",

    @ColumnInfo(name = "description_swahili")
    val descriptionSwahili: String = "",

    @ColumnInfo(name = "tip_english")
    val tipEnglish: String = "",

    @ColumnInfo(name = "tip_swahili")
    val tipSwahili: String = "",

    @ColumnInfo(name = "is_completed")
    val isCompleted: Boolean = false,

    @ColumnInfo(name = "completion_status")
    val completionStatus: String = if (isCompleted) MilestoneStatus.ACHIEVED.name else MilestoneStatus.NOT_YET.name,

    @ColumnInfo(name = "date_logged")
    val dateLogged: String? = null,

    @ColumnInfo(name = "parent_notes")
    val parentNotes: String = "",

    @ColumnInfo(name = "expected_age_months")
    val expectedAgeMonths: String = "",

    @ColumnInfo(name = "is_custom")
    val isCustom: Boolean = false,

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
) {
    fun toDomain(): Milestone {
        val parsedCategory = runCatching { MilestoneCategory.valueOf(category) }
            .getOrDefault(MilestoneCategory.COGNITIVE)
        val parsedStatus = runCatching { MilestoneStatus.valueOf(completionStatus) }
            .getOrDefault(if (isCompleted) MilestoneStatus.ACHIEVED else MilestoneStatus.NOT_YET)

        return Milestone(
            id = id,
            ageGroup = ageGroup,
            category = parsedCategory,
            titleSwahili = nameSwahili.ifBlank { name },
            titleEnglish = name.ifBlank { nameSwahili },
            descriptionSwahili = descriptionSwahili.ifBlank { descriptionEnglish },
            descriptionEnglish = descriptionEnglish.ifBlank { descriptionSwahili },
            tipSwahili = tipSwahili.ifBlank { tipEnglish },
            tipEnglish = tipEnglish.ifBlank { tipSwahili },
            isAchieved = isCompleted,
            status = parsedStatus,
            dateLogged = dateLogged,
            parentNotes = parentNotes,
            expectedAgeMonths = expectedAgeMonths.ifBlank { ageGroup }
        )
    }

    companion object {
        fun fromDomain(
            milestone: Milestone,
            childId: String = "child_1",
            isCustom: Boolean = false
        ): DevelopmentalMilestoneEntity {
            return DevelopmentalMilestoneEntity(
                id = milestone.id,
                childId = childId,
                name = milestone.titleEnglish,
                nameSwahili = milestone.titleSwahili,
                ageGroup = milestone.ageGroup,
                category = milestone.category.name,
                descriptionEnglish = milestone.descriptionEnglish,
                descriptionSwahili = milestone.descriptionSwahili,
                tipEnglish = milestone.tipEnglish,
                tipSwahili = milestone.tipSwahili,
                isCompleted = milestone.isAchieved,
                completionStatus = milestone.status.name,
                dateLogged = milestone.dateLogged,
                parentNotes = milestone.parentNotes,
                expectedAgeMonths = milestone.expectedAgeMonths,
                isCustom = isCustom,
                updatedAt = System.currentTimeMillis()
            )
        }
    }
}
