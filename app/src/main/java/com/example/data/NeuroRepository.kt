package com.example.data

import android.content.Context
import com.example.data.local.NeuroTotoDatabase
import com.example.model.*
import com.example.util.SecurityUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

object NeuroRepository {

    // Current active child profile
    private val _childProfile = MutableStateFlow(
        ChildProfile(
            id = "child_1",
            name = "Baraka Omari",
            birthDate = "15 Jan 2024",
            ageMonths = 18,
            gender = "Mvulana",
            bloodGroup = "B+",
            weightKg = 11.2,
            heightCm = 83.5,
            notes = "Anafurahia kuunganisha mbao za maumbo na kujaribu kuongea maneno mafupi."
        )
    )
    val childProfile: StateFlow<ChildProfile> = _childProfile.asStateFlow()

    // Subscription & Pro Status
    // Free trial starts active for 7 days
    private val _isProUser = MutableStateFlow(true)
    val isProUser: StateFlow<Boolean> = _isProUser.asStateFlow()

    private val _freeTrialDaysRemaining = MutableStateFlow(5)
    val freeTrialDaysRemaining: StateFlow<Int> = _freeTrialDaysRemaining.asStateFlow()

    private val _currentLanguage = MutableStateFlow("sw") // "sw" for Kiswahili, "en" for English
    val currentLanguage: StateFlow<String> = _currentLanguage.asStateFlow()

    // Milestones by age brackets
    private val _milestones = MutableStateFlow(getInitialMilestones())
    val milestones: StateFlow<List<Milestone>> = _milestones.asStateFlow()

    private val _milestoneObservations = MutableStateFlow(getInitialObservations())
    val milestoneObservations: StateFlow<List<MilestoneObservation>> = _milestoneObservations.asStateFlow()

    private val _redFlags = MutableStateFlow(getInitialRedFlags())
    val redFlags: StateFlow<List<RedFlag>> = _redFlags.asStateFlow()

    // Articles & Video Lessons
    private val _contentItems = MutableStateFlow(getInitialContent())
    val contentItems: StateFlow<List<ContentItem>> = _contentItems.asStateFlow()

    // Verified Health Specialists
    private val _specialists = MutableStateFlow(getInitialSpecialists())
    val specialists: StateFlow<List<Consultant>> = _specialists.asStateFlow()

    // Expert Q&A archive
    private val _expertAnswers = MutableStateFlow(getInitialExpertAnswers())
    val expertAnswers: StateFlow<List<ExpertAnswer>> = _expertAnswers.asStateFlow()

    // Community Posts
    private val _communityPosts = MutableStateFlow(getInitialCommunityPosts())
    val communityPosts: StateFlow<List<CommunityPost>> = _communityPosts.asStateFlow()

    // Persistent Chat History
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()
    private var isChatPersistenceLoaded = false

    // Official Direct Contacts & Payment
    const val WHATSAPP_CONTACT = "+255759237454"
    const val CALL_PHONE_CONTACT = "+255792237454"
    const val LIPA_NAMBA = PaymentConstants.LIPA_NAMBA
    const val LIPA_NAME = PaymentConstants.ACCOUNT_NAME

    // Admin Account & Registration
    private const val INITIAL_OWNER_SALT = "8f1e6b3c9a4d2e70"
    private val _registeredAdmins = MutableStateFlow<List<AdminUser>>(
        listOf(
            AdminUser(
                id = "admin_owner_1",
                fullName = "Francis Owen",
                emailOrPhone = "+255759237454",
                passwordHash = SecurityUtils.hashPassword("neurototo2026", INITIAL_OWNER_SALT),
                passwordSalt = INITIAL_OWNER_SALT,
                role = "Mmiliki Mkuu (Owner)",
                registeredDate = "Oktoba 2026"
            )
        )
    )
    val registeredAdmins: StateFlow<List<AdminUser>> = _registeredAdmins.asStateFlow()

    private val _currentAdmin = MutableStateFlow<AdminUser?>(null)
    val currentAdmin: StateFlow<AdminUser?> = _currentAdmin.asStateFlow()

    // Brute-force throttling & Account Lockout
    private var failedLoginAttempts = 0
    private var lockoutUntilTimestamp: Long = 0L
    private const val MAX_FAILED_ATTEMPTS = 5
    private const val LOCKOUT_DURATION_MS = 30_000L

    fun getAdminLockoutRemainingSeconds(): Long {
        val diff = lockoutUntilTimestamp - System.currentTimeMillis()
        return if (diff > 0) (diff / 1000) + 1 else 0L
    }

    // Article & Video Lessons Publishing Schedule
    private val _articleSchedules = MutableStateFlow(getInitialSchedules())
    val articleSchedules: StateFlow<List<ArticleSchedule>> = _articleSchedules.asStateFlow()

    // Push Notifications
    private val _pushNotifications = MutableStateFlow(getInitialNotifications())
    val pushNotifications: StateFlow<List<PushNotificationItem>> = _pushNotifications.asStateFlow()

    private val _activePushAlert = MutableStateFlow<PushNotificationItem?>(null)
    val activePushAlert: StateFlow<PushNotificationItem?> = _activePushAlert.asStateFlow()

    // Categorised FAQs
    private val _faqs = MutableStateFlow(getInitialFaqs())
    val faqs: StateFlow<List<FaqItem>> = _faqs.asStateFlow()

    // Daily Tips
    private val _dailyTip = MutableStateFlow(
        DailyTip(
            id = "tip_today",
            titleSwahili = "Ubongo wa Mtoto Hujengwa Kupitia Mazungumzo ya Moja kwa Moja",
            titleEnglish = "Serve & Return: How Direct Talking Builds Your Child's Neural Pathways",
            bodySwahili = "Wakati mtoto anapopiga kelele, kuelekeza kidole au kuongea, na wewe ukamtazama machoni na kumjibu (Serve and Return), mamilioni ya njia za neva kwenye ubongo wake huungana kwa kasi.",
            bodyEnglish = "When an infant babbles or points and a caregiver responds with eye contact and words, key neural connections form for lifelong cognitive and language resilience.",
            brainActivitySwahili = "Zoezi la Leo: Cheza mchezo wa kuiga sauti na mtoto wako kwa dakika 5 bila simu wala televisheni.",
            brainActivityEnglish = "Today's Activity: Spend 5 undistracted minutes imitating your child's sounds and naming 3 colorful objects around you.",
            ageTarget = "Miezi 6 - Miaka 3"
        )
    )
    val dailyTip: StateFlow<DailyTip> = _dailyTip.asStateFlow()

    // Admin analytics
    private val _adminAnalytics = MutableStateFlow(AdminAnalytics())
    val adminAnalytics: StateFlow<AdminAnalytics> = _adminAnalytics.asStateFlow()

    fun setLanguage(lang: String) {
        _currentLanguage.value = lang
    }

    fun updateChildProfile(profile: ChildProfile) {
        _childProfile.value = profile
    }

    fun toggleMilestone(milestoneId: String) {
        _milestones.update { list ->
            list.map {
                if (it.id == milestoneId) {
                    val newAchieved = !it.isAchieved
                    it.copy(
                        isAchieved = newAchieved,
                        status = if (newAchieved) MilestoneStatus.ACHIEVED else MilestoneStatus.NOT_YET,
                        dateLogged = if (newAchieved) "Leo" else null
                    )
                } else it
            }
        }
    }

    fun updateMilestoneStatus(milestoneId: String, status: MilestoneStatus, notes: String, date: String) {
        _milestones.update { list ->
            list.map { m ->
                if (m.id == milestoneId) {
                    m.copy(
                        isAchieved = status == MilestoneStatus.ACHIEVED,
                        status = status,
                        parentNotes = notes,
                        dateLogged = date
                    )
                } else m
            }
        }
    }

    fun syncMilestonesFromDatabase(updated: List<Milestone>) {
        if (updated.isNotEmpty()) {
            _milestones.value = updated
        }
    }

    fun getDefaultMilestones(): List<Milestone> = getInitialMilestones()

    fun logMilestoneObservation(observation: MilestoneObservation) {
        _milestoneObservations.update { listOf(observation) + it }
        if (observation.milestoneId != null) {
            _milestones.update { list ->
                list.map { m ->
                    if (m.id == observation.milestoneId) {
                        m.copy(
                            isAchieved = observation.status == MilestoneStatus.ACHIEVED,
                            status = observation.status,
                            dateLogged = observation.date,
                            parentNotes = observation.observationNote
                        )
                    } else m
                }
            }
        }
    }

    fun initChatPersistence(context: Context) {
        if (isChatPersistenceLoaded) return
        val prefs = context.getSharedPreferences("neurototo_chat_history_store", Context.MODE_PRIVATE)
        val json = prefs.getString("chat_messages_json", null)
        if (!json.isNullOrBlank()) {
            try {
                val array = org.json.JSONArray(json)
                val list = mutableListOf<ChatMessage>()
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    val catString = if (obj.has("relatedMilestoneCategory") && !obj.isNull("relatedMilestoneCategory")) obj.getString("relatedMilestoneCategory") else null
                    val category = catString?.let { runCatching { MilestoneCategory.valueOf(it) }.getOrNull() }
                    list.add(
                        ChatMessage(
                            id = obj.getString("id"),
                            sender = obj.getString("sender"),
                            text = obj.getString("text"),
                            timestamp = obj.getString("timestamp"),
                            timestampMillis = obj.optLong("timestampMillis", System.currentTimeMillis()),
                            isWarning = obj.optBoolean("isWarning", false),
                            recommendedDoctorCategory = if (obj.has("recommendedDoctorCategory") && !obj.isNull("recommendedDoctorCategory")) obj.getString("recommendedDoctorCategory") else null,
                            isAiGenerated = obj.optBoolean("isAiGenerated", true),
                            relatedMilestoneCategory = category,
                            relatedMilestoneId = if (obj.has("relatedMilestoneId") && !obj.isNull("relatedMilestoneId")) obj.getString("relatedMilestoneId") else null,
                            relatedMilestoneTitle = if (obj.has("relatedMilestoneTitle") && !obj.isNull("relatedMilestoneTitle")) obj.getString("relatedMilestoneTitle") else null,
                            developmentalTipSwahili = if (obj.has("developmentalTipSwahili") && !obj.isNull("developmentalTipSwahili")) obj.getString("developmentalTipSwahili") else null,
                            developmentalTipEnglish = if (obj.has("developmentalTipEnglish") && !obj.isNull("developmentalTipEnglish")) obj.getString("developmentalTipEnglish") else null,
                            suggestedObservationNote = if (obj.has("suggestedObservationNote") && !obj.isNull("suggestedObservationNote")) obj.getString("suggestedObservationNote") else null,
                            isMilestoneLogged = obj.optBoolean("isMilestoneLogged", false)
                        )
                    )
                }
                if (list.isNotEmpty()) {
                    _chatMessages.value = list
                    isChatPersistenceLoaded = true
                    return
                }
            } catch (_: Exception) {
                // Ignore parse errors and populate initial greeting
            }
        }

        // Default initial greeting if no history saved yet
        val lang = _currentLanguage.value
        val child = _childProfile.value
        val initialGreeting = ChatMessage(
            id = "init_welcome",
            sender = "toto_ai",
            text = if (lang == "sw")
                "Habari mzazi! Mimi ni TotoAI, msaidizi wako mahiri wa ukuaji wa mtoto, mfumo wa fahamu, na malezi ya kidijitali. Nimepata taarifa za ${child.name} mwenye umri wa miezi ${child.ageMonths}. Una swali lolote kuhusu hatua za ukuaji, miondoko, lugha, au lishe ya ubongo leo?"
            else
                "Hello! I am TotoAI, your pediatric neurodevelopment and growth milestone assistant. I have ${child.name}'s profile (${child.ageMonths} months old). What developmental, motor, speech, or brain nutrition question can I assist with today?",
            timestamp = "Sasa hivi",
            timestampMillis = System.currentTimeMillis(),
            isAiGenerated = true,
            relatedMilestoneCategory = MilestoneCategory.COGNITIVE,
            relatedMilestoneId = "m_1_2_cog_point",
            relatedMilestoneTitle = if (lang == "sw") "Kuelekeza Kidole Kushirikisha Hisia (Joint Attention)" else "Pointing to Share Interest",
            suggestedObservationNote = "Ufuatiliaji wa ukuaji unaendelea vizuri."
        )
        _chatMessages.value = listOf(initialGreeting)
        isChatPersistenceLoaded = true
        persistChatMessages(context, _chatMessages.value)
    }

    private fun persistChatMessages(context: Context?, messages: List<ChatMessage>) {
        if (context == null) return
        try {
            val prefs = context.getSharedPreferences("neurototo_chat_history_store", Context.MODE_PRIVATE)
            val array = org.json.JSONArray()
            messages.forEach { msg ->
                val obj = org.json.JSONObject()
                obj.put("id", msg.id)
                obj.put("sender", msg.sender)
                obj.put("text", msg.text)
                obj.put("timestamp", msg.timestamp)
                obj.put("timestampMillis", msg.timestampMillis)
                obj.put("isWarning", msg.isWarning)
                obj.put("recommendedDoctorCategory", msg.recommendedDoctorCategory)
                obj.put("isAiGenerated", msg.isAiGenerated)
                obj.put("relatedMilestoneCategory", msg.relatedMilestoneCategory?.name)
                obj.put("relatedMilestoneId", msg.relatedMilestoneId)
                obj.put("relatedMilestoneTitle", msg.relatedMilestoneTitle)
                obj.put("developmentalTipSwahili", msg.developmentalTipSwahili)
                obj.put("developmentalTipEnglish", msg.developmentalTipEnglish)
                obj.put("suggestedObservationNote", msg.suggestedObservationNote)
                obj.put("isMilestoneLogged", msg.isMilestoneLogged)
                array.put(obj)
            }
            prefs.edit().putString("chat_messages_json", array.toString()).apply()
        } catch (_: Exception) {}
    }

    fun addChatMessage(context: Context?, message: ChatMessage) {
        _chatMessages.update { it + message }
        persistChatMessages(context, _chatMessages.value)
    }

    fun clearChatHistory(context: Context?, childName: String, ageMonths: Int, lang: String) {
        val initialGreeting = ChatMessage(
            id = "init_${System.currentTimeMillis()}",
            sender = "toto_ai",
            text = if (lang == "sw")
                "Historia ya mazungumzo imefutwa. Habari mzazi wa $childName! Ni swali gani jipya la makuzi, mfumo wa fahamu au malezi ungependa kuniuliza leo?"
            else
                "Chat history cleared. Hello! What new developmental or neurological question about $childName can I answer for you?",
            timestamp = "Sasa hivi",
            timestampMillis = System.currentTimeMillis(),
            isAiGenerated = true
        )
        _chatMessages.value = listOf(initialGreeting)
        persistChatMessages(context, _chatMessages.value)
    }

    fun logMilestoneFromChat(context: Context?, messageId: String, milestoneId: String, note: String) {
        // 1. Mark message as logged
        _chatMessages.update { list ->
            list.map { if (it.id == messageId) it.copy(isMilestoneLogged = true) else it }
        }
        persistChatMessages(context, _chatMessages.value)

        // 2. Find matching milestone and mark achieved
        val matchingMilestone = _milestones.value.firstOrNull { it.id == milestoneId }
        val category = matchingMilestone?.category ?: MilestoneCategory.COGNITIVE
        val title = matchingMilestone?.titleSwahili ?: "Hatua ya Ukuaji"

        val observation = MilestoneObservation(
            id = "obs_chat_${System.currentTimeMillis()}",
            milestoneId = milestoneId,
            milestoneTitle = title,
            childAgeMonths = _childProfile.value.ageMonths,
            date = "Leo (Kupitia Ushauri wa AI)",
            category = category,
            observationNote = note.ifBlank { "Imerekodiwa moja kwa moja kupitia ushauri wa maendeleo wa TotoAI." },
            status = MilestoneStatus.ACHIEVED,
            celebrationBadge = "🌟"
        )
        logMilestoneObservation(observation)

        if (context != null) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val dao = NeuroTotoDatabase.getInstance(context).developmentalMilestoneDao()
                    dao.updateMilestoneDetails(
                        id = milestoneId,
                        isCompleted = true,
                        completionStatus = MilestoneStatus.ACHIEVED.name,
                        parentNotes = observation.observationNote,
                        dateLogged = observation.date
                    )
                } catch (_: Exception) {}
            }
        }
    }

    fun toggleBookmark(contentId: String) {
        _contentItems.update { list ->
            list.map {
                if (it.id == contentId) it.copy(bookmarked = !it.bookmarked) else it
            }
        }
    }

    fun addContentItem(item: ContentItem) {
        _contentItems.update { listOf(item) + it }
    }

    fun toggleLikePost(postId: String) {
        _communityPosts.update { list ->
            list.map { post ->
                if (post.id == postId) {
                    val newLiked = !post.isLikedByUser
                    val newCount = if (newLiked) post.likes + 1 else post.likes - 1
                    post.copy(isLikedByUser = newLiked, likes = newCount)
                } else post
            }
        }
    }

    fun addCommentToPost(postId: String, text: String, author: String = "Mzazi Baraka") {
        _communityPosts.update { list ->
            list.map { post ->
                if (post.id == postId) {
                    val newComment = Comment(
                        id = "c_${System.currentTimeMillis()}",
                        author = author,
                        text = text,
                        timestamp = "Hivi sasa"
                    )
                    val updatedComments = post.comments.toMutableList()
                    updatedComments.add(newComment)
                    post.copy(comments = updatedComments)
                } else post
            }
        }
    }

    fun createCommunityPost(title: String, content: String, category: String, childAge: String) {
        val newPost = CommunityPost(
            id = "post_${System.currentTimeMillis()}",
            authorName = "Mzazi Baraka",
            authorCity = "Dar es Salaam",
            childAgeTag = childAge,
            category = category,
            title = title,
            content = content,
            timeAgo = "Muda mfupi uliopita",
            likes = 0,
            isLikedByUser = false,
            comments = mutableListOf()
        )
        _communityPosts.update { listOf(newPost) + it }
    }

    fun activateProSubscription(tier: SubscriptionTier) {
        _isProUser.value = true
        _freeTrialDaysRemaining.value = 30
    }

    fun registerAdmin(fullName: String, emailOrPhone: String, password: String): AdminRegistrationResult {
        if (fullName.isBlank()) {
            return AdminRegistrationResult(false, "Tafadhali ingiza jina lako kamili.")
        }
        if (emailOrPhone.isBlank()) {
            return AdminRegistrationResult(false, "Weka namba ya simu au barua pepe halali.")
        }
        val policy = SecurityUtils.validatePasswordPolicy(password.trim())
        if (!policy.isValid) {
            return AdminRegistrationResult(false, policy.errorMessageSwahili)
        }
        val normalizedIdentifier = emailOrPhone.trim().lowercase()
        val exists = _registeredAdmins.value.any { it.emailOrPhone.lowercase() == normalizedIdentifier }
        if (exists) {
            return AdminRegistrationResult(false, "Akaunti yenye namba/barua pepe hii tayari ipo.")
        }

        val salt = SecurityUtils.generateSalt()
        val hash = SecurityUtils.hashPassword(password.trim(), salt)
        val newAdmin = AdminUser(
            id = "admin_${System.currentTimeMillis()}",
            fullName = fullName.trim(),
            emailOrPhone = emailOrPhone.trim(),
            passwordHash = hash,
            passwordSalt = salt,
            role = "Mmiliki Mkuu (Owner)",
            registeredDate = "Oktoba 2026"
        )
        _registeredAdmins.update { it + newAdmin }
        _currentAdmin.value = newAdmin
        return AdminRegistrationResult(true)
    }

    fun loginAdmin(emailOrPhone: String, password: String): AdminAuthResult {
        val remaining = getAdminLockoutRemainingSeconds()
        if (remaining > 0) {
            return AdminAuthResult(
                success = false,
                isLockedOut = true,
                remainingLockoutSeconds = remaining,
                errorMessage = "Mfumo umezuiwa kwa sekunde $remaining kulinda dhidi ya majaribio mengi (Brute-Force Protection)."
            )
        }

        val trimmedIdentifier = emailOrPhone.trim()
        val candidatePassword = password.trim()

        val found = _registeredAdmins.value.firstOrNull {
            it.emailOrPhone.equals(trimmedIdentifier, ignoreCase = true)
        }

        if (found != null && SecurityUtils.verifyPassword(candidatePassword, found.passwordHash, found.passwordSalt)) {
            // Successful authentication resets brute-force failure counter
            failedLoginAttempts = 0
            lockoutUntilTimestamp = 0L
            _currentAdmin.value = found
            return AdminAuthResult(success = true)
        }

        // Increment failure counter for brute-force protection
        failedLoginAttempts++
        if (failedLoginAttempts >= MAX_FAILED_ATTEMPTS) {
            lockoutUntilTimestamp = System.currentTimeMillis() + LOCKOUT_DURATION_MS
            failedLoginAttempts = 0
            val waitSec = LOCKOUT_DURATION_MS / 1000
            return AdminAuthResult(
                success = false,
                isLockedOut = true,
                remainingLockoutSeconds = waitSec,
                errorMessage = "Majaribio 5 yasiyo sahihi! Dashibodi imefungwa kwa sekunde $waitSec kulinda usalama wa mfumo."
            )
        }

        val remainingAttempts = MAX_FAILED_ATTEMPTS - failedLoginAttempts
        return AdminAuthResult(
            success = false,
            remainingLockoutSeconds = 0,
            errorMessage = "Namba au nenosiri si sahihi. Majaribio $remainingAttempts yamebaki kabla ya mfumo kufungwa."
        )
    }

    fun logoutAdmin() {
        _currentAdmin.value = null
    }

    fun toggleScheduleReminder(scheduleId: String) {
        _articleSchedules.update { list ->
            list.map {
                if (it.id == scheduleId) it.copy(isReminderSet = !it.isReminderSet) else it
            }
        }
    }

    fun addArticleSchedule(schedule: ArticleSchedule) {
        _articleSchedules.update { listOf(schedule) + it }
        triggerPushNotification(
            title = "Ratiba Mpya: ${schedule.titleSwahili}",
            body = "Somo la ${if (schedule.type == ContentType.VIDEO) "video" else "makala"} limepangwa tarehe ${schedule.scheduledDate}.",
            contentId = schedule.id
        )
    }

    fun triggerPushNotification(title: String, body: String, contentId: String? = null) {
        val notification = PushNotificationItem(
            id = "notif_${System.currentTimeMillis()}",
            title = title,
            body = body,
            timestamp = "Hivi punde",
            contentId = contentId,
            isRead = false
        )
        _pushNotifications.update { listOf(notification) + it }
        _activePushAlert.value = notification
    }

    fun clearActivePushAlert() {
        _activePushAlert.value = null
    }

    private fun getInitialObservations(): List<MilestoneObservation> {
        return listOf(
            MilestoneObservation(
                id = "obs_1",
                milestoneId = "m_1_2_motor_walk",
                milestoneTitle = "Kutembea Bila Kushikilia",
                childAgeMonths = 18,
                date = "12 Okt 2026",
                category = MilestoneCategory.MOTOR,
                observationNote = "Baraka alipiga hatua 8 za kwanza peke yake kutoka kwenye sofa hadi kwa mama bila kuanguka!",
                status = MilestoneStatus.ACHIEVED,
                celebrationBadge = "🏆"
            ),
            MilestoneObservation(
                id = "obs_2",
                milestoneId = "m_1_2_speech_words",
                milestoneTitle = "Kutamka Maneno 5 Hadi 10 Yenye Maana",
                childAgeMonths = 17,
                date = "28 Sep 2026",
                category = MilestoneCategory.SPEECH,
                observationNote = "Ameanza kutamka 'Maziwa' na 'Nipe' akielekeza kikombe chake jikoni.",
                status = MilestoneStatus.ACHIEVED,
                celebrationBadge = "⭐"
            ),
            MilestoneObservation(
                id = "obs_3",
                milestoneId = "m_1_2_cog_point",
                milestoneTitle = "Kuelekeza Kidole Kushirikisha Hisia (Joint Attention)",
                childAgeMonths = 18,
                date = "24 Okt 2026",
                category = MilestoneCategory.COGNITIVE,
                observationNote = "Anaelekeza kidole ndege wanapopita dirishani na kunitazama kwa tabasamu.",
                status = MilestoneStatus.EMERGING,
                celebrationBadge = "🌱"
            )
        )
    }

    private fun getInitialMilestones(): List<Milestone> {
        return listOf(
            // 0-3 Miezi
            Milestone(
                id = "m_0_3_motor_head",
                ageGroup = "0-3 Miezi",
                category = MilestoneCategory.MOTOR,
                titleSwahili = "Kunyanyua Kichwa Akiwa Juu ya Tumbo (Tummy Time)",
                titleEnglish = "Lifts Head During Tummy Time",
                descriptionSwahili = "Akiwa amelala kifudifudi, anaweza kunyanyua kichwa na kuanza kusukuma kifua kwa mikono yake.",
                descriptionEnglish = "When on tummy, lifts head 45 to 90 degrees and pushes up on forearms.",
                tipSwahili = "Fanya mazoezi ya tummy time mara 3-4 kwa siku kwa dakika 3-5 akiwa macho.",
                tipEnglish = "Practice tummy time 3-4 times daily for 3-5 minutes while fully awake.",
                isAchieved = true,
                status = MilestoneStatus.ACHIEVED,
                dateLogged = "Miezi 2",
                expectedAgeMonths = "Miezi 2"
            ),
            Milestone(
                id = "m_0_3_social_smile",
                ageGroup = "0-3 Miezi",
                category = MilestoneCategory.EMOTIONAL,
                titleSwahili = "Tabasamu la Kijamii (Social Smile)",
                titleEnglish = "Spontaneous Social Smile",
                descriptionSwahili = "Hutabasamu kwa hiari anapoona sura ya mzazi au anaposikia sauti tulivu ya mama au baba.",
                descriptionEnglish = "Smiles spontaneously and warmly in response to parent's face or voice.",
                tipSwahili = "Mjibu kwa tabasamu na maneno laini pindi anapotabasamu ili kuchochea hisia za ubongo.",
                tipEnglish = "Mirror their smile immediately to reinforce brain neural bonding circuitry.",
                isAchieved = true,
                status = MilestoneStatus.ACHIEVED,
                dateLogged = "Miezi 2",
                expectedAgeMonths = "Miezi 2"
            ),
            Milestone(
                id = "m_0_3_speech_coo",
                ageGroup = "0-3 Miezi",
                category = MilestoneCategory.SPEECH,
                titleSwahili = "Kutoa Sauti za 'Cooing' (Ooh, Aah)",
                titleEnglish = "Cooing and Gurgling Sounds",
                descriptionSwahili = "Hutoa milio ya kuvutia ya vokali kama 'ooooh' na 'aaah' unapozungumza naye.",
                descriptionEnglish = "Produces reciprocal vowel sounds when spoken to in gentle tones.",
                tipSwahili = "Subiri amalize kutoa sauti kisha mjibu kwa kurudia sauti yake ili kumfundisha mpangilio wa mazungumzo.",
                tipEnglish = "Pause after they vocalize, then reply to teach conversational turn-taking.",
                isAchieved = true,
                status = MilestoneStatus.ACHIEVED,
                dateLogged = "Miezi 2",
                expectedAgeMonths = "Miezi 2"
            ),

            // 4-6 Miezi
            Milestone(
                id = "m_4_6_motor_roll",
                ageGroup = "4-6 Miezi",
                category = MilestoneCategory.MOTOR,
                titleSwahili = "Kujigeuza kutoka Mgongo kwenda Tumbo",
                titleEnglish = "Rolling from Back to Tummy",
                descriptionSwahili = "Anaweza kujigeuza akiwa amelala na kuanza kuinua kifua chake akiwa juu ya tumbo.",
                descriptionEnglish = "Rolls independently from back to stomach and pushes up on extended elbows.",
                tipSwahili = "Weka vinyago vyenye rangi upande wake ili kumhamasisha kujigeuza akivifuata.",
                tipEnglish = "Place colorful toys slightly to the side to motivate rolling rotation.",
                isAchieved = true,
                status = MilestoneStatus.ACHIEVED,
                dateLogged = "Miezi 5",
                expectedAgeMonths = "Miezi 4-6"
            ),
            Milestone(
                id = "m_4_6_speech_babble",
                ageGroup = "4-6 Miezi",
                category = MilestoneCategory.SPEECH,
                titleSwahili = "Kutoa Sauti za Konsonanti (Babbling 'ba', 'ma', 'da')",
                titleEnglish = "Babbling Consonants & Laughing",
                descriptionSwahili = "Anatamka sauti za herufi na hucheka kwa sauti kubwa wakati wa michezo.",
                descriptionEnglish = "Babbles with repetitive consonants and laughs aloud when tickled or played with.",
                tipSwahili = "Imba naye nyimbo fupi za watoto na mpigie makofi anapocheka.",
                tipEnglish = "Sing rhymes and clap along to encourage vocal joy and cadence.",
                isAchieved = true,
                status = MilestoneStatus.ACHIEVED,
                dateLogged = "Miezi 5",
                expectedAgeMonths = "Miezi 6"
            ),
            Milestone(
                id = "m_4_6_motor_grasp",
                ageGroup = "4-6 Miezi",
                category = MilestoneCategory.MOTOR,
                titleSwahili = "Kufikia na Kushika Vinyago kwa Mikono Miwili",
                titleEnglish = "Reaching and Grasping Objects",
                descriptionSwahili = "Hunyosha mikono kwa usahihi kushika vitu anavyoviona na kuvipeleka mdomoni.",
                descriptionEnglish = "Reaches intentionally with both hands and brings objects safely to mouth for sensory exploration.",
                tipSwahili = "Mpe vitu salama, visivyo na sumu na vikubwa ambavyo hawezi kuvimeza.",
                tipEnglish = "Offer large, non-toxic grasping toys to foster hand-eye coordination.",
                isAchieved = true,
                status = MilestoneStatus.ACHIEVED,
                dateLogged = "Miezi 5",
                expectedAgeMonths = "Miezi 6"
            ),

            // 7-9 Miezi
            Milestone(
                id = "m_7_9_motor_sit",
                ageGroup = "7-9 Miezi",
                category = MilestoneCategory.MOTOR,
                titleSwahili = "Kukaa Mwenyewe Bila Kuegemea",
                titleEnglish = "Sitting Without Hand Support",
                descriptionSwahili = "Hukaa wima sakafuni bila kuanguka huku mikono yake ikiwa huru kuchezea vinyago.",
                descriptionEnglish = "Sits stably upright with hands free to hold and manipulate objects.",
                tipSwahili = "Muwekee mito laini pembeni anapojifunza kukaa ili kumkinga asijigonge.",
                tipEnglish = "Surround with soft floor pillows during seating balance training.",
                isAchieved = true,
                status = MilestoneStatus.ACHIEVED,
                dateLogged = "Miezi 8",
                expectedAgeMonths = "Miezi 9"
            ),
            Milestone(
                id = "m_7_9_cog_object",
                ageGroup = "7-9 Miezi",
                category = MilestoneCategory.COGNITIVE,
                titleSwahili = "Kutafuta Vitu Vilivyofichwa (Object Permanence)",
                titleEnglish = "Looks for Hidden Objects",
                descriptionSwahili = "Kama kinyago kikifunikwa na kitambaa kidogo, mtoto anajua bado kipo na anakifunua.",
                descriptionEnglish = "Recognizes that unseen objects continue to exist and uncovers hidden toys.",
                tipSwahili = "Cheza mchezo wa kufunika kinyago kwa kitambaa na kumuuliza 'Kiko wapi?'.",
                tipEnglish = "Play hide-and-seek games with a light scarf over favorite rattle.",
                isAchieved = true,
                status = MilestoneStatus.ACHIEVED,
                dateLogged = "Miezi 8",
                expectedAgeMonths = "Miezi 9"
            ),
            Milestone(
                id = "m_7_9_speech_name",
                ageGroup = "7-9 Miezi",
                category = MilestoneCategory.SPEECH,
                titleSwahili = "Kugeuka Haraka Anapoitwa Jina Lake",
                titleEnglish = "Responds Consistently to Own Name",
                descriptionSwahili = "Anapoitwa jina lake na mzazi anayeongea kwa utulivu, humtazama moja kwa moja.",
                descriptionEnglish = "Turns head promptly and makes eye contact when parent calls their name.",
                tipSwahili = "Tumia jina lake halisi mara kwa mara badala ya majina ya utani pekee.",
                tipEnglish = "Use their actual name frequently in joyful, engaging daily routines.",
                isAchieved = true,
                status = MilestoneStatus.ACHIEVED,
                dateLogged = "Miezi 8",
                expectedAgeMonths = "Miezi 9"
            ),

            // 10-12 Miezi
            Milestone(
                id = "m_10_12_motor_stand",
                ageGroup = "10-12 Miezi",
                category = MilestoneCategory.MOTOR,
                titleSwahili = "Kusimama kwa Kushikilia Fenicha (Cruising)",
                titleEnglish = "Pulls to Stand & Cruises Along Furniture",
                descriptionSwahili = "Hujivuta juu kwa mikono na kusimama akishikilia kochi au kitanda na kusogea hatua chache.",
                descriptionEnglish = "Pulls up to a stand and cruises smoothly along low stable furniture.",
                tipSwahili = "Hakikisha fenicha ni imara na sakafu haina utelezi ili ajisikie salama kujaribu.",
                tipEnglish = "Secure furniture and clear barefoot play zones for solid foot grip.",
                isAchieved = true,
                status = MilestoneStatus.ACHIEVED,
                dateLogged = "Miezi 11",
                expectedAgeMonths = "Miezi 12"
            ),
            Milestone(
                id = "m_10_12_motor_pincer",
                ageGroup = "10-12 Miezi",
                category = MilestoneCategory.MOTOR,
                titleSwahili = "Kushika Vitu Vidogo kwa Vidole Viwili (Pincer Grasp)",
                titleEnglish = "Mature Pincer Grasp",
                descriptionSwahili = "Hunyanyua vipande vidogo vya chakula au nafaka kwa ncha ya kidole gumba na shahada.",
                descriptionEnglish = "Picks up small safe food pieces between the tips of thumb and index finger.",
                tipSwahili = "Mpe vipande laini vya ndizi au parachichi ale mwenyewe kwa vidole (Finger Foods).",
                tipEnglish = "Offer soft finger foods like diced banana to hone fine motor precision.",
                isAchieved = true,
                status = MilestoneStatus.ACHIEVED,
                dateLogged = "Miezi 11",
                expectedAgeMonths = "Miezi 12"
            ),
            Milestone(
                id = "m_10_12_social_wave",
                ageGroup = "10-12 Miezi",
                category = MilestoneCategory.EMOTIONAL,
                titleSwahili = "Kupunga Mkono 'Kwaheri' na Kupiga Makofi",
                titleEnglish = "Waves Bye-bye and Claps Hands",
                descriptionSwahili = "Huonyesha ishara za kijamii kwa kupunga mkono au kupiga makofi unapomwambia.",
                descriptionEnglish = "Waves bye-bye reciprocally and claps hands playfully during social exchanges.",
                tipSwahili = "Mzoeshe kusema kwaheri kwa mkono kila mtu anapoondoka au kurudi.",
                tipEnglish = "Model reciprocal hand-waving and cheer when they copy the gesture.",
                isAchieved = true,
                status = MilestoneStatus.ACHIEVED,
                dateLogged = "Miezi 12",
                expectedAgeMonths = "Miezi 12"
            ),

            // 1-2 Miaka (Current bracket for Baraka, 18 months)
            Milestone(
                id = "m_1_2_motor_walk",
                ageGroup = "1-2 Miaka",
                category = MilestoneCategory.MOTOR,
                titleSwahili = "Kutembea Bila Kushikilia",
                titleEnglish = "Walking Independently",
                descriptionSwahili = "Mtoto anaweza kusimama na kupiga hatua kadhaa mbele bila kushikilia mikono au fenicha.",
                descriptionEnglish = "Child can stand and take several confident steps without holding onto hands or furniture.",
                tipSwahili = "Mtie moyo kwa kumpigia makofi na kumuwekea vinyago salama mbele yake ili atembee kuvifikia.",
                tipEnglish = "Encourage them with praise and place safe toys a few steps ahead to invite walking.",
                isAchieved = true,
                status = MilestoneStatus.ACHIEVED,
                dateLogged = "12 Okt 2026",
                parentNotes = "Alitembea hatua 8 peke yake!",
                expectedAgeMonths = "Miezi 15-18"
            ),
            Milestone(
                id = "m_1_2_speech_words",
                ageGroup = "1-2 Miaka",
                category = MilestoneCategory.SPEECH,
                titleSwahili = "Kutamka Maneno 5 Hadi 10 Yenye Maana",
                titleEnglish = "Saying 5 to 10 Meaningful Words",
                descriptionSwahili = "Anatumia maneno kama 'Mama', 'Baba', 'Maziwa', 'Nipe' kwa kuelewa maana yake halisi.",
                descriptionEnglish = "Uses words like 'Mama', 'Dada', 'Milk', 'No' with purposeful meaning.",
                tipSwahili = "Zungumza naye mara kwa mara kwa kutaja majina halisi ya vitu badala ya sauti za kitoto pekee.",
                tipEnglish = "Talk frequently and name real items slowly rather than only using baby sounds.",
                isAchieved = true,
                status = MilestoneStatus.ACHIEVED,
                dateLogged = "28 Sep 2026",
                parentNotes = "Anasema Mama, Baba, Maziwa, Nipe na Gari.",
                expectedAgeMonths = "Miezi 18"
            ),
            Milestone(
                id = "m_1_2_cog_point",
                ageGroup = "1-2 Miaka",
                category = MilestoneCategory.COGNITIVE,
                titleSwahili = "Kuelekeza Kidole Kushirikisha Hisia (Joint Attention)",
                titleEnglish = "Pointing to Share Interest & Imitating",
                descriptionSwahili = "Anaelekeza kidole kuonyesha kitu anachotaka au anachokiona (kama ndege au gari) na kukuangalia.",
                descriptionEnglish = "Points to show an interesting object and looks back at you to share joint attention.",
                tipSwahili = "Kitendo hiki ni dalili kubwa ya maendeleo mema ya ubongo na hisia za mawasiliano (Joint Attention).",
                tipEnglish = "Joint attention is a key marker of healthy neurological and social development.",
                isAchieved = false,
                status = MilestoneStatus.EMERGING,
                parentNotes = "Anajaribu kuelekeza ndege wanapopita lakini mara chache hutazama machoni.",
                expectedAgeMonths = "Miezi 18"
            ),
            Milestone(
                id = "m_1_2_social_empathy",
                ageGroup = "1-2 Miaka",
                category = MilestoneCategory.EMOTIONAL,
                titleSwahili = "Kuonyesha Upendo na Wasiwasi wa Kutengana",
                titleEnglish = "Showing Affection & Secure Attachment",
                descriptionSwahili = "Hukumbatia wazazi na huonyesha kusononeka kidogo wakati mzazi anapoondoka, na kufurahi anaporudi.",
                descriptionEnglish = "Hugs familiar caregivers and demonstrates healthy reciprocal affection.",
                tipSwahili = "Usimtoroke mtoto unapotoka nyumbani; muage kwa tabasamu na kumhakikishia unarudi.",
                tipEnglish = "Avoid sneaking away; give a gentle, calm goodbye to build healthy attachment security.",
                isAchieved = true,
                status = MilestoneStatus.ACHIEVED,
                dateLogged = "Miezi 16",
                expectedAgeMonths = "Miezi 18"
            ),
            Milestone(
                id = "m_1_2_motor_scribble",
                ageGroup = "1-2 Miaka",
                category = MilestoneCategory.MOTOR,
                titleSwahili = "Kuchora Mistari kwa Kalamu ya Rangi (Scribbling)",
                titleEnglish = "Spontaneous Scribbling with Crayon",
                descriptionSwahili = "Hushika kalamu nene ya rangi kwa ngumi na kuchora mistari ya hiari kwenye karatasi.",
                descriptionEnglish = "Grasps a thick crayon and scribbles spontaneous marks across paper.",
                tipSwahili = "Mpe karatasi kubwa na rangi salama asizoweza kula ili kuamsha uratibu wa macho na mikono.",
                tipEnglish = "Provide large sheets of butcher paper and chunky crayons to explore marks.",
                isAchieved = false,
                status = MilestoneStatus.NOT_YET,
                expectedAgeMonths = "Miezi 18-24"
            ),

            // 2-3 Miaka
            Milestone(
                id = "m_2_3_speech_phrases",
                ageGroup = "2-3 Miaka",
                category = MilestoneCategory.SPEECH,
                titleSwahili = "Kuunganisha Sentensi Fupi za Maneno 2 hadi 3",
                titleEnglish = "2 to 3 Word Combined Phrases",
                descriptionSwahili = "Huweka maneno pamoja kama 'Mama nipe maji', 'Gari kubwa limeenda' badala ya neno moja moja.",
                descriptionEnglish = "Combines noun-verb phrases like 'Want water', 'Daddy go car'.",
                tipSwahili = "Panua maneno yake: akisema 'Gari', mjibu 'Ndiyo, lile ni gari kubwa jekundu!'.",
                tipEnglish = "Expand on their words: if they say 'Truck', say 'Yes, that is a fast red truck!'.",
                isAchieved = false,
                status = MilestoneStatus.NOT_YET,
                expectedAgeMonths = "Miaka 2"
            ),
            Milestone(
                id = "m_2_3_motor_stairs",
                ageGroup = "2-3 Miaka",
                category = MilestoneCategory.MOTOR,
                titleSwahili = "Kupanda Ngazi kwa Kubadilisha Miguu",
                titleEnglish = "Climbing Stairs Alternating Feet",
                descriptionSwahili = "Hupanda ngazi kwa utulivu akishikilia reli au mkono na kupiga hatua moja kwa ngazi.",
                descriptionEnglish = "Walks up stairs placing one foot on each step while holding banister.",
                tipSwahili = "Fanya mazoezi naye ya kupanda ngazi polepole ukiwa mbele au nyuma yake kwa usalama.",
                tipEnglish = "Practice gentle stair climbing together with close spotting for balance.",
                isAchieved = false,
                status = MilestoneStatus.NOT_YET,
                expectedAgeMonths = "Miaka 2-3"
            ),
            Milestone(
                id = "m_2_3_cog_sort",
                ageGroup = "2-3 Miaka",
                category = MilestoneCategory.COGNITIVE,
                titleSwahili = "Kupanga Vitu Kulingana na Rangi au Maumbo",
                titleEnglish = "Sorting Objects by Color or Shape",
                descriptionSwahili = "Anaweza kutofautisha maumbo ya duara na mraba na kupanga vinyago vya rangi moja.",
                descriptionEnglish = "Sorts basic geometric shapes and groups toys by color or function.",
                tipSwahili = "Tumia vikombe vya rangi tofauti na umuombe aweke mipira ya rangi husika ndani yake.",
                tipEnglish = "Use colored bowls and encourage matching colored balls into each.",
                isAchieved = false,
                status = MilestoneStatus.NOT_YET,
                expectedAgeMonths = "Miaka 2-3"
            ),

            // 3-5 Miaka
            Milestone(
                id = "m_3_5_cog_why",
                ageGroup = "3-5 Miaka",
                category = MilestoneCategory.COGNITIVE,
                titleSwahili = "Kueleza Hadithi Fupi na Kuuliza 'Kwa Nini?'",
                titleEnglish = "Telling Short Stories & Asking 'Why?'",
                descriptionSwahili = "Udadisi mkubwa wa ubongo unapoanza kuuliza sababu za vitu na kutunga sentensi za maneno 4 hadi 5.",
                descriptionEnglish = "Curious brain queries about cause and effect, constructing full 4-5 word sentences.",
                tipSwahili = "Jibu maswali yake kwa utulivu na lugha nyepesi ili kukuza uwezo wa kufikiri na ubunifu.",
                tipEnglish = "Answer their queries warmly to nurture critical thinking and cognitive confidence.",
                isAchieved = false,
                status = MilestoneStatus.NOT_YET,
                expectedAgeMonths = "Miaka 3-4"
            ),
            Milestone(
                id = "m_3_5_motor_hop",
                ageGroup = "3-5 Miaka",
                category = MilestoneCategory.MOTOR,
                titleSwahili = "Kuruka kwa Mguu Mmoja na Kukata kwa Mikasi ya Watoto",
                titleEnglish = "Hopping on One Foot & Child Scissors",
                descriptionSwahili = "Anaweza kusimama na kuruka kwa mguu mmoja kwa sekunde kadhaa na kukata karatasi kwa mkasi salama.",
                descriptionEnglish = "Hops on one foot for 2-3 seconds and cuts safely along paper lines.",
                tipSwahili = "Chezeni michezo ya kuruka kama 'kidali' sakafuni ili kuimarisha misuli na uwiano wa neva.",
                tipEnglish = "Play hopscotch outdoors to calibrate vestibular and cerebellar balance systems.",
                isAchieved = false,
                status = MilestoneStatus.NOT_YET,
                expectedAgeMonths = "Miaka 4"
            ),
            Milestone(
                id = "m_3_5_social_coop",
                ageGroup = "3-5 Miaka",
                category = MilestoneCategory.EMOTIONAL,
                titleSwahili = "Kushirikiana na Watoto Wenzake kwenye Michezo ya Pamoja",
                titleEnglish = "Cooperative Group Play & Rule Following",
                descriptionSwahili = "Hucheza pamoja na watoto wengine akielewa sheria rahisi na kusubiri zamu yake bila ugomvi mkubwa.",
                descriptionEnglish = "Cooperates smoothly with peers in pretend play, sharing and taking turns.",
                tipSwahili = "Mpangie muda wa kucheza na watoto wa rika lake ili ajifunze kuheshimu hisia za wengine.",
                tipEnglish = "Schedule regular peer playdates to develop empathy and collaborative problem solving.",
                isAchieved = false,
                status = MilestoneStatus.NOT_YET,
                expectedAgeMonths = "Miaka 4-5"
            )
        )
    }

    private fun getInitialRedFlags(): List<RedFlag> {
        return listOf(
            RedFlag(
                id = "rf1",
                ageGroup = "Miezi 12 - 18",
                warningSwahili = "Kutojibu jina lake wala kuelekeza kidole",
                warningEnglish = "Not responding to their name or pointing to show interest",
                reasonSwahili = "Inaweza kuashiria changamoto ya usikivu au dalili za awali za utofauti wa kimfumo wa fahamu (kama Autism Spectrum).",
                reasonEnglish = "Could indicate auditory processing issues or early signs of neurodevelopmental conditions such as autism.",
                recommendedActionSwahili = "Wasiliana na daktari wa watoto au mtaalamu wa mfumo wa fahamu kwa uchunguzi wa mapema.",
                recommendedActionEnglish = "Schedule an early screening with a developmental pediatrician or neurologist."
            ),
            RedFlag(
                id = "rf2",
                ageGroup = "Miezi 18 - 24",
                warningSwahili = "Kupoteza ujuzi aliokuwa nao hapo awali (Regression)",
                warningEnglish = "Loss of previously acquired speech or social skills",
                reasonSwahili = "Mtoto aliyekuwa akisema maneno au akitabasamu ghafla akiacha kuongea na kujitenga inahitaji uchunguzi wa haraka wa neva.",
                reasonEnglish = "Sudden loss of vocabulary or social engagement requires prompt neurological and medical evaluation.",
                recommendedActionSwahili = "Hili ni jambo la dharura ya kitabibu; wasiliana na kituo cha afya au fanya tathmini ya haraka.",
                recommendedActionEnglish = "This is a priority clinical red flag; seek urgent pediatric neurological evaluation."
            ),
            RedFlag(
                id = "rf3",
                ageGroup = "Miezi 6 - 9",
                warningSwahili = "Kukakamaa sana mwili au kulegea kabisa kama kitambaa",
                warningEnglish = "Extreme stiffness (hypertonia) or extreme floppiness (hypotonia)",
                reasonSwahili = "Hali ya misuli (muscle tone) isiyo ya kawaida inaweza kuashiria changamoto za mfumo mkuu wa neva (Cerebral Palsy n.k.).",
                reasonEnglish = "Abnormal muscle tone can be an indicator of central nervous system developmental concerns.",
                recommendedActionSwahili = "Muone daktari wa watoto kwa ajili ya vipimo vya msingi na mazoezi tiba (Physiotherapy).",
                recommendedActionEnglish = "Consult a pediatrician for neurological reflex assessment and pediatric physiotherapy."
            )
        )
    }

    private fun getInitialContent(): List<ContentItem> {
        return listOf(
            ContentItem(
                id = "vid_1",
                type = ContentType.VIDEO,
                category = ContentCategory.NEURO,
                titleSwahili = "Miaka 3 ya Kwanza: Jinsi Ubongo wa Mtoto Unavyojengwa",
                titleEnglish = "The First 1,000 Days: Building Your Child's Neural Architecture",
                summarySwahili = "Video hii ya kipekee inaeleza kwa lugha nyepesi jinsi ubongo wa mtoto unavyozalisha muunganiko wa neva milioni 1 kila sekunde na namna wazazi wanavyoweza kuimarisha uwezo huo.",
                summaryEnglish = "An illuminating video lesson explaining how baby brains form 1 million neural connections per second and how parent interaction shapes brain wiring.",
                fullBodySwahili = """
                    Ubongo wa binadamu hauzaliwi ukiwa umekamilika kama kompyuta. Unajengwa kutokana na mazingira na mahusiano ya kila siku.
                    
                    Katika miaka ya kwanza mitatu ya maisha, ubongo hukua kwa kasi kuliko wakati mwingine wowote wa maisha ya binadamu:
                    1. **Muunganiko wa Neva (Synaptogenesis):** Kila mara mtoto anaposikia sauti yako, akionja chakula, au akiguswa kwa upendo, njia mpya za neva hufunguka.
                    2. **Umuhimu wa 'Serve & Return':** Mtoto anapokutazama na wewe ukatabasamu na kumjibu, unamfundisha ubongo wake kuwa dunia ni salama na inasikiliza.
                    3. **Athari za Mfadhaiko Mkubwa (Toxic Stress):** Kelele kali, unyanyasaji na kutelekezwa huzalisha homoni ya Cortisol ambayo inadumaza ukuaji wa sehemu ya ubongo inayohusika na kumbukumbu (Hippocampus).
                """.trimIndent(),
                fullBodyEnglish = """
                    Brain architecture is constructed through an ongoing process that begins before birth and continues into adulthood.
                    
                    Early experiences affect the nature and quality of the brain's developing architecture:
                    1. Synaptogenesis creates over 1 million new neural connections every second.
                    2. Serve and return interactions shape brain circuitry.
                    3. Toxic stress can impair the development of executive functioning centers.
                """.trimIndent(),
                authorName = "Dkt. Amani Mwenda",
                authorRole = "Daktari Bingwa wa Mfumo wa Fahamu wa Watoto (Pediatric Neurologist)",
                durationText = "Dakika 14 • Video ya Ndani ya App",
                isPro = false,
                viewsCount = 3840,
                videoChapters = listOf(
                    VideoChapter("00:00", "Utangulizi na Maajabu ya Ubongo", "Introduction & Brain Wonder"),
                    VideoChapter("03:15", "Sayansi ya 'Serve and Return'", "The Science of Serve & Return"),
                    VideoChapter("07:40", "Mazingira Salama Nyumbani", "Safe Home Environment"),
                    VideoChapter("11:20", "Mazoezi ya Kila Siku kwa Mzazi", "Daily Practical Exercises")
                ),
                keyTakeawaysSwahili = listOf(
                    "Ubongo wa mtoto hukua kwa asilimia 80 kabla ya kufikisha miaka mitatu.",
                    "Kuzungumza na kumtazama machoni ni tiba bora ya kukuza akili kuliko vinyago vya bei ghali.",
                    "Kukumbatiwa na kuhisi upendo kunazalisha Oxytocin inayolinda ubongo dhidi ya wasiwasi."
                ),
                keyTakeawaysEnglish = listOf(
                    "80% of brain development occurs before age 3.",
                    "Face-to-face interaction is far more potent than expensive electronic toys.",
                    "Warm affection produces oxytocin, shielding neural pathways from toxic stress."
                )
            ),
            ContentItem(
                id = "vid_2",
                type = ContentType.VIDEO,
                category = ContentCategory.DIGITAL_PARENTING,
                titleSwahili = "Malezi ya Kidijitali: Simu, TV, na Athari kwenye Lugha ya Mtoto",
                titleEnglish = "Digital Parenting: Screens, Dopamine & Speech Delay in Toddlers",
                summarySwahili = "Wazazi wengi huwapa watoto simu ili wanyamaze au wale chakula. Jifunze kwa nini wataalamu wanashauri kutowapa skrini watoto chini ya miaka miwili na mbinu za kisasa za kudhibiti.",
                summaryEnglish = "Why pediatric health organizations advise zero screen time under 2 years and actionable strategies to replace screen-soothing with real-world engagement.",
                fullBodySwahili = """
                    Katika ulimwengu wa sasa wa kidijitali, simu za mkononi zimekuwa 'yaya wa kidijitali'. Hata hivyo, tafiti za kisasa za afya ya mfumo wa fahamu zinaonyesha:
                    
                    - **Kuchelewa Kuongea (Speech Delay):** Mtoto anayetumia zaidi ya masaa 2 kwa siku kwenye simu kabla ya miaka 2 ana uwezekano mkubwa wa kuchelewa kuanza kutamka maneno kwa sababu video hazimpi nafasi ya mazungumzo ya pande mbili.
                    - **Dopamine Rush:** Video fupi fupi zenye rangi zinazobadilika haraka huuchosha ubongo wa mtoto na kusababisha akose subira (Low Attention Span).
                    - **Mbinu za Kisasa:**
                      1. Weka 'Sheria ya Bila Simu' wakati wa kula na wakati wa kulala.
                      2. Tumia sauti (Audio/Podcasts za watoto) au vitabu badala ya video.
                      3. Ikiwa atatazama, tazama naye na muelezee kinachoendelea badala ya kumuacha peke yake.
                """.trimIndent(),
                fullBodyEnglish = """
                    Screens have increasingly become digital pacifiers. However, pediatric neurology highlights clear guidelines:
                    - Zero passive screen time under 18-24 months.
                    - Overstimulation from rapid scene changes exhausts developing dopamine receptors.
                    - Co-viewing and interactive media education when screens are introduced.
                """.trimIndent(),
                authorName = "Zawadi Kimaro, MSc",
                authorRole = "Mwanasaikolojia wa Malezi ya Mtoto & Teknolojia",
                durationText = "Dakika 11 • Video ya Ndani ya App",
                isPro = true,
                viewsCount = 5910,
                videoChapters = listOf(
                    VideoChapter("00:00", "Changamoto ya Skrini Afrika Mashariki", "Screen Challenges in East Africa"),
                    VideoChapter("02:50", "Kwa Nini Mtoto Huacha Kuongea?", "Why Toddlers Stop Speaking"),
                    VideoChapter("06:10", "Mbadala wa Simu Wakati wa Chakula", "Screen Alternatives During Meals"),
                    VideoChapter("09:00", "Mwongozo wa Hatua kwa Hatua", "Step-by-Step Detox Plan")
                ),
                keyTakeawaysSwahili = listOf(
                    "Hakuna skrini kabisa kwa watoto chini ya miezi 18 isipokuwa video call fupi na familia.",
                    "Usimpe mtoto simu akiwa analia ili asishindwe kujifunza kudhibiti hisia zake (Emotional Regulation).",
                    "Vitabu vya hadithi na michezo ya mikono huamsha ubongo mara 10 zaidi ya video za YouTube."
                ),
                keyTakeawaysEnglish = listOf(
                    "Zero solitary screen time under 18 months.",
                    "Avoid using screens to soothe tantrums so children learn emotional regulation.",
                    "Physical interactive play activates brain circuits 10x more effectively than passive cartoons."
                )
            ),
            ContentItem(
                id = "art_1",
                type = ContentType.ARTICLE,
                category = ContentCategory.NUTRITION,
                titleSwahili = "Vyula vya Ubongo: Vyakula vya Asili vya Afrika Mashariki Vinavyoongeza Uwezo wa Kufikiri",
                titleEnglish = "Brain Foods: East African Traditional Nutrition for Cognitive Power",
                summarySwahili = "Chakula bora cha ubongo hakihitaji gharama kubwa. Gundua umuhimu wa dagaa wa ziwa, viazi lishe vya njano, parachichi, na nafaka zisizokobolewa kwa mtoto wako.",
                summaryEnglish = "Essential local nutrients for infant brain development: Omega-3 from freshwater sardines (Dagaa/Omena), Vitamin A sweet potatoes, avocado, and whole grains.",
                fullBodySwahili = """
                    Asilimia 60 ya ubongo wa binadamu imeundwa na mafuta. Ili ubongo ukue vizuri, mtoto anahitaji virutubisho mahususi tangu anapoanza kula chakula kigumu (miezi 6 na kuendelea):
                    
                    ### 1. Dagaa wa Ziwa (Omena / Mukene)
                    - Wana wingi wa **DHA na Omega-3 fatty acids** ambazo hujenga kuta za seli za ubongo na macho.
                    - Pia wana madini ya chuma na kalsiamu ya kutosha. Unaweza kuwasaga vizuri kuwa unga laini na kuongeza kijiko kimoja kwenye uji au mboga ya mtoto.
                    
                    ### 2. Viazi Lishe vya Njano (Orange-fleshed Sweet Potato)
                    - Chanzo kikuu cha **Beta-Carotene (Vitamini A)** inayolinda seli za neva na kuimarisha kinga ya mwili.
                    
                    ### 3. Mayai ya Kienyeji na Parachichi
                    - Yai lina **Choline**, kirutubisho muhimu sana kwa ujenzi wa kumbukumbu (Neurotransmitter Acetylcholine).
                    - Parachichi lina mafuta mazuri ya monounsaturated yanayosaidia damu kutiririka vizuri kwenye ubongo.
                    
                    ### Tahadhari:
                    Epuka kuweka sukari na chumvi nyingi kwenye chakula cha mtoto chini ya mwaka mmoja; sukari nyingi huharibu tabia ya kula na kusababisha uchangamfu usio na utulivu (hyperactivity).
                """.trimIndent(),
                fullBodyEnglish = """
                    60% of the human brain is composed of lipids. During weaning, specific nutrients drive myelin formation and synaptic plasticity:
                    1. Lake Sardines (Dagaa / Omena): exceptional sources of DHA, Omega-3s, and iron.
                    2. Orange-fleshed sweet potatoes: natural beta-carotene for neuroprotection.
                    3. Eggs: rich in choline for memory neurotransmitter synthesis.
                """.trimIndent(),
                authorName = "Dkt. Neema Ochieng, PhD",
                authorRole = "Mtaalamu wa Lishe ya Watoto na Afya ya Jamii",
                durationText = "Dakika 6 • Makala ya Kusoma",
                isPro = false,
                viewsCount = 7420,
                keyTakeawaysSwahili = listOf(
                    "Unga wa dagaa ulioandaliwa kwa usafi ni mfalme wa Omega-3 kwa bei nafuu.",
                    "Yai moja kwa siku linatosheleza mahitaji ya Choline kwa mtoto anayeanza kutembea.",
                    "Maji safi na matunda halisi ni bora mara dufu kuliko juisi za viwandani zenye sukari."
                ),
                keyTakeawaysEnglish = listOf(
                    "Hygienically milled lake sardine powder is the most cost-effective local DHA source.",
                    "One egg daily covers primary choline requirements for growing toddlers.",
                    "Fresh whole fruit slices trump packaged sugary juices."
                )
            ),
            ContentItem(
                id = "art_2",
                type = ContentType.ARTICLE,
                category = ContentCategory.EMOTIONAL,
                titleSwahili = "Kuelewa Hasira za Ghafla (Tantrums): Mtoto Hakukusudii, Ubongo Wake Umelemewa",
                titleEnglish = "De-escalating Tantrums: It's Not Defiance, It's an Overwhelmed Nervous System",
                summarySwahili = "Mtoto wa miaka 2 akilala chini na kulia kwa nguvu haonyeshi utovu wa nidhamu, bali sehemu ya ubongo wake ya hisia (Amygdala) imezidiwa. Jifunze mbinu ya 'Connect before you Correct'.",
                summaryEnglish = "Why toddler meltdowns represent an underdeveloped emotional brake system (prefrontal cortex), and how connection de-escalates tantrums without violence or shouting.",
                fullBodySwahili = """
                    Wazazi wengi wa Kiafrika huamini kuwa mtoto anapolilia hadharani au kugoma anahitaji 'kucharazwa viboko' mara moja.
                    Hata hivyo, elimu ya mfumo wa fahamu inatupa mtazamo mpya:
                    
                    - **Sehemu ya Juu ya Ubongo (Prefrontal Cortex)** inayohusika na mantiki, subira na utulivu haijakomaa hadi miaka ya 20!
                    - **Sehemu ya Chini ya Ubongo (Amygdala)** inayohusika na hisia za hofu na hasira ndio inayoongoza.
                    
                    ### Hatua 3 za Kitaalamu Mtoto Anapopatwa na Hasira:
                    1. **Tuliza Mfumo Wako Kwanza:** Mzazi asiye na utulivu hawezi kumtuliza mtoto aliyepandwa na hasira. Vuta pumzi.
                    2. **Ungana Naye Kihisia (Connection):** Shuka chini hadi usawa wa macho yake. Mwambie: "Najua una hasira kwa sababu mchezo umekwisha, nipo hapa pamoja nawe."
                    3. **Mpe Nafasi ya Kutulia:** Mzuie asijiumize wala asivunje vitu, lakini mruhusu atoe hisia hizo. Akishatulia, ndipo unamfundisha mbadala mzuri.
                """.trimIndent(),
                fullBodyEnglish = """
                    Toddler tantrums are neurological emergencies for the child. The amygdala triggers fight-or-flight while the prefrontal cortex cannot yet regulate intense emotional stimuli.
                    - Regulate your own nervous system first.
                    - Co-regulate through soothing presence and eye contact.
                    - Teach alternate expression after physiological calm is restored.
                """.trimIndent(),
                authorName = "Zawadi Kimaro, MSc",
                authorRole = "Mwanasaikolojia wa Malezi ya Mtoto",
                durationText = "Dakika 8 • Makala ya Kusoma",
                isPro = true,
                viewsCount = 4230,
                keyTakeawaysSwahili = listOf(
                    "Mtoto halei tabia ya hasira kwa sababu ya kukosa upendo; anajifunza kudhibiti dhoruba ya hisia.",
                    "Kumpiga mtoto akiwa katikati ya hasira huchochea hofu badala ya kumfunza utulivu.",
                    "Ungana naye kabla ya kumkosoa (Connect before you correct)."
                ),
                keyTakeawaysEnglish = listOf(
                    "Tantrums are neurological storms, not calculated manipulation.",
                    "Physical punishment during emotional overwhelm reinforces threat pathways.",
                    "Connect emotionally before attempting behavioral correction."
                )
            ),
            ContentItem(
                id = "vid_3",
                type = ContentType.VIDEO,
                category = ContentCategory.GENERAL_HEALTH,
                titleSwahili = "Usingizi na Afya ya Neva: Kwa Nini Mtoto Anahitaji Usingizi wa Kina",
                titleEnglish = "Pediatric Sleep Hygiene & Memory Consolidation in Early Childhood",
                summarySwahili = "Wakati mtoto amelala, ubongo husafisha sumu na kupanga kumbukumbu za siku. Jinsi ya kutengeneza ratiba thabiti ya usingizi inayomsaidia mtoto kulala bila kulia kwa masaa.",
                summaryEnglish = "The neurological role of deep sleep in memory consolidation, toxin clearance via the glymphatic system, and gentle sleep routines for African households.",
                fullBodySwahili = """
                    Mtoto anapolala si kwamba mwili umezimika tu. Kinyume chake, ubongo hufanya kazi kubwa zaidi:
                    - **Homoni ya Ukuaji (Growth Hormone):** Asilimia 80 ya homoni hii huzalishwa wakati wa usingizi mzito (Deep REM Sleep).
                    - **Usafi wa Ubongo (Glymphatic System):** Seli za ubongo husinyaa kidogo ili kuruhusu maji ya uti wa mgongo kusafisha taka na kemikali zilizozalishwa mchana.
                    - **Ratiba ya Dhahabu:** Oga mtoto kwa maji ya vuguvugu, mwimbie wimbo wa kitamaduni au msomee hadithi, zima taa kali, na punguza kelele.
                """.trimIndent(),
                fullBodyEnglish = """
                    During infant sleep, the brain activates the glymphatic system to eliminate metabolic waste while organizing synaptogenesis.
                    - Growth hormone peaks during non-REM stages.
                    - Consistent soothing bedtime rituals reduce bedtime anxiety.
                """.trimIndent(),
                authorName = "Dkt. Amani Mwenda",
                authorRole = "Pediatric Neurologist",
                durationText = "Dakika 9 • Video ya Ndani ya App",
                isPro = false,
                viewsCount = 3120,
                videoChapters = listOf(
                    VideoChapter("00:00", "Kwa Nini Usingizi ni Tiba ya Ubongo", "Why Sleep is Brain Medicine"),
                    VideoChapter("03:10", "Idadi ya Masaa Kulingana na Umri", "Recommended Hours by Age"),
                    VideoChapter("06:00", "Ratiba ya Usiku Nyumbani", "Creating an Evening Routine")
                ),
                keyTakeawaysSwahili = listOf(
                    "Mtoto wa miaka 1-3 anahitaji masaa 11 hadi 14 ya usingizi kila siku.",
                    "Mwanga wa skrini jioni huzuia uzalishaji wa homoni ya usingizi (Melatonin).",
                    "Ratiba ya mara kwa mara inayotabirika humfanya mtoto ahisi usalama na kulala kwa amani."
                ),
                keyTakeawaysEnglish = listOf(
                    "Toddlers require 11-14 total hours of daily sleep including naps.",
                    "Blue screen light inhibits nocturnal melatonin secretion.",
                    "Predictable bedtime cues foster neural safety and calmer transitions."
                )
            )
        )
    }

    private fun getInitialSpecialists(): List<Consultant> {
        return listOf(
            Consultant(
                id = "doc_1",
                name = "Dkt. Amani Mwenda, MD, MMed",
                title = "Daktari Bingwa wa Mfumo wa Fahamu wa Watoto",
                specializationSwahili = "Uchelewaji wa Ukuaji, Kifafa cha Watoto, Usonji (Autism) & ADHD",
                specializationEnglish = "Pediatric Neurology, Developmental Delays, Epilepsy & Autism",
                hospitalAffiliation = "Aga Khan Hospital / Muhimbili National Hospital",
                location = "Dar es Salaam, Tanzania",
                consultationFee = "TZS 35,000 / KES 1,900",
                rating = 4.9,
                reviewsCount = 142,
                yearsExperience = 16,
                availableToday = true,
                bioSwahili = "Mtaalamu aliyebobea katika afya ya neva na ubongo wa watoto tangu kuzaliwa hadi miaka 14, mwenye uzoefu wa miaka 16 katika nchi za Afrika Mashariki na Uingereza.",
                bioEnglish = "Senior pediatric neurologist dedicated to early diagnosis, family counseling, and personalized intervention plans for East African children."
            ),
            Consultant(
                id = "doc_2",
                name = "Zawadi Kimaro, MSc, RegPsych",
                title = "Mwanasaikolojia wa Malezi na Maendeleo ya Mtoto",
                specializationSwahili = "Elimu ya Kihisia, Tabia za Watoto, Malezi ya Kidijitali na Wasiwasi",
                specializationEnglish = "Child Psychology, Behavioral Regulation, Digital Wellbeing",
                hospitalAffiliation = "Gertrude's Children's Hospital & Private Practice",
                location = "Nairobi, Kenya / Online",
                consultationFee = "TZS 30,000 / KES 1,600",
                rating = 4.8,
                reviewsCount = 98,
                yearsExperience = 11,
                availableToday = true,
                bioSwahili = "Huwaongoza wazazi kuelewa hisia na tabia za watoto wao kwa kutumia saikolojia ya kisasa inayozingatia mazingira ya kitamaduni ya Kiafrika.",
                bioEnglish = "Child developmental psychologist helping families navigate emotional storms, school readiness, and modern digital parenting."
            ),
            Consultant(
                id = "doc_3",
                name = "Dkt. Neema Ochieng, PhD, RD",
                title = "Mtaalamu Mwandamizi wa Lishe ya Watoto",
                specializationSwahili = "Lishe ya Ubongo, Watoto Wanaogoma Kula, Mzio wa Vyakula (Allergies)",
                specializationEnglish = "Pediatric Nutrition, Brain Micronutrients, Selective Eating",
                hospitalAffiliation = "Uganda Pediatric Association / Mulago Hospital",
                location = "Kampala, Uganda / Online",
                consultationFee = "TZS 25,000 / KES 1,400",
                rating = 4.9,
                reviewsCount = 115,
                yearsExperience = 13,
                availableToday = false,
                bioSwahili = "Ana utaalamu wa kina wa vyakula asilia vya kienyeji vya ukanda wa Maziwa Makuu na namna vinavyoweza kutumika kuongeza uwezo wa akili na uzito wa mtoto.",
                bioEnglish = "Clinical pediatric nutritionist bridging indigenous African superfoods with modern micronutrient guidelines."
            )
        )
    }

    private fun getInitialExpertAnswers(): List<ExpertAnswer> {
        return listOf(
            ExpertAnswer(
                questionId = "q_1",
                parentQuestion = "Mtoto wangu ana mwaka 1 na nusu, anapenda sana kutazama video kwenye simu. Nikimnyang'anya analia sana hadi anashindwa kupumua. Nifanyeje?",
                childAge = "Mwaka 1 na nusu",
                doctorName = "Zawadi Kimaro (Mwanasaikolojia)",
                answerSwahili = "Hii ni tabia ya kawaida ya utegemezi wa dopamine ya simu. Anapolia sana, usimrudishie simu kwani utamfundisha kuwa kulia ndio njia ya kuipata. Badala yake, mshike kwa utulivu na mpe kitu mbadala kama mpira wenye rangi, kumuogesha, au kwenda naye nje kuangalia miti na magari. Ndani ya siku 3 hadi 5 atazoea.",
                answerEnglish = "This reflects screen-induced dopamine craving. Returning the phone teaches that crying yields screens. Offer tactile alternatives and outdoor stimuli calmly.",
                category = "Malezi ya Kidijitali",
                date = "Jana"
            ),
            ExpertAnswer(
                questionId = "q_2",
                parentQuestion = "Mwanangu ana miezi 20 lakini hatamki neno lolote zaidi ya kuguna na kuelekeza kidole. Je, nianze kuwa na wasiwasi?",
                childAge = "Miezi 20",
                doctorName = "Dkt. Amani Mwenda (Pediatric Neurologist)",
                answerSwahili = "Jambo zuri ni kwamba anaelekeza kidole, ikimaanisha anatamani kuwasiliana. Hata hivyo, katika umri wa miezi 20 mtoto anapaswa kuwa na walau maneno 6 yenye maana. Ninakushauri umpeleke kwa daktari wa masikio (Audiologist) ili kupima usikivu kwanza, kisha mtaalamu wa lugha (Speech Therapist). Pia punguza kabisa skrini nyumbani.",
                answerEnglish = "Pointing is a reassuring sign of communicative intent. However, a 20-month-old should speak at least 6 distinct words. Evaluate hearing first, then consult a speech-language therapist.",
                category = "Ukuaji wa Lugha",
                date = "Siku 3 zilizopita"
            )
        )
    }

    private fun getInitialCommunityPosts(): List<CommunityPost> {
        val post1 = CommunityPost(
            id = "cp_1",
            authorName = "Mama Ethan",
            authorCity = "Nairobi, KE",
            childAgeTag = "Mtoto wa Miaka 2",
            category = "Malezi ya Kidijitali",
            title = "Jinsi Nilivyofaulu Kuondoa Simu kwa Mtoto Wangu Ndani ya Wiki Moja",
            content = "Nilikuwa nimemzoesha Ethan simu wakati wa kula. Alikuwa hali bila katuni. Wiki iliyopita niliamua kubadilisha: tukanunua vijiko vyenye rangi na tukaanza kula wote mezani. Siku mbili za kwanza zilikuwa ngumu, lakini sasa anafurahia chakula bila skrini kabisa! Wazazi msikate tamaa.",
            timeAgo = "Masaa 4 yaliyopita",
            likes = 34,
            isLikedByUser = false,
            comments = mutableListOf(
                Comment("c1", "Dorah K.", "Hongera sana mama Ethan! Nami naanza leo kwa mwanangu.", "Masaa 2 yaliyopita"),
                Comment("c2", "Dkt. Amani Mwenda", "Uamuzi mzuri sana mama. Kula pamoja kama familia pia hukuza hisia za mtoto.", "Saa 1 lililopita", isExpert = true)
            )
        )

        val post2 = CommunityPost(
            id = "cp_2",
            authorName = "Baba Brian",
            authorCity = "Mwanza, TZ",
            childAgeTag = "Miezi 14",
            category = "Ukuaji & Lugha",
            title = "Mbinu ya 'Dagaa na Viazi Lishe' Imemsaidia Sana Mwanangu",
            content = "Daktari alitushauri kuongeza unga wa dagaa kwenye chakula cha Brian kuanzia mwezi wa 8. Sasa hivi ana miezi 14, ana afya nzuri, macho machangamfu na anajifunza maneno mapya kwa haraka sana. Wazazi wenzangu msidharau vyakula vyetu vya asili!",
            timeAgo = "Jana",
            likes = 52,
            isLikedByUser = true,
            comments = mutableListOf(
                Comment("c3", "Fatma Ally", "Unga wa dagaa unamchanganyia na nini zaidi baba Brian?", "Masaa 18 yaliyopita"),
                Comment("c4", "Baba Brian", "Tunaweka kwenye uji wa dona au supu ya viazi lishe na karoti.", "Masaa 12 yaliyopita")
            )
        )

        return listOf(post1, post2)
    }

    private fun getInitialSchedules(): List<ArticleSchedule> {
        return listOf(
            ArticleSchedule(
                id = "sch_1",
                titleSwahili = "Usingizi Mzito na Ukuaji wa Neva za Ubongo",
                titleEnglish = "Deep REM Sleep & Infant Neuroplasticity",
                category = ContentCategory.NEURO,
                type = ContentType.VIDEO,
                scheduledDate = "Jumanne, 28 Oktoba",
                scheduledTime = "09:00 Asubuhi",
                instructorName = "Dkt. Amani Mwenda",
                summarySwahili = "Jinsi ya kumfundisha mtoto kulala bila kutegemea skrini au kubembelezwa kwa masaa mawili.",
                summaryEnglish = "Evidence-based infant sleep hygiene to optimize overnight synaptic pruning.",
                isReminderSet = true
            ),
            ArticleSchedule(
                id = "sch_2",
                titleSwahili = "Mbinu 5 za Kuondoa Hasira za Mtoto Bila Kumpiga",
                titleEnglish = "5 Positive Discipline Strategies for Toddler Tantrums",
                category = ContentCategory.EMOTIONAL,
                type = ContentType.ARTICLE,
                scheduledDate = "Alhamisi, 30 Oktoba",
                scheduledTime = "10:30 Asubuhi",
                instructorName = "Zawadi Kimaro (Psychologist)",
                summarySwahili = "Saikolojia ya kisasa ya 'Connect before Correct' kwa familia za Kiafrika.",
                summaryEnglish = "De-escalating fight-or-flight tantrum storms through compassionate co-regulation.",
                isReminderSet = false
            ),
            ArticleSchedule(
                id = "sch_3",
                titleSwahili = "Mwongozo wa Unga wa Dagaa na Lishe ya Ubongo",
                titleEnglish = "Lake Sardines & Omega-3 DHA Weaning Protocols",
                category = ContentCategory.NUTRITION,
                type = ContentType.VIDEO,
                scheduledDate = "Ijumaa, 31 Oktoba",
                scheduledTime = "02:00 Mchana",
                instructorName = "Dkt. Neema Ochieng",
                summarySwahili = "Mapishi rahisi ya kienyeji ya unga wa dagaa kwa watoto wanaoanza kula miezi 6 hadi 24.",
                summaryEnglish = "Hygienic processing and recipes for indigenous freshwater sardine brain foods.",
                isReminderSet = false
            ),
            ArticleSchedule(
                id = "sch_4",
                titleSwahili = "Mipaka ya Skrini na TV kwa Watoto wa Miaka 2-5",
                titleEnglish = "Establishing Screen Limits Without Warfare (Ages 2-5)",
                category = ContentCategory.DIGITAL_PARENTING,
                type = ContentType.ARTICLE,
                scheduledDate = "Jumapili, 2 Novemba",
                scheduledTime = "04:00 Jioni",
                instructorName = "Zawadi Kimaro (Psychologist)",
                summarySwahili = "Kanuni za kisasa za kudhibiti muda wa kompyuta na runinga bila kusababisha magomvi nyumbani.",
                summaryEnglish = "Healthy screen boundaries, parent co-viewing, and non-digital boredom busters.",
                isReminderSet = false
            )
        )
    }

    private fun getInitialNotifications(): List<PushNotificationItem> {
        return listOf(
            PushNotificationItem(
                id = "notif_1",
                title = "🔔 Somo Jipya: Miaka 3 ya Kwanza ya Ubongo",
                body = "Dkt. Amani Mwenda amechapisha video mpya ya namna njia za neva zinavyoungana.",
                timestamp = "Masaa 2 yaliyopita"
            ),
            PushNotificationItem(
                id = "notif_2",
                title = "🥗 Dokezo la Lishe ya Asili",
                body = "Kumbuka kuweka unga wa dagaa na viazi lishe kwenye chakula cha mtoto leo!",
                timestamp = "Jana"
            )
        )
    }

    private fun getInitialFaqs(): List<FaqItem> {
        return listOf(
            FaqItem(
                id = "faq_1",
                category = "Mfumo wa Fahamu",
                questionSwahili = "Kwa nini miaka 3 ya mwanzo ni muhimu sana kwa ubongo wa mtoto?",
                questionEnglish = "Why are the first 3 years critical for child brain development?",
                answerSwahili = "Katika miaka ya kwanza mitatu, ubongo huzalisha zaidi ya miunganiko ya neva (synapses) milioni 1 kila sekunde. Hiki ndicho kipindi pekee ambapo misingi ya lugha, utambuzi, kumbukumbu, na hisia hujengwa kwa 85%.",
                answerEnglish = "During the first 1,000 days, over 1 million neural connections form every second, establishing 85% of life-long emotional and cognitive architecture."
            ),
            FaqItem(
                id = "faq_2",
                category = "Malezi ya Kidijitali",
                questionSwahili = "Je, mtoto wa mwaka 1 anaruhusiwa kutazama video kwenye simu au TV?",
                questionEnglish = "Should a 1-year-old watch videos on phones or TV?",
                answerSwahili = "Mashirika ya madaktari wa watoto duniani yanashauri skrini SIFURI (Zero Screen Time) kwa watoto chini ya miaka miwili. Ubongo wao unahitaji mazungumzo halisi na kugusa vitu halisi (3D) ili kuwezesha usemi na kuzuia kuchelewa kuongea.",
                answerEnglish = "Pediatric health guidelines recommend zero passive screens under 24 months to protect language acquisition and dopamine regulation."
            ),
            FaqItem(
                id = "faq_3",
                category = "Lishe ya Mtoto",
                questionSwahili = "Vyakula gani vya asili vya Afrika Mashariki vinakuza akili ya mtoto?",
                questionEnglish = "Which indigenous East African foods boost infant cognitive growth?",
                answerSwahili = "Vyakula bora zaidi ni: Unga wa dagaa wa ziwa (Omega-3 DHA na Chuma), mayai ya kienyeji (Choline kwa ajili ya kumbukumbu), viazi lishe vya njano (Beta-carotene), na parachichi lililopondwa vizuri.",
                answerEnglish = "Lake sardines (DHA Omega-3), indigenous eggs (Choline), orange sweet potatoes (Vitamin A), and fresh avocado."
            ),
            FaqItem(
                id = "faq_4",
                category = "Usonji & Uchelewaji",
                questionSwahili = "Je, ni dalili zipi zinapaswa kunifanya nimpeleke mtoto kwa daktari wa neva?",
                questionEnglish = "What warning signs require a pediatric neurologist visit?",
                answerSwahili = "Kutojibu anapoitwa jina lake akiwa na miezi 12+, kutokuwa na maneno yenye maana akiwa na miezi 18, kupoteza ujuzi aliokuwa nao hapo awali (regression), kutotazama machoni, au kukakamaa mwili.",
                answerEnglish = "Not turning to their name by 12 months, no meaningful words by 18 months, lack of pointing/eye contact, regression, or abnormal muscle tone."
            ),
            FaqItem(
                id = "faq_5",
                category = "Malipo & Huduma ya Pro",
                questionSwahili = "Je, ninawezaje kufanya malipo ya NeuroToto PRO kwa simu?",
                questionEnglish = "How do I pay for NeuroToto PRO via mobile money?",
                answerSwahili = "Malipo hufanyika moja kwa moja kwa Vodacom M-Pesa, Tigo Pesa, Airtel Money, au Halopesa. Unapata wiki ya kwanza bure (siku 7), na baada ya hapo unalipa TZS 15,000 tu kwa mwezi au TZS 120,000 kwa mwaka.",
                answerEnglish = "Seamlessly pay via M-Pesa, Tigo Pesa, or Airtel Money. Enjoy a 7-day free trial, followed by flexible monthly or discounted annual subscription."
            ),
            FaqItem(
                id = "faq_6",
                category = "Mawasiliano",
                questionSwahili = "Ninawezaje kuwasiliana na timu ya NeuroToto Insight?",
                questionEnglish = "How do I reach the NeuroToto Insight clinical team?",
                answerSwahili = "Unaweza kuwasiliana nasi moja kwa moja kupitia WhatsApp kwa namba +255759237454 au kupiga simu kwa namba +255792237454 wakati wowote.",
                answerEnglish = "Contact us directly via WhatsApp at +255759237454 or phone call at +255792237454."
            )
        )
    }
}
