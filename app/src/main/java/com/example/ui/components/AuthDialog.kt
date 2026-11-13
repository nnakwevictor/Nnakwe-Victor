package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.HavenGold
import com.example.ui.theme.HavenNavy

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthDialog(
    onLogin: (email: String) -> Unit,
    onRegister: (name: String, email: String, phone: String, role: String, agencyName: String?, country: String) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    // Login state
    var loginEmail by remember { mutableStateOf("") }
    var loginPassword by remember { mutableStateOf("") }
    var loginPasswordVisible by remember { mutableStateOf(false) }

    // Register state
    var registerName by remember { mutableStateOf("") }
    var registerEmail by remember { mutableStateOf("") }
    var registerPhone by remember { mutableStateOf("") }
    var registerPassword by remember { mutableStateOf("") }
    var registerPasswordVisible by remember { mutableStateOf(false) }
    var registerRole by remember { mutableStateOf("customer") } // customer or agent
    var registerAgencyName by remember { mutableStateOf("") }
    var registerCountry by remember { mutableStateOf("NG") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            Text(
                text = "Haven Authentication",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = HavenNavy
            )
            Text(
                text = "Sign in or register as a Customer or Licensed Real Estate Agent",
                fontSize = 12.sp,
                color = Color(0xFF64748B),
                modifier = Modifier.padding(top = 2.dp, bottom = 14.dp)
            )

            // Tab Row: Sign In vs Create Account
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = Color.White,
                contentColor = HavenNavy,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = HavenGold,
                        height = 3.dp
                    )
                }
            ) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    text = {
                        Text(
                            text = "Log In",
                            fontWeight = if (selectedTabIndex == 0) FontWeight.Bold else FontWeight.Medium
                        )
                    },
                    modifier = Modifier.testTag("auth_tab_login")
                )
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    text = {
                        Text(
                            text = "Register",
                            fontWeight = if (selectedTabIndex == 1) FontWeight.Bold else FontWeight.Medium
                        )
                    },
                    modifier = Modifier.testTag("auth_tab_register")
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (selectedTabIndex == 0) {
                // Login Form
                OutlinedTextField(
                    value = loginEmail,
                    onValueChange = { loginEmail = it },
                    label = { Text("Email Address") },
                    placeholder = { Text("e.g. buyer@haven.ng or agent@haven.ng") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = HavenNavy) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("login_email_input"),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = loginPassword,
                    onValueChange = { loginPassword = it },
                    label = { Text("Password") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = HavenNavy) },
                    trailingIcon = {
                        IconButton(onClick = { loginPasswordVisible = !loginPasswordVisible }) {
                            Icon(
                                imageVector = if (loginPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = "Toggle password"
                            )
                        }
                    },
                    visualTransformation = if (loginPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("login_password_input"),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        if (loginEmail.isNotBlank()) {
                            onLogin(loginEmail)
                            onDismiss()
                        }
                    },
                    enabled = loginEmail.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = HavenNavy),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("login_submit_btn")
                ) {
                    Text("Log In", color = HavenGold, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Quick Login suggestions
                Text(
                    text = "Demo Verified Accounts:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF64748B)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFF1F5F9),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { loginEmail = "chidi.okonkwo@havenbuyer.com"; loginPassword = "password123" }
                    ) {
                        Text(
                            text = "Customer (Chidi)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = HavenNavy,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFF1F5F9),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { loginEmail = "amina.bello@havenluxury.ng"; loginPassword = "password123" }
                    ) {
                        Text(
                            text = "Agent (Amina)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = HavenNavy,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                // Register Form
                // Account Type Selector: Customer vs Agent
                Text(
                    text = "I am registering as:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = HavenNavy
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (registerRole == "customer") HavenNavy else Color(0xFFF1F5F9),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { registerRole = "customer" }
                            .testTag("register_role_customer")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = if (registerRole == "customer") HavenGold else Color(0xFF475569),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Customer",
                                color = if (registerRole == "customer") HavenGold else Color(0xFF475569),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (registerRole == "agent") HavenNavy else Color(0xFFF1F5F9),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { registerRole = "agent" }
                            .testTag("register_role_agent")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.BusinessCenter,
                                contentDescription = null,
                                tint = if (registerRole == "agent") HavenGold else Color(0xFF475569),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Agent",
                                color = if (registerRole == "agent") HavenGold else Color(0xFF475569),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = registerName,
                    onValueChange = { registerName = it },
                    label = { Text("Full Name") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = HavenNavy) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("register_name_input"),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )

                if (registerRole == "agent") {
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = registerAgencyName,
                        onValueChange = { registerAgencyName = it },
                        label = { Text("Agency / Firm Name") },
                        placeholder = { Text("e.g. Haven Prime Properties") },
                        leadingIcon = { Icon(Icons.Default.BusinessCenter, contentDescription = null, tint = HavenNavy) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("register_agency_input"),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = registerEmail,
                    onValueChange = { registerEmail = it },
                    label = { Text("Email Address") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = HavenNavy) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("register_email_input"),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = registerPhone,
                    onValueChange = { registerPhone = it },
                    label = { Text("Phone Number") },
                    placeholder = { Text("+234 803 000 0000") },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = HavenNavy) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("register_phone_input"),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Country selector
                Text(
                    text = "Primary Territory:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = HavenNavy
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val countryCodes = listOf("NG" to "NG 🇳🇬", "US" to "US 🇺🇸", "GB" to "UK 🇬🇧", "AE" to "UAE 🇦🇪")
                    countryCodes.forEach { (code, label) ->
                        val isSel = registerCountry == code
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSel) HavenNavy else Color(0xFFF1F5F9),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { registerCountry = code }
                        ) {
                            Text(
                                text = label,
                                color = if (isSel) HavenGold else Color(0xFF334155),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 8.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = registerPassword,
                    onValueChange = { registerPassword = it },
                    label = { Text("Password") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = HavenNavy) },
                    trailingIcon = {
                        IconButton(onClick = { registerPasswordVisible = !registerPasswordVisible }) {
                            Icon(
                                imageVector = if (registerPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = "Toggle password"
                            )
                        }
                    },
                    visualTransformation = if (registerPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("register_password_input"),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        if (registerName.isNotBlank() && registerEmail.isNotBlank()) {
                            onRegister(
                                registerName,
                                registerEmail,
                                registerPhone,
                                registerRole,
                                registerAgencyName.ifBlank { null },
                                registerCountry
                            )
                            onDismiss()
                        }
                    },
                    enabled = registerName.isNotBlank() && registerEmail.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = HavenNavy),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("register_submit_btn")
                ) {
                    val btnText = if (registerRole == "agent") "Register as Licensed Agent" else "Create Customer Account"
                    Text(btnText, color = HavenGold, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
