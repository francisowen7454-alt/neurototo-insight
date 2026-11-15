package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DevelopmentalMilestoneDao {

    @Query("SELECT * FROM developmental_milestones ORDER BY is_custom DESC, updated_at ASC")
    fun getAllMilestones(): Flow<List<DevelopmentalMilestoneEntity>>

    @Query("SELECT * FROM developmental_milestones WHERE age_group = :ageGroup ORDER BY name ASC")
    fun getMilestonesByAgeGroup(ageGroup: String): Flow<List<DevelopmentalMilestoneEntity>>

    @Query("SELECT * FROM developmental_milestones WHERE is_completed = :isCompleted ORDER BY updated_at DESC")
    fun getMilestonesByCompletionStatus(isCompleted: Boolean): Flow<List<DevelopmentalMilestoneEntity>>

    @Query("SELECT * FROM developmental_milestones WHERE id = :id LIMIT 1")
    suspend fun getMilestoneById(id: String): DevelopmentalMilestoneEntity?

    @Query("SELECT COUNT(*) FROM developmental_milestones")
    suspend fun getMilestoneCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMilestone(milestone: DevelopmentalMilestoneEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(milestones: List<DevelopmentalMilestoneEntity>)

    @Update
    suspend fun updateMilestone(milestone: DevelopmentalMilestoneEntity)

    @Query(
        """
        UPDATE developmental_milestones
        SET is_completed = :isCompleted,
            completion_status = :completionStatus,
            date_logged = :dateLogged,
            updated_at = :updatedAt
        WHERE id = :id
        """
    )
    suspend fun updateCompletionStatus(
        id: String,
        isCompleted: Boolean,
        completionStatus: String,
        dateLogged: String?,
        updatedAt: Long = System.currentTimeMillis()
    )

    @Query(
        """
        UPDATE developmental_milestones
        SET is_completed = :isCompleted,
            completion_status = :completionStatus,
            parent_notes = :parentNotes,
            date_logged = :dateLogged,
            updated_at = :updatedAt
        WHERE id = :id
        """
    )
    suspend fun updateMilestoneDetails(
        id: String,
        isCompleted: Boolean,
        completionStatus: String,
        parentNotes: String,
        dateLogged: String?,
        updatedAt: Long = System.currentTimeMillis()
    )

    @Query("DELETE FROM developmental_milestones WHERE id = :id")
    suspend fun deleteMilestoneById(id: String)
}
