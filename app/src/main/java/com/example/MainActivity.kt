package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.NeuroRepository
import com.example.ui.viewmodel.MilestoneViewModel
import com.example.model.ContentItem
import com.example.ui.components.AdminAuthDialog
import com.example.ui.components.ChildProfileDialog
import com.example.ui.components.NeuroTopBar
import com.example.ui.components.ProPaywallDialog
import com.example.ui.components.PushNotificationBanner
import com.example.ui.components.VideoPlayerDialog
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme

enum class MainDestination(
    val swahiliTitle: String,
    val englishTitle: String,
    val icon: ImageVector
) {
    HOME("Nyumbani", "Home", Icons.Default.Home),
    MILESTONES("Ukuaji", "Milestones", Icons.Default.CheckCircle),
    LEARNING("Masomo", "Learning", Icons.Default.VideoLibrary),
    AI("TotoAI", "TotoAI", Icons.Default.AutoAwesome),
    SPECIALISTS("Wataalamu", "Specialists", Icons.Default.MedicalServices),
    COMMUNITY("Jumuiya", "Circles", Icons.Default.Groups),
    PROFILE("Wasifu", "Profile", Icons.Default.Person)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                NeuroTotoApp()
            }
        }
    }
}

@Composable
fun NeuroTotoApp() {
    val context = LocalContext.current
    val milestoneViewModel: MilestoneViewModel = viewModel(
        factory = MilestoneViewModel.factory(context)
    )
    val milestoneUiState by milestoneViewModel.uiState.collectAsStateWithLifecycle()

    val childProfile by NeuroRepository.childProfile.collectAsStateWithLifecycle()
    val isProUser by NeuroRepository.isProUser.collectAsStateWithLifecycle()
    val freeDaysRemaining by NeuroRepository.freeTrialDaysRemaining.collectAsStateWithLifecycle()
    val language by NeuroRepository.currentLanguage.collectAsStateWithLifecycle()
    val milestones by NeuroRepository.milestones.collectAsStateWithLifecycle()
    val redFlags by NeuroRepository.redFlags.collectAsStateWithLifecycle()
    val contentItems by NeuroRepository.contentItems.collectAsStateWithLifecycle()
    val specialists by NeuroRepository.specialists.collectAsStateWithLifecycle()
    val expertAnswers by NeuroRepository.expertAnswers.collectAsStateWithLifecycle()
    val communityPosts by NeuroRepository.communityPosts.collectAsStateWithLifecycle()
    val dailyTip by NeuroRepository.dailyTip.collectAsStateWithLifecycle()
    val adminAnalytics by NeuroRepository.adminAnalytics.collectAsStateWithLifecycle()
    val articleSchedules by NeuroRepository.articleSchedules.collectAsStateWithLifecycle()
    val faqs by NeuroRepository.faqs.collectAsStateWithLifecycle()
    val activePushAlert by NeuroRepository.activePushAlert.collectAsStateWithLifecycle()
    val currentAdmin by NeuroRepository.currentAdmin.collectAsStateWithLifecycle()

    var currentDestination by remember { mutableStateOf(MainDestination.HOME) }
    var isAdminMode by remember { mutableStateOf(false) }

    // Dialog states
    var showProPaywall by remember { mutableStateOf(false) }
    var showChildProfileDialog by remember { mutableStateOf(false) }
    var showAdminAuthDialog by remember { mutableStateOf(false) }
    var activeContentItem by remember { mutableStateOf<ContentItem?>(null) }

    // Predictive back handling
    BackHandler(enabled = isAdminMode || currentDestination != MainDestination.HOME) {
        if (isAdminMode) {
            isAdminMode = false
        } else {
            currentDestination = MainDestination.HOME
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            if (!isAdminMode) {
                NeuroTopBar(
                    child = childProfile,
                    language = language,
                    isPro = isProUser,
                    freeDaysRemaining = freeDaysRemaining,
                    onLanguageToggle = {
                        NeuroRepository.setLanguage(if (language == "sw") "en" else "sw")
                    },
                    onChildProfileClick = { showChildProfileDialog = true },
                    onProClick = { showProPaywall = true },
                    onAdminClick = { showAdminAuthDialog = true }
                )
            }
        },
        bottomBar = {
            if (!isAdminMode) {
                NavigationBar(
                    tonalElevation = 4.dp,
                    modifier = Modifier.testTag("main_bottom_nav")
                ) {
                    val navItems = listOf(
                        MainDestination.HOME,
                        MainDestination.MILESTONES,
                        MainDestination.LEARNING,
                        MainDestination.AI,
                        MainDestination.SPECIALISTS,
                        MainDestination.COMMUNITY
                    )

                    navItems.forEach { destination ->
                        val selected = currentDestination == destination
                        NavigationBarItem(
                            selected = selected,
                            onClick = { currentDestination = destination },
                            icon = {
                                Icon(
                                    imageVector = destination.icon,
                                    contentDescription = destination.swahiliTitle,
                                    modifier = Modifier.size(22.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = if (language == "sw") destination.swahiliTitle else destination.englishTitle,
                                    fontSize = 10.sp,
                                    maxLines = 1
                                )
                            },
                            modifier = Modifier.testTag("nav_item_${destination.name.lowercase()}")
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (isAdminMode) {
                AdminDashboardScreen(
                    language = language,
                    analytics = adminAnalytics,
                    adminUser = currentAdmin,
                    onAddNewContent = { newItem ->
                        NeuroRepository.addContentItem(newItem)
                    },
                    onAddSchedule = { newSchedule ->
                        NeuroRepository.addArticleSchedule(newSchedule)
                    },
                    onSendNotification = { title, body ->
                        NeuroRepository.triggerPushNotification(title, body)
                    },
                    onLogout = {
                        NeuroRepository.logoutAdmin()
                        isAdminMode = false
                    },
                    onCloseAdmin = { isAdminMode = false }
                )
            } else {
                when (currentDestination) {
                    MainDestination.HOME -> {
                        HomeScreen(
                            child = childProfile,
                            language = language,
                            dailyTip = dailyTip,
                            milestones = milestoneUiState.allMilestones.ifEmpty { milestones },
                            contentItems = contentItems,
                            faqs = faqs,
                            isPro = isProUser,
                            onNavigateToMilestones = { currentDestination = MainDestination.MILESTONES },
                            onNavigateToAi = { currentDestination = MainDestination.AI },
                            onNavigateToSpecialists = { currentDestination = MainDestination.SPECIALISTS },
                            onNavigateToLearning = { currentDestination = MainDestination.LEARNING },
                            onNavigateToCommunity = { currentDestination = MainDestination.COMMUNITY },
                            onSelectContent = { activeContentItem = it },
                            onProClick = { showProPaywall = true }
                        )
                    }
                    MainDestination.MILESTONES -> {
                        MilestonesScreen(
                            child = childProfile,
                            language = language,
                            uiState = milestoneUiState,
                            redFlags = redFlags,
                            onSelectAgeGroup = { milestoneViewModel.selectAgeGroup(it) },
                            onSelectCategory = { milestoneViewModel.selectCategory(it) },
                            onSelectCompletionFilter = { milestoneViewModel.setCompletionFilter(it) },
                            onToggleMilestone = { id -> milestoneViewModel.toggleMilestoneCompletion(id) },
                            onAddMilestone = { name, ageGroup, category, description, isCompleted ->
                                milestoneViewModel.addMilestone(
                                    name = name,
                                    ageGroup = ageGroup,
                                    category = category,
                                    description = description,
                                    isCompleted = isCompleted
                                )
                            },
                            onUpdateMilestoneDetails = { id, status, notes ->
                                milestoneViewModel.updateMilestoneDetails(id, status, notes)
                            },
                            onDeleteMilestone = { id -> milestoneViewModel.deleteMilestone(id) },
                            onConsultDoctor = { currentDestination = MainDestination.SPECIALISTS }
                        )
                    }
                    MainDestination.LEARNING -> {
                        LearningHubScreen(
                            language = language,
                            contentItems = contentItems,
                            schedules = articleSchedules,
                            faqs = faqs,
                            isProUser = isProUser,
                            onSelectItem = { activeContentItem = it },
                            onToggleBookmark = { id -> NeuroRepository.toggleBookmark(id) },
                            onToggleReminder = { id -> NeuroRepository.toggleScheduleReminder(id) },
                            onProClick = { showProPaywall = true }
                        )
                    }
                    MainDestination.AI -> {
                        AiConsultantScreen(
                            child = childProfile,
                            language = language,
                            onConsultDoctor = { currentDestination = MainDestination.SPECIALISTS },
                            onNavigateToMilestones = { currentDestination = MainDestination.MILESTONES }
                        )
                    }
                    MainDestination.SPECIALISTS -> {
                        SpecialistScreen(
                            language = language,
                            specialists = specialists,
                            expertAnswers = expertAnswers,
                            isPro = isProUser,
                            onProClick = { showProPaywall = true }
                        )
                    }
                    MainDestination.COMMUNITY -> {
                        CommunityScreen(
                            language = language,
                            posts = communityPosts,
                            onLikePost = { id -> NeuroRepository.toggleLikePost(id) },
                            onAddComment = { id, text -> NeuroRepository.addCommentToPost(id, text) },
                            onCreatePost = { title, content, cat, age ->
                                NeuroRepository.createCommunityPost(title, content, cat, age)
                            }
                        )
                    }
                    MainDestination.PROFILE -> {
                        ProfileScreen(
                            child = childProfile,
                            language = language,
                            isPro = isProUser,
                            freeTrialDays = freeDaysRemaining,
                            onEditChild = { showChildProfileDialog = true },
                            onProClick = { showProPaywall = true },
                            onLanguageToggle = {
                                NeuroRepository.setLanguage(if (language == "sw") "en" else "sw")
                            },
                            onOpenAdmin = { showAdminAuthDialog = true }
                        )
                    }
                }
            }

            // Real-time Push Notification Floating Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
            ) {
                PushNotificationBanner(
                    notification = activePushAlert,
                    onDismiss = { NeuroRepository.clearActivePushAlert() },
                    onClick = {
                        NeuroRepository.clearActivePushAlert()
                        currentDestination = MainDestination.LEARNING
                    }
                )
            }
        }
    }

    // Admin Password Authentication Dialog
    if (showAdminAuthDialog) {
        AdminAuthDialog(
            language = language,
            onDismiss = { showAdminAuthDialog = false },
            onAuthenticated = {
                showAdminAuthDialog = false
                isAdminMode = true
            }
        )
    }

    // Pro Paywall Dialog
    if (showProPaywall) {
        ProPaywallDialog(
            language = language,
            currentDaysRemaining = freeDaysRemaining,
            onDismiss = { showProPaywall = false },
            onActivatePlan = { tier ->
                NeuroRepository.activateProSubscription(tier)
            }
        )
    }

    // Child Profile Dialog
    if (showChildProfileDialog) {
        ChildProfileDialog(
            child = childProfile,
            language = language,
            onDismiss = { showChildProfileDialog = false },
            onSave = { updated ->
                NeuroRepository.updateChildProfile(updated)
            }
        )
    }

    // Video Player & Article Reading Dialog
    if (activeContentItem != null) {
        VideoPlayerDialog(
            item = activeContentItem!!,
            language = language,
            onDismiss = { activeContentItem = null },
            onBookmarkToggle = {
                NeuroRepository.toggleBookmark(activeContentItem!!.id)
                activeContentItem = activeContentItem!!.copy(bookmarked = !activeContentItem!!.bookmarked)
            }
        )
    }
}
