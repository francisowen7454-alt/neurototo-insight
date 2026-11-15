package com.example

import com.example.data.AiAssistantService
import com.example.data.NeuroRepository
import com.example.model.SubscriptionTier
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testMilestoneToggle() {
        val initialMilestones = NeuroRepository.milestones.value
        val firstMilestone = initialMilestones.first()
        val initialAchieved = firstMilestone.isAchieved

        NeuroRepository.toggleMilestone(firstMilestone.id)
        val updated = NeuroRepository.milestones.value.first { it.id == firstMilestone.id }
        assertEquals(!initialAchieved, updated.isAchieved)

        // Toggle back
        NeuroRepository.toggleMilestone(firstMilestone.id)
    }

    @Test
    fun testProSubscriptionActivation() {
        NeuroRepository.activateProSubscription(SubscriptionTier.MONTHLY)
        assertTrue(NeuroRepository.isProUser.value)
        assertEquals(30, NeuroRepository.freeTrialDaysRemaining.value)
    }

    @Test
    fun testAiAssistantGuidance() = runBlocking {
        val child = NeuroRepository.childProfile.value
        val speechReply = AiAssistantService.generateResponse("Mtoto wangu anachelewa kuongea", child, "sw")
        assertNotNull(speechReply.text)
        assertTrue(speechReply.text.contains("lugha") || speechReply.text.contains("Skrini") || speechReply.text.contains("Baraka"))

        val screenReply = AiAssistantService.generateResponse("Athari za simu na TV", child, "sw")
        assertNotNull(screenReply.text)
        assertTrue(screenReply.text.contains("simu") || screenReply.text.contains("Skrini") || screenReply.text.contains("Dopamine"))
    }

    @Test
    fun testCommunityPostAndLike() {
        val initialPosts = NeuroRepository.communityPosts.value
        val post = initialPosts.first()
        val initialLikes = post.likes

        NeuroRepository.toggleLikePost(post.id)
        val updatedPost = NeuroRepository.communityPosts.value.first { it.id == post.id }
        assertEquals(initialLikes + 1, updatedPost.likes)
        assertTrue(updatedPost.isLikedByUser)

        NeuroRepository.addCommentToPost(post.id, "Asante kwa ushauri mzuri!", "Mama Aisha")
        val postWithComment = NeuroRepository.communityPosts.value.first { it.id == post.id }
        assertTrue(postWithComment.comments.any { it.text == "Asante kwa ushauri mzuri!" })
    }
}
