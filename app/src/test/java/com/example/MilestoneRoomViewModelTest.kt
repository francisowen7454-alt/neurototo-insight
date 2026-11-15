package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.MilestoneRepository
import com.example.data.local.DevelopmentalMilestoneEntity
import com.example.data.local.NeuroTotoDatabase
import com.example.model.MilestoneCategory
import com.example.model.MilestoneStatus
import com.example.ui.viewmodel.CompletionFilter
import com.example.ui.viewmodel.MilestoneViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class MilestoneRoomViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var database: NeuroTotoDatabase
    private lateinit var repository: MilestoneRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, NeuroTotoDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = MilestoneRepository(database.developmentalMilestoneDao())
    }

    @After
    fun tearDown() {
        database.close()
        Dispatchers.resetMain()
    }

    @Test
    fun daoInsertsQueriesAndUpdatesCompletionStatus() = runTest(testDispatcher) {
        val dao = database.developmentalMilestoneDao()
        val entity = DevelopmentalMilestoneEntity(
            id = "m_test_1",
            name = "Walks Without Support",
            nameSwahili = "Kutembea Bila Kushikilia",
            ageGroup = "1-2 Miaka",
            category = MilestoneCategory.MOTOR.name,
            isCompleted = false,
            completionStatus = MilestoneStatus.NOT_YET.name
        )

        dao.insertMilestone(entity)
        val byAge = dao.getMilestonesByAgeGroup("1-2 Miaka").first()
        assertEquals(1, byAge.size)
        assertEquals("Walks Without Support", byAge.first().name)
        assertFalse(byAge.first().isCompleted)

        dao.updateCompletionStatus(
            id = "m_test_1",
            isCompleted = true,
            completionStatus = MilestoneStatus.ACHIEVED.name,
            dateLogged = "Leo"
        )

        val completed = dao.getMilestonesByCompletionStatus(true).first()
        assertEquals(1, completed.size)
        assertEquals("m_test_1", completed.first().id)
        assertEquals(MilestoneStatus.ACHIEVED.name, completed.first().completionStatus)
    }

    @Test
    fun viewModelSeedsMilestonesAndSupportsFilteringAndAdding() = runTest(testDispatcher) {
        val viewModel = MilestoneViewModel(repository)
        advanceUntilIdle()

        // Collect initial seeded state
        val seededState = viewModel.uiState.first { !it.isLoading }
        assertTrue("Expected seeded milestones in Room", seededState.allMilestones.isNotEmpty())

        val initialCount = seededState.totalCount
        val initialCompleted = seededState.completedCount

        // Add a custom milestone via ViewModel
        viewModel.addMilestone(
            name = "Says Two-Word Phrases",
            ageGroup = "2-3 Miaka",
            category = MilestoneCategory.SPEECH,
            description = "Combines 'Mama water'",
            isCompleted = false
        )
        advanceUntilIdle()

        val afterAdd = viewModel.uiState.first { it.allMilestones.size == initialCount + 1 }
        val custom = afterAdd.allMilestones.firstOrNull { it.titleEnglish == "Says Two-Word Phrases" }
        assertNotNull(custom)
        assertEquals("2-3 Miaka", custom!!.ageGroup)
        assertFalse(custom.isAchieved)

        // Toggle completion status on the custom milestone
        viewModel.toggleMilestoneCompletion(custom.id)
        advanceUntilIdle()

        val afterToggle = viewModel.uiState.first { state ->
            state.allMilestones.firstOrNull { it.id == custom.id }?.isAchieved == true
        }
        assertEquals(initialCompleted + 1, afterToggle.completedCount)

        // Filter by age group and completion status
        viewModel.selectAgeGroup("2-3 Miaka")
        viewModel.setCompletionFilter(CompletionFilter.COMPLETED)
        advanceUntilIdle()

        val filtered = viewModel.uiState.first {
            it.selectedAgeGroup == "2-3 Miaka" && it.completionFilter == CompletionFilter.COMPLETED
        }
        assertTrue(filtered.filteredMilestones.all { it.ageGroup == "2-3 Miaka" && it.isAchieved })
        assertTrue(filtered.filteredMilestones.any { it.id == custom.id })
    }
}
