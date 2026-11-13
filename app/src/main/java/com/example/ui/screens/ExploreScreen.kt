package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.enums.PropertyStatus
import com.example.data.model.Property
import com.example.ui.components.ExploreFilterState
import com.example.ui.components.FilterSheet
import com.example.ui.components.PropertyCard
import com.example.ui.components.PropertyCardHorizontal
import com.example.ui.theme.HavenGold
import com.example.ui.theme.HavenGoldContainer
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

@Composable
fun ExploreScreen(
    properties: List<Property>,
    favouriteIds: Set<String>,
    selectedCountry: String,
    onToggleFavourite: (Property) -> Unit,
    onPropertyClick: (Property) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var filterState by remember { mutableStateOf(ExploreFilterState()) }
    var showFilterSheet by remember { mutableStateOf(false) }

    val filteredProperties by remember(properties, searchQuery, filterState, selectedCountry) {
        derivedStateOf {
            properties.filter { p ->
                val statusMatches = when {
                    filterState.includeClosed -> true
                    p.status.equals(PropertyStatus.PUBLISHED.value, ignoreCase = true) -> true
                    else -> false
                }
                if (!statusMatches) return@filter false

                if (selectedCountry != "ALL" && !p.countryCode.equals(selectedCountry, ignoreCase = true)) {
                    return@filter false
                }

                if (filterState.listingType != "ALL" && !p.listingType.equals(filterState.listingType, ignoreCase = true)) {
                    return@filter false
                }

                if (filterState.verifiedOnly && !p.isVerified) {
                    return@filter false
                }

                if (filterState.minBedrooms > 0 && p.bedrooms < filterState.minBedrooms) return@filter false
                if (filterState.minBathrooms > 0 && p.bathrooms < filterState.minBathrooms) return@filter false

                if (searchQuery.isNotBlank()) {
                    val query = searchQuery.trim().lowercase()
                    val matches = p.title.lowercase().contains(query) ||
                            p.city.lowercase().contains(query) ||
                            p.state.lowercase().contains(query) ||
                            p.lga.lowercase().contains(query) ||
                            p.neighbourhood.lowercase().contains(query) ||
                            p.address.lowercase().contains(query) ||
                            p.countryCode.lowercase().contains(query)
                    if (!matches) return@filter false
                }

                true
            }.sortedWith { a, b ->
                when (filterState.sortBy) {
                    "price_asc" -> a.originalPrice.compareTo(b.originalPrice)
                    "price_desc" -> b.originalPrice.compareTo(a.originalPrice)
                    else -> b.createdAt.compareTo(a.createdAt)
                }
            }
        }
    }

    val activeFilterCount by remember(filterState) {
        derivedStateOf {
            var count = 0
            if (filterState.listingType != "ALL") count++
            if (filterState.minBedrooms > 0) count++
            if (filterState.minBathrooms > 0) count++
            if (filterState.verifiedOnly) count++
            if (filterState.includeClosed) count++
            if (filterState.sortBy != "newest") count++
            count
        }
    }

    Box(modifier = modifier.fillMaxSize().background(Color(0xFFFAFAF8))) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 96.dp)
        ) {
            // 1. ARCHITECTURAL EDITORIAL HERO SECTION
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(340.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_haven_estate),
                        contentDescription = "Haven Architectural Masterpiece",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    // Sophisticated Editorial Scrim
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        HavenNavyDark.copy(alpha = 0.5f),
                                        HavenNavyDark.copy(alpha = 0.85f),
                                        HavenNavyDark
                                    )
                                )
                            )
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp, vertical = 24.dp),
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Surface(
                            color = HavenGoldContainer,
                            shape = RoundedCornerShape(2.dp),
                            border = BorderStroke(0.8.dp, HavenGold.copy(alpha = 0.6f))
                        ) {
                            Text(
                                text = "PROJECT X — GLOBAL PROPERTY GROUP",
                                fontFamily = PlusJakartaFontFamily,
                                color = HavenGold,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "A New World of Property Opportunities",
                            fontFamily = PlayfairFontFamily,
                            color = Color.White,
                            fontSize = 25.sp,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 31.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Curated luxury residences with verified land titles, institutional legal security, and dedicated private client advisory.",
                            fontFamily = PlusJakartaFontFamily,
                            color = Color.White.copy(alpha = 0.75f),
                            fontSize = 12.sp,
                            lineHeight = 17.sp
                        )
                    }
                }
            }

            // 2. PROMINENT PROPERTY SEARCH & FILTER CONSOLE
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(2.dp),
                        border = BorderStroke(1.dp, HavenGreyBorder)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            // Buy / Lease / Stay Tab Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                val searchTabs = listOf(
                                    "ALL" to "All Properties",
                                    "sale" to "Buy",
                                    "rent" to "Rent",
                                    "shortlet" to "Prime Stay"
                                )
                                searchTabs.forEach { (key, label) ->
                                    val isSelected = filterState.listingType.equals(key, ignoreCase = true)
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { filterState = filterState.copy(listingType = key) }
                                            .background(if (isSelected) HavenNavy else HavenGreyLight)
                                            .padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = label,
                                            fontFamily = PlusJakartaFontFamily,
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) HavenGold else HavenGreyText
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Search input field with clear & filter actions
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = searchQuery,
                                    onValueChange = { searchQuery = it },
                                    placeholder = {
                                        Text(
                                            text = "Search Banana Island, Mayfair, Palm Jumeirah...",
                                            fontFamily = PlusJakartaFontFamily,
                                            fontSize = 12.sp,
                                            color = HavenGreyMuted
                                        )
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Search,
                                            contentDescription = "Search",
                                            tint = HavenNavy,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    },
                                    trailingIcon = {
                                        if (searchQuery.isNotEmpty()) {
                                            IconButton(onClick = { searchQuery = "" }) {
                                                Icon(
                                                    imageVector = Icons.Default.Clear,
                                                    contentDescription = "Clear",
                                                    tint = HavenGreyMuted,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("explore_search_input"),
                                    shape = RoundedCornerShape(2.dp),
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = HavenNavy,
                                        unfocusedBorderColor = HavenGreyBorder,
                                        focusedContainerColor = Color.White,
                                        unfocusedContainerColor = Color.White
                                    )
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                Surface(
                                    shape = RoundedCornerShape(2.dp),
                                    color = if (activeFilterCount > 0) HavenGold else HavenNavy,
                                    modifier = Modifier
                                        .size(50.dp)
                                        .clickable { showFilterSheet = true }
                                        .testTag("filter_button")
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        if (activeFilterCount > 0) {
                                            BadgedBox(
                                                badge = {
                                                    Badge(
                                                        containerColor = HavenNavy,
                                                        contentColor = Color.White
                                                    ) {
                                                        Text(activeFilterCount.toString(), fontSize = 10.sp)
                                                    }
                                                }
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.FilterList,
                                                    contentDescription = "Filters",
                                                    tint = HavenNavy,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        } else {
                                            Icon(
                                                imageVector = Icons.Default.FilterList,
                                                contentDescription = "Filters",
                                                tint = HavenGold,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 3. EXPLORE GLOBAL CAPITALS & ENCLAVES (Editorial City Cards)
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "GLOBAL ENCLAVES",
                                fontFamily = PlusJakartaFontFamily,
                                color = HavenGoldDark,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.5.sp
                            )
                            Text(
                                text = "Prime Capital Markets",
                                fontFamily = PlayfairFontFamily,
                                color = HavenNavyDark,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    val enclaves = listOf(
                        EnclaveInfo("NG", "Lagos, NG", "Banana Island & Ikoyi", "₦1.8B avg", R.drawable.haven_hero_banner),
                        EnclaveInfo("AE", "Dubai, UAE", "Palm Jumeirah & Downtown", "AED 42M avg", R.drawable.img_haven_waterfront),
                        EnclaveInfo("GB", "London, UK", "Mayfair & Belgravia", "£16.5M avg", R.drawable.haven_penthouse),
                        EnclaveInfo("US", "Miami, US", "Star Island & Brickell", "$14.5M avg", R.drawable.img_haven_estate)
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(enclaves) { enc ->
                            Surface(
                                modifier = Modifier
                                    .width(170.dp)
                                    .height(115.dp)
                                    .clickable { searchQuery = enc.name.split(",")[0] },
                                shape = RoundedCornerShape(2.dp),
                                border = BorderStroke(1.dp, HavenGreyBorder)
                            ) {
                                Box(modifier = Modifier.fillMaxSize()) {
                                    Image(
                                        painter = painterResource(id = enc.imageRes),
                                        contentDescription = enc.name,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(
                                                Brush.verticalGradient(
                                                    colors = listOf(Color.Transparent, HavenNavyDark.copy(alpha = 0.9f))
                                                )
                                            )
                                    )
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(10.dp),
                                        verticalArrangement = Arrangement.Bottom
                                    ) {
                                        Text(
                                            text = enc.name,
                                            fontFamily = PlusJakartaFontFamily,
                                            color = Color.White,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = enc.subDistrict,
                                            fontFamily = PlusJakartaFontFamily,
                                            color = HavenGold,
                                            fontSize = 9.5.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = enc.avgPrice,
                                            fontFamily = PlusJakartaFontFamily,
                                            color = Color.White.copy(alpha = 0.75f),
                                            fontSize = 9.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 4. INSTITUTIONAL SECURITY & LEGAL TITLE TRUST SEAL (User explicit request)
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    shape = RoundedCornerShape(2.dp),
                    color = HavenNavyDark,
                    border = BorderStroke(1.dp, HavenGold.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Trust Crest Asset
                        Image(
                            painter = painterResource(id = R.drawable.img_haven_security),
                            contentDescription = "Haven Title Deed & Escrow Seal",
                            modifier = Modifier
                                .size(72.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            contentScale = ContentScale.Fit
                        )

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = null,
                                    tint = HavenGold,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "INSTITUTIONAL TRUST GUARANTEE",
                                    fontFamily = PlusJakartaFontFamily,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = HavenGold,
                                    letterSpacing = 1.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Government Title & Escrow Audited",
                                fontFamily = PlayfairFontFamily,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "C of O, Governor's Consent, and Land Registry searches verified by licensed conveyancing barristers prior to publication.",
                                fontFamily = PlusJakartaFontFamily,
                                fontSize = 10.5.sp,
                                color = Color.White.copy(alpha = 0.75f),
                                lineHeight = 14.sp
                            )
                        }
                    }
                }
            }

            // 5. FEATURED PORTFOLIO HEADER & COUNTER
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "CURATED RESIDENCES",
                            fontFamily = PlusJakartaFontFamily,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = HavenGoldDark,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "${filteredProperties.size} Available Estates",
                            fontFamily = PlayfairFontFamily,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = HavenNavyDark
                        )
                    }

                    if (activeFilterCount > 0 || searchQuery.isNotEmpty()) {
                        Text(
                            text = "Reset Filters",
                            fontFamily = PlusJakartaFontFamily,
                            fontSize = 11.5.sp,
                            color = HavenGoldDark,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable {
                                searchQuery = ""
                                filterState = ExploreFilterState()
                            }
                        )
                    }
                }
            }

            // 6. PROPERTY LISTINGS (Magazine Layout: Varied Vertical & Horizontal Cards)
            if (filteredProperties.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = null,
                            tint = HavenGreyMuted,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No matching residences located",
                            fontFamily = PlayfairFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = HavenNavyDark
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Adjust your territory or criteria to view alternative prime residences.",
                            fontFamily = PlusJakartaFontFamily,
                            fontSize = 12.sp,
                            color = HavenGreyText,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Surface(
                            shape = RoundedCornerShape(2.dp),
                            color = HavenNavy,
                            modifier = Modifier.clickable {
                                searchQuery = ""
                                filterState = ExploreFilterState()
                            }
                        ) {
                            Text(
                                text = "Reset Search Filters",
                                fontFamily = PlusJakartaFontFamily,
                                color = HavenGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 9.dp)
                            )
                        }
                    }
                }
            } else {
                itemsIndexed(filteredProperties, key = { _, prop -> prop.id }) { index, prop ->
                    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                        // Alternate every 3rd card with an architectural horizontal card for visual variety
                        if (index > 0 && index % 3 == 0) {
                            PropertyCardHorizontal(
                                property = prop,
                                isFavourite = favouriteIds.contains(prop.id),
                                onToggleFavourite = onToggleFavourite,
                                onClick = onPropertyClick
                            )
                        } else {
                            PropertyCard(
                                property = prop,
                                isFavourite = favouriteIds.contains(prop.id),
                                onToggleFavourite = onToggleFavourite,
                                onClick = onPropertyClick
                            )
                        }
                    }
                }
            }

            // 7. EDITORIAL FOOTER & DEMONSTRATION NOTICE (Phase 9 & Sotheby's standard)
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp, bottom = 12.dp, start = 20.dp, end = 20.dp)
                ) {
                    HorizontalDivider(color = HavenGreyBorder, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "PROJECT X — GLOBAL PROPERTY GROUP",
                        fontFamily = PlayfairFontFamily,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = HavenNavyDark,
                        letterSpacing = 2.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "A New World of Property Opportunities",
                        fontFamily = PlusJakartaFontFamily,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = HavenGoldDark
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "International Hubs: Lagos • London • Dubai • New York • Miami • Toronto • Johannesburg",
                        fontFamily = PlusJakartaFontFamily,
                        fontSize = 10.5.sp,
                        color = HavenGreyText
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                        color = HavenGreyLight,
                        shape = RoundedCornerShape(2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "DEMONSTRATION SYSTEM: All property listings, developer identities, and title certifications are generated for demonstration and architectural evaluation purposes. Equal Housing Opportunity.",
                            fontFamily = PlusJakartaFontFamily,
                            fontSize = 9.5.sp,
                            color = HavenGreyDark.copy(alpha = 0.7f),
                            lineHeight = 14.sp,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            }
        }

        // Filter Bottom Sheet
        if (showFilterSheet) {
            FilterSheet(
                initialFilters = filterState,
                onApplyFilters = { filterState = it },
                onDismiss = { showFilterSheet = false }
            )
        }
    }
}

private data class EnclaveInfo(
    val code: String,
    val name: String,
    val subDistrict: String,
    val avgPrice: String,
    val imageRes: Int
)

