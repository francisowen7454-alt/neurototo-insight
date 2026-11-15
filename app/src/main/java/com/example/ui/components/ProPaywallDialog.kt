package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import android.widget.Toast
import com.example.model.MobilePaymentProvider
import com.example.model.PaymentConstants
import com.example.model.SubscriptionTier
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ProPaywallDialog(
    language: String,
    currentDaysRemaining: Int,
    onDismiss: () -> Unit,
    onActivatePlan: (SubscriptionTier) -> Unit
) {
    val context = LocalContext.current
    var selectedTier by remember { mutableStateOf(SubscriptionTier.MONTHLY) }
    var selectedProvider by remember { mutableStateOf(MobilePaymentProvider.MPESA_TZ) }
    var phoneNumber by remember { mutableStateOf("0754 123 456") }
    var transactionReference by remember { mutableStateOf("") }
    var isProcessing by remember { mutableStateOf(false) }
    var paymentStep by remember { mutableStateOf("form") } // form, ussd_prompt, success
    val coroutineScope = rememberCoroutineScope()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.90f)
                .clip(RoundedCornerShape(24.dp)),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header with Close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = WarmAmberContainer,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Stars,
                                    contentDescription = null,
                                    tint = WarmAmber,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "NeuroToto PRO",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                            Text(
                                text = if (language == "sw") "Huduma Kamili za Kitaalamu" else "Complete Specialist Access",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_paywall_button")
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Free Trial Highlight Banner
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
                            imageVector = Icons.Default.CardGiftcard,
                            contentDescription = null,
                            tint = GrowthTeal,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (language == "sw") "Wiki ya 1 Bure Kabisa! (7 Days Free Trial)" else "First Week 100% Free! (7-Day Trial)",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = GrowthTeal
                                )
                            )
                            Text(
                                text = if (language == "sw")
                                    "Furahia huduma zote bila malipo kwa siku 7. Malipo ya simu yataanza tu baada ya wiki ya kwanza kuisha."
                                else
                                    "Enjoy all premium features for 7 days. Mobile money billing begins only after your free trial ends.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                when (paymentStep) {
                    "form" -> {
                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            // Pro Features Checklist
                            item {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(
                                        text = if (language == "sw") "Vipengele vya NeuroToto PRO:" else "NeuroToto PRO Features:",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                    )
                                    val benefits = if (language == "sw") listOf(
                                        "Ushauri wa moja kwa moja na madaktari bingwa wa watoto (Chat & Video Call)",
                                        "Video zote za ndani ya app na masomo ya kina ya neva na malezi",
                                        "TotoAI bila kikomo: majibu yaliyobinafsishwa kulingana na umri wa mtoto",
                                        "Ufuatiliaji wa hatua zote za ukuaji na taarifa za dharura (Red Flags)",
                                        "Arifa za kila siku na mazoezi ya vitendo ya kukuza ubongo nyumbani"
                                    ) else listOf(
                                        "Direct chat & video consultations with pediatric specialists",
                                        "Full video lessons archive on neurobiology & digital parenting",
                                        "Unlimited TotoAI clinical queries tailored to your child",
                                        "Comprehensive milestone tracker & red flag neurological alerts",
                                        "Daily smart notifications & home brain stimulation routines"
                                    )

                                    benefits.forEach { benefit ->
                                        Row(
                                            verticalAlignment = Alignment.Top,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = null,
                                                tint = GrowthTeal,
                                                modifier = Modifier
                                                    .size(16.dp)
                                                    .padding(top = 2.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = benefit,
                                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp)
                                            )
                                        }
                                    }
                                }
                            }

                            // Plan Selection
                            item {
                                Text(
                                    text = if (language == "sw") "Chagua Mpango wa Usajili:" else "Choose Subscription Plan:",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    // Monthly
                                    Surface(
                                        shape = RoundedCornerShape(14.dp),
                                        color = if (selectedTier == SubscriptionTier.MONTHLY)
                                            MaterialTheme.colorScheme.primaryContainer
                                        else MaterialTheme.colorScheme.surfaceVariant,
                                        border = if (selectedTier == SubscriptionTier.MONTHLY)
                                            androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                                        else null,
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { selectedTier = SubscriptionTier.MONTHLY }
                                            .testTag("plan_monthly")
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Text(
                                                text = if (language == "sw") "Kila Mwezi" else "Monthly",
                                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                            )
                                            Text(
                                                text = "TZS 15,000",
                                                style = MaterialTheme.typography.titleMedium.copy(
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                            )
                                            Text(
                                                text = "au KES 850 / mwezi",
                                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp)
                                            )
                                        }
                                    }

                                    // Annual
                                    Surface(
                                        shape = RoundedCornerShape(14.dp),
                                        color = if (selectedTier == SubscriptionTier.ANNUAL)
                                            MaterialTheme.colorScheme.primaryContainer
                                        else MaterialTheme.colorScheme.surfaceVariant,
                                        border = if (selectedTier == SubscriptionTier.ANNUAL)
                                            androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                                        else null,
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { selectedTier = SubscriptionTier.ANNUAL }
                                            .testTag("plan_annual")
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(
                                                    text = if (language == "sw") "Mwaka Mzima" else "Annual",
                                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                                )
                                                Surface(
                                                    color = WarmAmber,
                                                    shape = RoundedCornerShape(6.dp)
                                                ) {
                                                    Text(
                                                        text = "-33%",
                                                        color = Color.White,
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }
                                            Text(
                                                text = "TZS 120,000",
                                                style = MaterialTheme.typography.titleMedium.copy(
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                            )
                                            Text(
                                                text = "au KES 6,800 / mwaka",
                                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp)
                                            )
                                        }
                                    }
                                }
                            }

                            // Payment Method Selection (East Africa Mobile Money)
                            item {
                                Text(
                                    text = if (language == "sw") "Njia Rahisi ya Malipo ya Simu:" else "Mobile Money Provider:",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                )
                                Spacer(modifier = Modifier.height(6.dp))

                                val providers = listOf(
                                    MobilePaymentProvider.MPESA_TZ,
                                    MobilePaymentProvider.TIGO_PESA,
                                    MobilePaymentProvider.AIRTEL_MONEY_TZ,
                                    MobilePaymentProvider.MPESA_KE
                                )

                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    providers.forEach { provider ->
                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = if (selectedProvider == provider)
                                                GrowthTealContainer
                                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                            border = if (selectedProvider == provider)
                                                androidx.compose.foundation.BorderStroke(1.5.dp, GrowthTeal)
                                            else null,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable { selectedProvider = provider }
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(
                                                        imageVector = Icons.Default.PhoneAndroid,
                                                        contentDescription = null,
                                                        tint = if (selectedProvider == provider) GrowthTeal else MaterialTheme.colorScheme.onSurfaceVariant,
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text(
                                                        text = provider.displayName,
                                                        style = MaterialTheme.typography.bodyMedium.copy(
                                                            fontWeight = if (selectedProvider == provider) FontWeight.Bold else FontWeight.Normal,
                                                            fontSize = 12.sp
                                                        )
                                                    )
                                                }
                                                if (selectedProvider == provider) {
                                                    Icon(
                                                        imageVector = Icons.Default.CheckCircle,
                                                        contentDescription = null,
                                                        tint = GrowthTeal,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            // Official Lipa Namba Card
                            item {
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = WarmAmberContainer.copy(alpha = 0.6f),
                                    border = androidx.compose.foundation.BorderStroke(1.5.dp, WarmAmber),
                                    modifier = Modifier.fillMaxWidth().testTag("lipa_namba_banner")
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.QrCodeScanner,
                                                    contentDescription = null,
                                                    tint = WarmAmber,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = if (language == "sw") "LIPA KWA SIMU / LIPA NAMBA" else "MERCHANT LIPA NAMBA",
                                                    style = MaterialTheme.typography.labelMedium.copy(
                                                        fontWeight = FontWeight.ExtraBold,
                                                        color = MaterialTheme.colorScheme.onTertiaryContainer
                                                    )
                                                )
                                            }
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = WarmAmber
                                            ) {
                                                Text(
                                                    text = "OFFICIAL",
                                                    color = Color.White,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text(
                                                    text = if (language == "sw") "Namba ya Malipo:" else "Till / Paybill Number:",
                                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp)
                                                )
                                                Text(
                                                    text = PaymentConstants.LIPA_NAMBA,
                                                    style = MaterialTheme.typography.titleLarge.copy(
                                                        fontWeight = FontWeight.ExtraBold,
                                                        color = MaterialTheme.colorScheme.primary,
                                                        letterSpacing = 1.5.sp
                                                    )
                                                )
                                                Text(
                                                    text = "Jina: ${PaymentConstants.ACCOUNT_NAME}",
                                                    style = MaterialTheme.typography.bodyMedium.copy(
                                                        fontWeight = FontWeight.Bold,
                                                        color = GrowthTeal
                                                    )
                                                )
                                            }

                                            FilledTonalButton(
                                                onClick = {
                                                    val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as? android.content.ClipboardManager
                                                    val clip = android.content.ClipData.newPlainText("Lipa Namba", PaymentConstants.LIPA_NAMBA)
                                                    clipboard?.setPrimaryClip(clip)
                                                    Toast.makeText(
                                                        context,
                                                        if (language == "sw") "Lipa Namba 67757495 imenakiliwa!" else "Lipa Namba copied to clipboard!",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                },
                                                shape = RoundedCornerShape(10.dp),
                                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                                modifier = Modifier.testTag("copy_lipa_namba_btn")
                                            ) {
                                                Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(text = if (language == "sw") "Nakili" else "Copy", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))

                                        val ussdGuide = when (selectedProvider) {
                                            MobilePaymentProvider.MPESA_TZ -> if (language == "sw")
                                                "M-Pesa: Piga *150*00# -> 4 (Lipa kwa Simu) -> 1 (Weka Lipa Namba: 67757495 - Owen Mmehwa) -> Weka Kiasi -> Weka PIN"
                                            else "M-Pesa: Dial *150*00# -> 4 (Pay by Phone) -> 1 (Till 67757495 - Owen Mmehwa) -> Enter PIN"
                                            MobilePaymentProvider.TIGO_PESA -> if (language == "sw")
                                                "Tigo Pesa / Mixx: Piga *150*01# -> 5 (Lipa kwa Simu) -> Weka 67757495 (Owen Mmehwa) -> Weka PIN"
                                            else "Tigo Pesa: Dial *150*01# -> 5 (Lipa kwa Simu) -> Enter 67757495 (Owen Mmehwa)"
                                            MobilePaymentProvider.AIRTEL_MONEY_TZ -> if (language == "sw")
                                                "Airtel Money: Piga *150*60# -> 5 (Lipa kwa Simu) -> Weka 67757495 (Owen Mmehwa) -> Weka PIN"
                                            else "Airtel Money: Dial *150*60# -> 5 (Lipa kwa Simu) -> Enter 67757495 (Owen Mmehwa)"
                                            else -> if (language == "sw")
                                                "Mitandao yote: Tumia Lipa Namba 67757495 (Jina: Owen Mmehwa)"
                                            else "All networks: Use Till 67757495 (Name: Owen Mmehwa)"
                                        }

                                        Text(
                                            text = ussdGuide,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        )
                                    }
                                }
                            }

                            // Phone Number Input
                            item {
                                OutlinedTextField(
                                    value = phoneNumber,
                                    onValueChange = { phoneNumber = it },
                                    label = { Text(if (language == "sw") "Namba ya Simu ya Mzazi" else "Parent's Mobile Number") },
                                    placeholder = { Text("07XX XXX XXX au +254...") },
                                    leadingIcon = {
                                        Icon(imageVector = Icons.Default.PhoneAndroid, contentDescription = null)
                                    },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("paywall_phone_input"),
                                    shape = RoundedCornerShape(12.dp),
                                    singleLine = true
                                )
                            }

                            // Transaction Reference ID (Optional confirmation)
                            item {
                                OutlinedTextField(
                                    value = transactionReference,
                                    onValueChange = { transactionReference = it },
                                    label = { Text(if (language == "sw") "Namba ya Kumbukumbu ya Muamala (Hiari)" else "Transaction Reference ID (Optional)") },
                                    placeholder = { Text("Mfano: 8NK94JX729") },
                                    leadingIcon = {
                                        Icon(imageVector = Icons.Default.ReceiptLong, contentDescription = null)
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("paywall_reference_input"),
                                    shape = RoundedCornerShape(12.dp),
                                    singleLine = true
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Submit Button
                        Button(
                            onClick = {
                                isProcessing = true
                                paymentStep = "ussd_prompt"
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("start_free_trial_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (language == "sw") "Anza Siku 7 za Bure Sasa" else "Start 7-Day Free Trial Now",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }

                    "ussd_prompt" -> {
                        // Interactive USSD Push Simulation
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.size(70.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator(
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(46.dp)
                                    )
                                    Icon(
                                        imageVector = Icons.Default.Sensors,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            Text(
                                text = if (language == "sw") "Ombi la USSD Limetumwa kwenye Simu Yako" else "USSD Push Sent to Your Phone",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = if (language == "sw")
                                    "Tafadhali ingiza PIN yako ya ${selectedProvider.displayName} kuthibitisha malipo kwa Lipa Namba 67757495 (Owen Mmehwa)."
                                else
                                    "Please enter your ${selectedProvider.displayName} PIN to authorize payment to Till 67757495 (Owen Mmehwa).",
                                style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.padding(horizontal = 20.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(
                                        text = "SIMULATED PUSH NOTIFICATION",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "${selectedProvider.displayName}: Thibitisha usajili wa NeuroToto PRO (Lipa Namba 67757495 - Owen Mmehwa). Siku 7 za mwanzo: TZS 0. Weka namba ya siri.",
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            Button(
                                onClick = {
                                    paymentStep = "success"
                                    coroutineScope.launch {
                                        delay(1500)
                                        onActivatePlan(selectedTier)
                                        onDismiss()
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("confirm_ussd_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = GrowthTeal)
                            ) {
                                Text(
                                    text = if (language == "sw") "Nimeshaingiza PIN (Thibitisha)" else "I Have Entered PIN (Confirm)",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    "success" -> {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = GrowthTeal,
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = if (language == "sw") "Hongera! Umejiunga na NeuroToto PRO" else "Welcome to NeuroToto PRO!",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (language == "sw")
                                    "Huduma zote zimefunguliwa kikamilifu. Anza kujifunza na kushauriana na madaktari sasa!"
                                else
                                    "All specialist access, videos, and AI features are now active.",
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}
