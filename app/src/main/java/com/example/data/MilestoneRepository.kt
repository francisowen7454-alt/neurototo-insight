package com.example.data

import com.example.data.local.DevelopmentalMilestoneDao
import com.example.data.local.DevelopmentalMilestoneEntity
import com.example.model.Milestone
import com.example.model.MilestoneCategory
import com.example.model.MilestoneStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Repository mediating access to the Room developmental_milestones table.
 * Follows the Repository Pattern to isolate SQLite/Room persistence from ViewModels and Compose UI.
 */
class MilestoneRepository(
    private val milestoneDao: DevelopmentalMilestoneDao
) {
    val allMilestones: Flow<List<Milestone>> = milestoneDao.getAllMilestones()
        .map { entities -> entities.map { it.toDomain() } }

    fun getMilestonesByAgeGroup(ageGroup: String): Flow<List<Milestone>> {
        return milestoneDao.getMilestonesByAgeGroup(ageGroup)
            .map { entities -> entities.map { it.toDomain() } }
    }

    fun getMilestonesByCompletionStatus(isCompleted: Boolean): Flow<List<Milestone>> {
        return milestoneDao.getMilestonesByCompletionStatus(isCompleted)
            .map { entities -> entities.map { it.toDomain() } }
    }

    suspend fun seedInitialMilestonesIfEmpty(
        defaultMilestones: List<Milestone> = NeuroRepository.getDefaultMilestones()
    ) {
        if (milestoneDao.getMilestoneCount() == 0) {
            val entities = defaultMilestones.map { milestone ->
                DevelopmentalMilestoneEntity.fromDomain(
                    milestone = milestone,
                    childId = "child_1",
                    isCustom = false
                )
            }
            milestoneDao.insertAll(entities)
        }
    }

    suspend fun addMilestone(
        name: String,
        nameSwahili: String = name,
        ageGroup: String,
        category: MilestoneCategory = MilestoneCategory.COGNITIVE,
        description: String = "",
        tip: String = "",
        isCompleted: Boolean = false,
        childId: String = "child_1"
    ): Milestone {
        val status = if (isCompleted) MilestoneStatus.ACHIEVED else MilestoneStatus.NOT_YET
        val entity = DevelopmentalMilestoneEntity(
            id = "m_custom_${System.currentTimeMillis()}_${(100..999).random()}",
            childId = childId,
            name = name.trim(),
            nameSwahili = nameSwahili.trim().ifBlank { name.trim() },
            ageGroup = ageGroup.trim(),
            category = category.name,
            descriptionEnglish = description.trim().ifBlank { "Parent-tracked developmental milestone for $ageGroup." },
            descriptionSwahili = description.trim().ifBlank { "Hatua ya ukuaji iliyoongezwa na mzazi kwa umri wa $ageGroup." },
            tipEnglish = tip.trim().ifBlank { "Celebrate progress and observe daily practice during natural play." },
            tipSwahili = tip.trim().ifBlank { "Mtie moyo mtoto kwa upendo na fuatilia maendeleo yake kila siku." },
            isCompleted = isCompleted,
            completionStatus = status.name,
            dateLogged = if (isCompleted) "Leo" else null,
            parentNotes = "",
            expectedAgeMonths = ageGroup.trim(),
            isCustom = true,
            updatedAt = System.currentTimeMillis()
        )
        milestoneDao.insertMilestone(entity)
        return entity.toDomain()
    }

    suspend fun toggleMilestoneCompletion(milestoneId: String) {
        val existing = milestoneDao.getMilestoneById(milestoneId) ?: return
        val newCompleted = !existing.isCompleted
        val newStatus = if (newCompleted) MilestoneStatus.ACHIEVED else MilestoneStatus.NOT_YET
        val newDate = if (newCompleted) "Leo" else null
        milestoneDao.updateCompletionStatus(
            id = milestoneId,
            isCompleted = newCompleted,
            completionStatus = newStatus.name,
            dateLogged = newDate,
            updatedAt = System.currentTimeMillis()
        )
    }

    suspend fun updateMilestoneStatus(
        milestoneId: String,
        status: MilestoneStatus,
        parentNotes: String,
        dateLogged: String? = null
    ) {
        val isCompleted = status == MilestoneStatus.ACHIEVED
        val resolvedDate = dateLogged ?: if (isCompleted || status == MilestoneStatus.EMERGING) "Leo" else null
        milestoneDao.updateMilestoneDetails(
            id = milestoneId,
            isCompleted = isCompleted,
            completionStatus = status.name,
            parentNotes = parentNotes.trim(),
            dateLogged = resolvedDate,
            updatedAt = System.currentTimeMillis()
        )
    }

    suspend fun deleteMilestone(milestoneId: String) {
        milestoneDao.deleteMilestoneById(milestoneId)
    }
}
