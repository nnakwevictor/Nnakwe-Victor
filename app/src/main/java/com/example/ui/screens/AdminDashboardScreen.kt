package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.enums.PropertyStatus
import com.example.data.model.Property
import com.example.data.model.User
import com.example.ui.theme.HavenGold
import com.example.ui.theme.HavenGoldDark
import com.example.ui.theme.HavenGreyBorder
import com.example.ui.theme.HavenGreyDark
import com.example.ui.theme.HavenGreyLight
import com.example.ui.theme.HavenGreyMuted
import com.example.ui.theme.HavenGreyText
import com.example.ui.theme.HavenNavy
import com.example.ui.theme.HavenNavyDark
import com.example.ui.theme.PlayfairFontFamily
import com.example.ui.theme.PlusJakartaFontFamily
import com.example.ui.theme.StatusSuccess

@Composable
fun AdminDashboardScreen(
    allProperties: List<Property>,
    pendingProperties: List<Property>,
    users: List<User>,
    onApproveProperty: (id: String) -> Unit,
    onRejectProperty: (id: String, reason: String) -> Unit,
    onToggleAgentVerification: (userId: String, isVerified: Boolean) -> Unit,
    onPropertyClick: (Property) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Review Queue, 1: Broker Licenses, 2: System Users, 3: Security & Audit
    var rejectTargetProperty by remember { mutableStateOf<Property?>(null) }
    var rejectionReasonText by remember { mutableStateOf("") }

    val agents = remember(users) { users.filter { it.isAgent } }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Admin Header
        item {
            Surface(
                shape = RoundedCornerShape(2.dp),
                color = HavenNavyDark,
                border = BorderStroke(1.dp, HavenGold.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "Admin",
                            tint = HavenGold,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "GLOBAL ADMINISTRATION & GOVERNANCE",
                                fontFamily = PlusJakartaFontFamily,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = HavenGold,
                                letterSpacing = 1.5.sp
                            )
                            Text(
                                text = "Platform Oversight & Legal Verification",
                                fontFamily = PlayfairFontFamily,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        // 2. Metrics Bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AdminMetricTile(
                    title = "Pending Review",
                    count = "${pendingProperties.size}",
                    icon = Icons.Default.HourglassTop,
                    modifier = Modifier.weight(1f)
                )
                AdminMetricTile(
                    title = "Live Estates",
                    count = "${allProperties.count { it.isPublished }}",
                    icon = Icons.Default.Apartment,
                    modifier = Modifier.weight(1f)
                )
                AdminMetricTile(
                    title = "Licensed Brokers",
                    count = "${agents.size}",
                    icon = Icons.Default.Group,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // 3. Information Architecture Tabs
        item {
            Surface(
                color = Color.White,
                shape = RoundedCornerShape(2.dp),
                border = BorderStroke(1.dp, HavenGreyBorder)
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    val tabs = listOf("Review Queue", "Broker Licenses", "Users", "Audit")
                    tabs.forEachIndexed { index, label ->
                        val isSelected = selectedTab == index
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedTab = index }
                                .background(if (isSelected) HavenNavyDark else Color.White)
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontFamily = PlusJakartaFontFamily,
                                fontSize = 10.5.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) HavenGold else HavenGreyText
                            )
                        }
                    }
                }
            }
        }

        // 4. Tab 0: Review Queue
        if (selectedTab == 0) {
            if (pendingProperties.isEmpty()) {
                item {
                    Surface(
                        shape = RoundedCornerShape(2.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, HavenGreyBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = StatusSuccess, modifier = Modifier.size(36.dp))
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Review Queue Cleared",
                                fontFamily = PlayfairFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = HavenNavyDark
                            )
                            Text(
                                text = "All submitted luxury residences have been audited and verified.",
                                fontFamily = PlusJakartaFontFamily,
                                fontSize = 12.sp,
                                color = HavenGreyText
                            )
                        }
                    }
                }
            } else {
                items(pendingProperties, key = { it.id }) { prop ->
                    Surface(
                        shape = RoundedCornerShape(2.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, HavenGreyBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onPropertyClick(prop) }
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = prop.title,
                                fontFamily = PlusJakartaFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = HavenNavyDark
                            )
                            Text(
                                text = "${prop.neighbourhood}, ${prop.city} • ${prop.formattedPrice}",
                                fontFamily = PlusJakartaFontFamily,
                                fontSize = 11.5.sp,
                                color = HavenGreyText
                            )
                            Text(
                                text = "Title Document: ${if (prop.titleDocument.isNotBlank()) prop.titleDocument else "C of O"}",
                                fontFamily = PlusJakartaFontFamily,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = HavenGoldDark
                            )

                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = HavenGreyBorder, thickness = 0.8.dp)
                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { rejectTargetProperty = prop },
                                    shape = RoundedCornerShape(2.dp),
                                    border = BorderStroke(1.dp, Color(0xFFB91C1C)),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = null, tint = Color(0xFFB91C1C), modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Reject", color = Color(0xFFB91C1C), fontSize = 11.5.sp)
                                }

                                Button(
                                    onClick = { onApproveProperty(prop.id) },
                                    colors = ButtonDefaults.buttonColors(containerColor = HavenNavyDark),
                                    shape = RoundedCornerShape(2.dp),
                                    modifier = Modifier.weight(1.3f)
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = HavenGold, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Approve & Publish", color = HavenGold, fontSize = 11.5.sp)
                                }
                            }
                        }
                    }
                }
            }
        } else if (selectedTab == 1) {
            // Broker Licenses
            items(agents) { agent ->
                Surface(
                    shape = RoundedCornerShape(2.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, HavenGreyBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = agent.name,
                                fontFamily = PlusJakartaFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.5.sp,
                                color = HavenNavyDark
                            )
                            Text(
                                text = "${agent.agencyName ?: "Independent Broker"} • ${agent.preferredCountry}",
                                fontFamily = PlusJakartaFontFamily,
                                fontSize = 11.sp,
                                color = HavenGreyText
                            )
                            Text(
                                text = if (agent.isVerified) "Verified Broker License" else "Verification Pending",
                                fontFamily = PlusJakartaFontFamily,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (agent.isVerified) StatusSuccess else HavenGoldDark
                            )
                        }

                        Switch(
                            checked = agent.isVerified,
                            onCheckedChange = { onToggleAgentVerification(agent.id, it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = HavenNavyDark,
                                checkedTrackColor = HavenGold,
                                uncheckedTrackColor = HavenGreyLight
                            )
                        )
                    }
                }
            }
        } else if (selectedTab == 2) {
            // Platform Users
            items(users) { u ->
                Surface(
                    shape = RoundedCornerShape(2.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, HavenGreyBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = u.name,
                                fontFamily = PlusJakartaFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = HavenNavyDark
                            )
                            Text(
                                text = u.email,
                                fontFamily = PlusJakartaFontFamily,
                                fontSize = 11.sp,
                                color = HavenGreyText
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(1.dp),
                            color = HavenGreyLight
                        ) {
                            Text(
                                text = u.role.uppercase(),
                                fontFamily = PlusJakartaFontFamily,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = HavenNavyDark,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }
        } else {
            // Security & Audit
            item {
                Surface(
                    shape = RoundedCornerShape(2.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, HavenGreyBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "SECURITY PROTOCOLS & GOVERNANCE",
                            fontFamily = PlusJakartaFontFamily,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = HavenGoldDark,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Institutional Title Deed Verification Engine",
                            fontFamily = PlayfairFontFamily,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = HavenNavyDark
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "1. Pre-publication audit against Federal Land Registries (Lagos Alausa, Abuja AGIS, UK HM Land Registry, Dubai DLD).\n2. Anti-Money Laundering (AML) and Source of Funds compliance checks for cross-border escrows.\n3. Biometric passkey authentication enforced for broker transactions.",
                            fontFamily = PlusJakartaFontFamily,
                            fontSize = 11.5.sp,
                            color = HavenNavyDark,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }

        // Rejection Dialog
        if (rejectTargetProperty != null) {
            item {
                AlertDialog(
                    onDismissRequest = { rejectTargetProperty = null },
                    title = {
                        Text(
                            text = "Reject Listing",
                            fontFamily = PlayfairFontFamily,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    text = {
                        Column {
                            Text(
                                text = "Specify feedback or required title deed updates for ${rejectTargetProperty?.title}:",
                                fontFamily = PlusJakartaFontFamily,
                                fontSize = 12.sp,
                                color = HavenGreyDark
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = rejectionReasonText,
                                onValueChange = { rejectionReasonText = it },
                                placeholder = { Text("e.g. Upload Governor's Consent survey plan...") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                onRejectProperty(rejectTargetProperty!!.id, rejectionReasonText.ifBlank { "Additional verification documents required." })
                                rejectTargetProperty = null
                                rejectionReasonText = ""
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB91C1C))
                        ) {
                            Text("Reject")
                        }
                    },
                    dismissButton = {
                        OutlinedButton(onClick = { rejectTargetProperty = null }) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun AdminMetricTile(
    title: String,
    count: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(2.dp),
        color = Color.White,
        border = BorderStroke(1.dp, HavenGreyBorder),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = HavenGoldDark,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = count,
                fontFamily = PlayfairFontFamily,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = HavenNavyDark
            )
            Text(
                text = title,
                fontFamily = PlusJakartaFontFamily,
                fontSize = 10.sp,
                color = HavenGreyText
            )
        }
    }
}
