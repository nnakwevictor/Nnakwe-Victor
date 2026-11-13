package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import com.example.data.model.CountryInfo
import com.example.data.model.GlobalJurisdictionCatalog
import com.example.ui.theme.HavenGold
import com.example.ui.theme.HavenGoldDark
import com.example.ui.theme.HavenGreyBorder
import com.example.ui.theme.HavenGreyLight
import com.example.ui.theme.HavenGreyMuted
import com.example.ui.theme.HavenGreyText
import com.example.ui.theme.HavenNavy
import com.example.ui.theme.HavenNavyDark
import com.example.ui.theme.PlayfairFontFamily
import com.example.ui.theme.PlusJakartaFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountrySelectorSheet(
    selectedCountryCode: String,
    onCountrySelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var searchQuery by remember { mutableStateOf("") }

    val countries = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            GlobalJurisdictionCatalog.ALL_COUNTRIES
        } else {
            val q = searchQuery.trim().lowercase()
            GlobalJurisdictionCatalog.ALL_COUNTRIES.filter {
                it.name.lowercase().contains(q) ||
                it.code.lowercase().contains(q) ||
                it.defaultCurrency.lowercase().contains(q)
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "INTERNATIONAL JURISDICTIONS",
                        fontFamily = PlusJakartaFontFamily,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = HavenGoldDark,
                        letterSpacing = 1.5.sp
                    )
                    Text(
                        text = "Select Country & Territory",
                        fontFamily = PlayfairFontFamily,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = HavenNavyDark
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = HavenGreyMuted)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Search Box
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text(
                        "Search country, code or currency (e.g. Ghana, GHS, Canada)...",
                        fontFamily = PlusJakartaFontFamily,
                        fontSize = 12.sp,
                        color = HavenGreyMuted
                    )
                },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = HavenNavy, modifier = Modifier.size(18.dp))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("country_search_input"),
                shape = RoundedCornerShape(2.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = HavenNavy,
                    unfocusedBorderColor = HavenGreyBorder,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            // "Global Portfolio (All)" Option
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onCountrySelected("ALL")
                        onDismiss()
                    }
                    .testTag("country_option_ALL"),
                shape = RoundedCornerShape(2.dp),
                color = if (selectedCountryCode == "ALL") HavenNavyDark else Color(0xFFFAFAF8),
                border = BorderStroke(1.dp, if (selectedCountryCode == "ALL") HavenGold else HavenGreyBorder)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Public,
                            contentDescription = null,
                            tint = if (selectedCountryCode == "ALL") HavenGold else HavenNavy,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Global Portfolio (All Territories)",
                                fontFamily = PlusJakartaFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = if (selectedCountryCode == "ALL") Color.White else HavenNavyDark
                            )
                            Text(
                                text = "Browse properties across all international regions",
                                fontFamily = PlusJakartaFontFamily,
                                fontSize = 11.sp,
                                color = if (selectedCountryCode == "ALL") HavenGold else HavenGreyText
                            )
                        }
                    }
                    if (selectedCountryCode == "ALL") {
                        Icon(imageVector = Icons.Default.Check, contentDescription = "Selected", tint = HavenGold, modifier = Modifier.size(18.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = HavenGreyBorder, thickness = 0.8.dp)
            Spacer(modifier = Modifier.height(6.dp))

            // Country List
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(360.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(countries, key = { it.code }) { c ->
                    val isSelected = selectedCountryCode.equals(c.code, ignoreCase = true)
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onCountrySelected(c.code)
                                onDismiss()
                            }
                            .testTag("country_option_${c.code}"),
                        shape = RoundedCornerShape(2.dp),
                        color = if (isSelected) HavenNavyDark else Color.White
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = c.flagEmoji,
                                    fontSize = 18.sp
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = c.name,
                                        fontFamily = PlusJakartaFontFamily,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 13.sp,
                                        color = if (isSelected) Color.White else HavenNavyDark
                                    )
                                    Text(
                                        text = "Currency: ${c.defaultCurrency} • Phone: ${c.callingCode} • Units: ${c.defaultUnit}",
                                        fontFamily = PlusJakartaFontFamily,
                                        fontSize = 10.5.sp,
                                        color = if (isSelected) HavenGold else HavenGreyMuted
                                    )
                                }
                            }

                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = HavenGold,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
