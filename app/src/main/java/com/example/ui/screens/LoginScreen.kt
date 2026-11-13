package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import com.example.R
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.HavenRepository
import com.example.ui.theme.HavenGold
import com.example.ui.theme.HavenGoldDark
import com.example.ui.theme.HavenGreyBorder
import com.example.ui.theme.HavenGreyDark
import com.example.ui.theme.HavenGreyLight
import com.example.ui.theme.HavenGreyMuted
import com.example.ui.theme.HavenGreyText
import com.example.ui.theme.HavenNavy
import com.example.ui.theme.HavenNavyDark
import com.example.ui.theme.HavenNavyLight
import com.example.ui.theme.PlayfairFontFamily
import com.example.ui.theme.PlusJakartaFontFamily
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    repository: HavenRepository,
    onLoginSuccess: () -> Unit,
    onContinueAsGuest: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    // Login state
    var loginEmail by remember { mutableStateOf("chidi.okonkwo@havenbuyer.com") }
    var loginPassword by remember { mutableStateOf("password123") }
    var loginPasswordVisible by remember { mutableStateOf(false) }

    // Register state
    var registerName by remember { mutableStateOf("") }
    var registerEmail by remember { mutableStateOf("") }
    var registerPhone by remember { mutableStateOf("") }
    var registerPassword by remember { mutableStateOf("") }
    var registerPasswordVisible by remember { mutableStateOf(false) }
    var registerRole by remember { mutableStateOf("customer") }
    var registerAgency by remember { mutableStateOf("") }
    var registerCountry by remember { mutableStateOf("NG") }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFAFAF8))
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Brand Crest: PROJECT X
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(HavenNavyDark)
                .border(1.dp, HavenGold.copy(alpha = 0.6f), RoundedCornerShape(4.dp)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.project_x_emblem_1794371429900),
                contentDescription = "Project X Crest",
                modifier = Modifier.size(54.dp),
                contentScale = androidx.compose.ui.layout.ContentScale.Fit
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "PROJECT X",
            fontFamily = PlayfairFontFamily,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 4.sp,
            color = HavenNavyDark
        )
        Text(
            text = "GLOBAL PROPERTY GROUP",
            fontFamily = PlusJakartaFontFamily,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp,
            color = HavenGoldDark
        )
        Text(
            text = "A New World of Property Opportunities",
            fontFamily = PlusJakartaFontFamily,
            fontSize = 10.5.sp,
            fontWeight = FontWeight.Medium,
            color = HavenGreyText,
            modifier = Modifier.padding(top = 2.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Tab Row: Log In / Register
        Surface(
            color = Color.White,
            shape = RoundedCornerShape(2.dp),
            border = BorderStroke(1.dp, HavenGreyBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selectedTabIndex = 0 }
                        .background(if (selectedTabIndex == 0) HavenNavyDark else Color.White)
                        .padding(vertical = 12.dp)
                        .testTag("nav_tab_login"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "CLIENT SIGN IN",
                        fontFamily = PlusJakartaFontFamily,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = if (selectedTabIndex == 0) HavenGold else HavenGreyText
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selectedTabIndex = 1 }
                        .background(if (selectedTabIndex == 1) HavenNavyDark else Color.White)
                        .padding(vertical = 12.dp)
                        .testTag("nav_tab_register"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "REGISTRATION",
                        fontFamily = PlusJakartaFontFamily,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = if (selectedTabIndex == 1) HavenGold else HavenGreyText
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (errorMessage != null) {
            Surface(
                shape = RoundedCornerShape(2.dp),
                color = Color(0xFFFEF2F2),
                border = BorderStroke(1.dp, Color(0xFFFCA5A5)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                Text(
                    text = errorMessage ?: "",
                    fontFamily = PlusJakartaFontFamily,
                    color = Color(0xFF991B1B),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        if (selectedTabIndex == 0) {
            // Login Form
            OutlinedTextField(
                value = loginEmail,
                onValueChange = { loginEmail = it },
                label = { Text("Client Email Address", fontFamily = PlusJakartaFontFamily, fontSize = 12.sp) },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = HavenNavyDark, modifier = Modifier.size(16.dp)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("login_email_input"),
                shape = RoundedCornerShape(2.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = HavenNavyDark,
                    unfocusedBorderColor = HavenGreyBorder,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = loginPassword,
                onValueChange = { loginPassword = it },
                label = { Text("Password", fontFamily = PlusJakartaFontFamily, fontSize = 12.sp) },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = HavenNavyDark, modifier = Modifier.size(16.dp)) },
                trailingIcon = {
                    IconButton(onClick = { loginPasswordVisible = !loginPasswordVisible }) {
                        Icon(
                            imageVector = if (loginPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = "Toggle password visibility",
                            tint = HavenGreyMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                },
                visualTransformation = if (loginPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("login_password_input"),
                shape = RoundedCornerShape(2.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = HavenNavyDark,
                    unfocusedBorderColor = HavenGreyBorder,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = {
                    coroutineScope.launch {
                        val loggedIn = repository.loginUser(loginEmail.trim())
                        if (loggedIn != null) {
                            errorMessage = null
                            onLoginSuccess()
                        } else {
                            errorMessage = "No client or broker account registered for $loginEmail. Use demonstration profiles or register below."
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = HavenNavyDark),
                shape = RoundedCornerShape(2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("login_submit_btn")
            ) {
                Text("AUTHENTICATE", fontFamily = PlusJakartaFontFamily, color = HavenGold, fontWeight = FontWeight.Bold, fontSize = 12.sp, letterSpacing = 1.sp)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Quick Demo Persona Selection
            Text(
                text = "DEMONSTRATION ACCESS CREDENTIALS",
                fontFamily = PlusJakartaFontFamily,
                fontSize = 9.5.sp,
                color = HavenGreyMuted,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(2.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, HavenGreyBorder),
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            loginEmail = "chidi.okonkwo@havenbuyer.com"
                            loginPassword = "password123"
                        }
                ) {
                    Text("Client", fontFamily = PlusJakartaFontFamily, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = HavenNavyDark, textAlign = TextAlign.Center, modifier = Modifier.padding(8.dp))
                }
                Surface(
                    shape = RoundedCornerShape(2.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, HavenGreyBorder),
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            loginEmail = "amina.bello@havenluxury.ng"
                            loginPassword = "password123"
                        }
                ) {
                    Text("Broker", fontFamily = PlusJakartaFontFamily, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = HavenNavyDark, textAlign = TextAlign.Center, modifier = Modifier.padding(8.dp))
                }
                Surface(
                    shape = RoundedCornerShape(2.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, HavenGreyBorder),
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            loginEmail = "nnakwevictor90@gmail.com"
                            loginPassword = "password123"
                        }
                ) {
                    Text("Admin", fontFamily = PlusJakartaFontFamily, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = HavenNavyDark, textAlign = TextAlign.Center, modifier = Modifier.padding(8.dp))
                }
            }
        } else {
            // Register Form
            // Intended Marketplace Participation Choice (Section 10 of brief)
            Text(
                text = "WHAT WOULD YOU LIKE TO DO?",
                fontFamily = PlusJakartaFontFamily,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = HavenGoldDark,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(2.dp),
                    color = if (registerRole == "customer") HavenNavyDark else Color.White,
                    border = BorderStroke(1.dp, if (registerRole == "customer") HavenNavyDark else HavenGreyBorder),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { registerRole = "customer" }
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(10.dp)
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = if (registerRole == "customer") HavenGold else HavenGreyText, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Find a Property", fontFamily = PlusJakartaFontFamily, color = if (registerRole == "customer") HavenGold else HavenGreyText, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
                Surface(
                    shape = RoundedCornerShape(2.dp),
                    color = if (registerRole == "agent") HavenNavyDark else Color.White,
                    border = BorderStroke(1.dp, if (registerRole == "agent") HavenNavyDark else HavenGreyBorder),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { registerRole = "agent" }
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(10.dp)
                    ) {
                        Icon(Icons.Default.BusinessCenter, contentDescription = null, tint = if (registerRole == "agent") HavenGold else HavenGreyText, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Join as Broker / Agent", fontFamily = PlusJakartaFontFamily, color = if (registerRole == "agent") HavenGold else HavenGreyText, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Preferred Country Jurisdiction Selector
            Surface(
                shape = RoundedCornerShape(2.dp),
                color = Color.White,
                border = BorderStroke(1.dp, HavenGreyBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val c = com.example.data.model.GlobalJurisdictionCatalog.getCountry(registerCountry)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = c?.flagEmoji ?: "🌐", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Operating Jurisdiction: ${c?.name ?: registerCountry}",
                                fontFamily = PlusJakartaFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = HavenNavyDark
                            )
                            Text(
                                text = "Settlement Currency: ${c?.defaultCurrency ?: "USD"} • Code: ${c?.callingCode ?: "+1"}",
                                fontFamily = PlusJakartaFontFamily,
                                fontSize = 10.5.sp,
                                color = HavenGreyText
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = registerName,
                onValueChange = { registerName = it },
                label = { Text("Full Legal Name", fontFamily = PlusJakartaFontFamily, fontSize = 12.sp) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(2.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = HavenNavyDark,
                    unfocusedBorderColor = HavenGreyBorder,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            if (registerRole == "agent") {
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = registerAgency,
                    onValueChange = { registerAgency = it },
                    label = { Text("Brokerage / Firm", fontFamily = PlusJakartaFontFamily, fontSize = 12.sp) },
                    placeholder = { Text("E.g., PROJECT X Prime Partners", fontFamily = PlusJakartaFontFamily, fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(2.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = HavenNavyDark,
                        unfocusedBorderColor = HavenGreyBorder,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = registerEmail,
                onValueChange = { registerEmail = it },
                label = { Text("Email Address", fontFamily = PlusJakartaFontFamily, fontSize = 12.sp) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(2.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = HavenNavyDark,
                    unfocusedBorderColor = HavenGreyBorder,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = registerPhone,
                onValueChange = { registerPhone = it },
                label = { Text("Phone Number", fontFamily = PlusJakartaFontFamily, fontSize = 12.sp) },
                placeholder = { Text("+234 800 000 0000", fontFamily = PlusJakartaFontFamily, fontSize = 12.sp) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(2.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = HavenNavyDark,
                    unfocusedBorderColor = HavenGreyBorder,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    coroutineScope.launch {
                        repository.registerUser(
                            name = registerName,
                            email = registerEmail,
                            phone = registerPhone,
                            role = registerRole,
                            agencyName = registerAgency.ifBlank { null },
                            country = registerCountry
                        )
                        onLoginSuccess()
                    }
                },
                enabled = registerName.isNotBlank() && registerEmail.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = HavenNavyDark),
                shape = RoundedCornerShape(2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text(
                    text = if (registerRole == "agent") "REGISTER CERTIFIED BROKER" else "CREATE CLIENT ACCOUNT",
                    fontFamily = PlusJakartaFontFamily,
                    color = HavenGold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    letterSpacing = 1.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Continue as Guest / Public Visitor
        OutlinedButton(
            onClick = {
                coroutineScope.launch {
                    repository.setCurrentUser(null)
                    onContinueAsGuest()
                }
            },
            shape = RoundedCornerShape(2.dp),
            border = BorderStroke(1.dp, HavenGreyBorder),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("continue_as_guest_btn")
        ) {
            Text("CONTINUE AS GUEST CLIENT", fontFamily = PlusJakartaFontFamily, color = HavenNavyDark, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp)
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}
