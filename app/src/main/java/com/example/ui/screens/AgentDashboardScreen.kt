package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.HomeWork
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.enums.PropertyStatus
import com.example.data.model.Enquiry
import com.example.data.model.Inspection
import com.example.data.model.Property
import com.example.data.model.User
import com.example.ui.theme.ProjectXBorder
import com.example.ui.theme.ProjectXChampagneGold
import com.example.ui.theme.ProjectXChampagneGoldContainer
import com.example.ui.theme.ProjectXChampagneGoldDark
import com.example.ui.theme.ProjectXChampagneGoldLight
import com.example.ui.theme.ProjectXChampagneGoldMuted
import com.example.ui.theme.ProjectXCharcoal
import com.example.ui.theme.ProjectXCharcoalDark
import com.example.ui.theme.ProjectXCharcoalLight
import com.example.ui.theme.ProjectXCharcoalMuted
import com.example.ui.theme.ProjectXNavy
import com.example.ui.theme.ProjectXNavyDeep
import com.example.ui.theme.ProjectXNavyLight
import com.example.ui.theme.ProjectXNavySubtle
import com.example.ui.theme.ProjectXSand
import com.example.ui.theme.ProjectXTextMuted
import com.example.ui.theme.ProjectXTextPrimary
import com.example.ui.theme.ProjectXTextSecondary
import com.example.ui.theme.PlayfairFontFamily
import com.example.ui.theme.PlusJakartaFontFamily
import java.text.NumberFormat
import java.util.Locale

enum class DashboardPerspective(val title: String) {
    AGENT("Agent / Broker"),
    OWNER("Property Owner / Landlord")
}

@Composable
fun AgentDashboardScreen(
    agentUser: User,
    myProperties: List<Property>,
    myEnquiries: List<Enquiry>,
    myInspections: List<Inspection>,
    onCreateListingClick: () -> Unit,
    onPropertyClick: (Property) -> Unit,
    onUpdatePropertyStatus: (id: String, status: String) -> Unit,
    onSyncWebsiteClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // 1. Dashboard Mode: Agent Perspective vs Property Owner Perspective
    var activePerspective by remember {
        mutableStateOf(
            if (agentUser.isOwner) DashboardPerspective.OWNER else DashboardPerspective.AGENT
        )
    }

    // 2. Global Search & Location/Price Filter State
    var searchQuery by remember { mutableStateOf("") }
    var selectedLocationCountry by remember { mutableStateOf("ALL") } // ALL, NG, US, GB, AE
    var selectedPriceTier by remember { mutableIntStateOf(0) } // 0: All, 1: < $1M / ₦1B, 2: $1M-$5M / ₦1B-₦5B, 3: $5M-$20M / ₦5B-₦25B, 4: > $20M / ₦25B+

    // 3. Tab State
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Listings/Assets, 1: Enquiries, 2: Inspections, 3: Performance & Analytics
    var listingStatusFilter by remember { mutableStateOf("ALL") } // ALL, published, pending_review, sold, rented

    // Filter properties based on Global Search, Location, Price Tier, and Listing Status
    val filteredProperties by remember(myProperties, searchQuery, selectedLocationCountry, selectedPriceTier, listingStatusFilter) {
        derivedStateOf {
            myProperties.filter { prop ->
                // Location Filter (Nigeria, US, UK, UAE)
                val matchesLocation = selectedLocationCountry == "ALL" ||
                        prop.countryCode.equals(selectedLocationCountry, ignoreCase = true)

                // Search Query Filter
                val query = searchQuery.trim().lowercase()
                val matchesSearch = query.isEmpty() ||
                        prop.title.lowercase().contains(query) ||
                        prop.neighbourhood.lowercase().contains(query) ||
                        prop.city.lowercase().contains(query) ||
                        prop.propertyType.lowercase().contains(query)

                // Price Range Filter
                val price = prop.originalPrice
                val matchesPrice = when (selectedPriceTier) {
                    1 -> price < 1_000_000_000.0 // Under ₦1B / $1M
                    2 -> price in 1_000_000_000.0..5_000_000_000.0 // ₦1B - ₦5B / $1M - $5M
                    3 -> price in 5_000_000_000.0..25_000_000_000.0 // ₦5B - ₦25B / $5M - $20M
                    4 -> price > 25_000_000_000.0 // Over ₦25B / $20M+
                    else -> true
                }

                // Listing Status Filter
                val matchesStatus = listingStatusFilter == "ALL" ||
                        prop.status.equals(listingStatusFilter, ignoreCase = true)

                matchesLocation && matchesSearch && matchesPrice && matchesStatus
            }
        }
    }

    // Dynamic Account Statistics
    val totalPortfolioValuation by remember(myProperties) {
        derivedStateOf {
            myProperties.sumOf { it.originalPrice }
        }
    }

    val activeListingsCount = myProperties.count { it.status == PropertyStatus.PUBLISHED.value }
    val pendingReviewCount = myProperties.count { it.status == PropertyStatus.PENDING_REVIEW.value }
    val soldRentedCount = myProperties.count {
        it.status == PropertyStatus.SOLD.value || it.status == PropertyStatus.RENTED.value
    }

    // Owner specific stats
    val ownerOccupancyRate = if (myProperties.isNotEmpty()) {
        val occupied = myProperties.count { it.status == PropertyStatus.RENTED.value || it.status == PropertyStatus.SOLD.value }
        ((occupied.toDouble() / myProperties.size) * 100).toInt()
    } else 88

    val estimatedRentalYield = "7.8% Net"

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // ==========================================
        // 1. GLOBAL SEARCH BAR (Location & Price Filters)
        // ==========================================
        item {
            Surface(
                shape = RoundedCornerShape(2.dp),
                color = Color.White,
                border = BorderStroke(1.dp, ProjectXBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "GLOBAL PORTFOLIO SEARCH",
                        fontFamily = PlusJakartaFontFamily,
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = ProjectXNavyDeep,
                        letterSpacing = 1.2.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Text Input
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = {
                            Text(
                                text = "Search by estate, district, city (e.g. Ikoyi, Mayfair, Palm Jumeirah)...",
                                fontFamily = PlusJakartaFontFamily,
                                fontSize = 11.5.sp,
                                color = ProjectXTextMuted
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = ProjectXNavy,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Clear",
                                        tint = ProjectXCharcoalMuted,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("dashboard_global_search_input"),
                        shape = RoundedCornerShape(2.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ProjectXNavy,
                            unfocusedBorderColor = ProjectXBorder,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Location Filter Chips (Nigeria, US, UK, UAE)
                    Text(
                        text = "FILTER BY LOCATION",
                        fontFamily = PlusJakartaFontFamily,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = ProjectXTextSecondary,
                        letterSpacing = 0.8.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val locationChips = listOf(
                            "ALL" to "All Global 🌐",
                            "NG" to "Nigeria 🇳🇬",
                            "US" to "United States 🇺🇸",
                            "GB" to "United Kingdom 🇬🇧",
                            "AE" to "United Arab Emirates 🇦🇪"
                        )
                        locationChips.forEach { (code, label) ->
                            val isSelected = selectedLocationCountry == code
                            Surface(
                                shape = RoundedCornerShape(2.dp),
                                color = if (isSelected) ProjectXNavyDeep else ProjectXSand,
                                border = BorderStroke(
                                    1.dp,
                                    if (isSelected) ProjectXChampagneGold else ProjectXBorder
                                ),
                                modifier = Modifier
                                    .clickable { selectedLocationCountry = code }
                                    .testTag("filter_loc_$code")
                            ) {
                                Text(
                                    text = label,
                                    fontFamily = PlusJakartaFontFamily,
                                    fontSize = 10.5.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) ProjectXChampagneGold else ProjectXTextSecondary,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Price Range Filter Chips
                    Text(
                        text = "FILTER BY PRICE RANGE",
                        fontFamily = PlusJakartaFontFamily,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = ProjectXTextSecondary,
                        letterSpacing = 0.8.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val priceChips = listOf(
                            0 to "All Prices",
                            1 to "< ₦1B / $1M",
                            2 to "₦1B - ₦5B / $1M-$5M",
                            3 to "₦5B - ₦25B / $5M-$20M",
                            4 to "> ₦25B / $20M+"
                        )
                        priceChips.forEach { (tier, label) ->
                            val isSelected = selectedPriceTier == tier
                            Surface(
                                shape = RoundedCornerShape(2.dp),
                                color = if (isSelected) ProjectXCharcoal else ProjectXSand,
                                border = BorderStroke(
                                    1.dp,
                                    if (isSelected) ProjectXChampagneGold else ProjectXBorder
                                ),
                                modifier = Modifier
                                    .clickable { selectedPriceTier = tier }
                                    .testTag("filter_price_tier_$tier")
                            ) {
                                Text(
                                    text = label,
                                    fontFamily = PlusJakartaFontFamily,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) ProjectXChampagneGold else ProjectXTextSecondary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }

                    // Matching Results Indicator
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Matching: ${filteredProperties.size} of ${myProperties.size} properties",
                            fontFamily = PlusJakartaFontFamily,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ProjectXNavy
                        )
                        if (searchQuery.isNotEmpty() || selectedLocationCountry != "ALL" || selectedPriceTier != 0) {
                            Text(
                                text = "RESET FILTERS",
                                fontFamily = PlusJakartaFontFamily,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = ProjectXChampagneGoldDark,
                                modifier = Modifier
                                    .clickable {
                                        searchQuery = ""
                                        selectedLocationCountry = "ALL"
                                        selectedPriceTier = 0
                                    }
                                    .padding(4.dp)
                            )
                        }
                    }
                }
            }
        }

        // ==========================================
        // 2. WEBSITE & CLOUD SYNCHRONIZATION STATUS BAR
        // ==========================================
        item {
            Surface(
                shape = RoundedCornerShape(2.dp),
                color = ProjectXNavyLight,
                border = BorderStroke(0.8.dp, ProjectXChampagneGold.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF22C55E))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "PROJECT X CLOUD BACKEND",
                                fontFamily = PlusJakartaFontFamily,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = ProjectXChampagneGold,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Website & App Properties Synchronized",
                                fontFamily = PlusJakartaFontFamily,
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                    }

                    Button(
                        onClick = onSyncWebsiteClick,
                        colors = ButtonDefaults.buttonColors(containerColor = ProjectXCharcoalDark),
                        border = BorderStroke(0.8.dp, ProjectXChampagneGold),
                        shape = RoundedCornerShape(2.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("sync_website_properties_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sync,
                            contentDescription = null,
                            tint = ProjectXChampagneGold,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "SYNC NOW",
                            fontFamily = PlusJakartaFontFamily,
                            color = ProjectXChampagneGold,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // ==========================================
        // 3. PERSPECTIVE SWITCHER: AGENT VS PROPERTY OWNER
        // ==========================================
        item {
            Surface(
                shape = RoundedCornerShape(2.dp),
                color = Color.White,
                border = BorderStroke(1.dp, ProjectXBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    DashboardPerspective.entries.forEach { perspective ->
                        val isSelected = activePerspective == perspective
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { activePerspective = perspective }
                                .background(if (isSelected) ProjectXNavyDeep else Color.White)
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (perspective == DashboardPerspective.AGENT)
                                        Icons.Default.BusinessCenter else Icons.Default.HomeWork,
                                    contentDescription = null,
                                    tint = if (isSelected) ProjectXChampagneGold else ProjectXTextSecondary,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = perspective.title.uppercase(),
                                    fontFamily = PlusJakartaFontFamily,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) ProjectXChampagneGold else ProjectXTextSecondary,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // ==========================================
        // 4. HEADER IDENTITY CARD (Role Specific)
        // ==========================================
        item {
            Surface(
                shape = RoundedCornerShape(2.dp),
                color = ProjectXNavyDeep,
                border = BorderStroke(1.dp, ProjectXChampagneGold.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (activePerspective == DashboardPerspective.AGENT) agentUser.name else (agentUser.agencyName ?: agentUser.name),
                                    fontFamily = PlayfairFontFamily,
                                    fontSize = 19.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = "Verified Identity",
                                    tint = ProjectXChampagneGold,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (activePerspective == DashboardPerspective.AGENT)
                                    "${agentUser.agencyName ?: "PROJECT X Advisory Partner"} • License #PJX-8820"
                                else
                                    "Private Asset & Landlord Holdings • Verified Titleholder",
                                fontFamily = PlusJakartaFontFamily,
                                fontSize = 11.sp,
                                color = ProjectXChampagneGold
                            )
                        }

                        Button(
                            onClick = onCreateListingClick,
                            colors = ButtonDefaults.buttonColors(containerColor = ProjectXNavyLight),
                            border = BorderStroke(1.dp, ProjectXChampagneGold),
                            shape = RoundedCornerShape(2.dp),
                            modifier = Modifier.testTag("dashboard_create_listing_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = ProjectXChampagneGold,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (activePerspective == DashboardPerspective.AGENT) "NEW LISTING" else "SUBMIT ASSET",
                                fontFamily = PlusJakartaFontFamily,
                                color = ProjectXChampagneGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }
            }
        }

        // ==========================================
        // 5. ACCOUNT STATISTICS GRID (Dynamic per Role)
        // ==========================================
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (activePerspective == DashboardPerspective.AGENT) {
                    // Agent Metrics
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        EditorialMetricCard(
                            title = "Active Portfolio",
                            count = "${myProperties.size}",
                            subtitle = "Properties listed",
                            icon = Icons.Default.Apartment,
                            modifier = Modifier.weight(1f)
                        )
                        EditorialMetricCard(
                            title = "Buyer Leads",
                            count = "${myEnquiries.size}",
                            subtitle = "Qualified prospects",
                            icon = Icons.Default.Message,
                            modifier = Modifier.weight(1f)
                        )
                        EditorialMetricCard(
                            title = "Inspections",
                            count = "${myInspections.size}",
                            subtitle = "Scheduled viewings",
                            icon = Icons.Default.CalendarMonth,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        EditorialMetricCard(
                            title = "Portfolio Value",
                            count = formatCompactCurrency(totalPortfolioValuation),
                            subtitle = "Total active asset value",
                            icon = Icons.Default.MonetizationOn,
                            modifier = Modifier.weight(1f)
                        )
                        EditorialMetricCard(
                            title = "Commission Pipeline",
                            count = formatCompactCurrency(totalPortfolioValuation * 0.025),
                            subtitle = "Est. 2.5% fee pipeline",
                            icon = Icons.Default.Payments,
                            modifier = Modifier.weight(1f)
                        )
                    }
                } else {
                    // Property Owner & Landlord Metrics
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        EditorialMetricCard(
                            title = "Total Asset Value",
                            count = formatCompactCurrency(totalPortfolioValuation),
                            subtitle = "Market equity valuation",
                            icon = Icons.Default.HomeWork,
                            modifier = Modifier.weight(1f)
                        )
                        EditorialMetricCard(
                            title = "Rental Yield",
                            count = estimatedRentalYield,
                            subtitle = "Annual cash return",
                            icon = Icons.Default.TrendingUp,
                            modifier = Modifier.weight(1f)
                        )
                        EditorialMetricCard(
                            title = "Occupancy Rate",
                            count = "$ownerOccupancyRate%",
                            subtitle = "Leased vs Vacant",
                            icon = Icons.Default.PieChart,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        EditorialMetricCard(
                            title = "Active Tenancies",
                            count = "$soldRentedCount Units",
                            subtitle = "Currently generating rent",
                            icon = Icons.Default.Key,
                            modifier = Modifier.weight(1f)
                        )
                        EditorialMetricCard(
                            title = "Title Deed Status",
                            count = "100% Verified",
                            subtitle = "C of O & Freehold Audit",
                            icon = Icons.Default.Security,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // ==========================================
        // 6. DASHBOARD NAVIGATION TABS
        // ==========================================
        item {
            Surface(
                color = Color.White,
                shape = RoundedCornerShape(2.dp),
                border = BorderStroke(1.dp, ProjectXBorder)
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    val tabs = if (activePerspective == DashboardPerspective.AGENT) {
                        listOf("Listings (${myProperties.size})", "Buyer Leads", "Viewings", "Accreditation")
                    } else {
                        listOf("Properties & Assets", "Tenant Inquiries", "Inspections", "Legal & Yields")
                    }
                    tabs.forEachIndexed { index, label ->
                        val isSelected = selectedTab == index
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedTab = index }
                                .background(if (isSelected) ProjectXNavyDeep else Color.White)
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontFamily = PlusJakartaFontFamily,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) ProjectXChampagneGold else ProjectXTextSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }

        // ==========================================
        // 7. LISTING & ASSET MANAGEMENT SECTION (Tab 0)
        // ==========================================
        if (selectedTab == 0) {
            // Status Sub-filter chips
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val statusChips = listOf(
                        "ALL" to "All (${myProperties.size})",
                        PropertyStatus.PUBLISHED.value to "Published ($activeListingsCount)",
                        PropertyStatus.PENDING_REVIEW.value to "Under Review ($pendingReviewCount)",
                        PropertyStatus.SOLD.value to "Sold / Rented ($soldRentedCount)"
                    )
                    statusChips.forEach { (statusKey, label) ->
                        val isSelected = listingStatusFilter == statusKey
                        Surface(
                            shape = RoundedCornerShape(2.dp),
                            color = if (isSelected) ProjectXCharcoal else ProjectXSand,
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) ProjectXChampagneGold else ProjectXBorder
                            ),
                            modifier = Modifier.clickable { listingStatusFilter = statusKey }
                        ) {
                            Text(
                                text = label,
                                fontFamily = PlusJakartaFontFamily,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) ProjectXChampagneGold else ProjectXTextSecondary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            if (filteredProperties.isEmpty()) {
                item {
                    EmptyStateCard(
                        title = "No properties match your filter",
                        subtitle = "Try resetting location or price range filters, or add a new listing to your portfolio."
                    )
                }
            } else {
                items(filteredProperties, key = { it.id }) { prop ->
                    Surface(
                        shape = RoundedCornerShape(2.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, ProjectXBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onPropertyClick(prop) }
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = prop.title,
                                        fontFamily = PlusJakartaFontFamily,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ProjectXNavyDeep,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "${prop.neighbourhood}, ${prop.city} (${prop.countryCode.uppercase()})",
                                        fontFamily = PlusJakartaFontFamily,
                                        fontSize = 11.5.sp,
                                        color = ProjectXTextSecondary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = prop.formattedPrice,
                                            fontFamily = PlayfairFontFamily,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = ProjectXChampagneGoldDark
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "• ${prop.bedrooms} Beds • ${prop.propertyType}",
                                            fontFamily = PlusJakartaFontFamily,
                                            fontSize = 11.sp,
                                            color = ProjectXCharcoalMuted
                                        )
                                    }
                                }

                                val statusObj = PropertyStatus.fromValue(prop.status)
                                Surface(
                                    shape = RoundedCornerShape(1.dp),
                                    color = when (statusObj) {
                                        PropertyStatus.PUBLISHED -> Color(0xFFF0FDF4)
                                        PropertyStatus.PENDING_REVIEW -> Color(0xFFFEF3C7)
                                        PropertyStatus.SOLD, PropertyStatus.RENTED -> Color(0xFFEFF6FF)
                                        else -> ProjectXSand
                                    },
                                    border = BorderStroke(
                                        0.8.dp,
                                        when (statusObj) {
                                            PropertyStatus.PUBLISHED -> Color(0xFF86EFAC)
                                            PropertyStatus.PENDING_REVIEW -> Color(0xFFFCD34D)
                                            PropertyStatus.SOLD, PropertyStatus.RENTED -> Color(0xFF93C5FD)
                                            else -> ProjectXBorder
                                        }
                                    )
                                ) {
                                    Text(
                                        text = statusObj.label.uppercase(),
                                        fontFamily = PlusJakartaFontFamily,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = when (statusObj) {
                                            PropertyStatus.PUBLISHED -> Color(0xFF15803D)
                                            PropertyStatus.PENDING_REVIEW -> Color(0xFFB45309)
                                            PropertyStatus.SOLD, PropertyStatus.RENTED -> Color(0xFF1D4ED8)
                                            else -> ProjectXCharcoal
                                        },
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                    )
                                }
                            }

                            // Media & Video badge
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(2.dp),
                                    color = ProjectXSand,
                                    border = BorderStroke(0.6.dp, ProjectXBorder)
                                ) {
                                    Text(
                                        text = "📷 ${prop.images.size} Photos",
                                        fontFamily = PlusJakartaFontFamily,
                                        fontSize = 9.sp,
                                        color = ProjectXTextSecondary,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }

                                if (!prop.videoUrl.isNullOrBlank() || prop.images.any { it.contains("video", ignoreCase = true) }) {
                                    Surface(
                                        shape = RoundedCornerShape(2.dp),
                                        color = ProjectXChampagneGoldContainer,
                                        border = BorderStroke(0.6.dp, ProjectXChampagneGold)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Videocam,
                                                contentDescription = null,
                                                tint = ProjectXChampagneGold,
                                                modifier = Modifier.size(11.dp)
                                            )
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Text(
                                                text = "VIDEO TOUR",
                                                fontFamily = PlusJakartaFontFamily,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = ProjectXChampagneGold
                                            )
                                        }
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(2.dp),
                                    color = ProjectXSand,
                                    border = BorderStroke(0.6.dp, ProjectXBorder)
                                ) {
                                    Text(
                                        text = "📜 ${prop.titleDocument}",
                                        fontFamily = PlusJakartaFontFamily,
                                        fontSize = 9.sp,
                                        color = ProjectXTextSecondary,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            HorizontalDivider(color = ProjectXBorder, thickness = 0.8.dp)
                            Spacer(modifier = Modifier.height(10.dp))

                            // Listing Management Action Buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                if (prop.status == PropertyStatus.PUBLISHED.value) {
                                    OutlinedButton(
                                        onClick = { onUpdatePropertyStatus(prop.id, PropertyStatus.SOLD.value) },
                                        shape = RoundedCornerShape(2.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Mark Sold", fontSize = 10.5.sp, color = ProjectXNavyDeep)
                                    }
                                    OutlinedButton(
                                        onClick = { onUpdatePropertyStatus(prop.id, PropertyStatus.RENTED.value) },
                                        shape = RoundedCornerShape(2.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Mark Leased", fontSize = 10.5.sp, color = ProjectXNavyDeep)
                                    }
                                    Button(
                                        onClick = { onPropertyClick(prop) },
                                        colors = ButtonDefaults.buttonColors(containerColor = ProjectXNavyLight),
                                        shape = RoundedCornerShape(2.dp),
                                        modifier = Modifier.weight(0.9f)
                                    ) {
                                        Text("View", fontSize = 10.5.sp, color = ProjectXChampagneGold)
                                    }
                                } else if (prop.status == PropertyStatus.DRAFT.value) {
                                    Button(
                                        onClick = { onUpdatePropertyStatus(prop.id, PropertyStatus.PENDING_REVIEW.value) },
                                        colors = ButtonDefaults.buttonColors(containerColor = ProjectXNavyDeep),
                                        shape = RoundedCornerShape(2.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Submit for Review", color = ProjectXChampagneGold, fontSize = 10.5.sp)
                                    }
                                } else if (prop.status == PropertyStatus.SOLD.value || prop.status == PropertyStatus.RENTED.value) {
                                    OutlinedButton(
                                        onClick = { onUpdatePropertyStatus(prop.id, PropertyStatus.PUBLISHED.value) },
                                        shape = RoundedCornerShape(2.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Re-list as Active", fontSize = 10.5.sp, color = ProjectXNavyDeep)
                                    }
                                } else {
                                    OutlinedButton(
                                        onClick = { onPropertyClick(prop) },
                                        shape = RoundedCornerShape(2.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("View Audit Status", fontSize = 10.5.sp, color = ProjectXNavyDeep)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // ==========================================
        // 8. BUYER LEADS / TENANT ENQUIRIES (Tab 1)
        // ==========================================
        else if (selectedTab == 1) {
            if (myEnquiries.isEmpty()) {
                item {
                    EmptyStateCard(
                        title = if (activePerspective == DashboardPerspective.AGENT) "No buyer enquiries currently" else "No tenant inquiries",
                        subtitle = "Client inquiries through the website and application channels will automatically appear here."
                    )
                }
            } else {
                items(myEnquiries) { enq ->
                    Surface(
                        shape = RoundedCornerShape(2.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, ProjectXBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = enq.customerName,
                                    fontFamily = PlusJakartaFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.5.sp,
                                    color = ProjectXNavyDeep
                                )
                                Surface(
                                    shape = RoundedCornerShape(1.dp),
                                    color = ProjectXSand,
                                    border = BorderStroke(0.6.dp, ProjectXBorder)
                                ) {
                                    Text(
                                        text = enq.status.uppercase(),
                                        fontFamily = PlusJakartaFontFamily,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ProjectXChampagneGoldDark,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "${enq.customerEmail} • ${enq.customerPhone}",
                                fontFamily = PlusJakartaFontFamily,
                                fontSize = 11.sp,
                                color = ProjectXTextSecondary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "\"${enq.message}\"",
                                fontFamily = PlusJakartaFontFamily,
                                fontSize = 12.sp,
                                color = ProjectXNavy
                            )
                        }
                    }
                }
            }
        }

        // ==========================================
        // 9. SCHEDULED INSPECTIONS / VIEWINGS (Tab 2)
        // ==========================================
        else if (selectedTab == 2) {
            if (myInspections.isEmpty()) {
                item {
                    EmptyStateCard(
                        title = "No scheduled viewings",
                        subtitle = "Private viewing and property inspection bookings will appear in this calendar."
                    )
                }
            } else {
                items(myInspections) { insp ->
                    Surface(
                        shape = RoundedCornerShape(2.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, ProjectXBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Viewing: ${insp.date} (${insp.timeSlot})",
                                    fontFamily = PlusJakartaFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.5.sp,
                                    color = ProjectXNavyDeep
                                )
                                Surface(
                                    shape = RoundedCornerShape(1.dp),
                                    color = ProjectXChampagneGoldMuted,
                                    border = BorderStroke(0.6.dp, ProjectXChampagneGold)
                                ) {
                                    Text(
                                        text = insp.status.uppercase(),
                                        fontFamily = PlusJakartaFontFamily,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ProjectXChampagneGoldDark,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Client: ${insp.customerName} (${insp.customerPhone})",
                                fontFamily = PlusJakartaFontFamily,
                                fontSize = 11.5.sp,
                                color = ProjectXTextSecondary
                            )
                            if (insp.notes.isNotBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Notes: ${insp.notes}",
                                    fontFamily = PlusJakartaFontFamily,
                                    fontSize = 11.sp,
                                    color = ProjectXCharcoalMuted
                                )
                            }
                        }
                    }
                }
            }
        }

        // ==========================================
        // 10. ACCREDITATION & ANALYTICS (Tab 3)
        // ==========================================
        else {
            item {
                Surface(
                    shape = RoundedCornerShape(2.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, ProjectXBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = if (activePerspective == DashboardPerspective.AGENT)
                                "REGULATORY ACCREDITATION & COMPLIANCE"
                            else
                                "PORTFOLIO YIELD & TITLE VERIFICATION AUDIT",
                            fontFamily = PlusJakartaFontFamily,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = ProjectXChampagneGoldDark,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (activePerspective == DashboardPerspective.AGENT)
                                "PROJECT X Certified International Property Consultant"
                            else
                                "PROJECT X Institutional Asset & Landlord Compliance",
                            fontFamily = PlayfairFontFamily,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = ProjectXNavyDeep
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (activePerspective == DashboardPerspective.AGENT)
                                "Certified by Estate Surveyors and Valuers Registration Board • NIESV • RICS Accredited Partner • AML/KYC Verified."
                            else
                                "All assets audited against verified state Land Registries (Certificate of Occupancy, Governor's Consent, Freehold Deeds).",
                            fontFamily = PlusJakartaFontFamily,
                            fontSize = 11.5.sp,
                            color = ProjectXTextSecondary
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = ProjectXBorder, thickness = 0.8.dp)
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "PORTFOLIO METRICS & CONVERSION",
                            fontFamily = PlusJakartaFontFamily,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = ProjectXChampagneGoldDark,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "• Inquiry Response Time: Under 12 minutes (Institutional Standard)\n• Verified Title Audit Compliance: 100% Passed\n• Multi-Currency Settlement: NGN, USD, GBP, AED active",
                            fontFamily = PlusJakartaFontFamily,
                            fontSize = 11.5.sp,
                            color = ProjectXNavy,
                            lineHeight = 19.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EditorialMetricCard(
    title: String,
    count: String,
    subtitle: String = "",
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(2.dp),
        color = Color.White,
        border = BorderStroke(1.dp, ProjectXBorder),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = ProjectXChampagneGoldDark,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = count,
                fontFamily = PlayfairFontFamily,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = ProjectXNavyDeep,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = title,
                fontFamily = PlusJakartaFontFamily,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = ProjectXNavy,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (subtitle.isNotEmpty()) {
                Text(
                    text = subtitle,
                    fontFamily = PlusJakartaFontFamily,
                    fontSize = 9.sp,
                    color = ProjectXCharcoalMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun EmptyStateCard(title: String, subtitle: String) {
    Surface(
        shape = RoundedCornerShape(2.dp),
        color = Color.White,
        border = BorderStroke(1.dp, ProjectXBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontFamily = PlayfairFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = ProjectXNavyDeep
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                fontFamily = PlusJakartaFontFamily,
                fontSize = 12.sp,
                color = ProjectXTextSecondary,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

private fun formatCompactCurrency(amount: Double): String {
    val billions = amount / 1_000_000_000.0
    val millions = amount / 1_000_000.0
    val format = NumberFormat.getNumberInstance(Locale.US)
    format.maximumFractionDigits = 1
    return when {
        billions >= 1.0 -> "₦${format.format(billions)}B"
        millions >= 1.0 -> "₦${format.format(millions)}M"
        else -> "₦${format.format(amount)}"
    }
}
