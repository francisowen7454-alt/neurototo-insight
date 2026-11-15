package com.example

import com.example.data.AiAssistantService
import com.example.data.NeuroRepository
import com.example.model.ChatMessage
import com.example.model.ChildProfile
import com.example.model.MilestoneCategory
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ChatMilestoneTest {

    private val testChild = ChildProfile(
        id = "test_child",
        name = "Baraka Omari",
        birthDate = "15 Jan 2024",
        ageMonths = 18,
        gender = "Mvulana",
        bloodGroup = "B+",
        weightKg = 11.2,
        heightCm = 83.5
    )

    @Before
    fun setUp() {
        NeuroRepository.clearChatHistory(null, testChild.name, testChild.ageMonths, "sw")
    }

    @Test
    fun testAiAssistantGeneratesMilestoneLinkedResponse() = runBlocking {
        // Query about speech delay
        val response = AiAssistantService.generateResponse(
            prompt = "Mtoto wangu wa miezi 18 haongei maneno mengi",
            child = testChild,
            language = "sw"
        )

        assertNotNull(response)
        assertEquals("toto_ai", response.sender)
        assertTrue(response.text.isNotBlank())
        assertEquals(MilestoneCategory.SPEECH, response.relatedMilestoneCategory)
        assertEquals("m_1_2_speech_words", response.relatedMilestoneId)
        assertNotNull(response.relatedMilestoneTitle)
        assertFalse(response.isMilestoneLogged)
    }

    @Test
    fun testLogMilestoneDirectlyFromChatMessage() {
        val testMessage = ChatMessage(
            id = "msg_test_speech",
            sender = "toto_ai",
            text = "Mwongozo wa lugha",
            timestamp = "10:00",
            relatedMilestoneCategory = MilestoneCategory.SPEECH,
            relatedMilestoneId = "m_1_2_speech_words",
            relatedMilestoneTitle = "Kutamka Maneno 5 Hadi 10 Yenye Maana"
        )

        NeuroRepository.addChatMessage(null, testMessage)
        val initialHistory = NeuroRepository.chatMessages.value
        assertTrue(initialHistory.any { it.id == "msg_test_speech" })

        // Log the milestone from chat
        NeuroRepository.logMilestoneFromChat(
            context = null,
            messageId = "msg_test_speech",
            milestoneId = "m_1_2_speech_words",
            note = "Alisema maneno mapya matatu leo."
        )

        val updatedHistory = NeuroRepository.chatMessages.value
        val loggedMessage = updatedHistory.firstOrNull { it.id == "msg_test_speech" }
        assertNotNull(loggedMessage)
        assertTrue("Message should be marked as milestone logged", loggedMessage!!.isMilestoneLogged)

        // Verify observation added to milestone tracking
        val observations = NeuroRepository.milestoneObservations.value
        assertTrue(observations.any { it.milestoneId == "m_1_2_speech_words" })
    }

    @Test
    fun testClearChatHistoryRetainsCleanWelcomeGreeting() {
        NeuroRepository.addChatMessage(
            null,
            ChatMessage(
                id = "dummy_1",
                sender = "user",
                text = "Swali la mtihani",
                timestamp = "11:00"
            )
        )
        assertEquals(2, NeuroRepository.chatMessages.value.size)

        NeuroRepository.clearChatHistory(null, testChild.name, testChild.ageMonths, "sw")
        val resetHistory = NeuroRepository.chatMessages.value
        assertEquals(1, resetHistory.size)
        assertEquals("toto_ai", resetHistory.first().sender)
    }
}
