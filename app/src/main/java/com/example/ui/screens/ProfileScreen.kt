package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.enums.UserRole
import com.example.data.model.User
import com.example.ui.theme.HavenGold
import com.example.ui.theme.HavenGoldDark
import com.example.ui.theme.HavenGoldMuted
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
import com.example.ui.theme.StatusSuccess

@Composable
fun ProfileScreen(
    currentUser: User?,
    onSwitchPersonaClick: () -> Unit,
    onOpenAuth: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "ACCOUNT & SECURITY",
            fontFamily = PlusJakartaFontFamily,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = HavenGoldDark,
            letterSpacing = 1.5.sp
        )
        Text(
            text = "Client Identity & Jurisdiction",
            fontFamily = PlayfairFontFamily,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = HavenNavyDark
        )
        Spacer(modifier = Modifier.height(14.dp))

        // 1. Client Identity Card
        Surface(
            shape = RoundedCornerShape(2.dp),
            color = HavenNavyDark,
            border = BorderStroke(1.dp, HavenGold.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .background(HavenGold),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = (currentUser?.name?.take(1) ?: "G"),
                            fontFamily = PlayfairFontFamily,
                            color = HavenNavyDark,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = currentUser?.name ?: "Guest Client",
                                fontFamily = PlayfairFontFamily,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            if (currentUser?.isVerified == true) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = "Verified",
                                    tint = HavenGold,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        val roleTag = when {
                            currentUser == null -> "Unregistered Guest Client"
                            currentUser.isAdmin -> "Global Administrator & Title Notary"
                            currentUser.isAgent -> "Licensed International Broker"
                            else -> "Verified Private Client"
                        }
                        Text(
                            text = roleTag,
                            fontFamily = PlusJakartaFontFamily,
                            fontSize = 11.5.sp,
                            color = HavenGold,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = HavenGreyBorder.copy(alpha = 0.2f), thickness = 0.8.dp)
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onSwitchPersonaClick,
                        shape = RoundedCornerShape(2.dp),
                        border = BorderStroke(1.dp, HavenGold.copy(alpha = 0.6f)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.Default.SwapHoriz, contentDescription = null, tint = HavenGold, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("SWITCH ROLE", fontFamily = PlusJakartaFontFamily, color = HavenGold, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = onOpenAuth,
                        colors = ButtonDefaults.buttonColors(containerColor = HavenNavyLight),
                        shape = RoundedCornerShape(2.dp),
                        border = BorderStroke(1.dp, HavenGold.copy(alpha = 0.6f)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = HavenGold, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (currentUser != null) "ACCOUNT" else "SIGN IN",
                            fontFamily = PlusJakartaFontFamily,
                            color = HavenGold,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // 2. Upgraded Institutional Security Setup (Mark Zuckerberg / Banking grade trust)
        Text(
            text = "SECURITY ARCHITECTURE",
            fontFamily = PlusJakartaFontFamily,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = HavenGoldDark,
            letterSpacing = 1.5.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        Surface(
            shape = RoundedCornerShape(2.dp),
            color = Color.White,
            border = BorderStroke(1.dp, HavenGreyBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                SecurityStatusRow(
                    icon = Icons.Default.Fingerprint,
                    title = "Biometric Passkey Authentication",
                    status = "ACTIVE",
                    statusColor = StatusSuccess,
                    description = "FIDO2 / Credential Manager cryptographic hardware verification."
                )
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = HavenGreyBorder, thickness = 0.8.dp)
                Spacer(modifier = Modifier.height(12.dp))
                SecurityStatusRow(
                    icon = Icons.Default.VpnKey,
                    title = "End-to-End Escrow Protection",
                    status = "ENFORCED",
                    statusColor = StatusSuccess,
                    description = "Wire disbursement protected by multi-signature fiduciary custody."
                )
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = HavenGreyBorder, thickness = 0.8.dp)
                Spacer(modifier = Modifier.height(12.dp))
                SecurityStatusRow(
                    icon = Icons.Default.Security,
                    title = "Land Registry Title Verification",
                    status = "COMPLIANT",
                    statusColor = HavenGoldDark,
                    description = "Certificate of Occupancy and Governor's Consent cross-audited."
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // 3. Client Contact & Regulatory Records
        Text(
            text = "CREDENTIALS & DISCLOSURES",
            fontFamily = PlusJakartaFontFamily,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = HavenGoldDark,
            letterSpacing = 1.5.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        Surface(
            shape = RoundedCornerShape(2.dp),
            color = Color.White,
            border = BorderStroke(1.dp, HavenGreyBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                InfoField(label = "CLIENT EMAIL", value = currentUser?.email ?: "guest.client@haven-realty.com")
                Spacer(modifier = Modifier.height(10.dp))
                InfoField(label = "CONTACT NUMBER", value = currentUser?.phone ?: "+234 (0) 803 000 8820")
                Spacer(modifier = Modifier.height(10.dp))
                InfoField(label = "DEFAULT SETTLEMENT CURRENCY", value = when (currentUser?.preferredCountry) {
                    "US" -> "USD ($) - United States Dollar"
                    "GB" -> "GBP (£) - British Pound Sterling"
                    "AE" -> "AED - United Arab Emirates Dirham"
                    else -> "NGN (₦) - Nigerian Naira"
                })
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // 4. Developer Contact & Engineering Governance
        Text(
            text = "PLATFORM DEVELOPER & ARCHITECT",
            fontFamily = PlusJakartaFontFamily,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = HavenGoldDark,
            letterSpacing = 1.5.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        Surface(
            shape = RoundedCornerShape(2.dp),
            color = Color.White,
            border = BorderStroke(1.dp, HavenGreyBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                InfoField(label = "LEAD ARCHITECT & DEVELOPER", value = "Nnakwe Victor")
                Spacer(modifier = Modifier.height(10.dp))
                InfoField(label = "DIRECT CONTACT TELEPHONE", value = "07017331773")
                Spacer(modifier = Modifier.height(10.dp))
                InfoField(label = "OFFICIAL CORRESPONDENCE EMAIL", value = "nnakwevictor90@gmail.com")
                Spacer(modifier = Modifier.height(10.dp))
                InfoField(label = "TECHNICAL PLATFORM", value = "PROJECT X Real Estate Operating Engine v1.0")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "PROJECT X — GLOBAL PROPERTY GROUP • A New World of Property Opportunities • Engineered by Nnakwe Victor • All rights reserved.",
            fontFamily = PlusJakartaFontFamily,
            fontSize = 10.sp,
            color = HavenGreyMuted,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun SecurityStatusRow(
    icon: ImageVector,
    title: String,
    status: String,
    statusColor: Color,
    description: String
) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = HavenNavyDark,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontFamily = PlusJakartaFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = HavenNavyDark
                )
                Surface(
                    shape = RoundedCornerShape(1.dp),
                    color = statusColor.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = status,
                        fontFamily = PlusJakartaFontFamily,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor,
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                fontFamily = PlusJakartaFontFamily,
                fontSize = 11.sp,
                color = HavenGreyText,
                lineHeight = 15.sp
            )
        }
    }
}

@Composable
private fun InfoField(label: String, value: String) {
    Column {
        Text(
            text = label,
            fontFamily = PlusJakartaFontFamily,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = HavenGreyMuted,
            letterSpacing = 0.8.sp
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            fontFamily = PlusJakartaFontFamily,
            fontSize = 12.5.sp,
            fontWeight = FontWeight.Medium,
            color = HavenNavyDark
        )
    }
}
