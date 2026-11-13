package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GlobalJurisdictionCatalog
import com.example.data.model.User
import com.example.ui.theme.HavenGold
import com.example.ui.theme.HavenGoldDark
import com.example.ui.theme.HavenNavy
import com.example.ui.theme.HavenNavyDark
import com.example.ui.theme.HavenNavyLight
import com.example.ui.theme.PlayfairFontFamily
import com.example.ui.theme.PlusJakartaFontFamily

@Composable
fun HavenTopBar(
    currentUser: User?,
    selectedCountry: String,
    onCountrySelected: (String) -> Unit,
    onSwitchRoleClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showCountrySelectorSheet by remember { mutableStateOf(false) }

    Surface(
        color = HavenNavyDark,
        tonalElevation = 0.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
        ) {
            // Main Editorial Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Brand Identity: PROJECT X — GLOBAL PROPERTY GROUP
                Column {
                    Text(
                        text = "PROJECT X",
                        fontFamily = PlayfairFontFamily,
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 4.sp
                    )
                    Text(
                        text = "GLOBAL PROPERTY GROUP",
                        fontFamily = PlusJakartaFontFamily,
                        color = HavenGold,
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 2.sp
                    )
                }

                // Security & Role Status Chip
                val roleLabel = when {
                    currentUser == null -> "GUEST CLIENT"
                    currentUser.isAdmin -> "ADMINISTRATOR"
                    currentUser.isAgent -> "CERTIFIED BROKER"
                    else -> "PRIVATE CLIENT"
                }

                Surface(
                    shape = RoundedCornerShape(2.dp),
                    color = HavenNavy,
                    modifier = Modifier
                        .border(1.dp, HavenGold.copy(alpha = 0.45f), RoundedCornerShape(2.dp))
                        .clickable { onSwitchRoleClick() }
                        .testTag("role_switcher_pill")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "Security Status",
                            tint = HavenGold,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = roleLabel,
                            fontFamily = PlusJakartaFontFamily,
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Switch persona",
                            tint = HavenGold,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            HorizontalDivider(
                color = HavenNavyLight.copy(alpha = 0.6f),
                thickness = 1.dp
            )

            // Country / Capital Territory Bar with Full Country Selector Launcher
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // All Countries Sheet Button
                Surface(
                    shape = RoundedCornerShape(2.dp),
                    color = HavenNavyLight,
                    border = androidx.compose.foundation.BorderStroke(1.dp, HavenGold.copy(alpha = 0.6f)),
                    modifier = Modifier
                        .clickable { showCountrySelectorSheet = true }
                        .testTag("open_country_catalog_btn")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Public,
                            contentDescription = "All Countries",
                            tint = HavenGold,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (selectedCountry == "ALL") "ALL JURISDICTIONS" else {
                                val c = GlobalJurisdictionCatalog.getCountry(selectedCountry)
                                "${c?.flagEmoji ?: "🌐"} ${c?.name?.uppercase() ?: selectedCountry}"
                            },
                            fontFamily = PlusJakartaFontFamily,
                            color = HavenGold,
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = HavenGold,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }

                // Quick tabs for active primary global hubs
                val primaryTerritories = listOf(
                    "ALL" to "GLOBAL PORTFOLIO",
                    "NG" to "NIGERIA",
                    "US" to "UNITED STATES",
                    "GB" to "UNITED KINGDOM",
                    "AE" to "UNITED ARAB EMIRATES",
                    "CA" to "CANADA",
                    "ZA" to "SOUTH AFRICA",
                    "GH" to "GHANA",
                    "KE" to "KENYA",
                    "FR" to "FRANCE"
                )

                primaryTerritories.forEach { (code, label) ->
                    val isSelected = selectedCountry.equals(code, ignoreCase = true)
                    Box(
                        modifier = Modifier
                            .clickable { onCountrySelected(code) }
                            .padding(vertical = 4.dp, horizontal = 2.dp)
                            .testTag("country_tab_$code")
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = label,
                                fontFamily = PlusJakartaFontFamily,
                                color = if (isSelected) HavenGold else Color.White.copy(alpha = 0.65f),
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .height(1.5.dp)
                                    .width(if (isSelected) 24.dp else 0.dp)
                                    .background(HavenGold)
                            )
                        }
                    }
                }
            }
        }
    }

    if (showCountrySelectorSheet) {
        CountrySelectorSheet(
            selectedCountryCode = selectedCountry,
            onCountrySelected = { onCountrySelected(it) },
            onDismiss = { showCountrySelectorSheet = false }
        )
    }
}
