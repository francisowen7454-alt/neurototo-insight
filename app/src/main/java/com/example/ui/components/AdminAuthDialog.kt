package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.NeuroRepository
import com.example.ui.theme.AlertCoral
import com.example.ui.theme.GrowthTeal
import com.example.ui.theme.GrowthTealContainer

@Composable
fun AdminAuthDialog(
    language: String,
    onDismiss: () -> Unit,
    onAuthenticated: () -> Unit
) {
    var isRegisterMode by remember { mutableStateOf(false) } // false = Login, true = Register

    // Login state
    var loginIdentifier by remember { mutableStateOf("+255759237454") }
    var loginPassword by remember { mutableStateOf("") }
    var loginPasswordVisible by remember { mutableStateOf(false) }
    var loginError by remember { mutableStateOf<String?>(null) }

    // Register state
    var regFullName by remember { mutableStateOf("") }
    var regIdentifier by remember { mutableStateOf("") }
    var regPassword by remember { mutableStateOf("") }
    var regConfirmPassword by remember { mutableStateOf("") }
    var regPasswordVisible by remember { mutableStateOf(false) }
    var regError by remember { mutableStateOf<String?>(null) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.85f)
                .clip(RoundedCornerShape(24.dp)),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(22.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (isRegisterMode) Icons.Default.PersonAdd else Icons.Default.AdminPanelSettings,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (isRegisterMode)
                                    (if (language == "sw") "Usajili wa Mmiliki Mkuu" else "Register Owner Account")
                                else
                                    (if (language == "sw") "Kuingia kwa Mmiliki" else "Admin Portal Login"),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = if (language == "sw") "Dashibodi ya NeuroToto Insight" else "Owner CMS & Analytics",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Toggle Tabs: Kuingia (Login) vs Kujisajili (Register)
                TabRow(
                    selectedTabIndex = if (isRegisterMode) 1 else 0,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.clip(RoundedCornerShape(12.dp))
                ) {
                    Tab(
                        selected = !isRegisterMode,
                        onClick = { isRegisterMode = false },
                        text = {
                            Text(
                                text = if (language == "sw") "Ingia (Login)" else "Sign In",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    )
                    Tab(
                        selected = isRegisterMode,
                        onClick = { isRegisterMode = true },
                        text = {
                            Text(
                                text = if (language == "sw") "Jisajili (Register)" else "Create Account",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (!isRegisterMode) {
                        // LOGIN FORM
                        item {
                            Text(
                                text = if (language == "sw")
                                    "Weka barua pepe au namba ya simu na nenosiri lako la mmiliki:"
                                else
                                    "Enter your registered owner email/phone and password:",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                        }

                        item {
                            OutlinedTextField(
                                value = loginIdentifier,
                                onValueChange = {
                                    loginIdentifier = it
                                    loginError = null
                                },
                                label = { Text(if (language == "sw") "Simu au Barua Pepe" else "Phone or Email") },
                                leadingIcon = { Icon(Icons.Default.AccountCircle, contentDescription = null) },
                                modifier = Modifier.fillMaxWidth().testTag("admin_login_id_input"),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true
                            )
                        }

                        item {
                            OutlinedTextField(
                                value = loginPassword,
                                onValueChange = {
                                    loginPassword = it
                                    loginError = null
                                },
                                label = { Text(if (language == "sw") "Nenosiri (Password)" else "Password") },
                                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                                trailingIcon = {
                                    IconButton(onClick = { loginPasswordVisible = !loginPasswordVisible }) {
                                        Icon(
                                            imageVector = if (loginPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                            contentDescription = "Toggle Visibility"
                                        )
                                    }
                                },
                                visualTransformation = if (loginPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                modifier = Modifier.fillMaxWidth().testTag("admin_login_pwd_input"),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true
                            )
                        }

                        if (loginError != null) {
                            item {
                                Text(
                                    text = loginError!!,
                                    color = AlertCoral,
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        }

                        item {
                            // Hint
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (language == "sw")
                                            "Akaunti ya awali ya mmiliki: 'neurototo2026'. Kwa usalama thabiti, tumia tab ya 'Jisajili' kuweka nenosiri lako binafsi lenye herufi na namba."
                                        else
                                            "Initial owner account: 'neurototo2026'. For elevated security, switch to 'Create Account' to establish a private strong password.",
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp)
                                    )
                                }
                            }
                        }
                    } else {
                        // REGISTRATION FORM
                        item {
                            Text(
                                text = if (language == "sw")
                                    "Unda akaunti mpya ya mmiliki kwa ajili ya kuratibu masomo, kuona takwimu za wazazi na kutuma arifa:"
                                else
                                    "Register an official owner account to manage content, analytics, and broadcast notifications:",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                        }

                        item {
                            OutlinedTextField(
                                value = regFullName,
                                onValueChange = {
                                    regFullName = it
                                    regError = null
                                },
                                label = { Text(if (language == "sw") "Jina Kamili la Mmiliki" else "Full Name") },
                                placeholder = { Text("Mfano: Francis Owen") },
                                leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
                                modifier = Modifier.fillMaxWidth().testTag("admin_reg_name_input"),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true
                            )
                        }

                        item {
                            OutlinedTextField(
                                value = regIdentifier,
                                onValueChange = {
                                    regIdentifier = it
                                    regError = null
                                },
                                label = { Text(if (language == "sw") "Namba ya Simu / Barua Pepe" else "Phone Number / Email") },
                                placeholder = { Text("+255 759 237 454") },
                                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                                modifier = Modifier.fillMaxWidth().testTag("admin_reg_phone_input"),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true
                            )
                        }

                        item {
                            OutlinedTextField(
                                value = regPassword,
                                onValueChange = {
                                    regPassword = it
                                    regError = null
                                },
                                label = { Text(if (language == "sw") "Tengeneza Nenosiri Mpya" else "Create Password") },
                                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                                trailingIcon = {
                                    IconButton(onClick = { regPasswordVisible = !regPasswordVisible }) {
                                        Icon(
                                            imageVector = if (regPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                            contentDescription = "Toggle"
                                        )
                                    }
                                },
                                visualTransformation = if (regPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                modifier = Modifier.fillMaxWidth().testTag("admin_reg_pwd_input"),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true
                            )
                        }

                        item {
                            OutlinedTextField(
                                value = regConfirmPassword,
                                onValueChange = {
                                    regConfirmPassword = it
                                    regError = null
                                },
                                label = { Text(if (language == "sw") "Thibitisha Nenosiri" else "Confirm Password") },
                                leadingIcon = { Icon(Icons.Default.LockReset, contentDescription = null) },
                                visualTransformation = if (regPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                modifier = Modifier.fillMaxWidth().testTag("admin_reg_confirm_pwd_input"),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true
                            )
                        }

                        if (regError != null) {
                            item {
                                Text(
                                    text = regError!!,
                                    color = AlertCoral,
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Submit Button
                Button(
                    onClick = {
                        if (!isRegisterMode) {
                            val result = NeuroRepository.loginAdmin(loginIdentifier, loginPassword)
                            if (result.success) {
                                loginError = null
                                onAuthenticated()
                            } else {
                                loginError = result.errorMessage ?: (
                                    if (language == "sw")
                                        "Namba ya simu au nenosiri si sahihi."
                                    else
                                        "Invalid credentials. Please verify your password."
                                )
                            }
                        } else {
                            if (regFullName.isBlank()) {
                                regError = if (language == "sw") "Tafadhali ingiza jina lako kamili." else "Enter your full name."
                            } else if (regIdentifier.isBlank()) {
                                regError = if (language == "sw") "Weka namba ya simu au barua pepe." else "Enter phone or email."
                            } else if (regPassword.length < 8) {
                                regError = if (language == "sw") "Nenosiri liwe na angalau herufi na namba 8." else "Password must be at least 8 chars with letters & numbers."
                            } else if (regPassword != regConfirmPassword) {
                                regError = if (language == "sw") "Nenosiri halilingani na uthibitisho." else "Passwords do not match."
                            } else {
                                val result = NeuroRepository.registerAdmin(regFullName, regIdentifier, regPassword)
                                if (result.success) {
                                    regError = null
                                    onAuthenticated()
                                } else {
                                    regError = result.errorMessage ?: (
                                        if (language == "sw") "Usajili umeshindikana." else "Registration failed."
                                    )
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp).testTag("admin_auth_submit_btn"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (!isRegisterMode)
                            (if (language == "sw") "Ingia Kwenye Dashibodi" else "Log In to Dashboard")
                        else
                            (if (language == "sw") "Kamilisha Usajili wa Mmiliki" else "Complete Registration"),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
