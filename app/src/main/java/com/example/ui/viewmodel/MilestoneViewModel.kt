package com.example.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.MilestoneRepository
import com.example.data.NeuroRepository
import com.example.data.local.NeuroTotoDatabase
import com.example.model.Milestone
import com.example.model.MilestoneCategory
import com.example.model.MilestoneStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class CompletionFilter(val swahiliLabel: String, val englishLabel: String) {
    ALL("Zote", "All"),
    COMPLETED("Zimekamilika", "Completed"),
    INCOMPLETE("Zinazoendelea", "In Progress")
}

data class MilestoneUiState(
    val allMilestones: List<Milestone> = emptyList(),
    val filteredMilestones: List<Milestone> = emptyList(),
    val selectedAgeGroup: String? = null,
    val selectedCategory: MilestoneCategory? = null,
    val completionFilter: CompletionFilter = CompletionFilter.ALL,
    val totalCount: Int = 0,
    val completedCount: Int = 0,
    val isLoading: Boolean = true
) {
    val completionFraction: Float
        get() = if (totalCount == 0) 0f else completedCount.toFloat() / totalCount.toFloat()
}

/**
 * ViewModel managing child developmental milestones backed by Room persistence.
 */
class MilestoneViewModel(
    private val repository: MilestoneRepository
) : ViewModel() {

    private val _selectedAgeGroup = MutableStateFlow<String?>(null)
    private val _selectedCategory = MutableStateFlow<MilestoneCategory?>(null)
    private val _completionFilter = MutableStateFlow(CompletionFilter.ALL)

    val uiState: StateFlow<MilestoneUiState> = combine(
        repository.allMilestones.onEach { milestones ->
            NeuroRepository.syncMilestonesFromDatabase(milestones)
        },
        _selectedAgeGroup,
        _selectedCategory,
        _completionFilter
    ) { milestones, ageGroup, category, completion ->
        val filtered = milestones.filter { item ->
            val matchesAge = ageGroup == null || item.ageGroup == ageGroup
            val matchesCategory = category == null || item.category == category
            val matchesCompletion = when (completion) {
                CompletionFilter.ALL -> true
                CompletionFilter.COMPLETED -> item.isAchieved
                CompletionFilter.INCOMPLETE -> !item.isAchieved
            }
            matchesAge && matchesCategory && matchesCompletion
        }

        val scopeForProgress = milestones.filter { item ->
            (ageGroup == null || item.ageGroup == ageGroup) &&
                (category == null || item.category == category)
        }

        MilestoneUiState(
            allMilestones = milestones,
            filteredMilestones = filtered,
            selectedAgeGroup = ageGroup,
            selectedCategory = category,
            completionFilter = completion,
            totalCount = scopeForProgress.size,
            completedCount = scopeForProgress.count { it.isAchieved },
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = MilestoneUiState(isLoading = true)
    )

    init {
        viewModelScope.launch {
            repository.seedInitialMilestonesIfEmpty()
        }
    }

    fun selectAgeGroup(ageGroup: String?) {
        _selectedAgeGroup.value = ageGroup
    }

    fun selectCategory(category: MilestoneCategory?) {
        _selectedCategory.value = category
    }

    fun setCompletionFilter(filter: CompletionFilter) {
        _completionFilter.value = filter
    }

    fun toggleMilestoneCompletion(milestoneId: String) {
        viewModelScope.launch {
            repository.toggleMilestoneCompletion(milestoneId)
        }
    }

    fun updateMilestoneDetails(
        milestoneId: String,
        status: MilestoneStatus,
        parentNotes: String
    ) {
        viewModelScope.launch {
            repository.updateMilestoneStatus(
                milestoneId = milestoneId,
                status = status,
                parentNotes = parentNotes
            )
        }
    }

    fun addMilestone(
        name: String,
        ageGroup: String,
        category: MilestoneCategory = MilestoneCategory.COGNITIVE,
        description: String = "",
        tip: String = "",
        isCompleted: Boolean = false
    ) {
        if (name.isBlank() || ageGroup.isBlank()) return
        viewModelScope.launch {
            repository.addMilestone(
                name = name,
                nameSwahili = name,
                ageGroup = ageGroup,
                category = category,
                description = description,
                tip = tip,
                isCompleted = isCompleted
            )
        }
    }

    fun deleteMilestone(milestoneId: String) {
        viewModelScope.launch {
            repository.deleteMilestone(milestoneId)
        }
    }

    companion object {
        fun factory(context: Context): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val dao = NeuroTotoDatabase.getInstance(context).developmentalMilestoneDao()
                    val repo = MilestoneRepository(dao)
                    return MilestoneViewModel(repo) as T
                }
            }
        }
    }
}
