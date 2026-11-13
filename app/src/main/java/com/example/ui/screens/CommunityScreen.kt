package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
fun CommunityScreen(
    users: List<User>,
    currentUser: User?,
    onStartChat: (User) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedTerritory by remember { mutableStateOf("ALL") }

    val filteredUsers by remember(users, searchQuery, selectedTerritory, currentUser) {
        derivedStateOf {
            users.filter { u ->
                if (currentUser != null && u.id == currentUser.id) return@filter false

                if (selectedTerritory != "ALL" && !u.preferredCountry.equals(selectedTerritory, ignoreCase = true)) {
                    return@filter false
                }

                if (searchQuery.isNotBlank()) {
                    val q = searchQuery.trim().lowercase()
                    val matches = u.name.lowercase().contains(q) ||
                            (u.agencyName?.lowercase()?.contains(q) == true) ||
                            u.email.lowercase().contains(q) ||
                            u.preferredCountry.lowercase().contains(q)
                    if (!matches) return@filter false
                }

                true
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "CERTIFIED ADVISORY NETWORK",
            fontFamily = PlusJakartaFontFamily,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = HavenGoldDark,
            letterSpacing = 1.5.sp
        )
        Text(
            text = "Private Client Advisors & Brokers",
            fontFamily = PlayfairFontFamily,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = HavenNavyDark
        )
        Text(
            text = "Direct, confidential communication with certified conveyancers, surveyors, and licensed real estate partners.",
            fontFamily = PlusJakartaFontFamily,
            fontSize = 11.5.sp,
            color = HavenGreyText,
            modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
        )

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search by name, brokerage firm, or jurisdiction...", fontFamily = PlusJakartaFontFamily, fontSize = 12.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = HavenNavyDark, modifier = Modifier.size(16.dp)) },
            shape = RoundedCornerShape(2.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("community_search_input"),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = HavenNavyDark,
                unfocusedBorderColor = HavenGreyBorder,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Territory filter tabs
        val territories = listOf(
            "ALL" to "All Jurisdictions",
            "NG" to "Nigeria",
            "US" to "United States",
            "GB" to "United Kingdom",
            "AE" to "United Arab Emirates"
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(territories) { (key, label) ->
                val isSelected = selectedTerritory == key
                Surface(
                    shape = RoundedCornerShape(2.dp),
                    color = if (isSelected) HavenNavyDark else Color.White,
                    border = BorderStroke(1.dp, if (isSelected) HavenNavyDark else HavenGreyBorder),
                    modifier = Modifier.clickable { selectedTerritory = key }
                ) {
                    Text(
                        text = label,
                        fontFamily = PlusJakartaFontFamily,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) HavenGold else HavenGreyText,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Users List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredUsers, key = { it.id }) { user ->
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
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Avatar
                        Box(modifier = Modifier.size(46.dp)) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(HavenNavyDark),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = user.name.take(1).uppercase(),
                                    fontFamily = PlayfairFontFamily,
                                    color = HavenGold,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            // Online Indicator
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(StatusSuccess)
                                    .border(1.5.dp, Color.White, CircleShape)
                                    .align(Alignment.BottomEnd)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = user.name,
                                    fontFamily = PlusJakartaFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = HavenNavyDark
                                )
                                if (user.isVerified) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Default.Verified,
                                        contentDescription = "Verified Partner",
                                        tint = HavenGoldDark,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                            Text(
                                text = user.agencyName ?: when {
                                    user.isAgent -> "PROJECT X Certified Broker"
                                    user.isOwner -> "PROJECT X Property Owner & Asset Partner"
                                    user.isDeveloper -> "PROJECT X Developer Partner"
                                    else -> "Private Client & Investor"
                                },
                                fontFamily = PlusJakartaFontFamily,
                                fontSize = 11.5.sp,
                                color = HavenGreyText
                            )
                            Text(
                                text = "Territory: ${user.preferredCountry} • Encrypted Communication",
                                fontFamily = PlusJakartaFontFamily,
                                fontSize = 10.sp,
                                color = HavenGoldDark
                            )
                        }

                        // Actions
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Surface(
                                shape = RoundedCornerShape(2.dp),
                                color = HavenNavyDark,
                                modifier = Modifier.size(36.dp)
                            ) {
                                IconButton(
                                    onClick = { onStartChat(user) },
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ChatBubble,
                                        contentDescription = "Message",
                                        tint = HavenGold,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            if (!user.phone.isNullOrBlank()) {
                                Surface(
                                    shape = RoundedCornerShape(2.dp),
                                    color = Color.White,
                                    border = BorderStroke(1.dp, HavenGreyBorder),
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    IconButton(
                                        onClick = {
                                            val callIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${user.phone}"))
                                            context.startActivity(callIntent)
                                        },
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Call,
                                            contentDescription = "Call",
                                            tint = HavenNavyDark,
                                            modifier = Modifier.size(16.dp)
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
}
