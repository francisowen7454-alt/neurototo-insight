package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.Consultant
import com.example.model.ExpertAnswer
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SpecialistScreen(
    language: String,
    specialists: List<Consultant>,
    expertAnswers: List<ExpertAnswer>,
    isPro: Boolean,
    onProClick: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: Specialists, 1: Q&A
    var activeChatDoctor by remember { mutableStateOf<Consultant?>(null) }
    var activeCallDoctor by remember { mutableStateOf<Consultant?>(null) }
    var showAskQuestionDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("specialist_screen")
    ) {
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.MedicalServices, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (language == "sw") "Madaktari Bingwa" else "Specialists",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.QuestionAnswer, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (language == "sw") "Maswali & Majibu" else "Doctor Q&A",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            )
        }

        when (selectedTab) {
            0 -> {
                // Specialists Directory
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 90.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Pro Assurance Banner
                    item {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = GrowthTealContainer,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VerifiedUser,
                                    contentDescription = null,
                                    tint = GrowthTeal,
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = if (language == "sw") "Madaktari Wote Wametahiniwa na Kuthibitishwa" else "All Specialists Clinically Verified",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = GrowthTeal)
                                    )
                                    Text(
                                        text = if (language == "sw")
                                            "Wataalamu wa hospitali kubwa za Afrika Mashariki (Muhimbili, Aga Khan, Gertrude's, Mulago)."
                                        else
                                            "Consult verified pediatric neurologists, psychologists, and clinical nutritionists.",
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp)
                                    )
                                }
                            }
                        }
                    }

                    items(specialists) { doc ->
                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = MaterialTheme.colorScheme.surface,
                            tonalElevation = 2.dp,
                            shadowElevation = 1.dp,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Doctor Avatar
                                    Box(
                                        modifier = Modifier
                                            .size(52.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.primaryContainer),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = doc.name.split(" ").getOrNull(1)?.firstOrNull()?.toString() ?: "D",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 20.sp,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = doc.name,
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                        )
                                        Text(
                                            text = doc.title,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = MaterialTheme.colorScheme.primary,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                        )
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(top = 2.dp)
                                        ) {
                                            Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = WarmAmber, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(2.dp))
                                            Text(
                                                text = "${doc.rating} (${doc.reviewsCount} maoni)",
                                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "• ${doc.yearsExperience} miaka ya uzoefu",
                                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(
                                            text = "${doc.hospitalAffiliation} • ${doc.location}",
                                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = if (language == "sw") doc.specializationSwahili else doc.specializationEnglish,
                                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    // Chat Button
                                    Button(
                                        onClick = {
                                            if (!isPro) onProClick() else activeChatDoctor = doc
                                        },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                    ) {
                                        Icon(imageVector = Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(text = if (language == "sw") "Chat ya Daktari" else "Chat Consult", fontSize = 12.sp)
                                    }

                                    // Voice/Video Call Button
                                    OutlinedButton(
                                        onClick = {
                                            if (!isPro) onProClick() else activeCallDoctor = doc
                                        },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Videocam, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(text = if (language == "sw") "Panga Simu" else "Video Call", fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            1 -> {
                // Doctor Q&A Section
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 90.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = if (language == "sw") "Una Swali kwa Madaktari Wetu?" else "Ask a Specialist a Question?",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (language == "sw")
                                        "Tuma swali lako kwa siri, madaktari bingwa watajibu ndani ya masaa 12."
                                    else
                                        "Submit your confidential question and receive verified clinical answers.",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Button(
                                    onClick = { showAskQuestionDialog = true },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                ) {
                                    Icon(imageVector = Icons.Default.HelpOutline, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = if (language == "sw") "Uliza Swali Jipya" else "Submit New Question")
                                }
                            }
                        }
                    }

                    items(expertAnswers) { qa ->
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surface,
                            tonalElevation = 2.dp,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = WarmAmberContainer
                                    ) {
                                        Text(
                                            text = qa.category,
                                            color = WarmAmber,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Text(
                                        text = "${qa.childAge} • ${qa.date}",
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "“${qa.parentQuestion}”",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = GrowthTealContainer.copy(alpha = 0.5f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(imageVector = Icons.Default.Verified, contentDescription = null, tint = GrowthTeal, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "Jibu la ${qa.doctorName}:",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = GrowthTeal)
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = if (language == "sw") qa.answerSwahili else qa.answerEnglish,
                                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp, lineHeight = 18.sp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Direct Doctor Chat Dialog
    if (activeChatDoctor != null) {
        val doc = activeChatDoctor!!
        var chatInput by remember { mutableStateOf("") }
        var messages by remember {
            mutableStateOf(
                listOf(
                    "Habari! Naitwa ${doc.name}. Nimepokea ombi lako la ushauri wa kitaalamu. Karibu, nieleze changamoto unayoiona kwa mtoto wako."
                )
            )
        }
        val scope = rememberCoroutineScope()

        Dialog(onDismissRequest = { activeChatDoctor = null }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .fillMaxHeight(0.85f)
                    .clip(RoundedCornerShape(20.dp)),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = doc.name, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                            Text(text = doc.title, style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = GrowthTeal))
                        }
                        IconButton(onClick = { activeChatDoctor = null }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyColumn(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(messages) { msg ->
                            val isDoc = msg.startsWith("Habari") || msg.startsWith("Nimekuelewa")
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isDoc) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primary,
                                modifier = Modifier.align(if (isDoc) Alignment.Start else Alignment.End)
                            ) {
                                Text(
                                    text = msg,
                                    color = if (isDoc) MaterialTheme.colorScheme.onSurface else Color.White,
                                    modifier = Modifier.padding(12.dp),
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = chatInput,
                            onValueChange = { chatInput = it },
                            placeholder = { Text("Andika ujumbe kwa daktari...", fontSize = 12.sp) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        IconButton(
                            onClick = {
                                if (chatInput.isNotBlank()) {
                                    val text = chatInput
                                    messages = messages + text
                                    chatInput = ""
                                    scope.launch {
                                        delay(1200)
                                        messages = messages + "Nimekuelewa vizuri. Ninakagua rekodi za ukuaji za mtoto na ninaandaa muongozo wa uchunguzi wa neva unaofaa."
                                    }
                                }
                            },
                            modifier = Modifier.size(42.dp).background(MaterialTheme.colorScheme.primary, CircleShape)
                        ) {
                            Icon(imageVector = Icons.Default.Send, contentDescription = "Send", tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }
    }

    // Video/Voice Call Scheduler Dialog
    if (activeCallDoctor != null) {
        val doc = activeCallDoctor!!
        var selectedSlot by remember { mutableStateOf(doc.availableTimes.firstOrNull() ?: "10:00 AM") }
        var isBooked by remember { mutableStateOf(false) }

        Dialog(onDismissRequest = { activeCallDoctor = null }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .clip(RoundedCornerShape(20.dp)),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = if (language == "sw") "Panga Miadi ya Video na ${doc.name}" else "Book Video Consultation",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "${doc.title} • Ada: ${doc.consultationFee}",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.primary)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    if (!isBooked) {
                        Text(
                            text = if (language == "sw") "Muda Unaopatikana Leo / Kesho:" else "Available Slots:",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            doc.availableTimes.forEach { slot ->
                                FilterChip(
                                    selected = selectedSlot == slot,
                                    onClick = { selectedSlot = slot },
                                    label = { Text(slot, fontSize = 11.sp) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Button(
                            onClick = { isBooked = true },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.VideoCall, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = if (language == "sw") "Thibitisha Miadi ya Simu" else "Confirm Appointment")
                        }
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = GrowthTeal, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = if (language == "sw") "Miadi Imethibitishwa!" else "Appointment Confirmed!",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (language == "sw")
                                    "Simu ya video itafanyika saa $selectedSlot. Utapokea kiungo cha siri cha Google Meet/WhatsApp ndani ya app."
                                else
                                    "Your consultation is locked at $selectedSlot. You will receive an encrypted video link.",
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Button(onClick = { activeCallDoctor = null }) {
                                Text("Sawa")
                            }
                        }
                    }
                }
            }
        }
    }

    // Ask Question Dialog
    if (showAskQuestionDialog) {
        var questionText by remember { mutableStateOf("") }
        var submitted by remember { mutableStateOf(false) }

        Dialog(onDismissRequest = { showAskQuestionDialog = false }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .clip(RoundedCornerShape(20.dp)),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = if (language == "sw") "Uliza Swali la Afya ya Mtoto" else "Submit Clinical Question",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    if (!submitted) {
                        OutlinedTextField(
                            value = questionText,
                            onValueChange = { questionText = it },
                            placeholder = { Text(if (language == "sw") "Elezea kwa kina swali lako na umri wa mtoto..." else "Detail your concern...") },
                            minLines = 4,
                            maxLines = 6,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = { submitted = true },
                            modifier = Modifier.fillMaxWidth().height(46.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(text = if (language == "sw") "Tuma Swali kwa Madaktari" else "Submit Question")
                        }
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = GrowthTeal, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (language == "sw") "Swali Limetumwa kwa Ufanisi!" else "Question Received!",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (language == "sw") "Madaktari wetu watalikagua na kutoa jibu lililothibitishwa." else "Our clinical team will reply promptly.",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(onClick = { showAskQuestionDialog = false }) {
                                Text("Funga")
                            }
                        }
                    }
                }
            }
        }
    }
}
