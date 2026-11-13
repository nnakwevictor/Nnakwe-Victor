package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.HavenGold
import com.example.ui.theme.HavenNavy

data class ExploreFilterState(
    val listingType: String = "ALL", // ALL, sale, rent, shortlet
    val minBedrooms: Int = 0,
    val minBathrooms: Int = 0,
    val sortBy: String = "newest", // newest, price_asc, price_desc
    val verifiedOnly: Boolean = false,
    val includeClosed: Boolean = false // includes sold & rented
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FilterSheet(
    initialFilters: ExploreFilterState,
    onApplyFilters: (ExploreFilterState) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var listingType by remember { mutableStateOf(initialFilters.listingType) }
    var minBedrooms by remember { mutableIntStateOf(initialFilters.minBedrooms) }
    var minBathrooms by remember { mutableIntStateOf(initialFilters.minBathrooms) }
    var sortBy by remember { mutableStateOf(initialFilters.sortBy) }
    var verifiedOnly by remember { mutableStateOf(initialFilters.verifiedOnly) }
    var includeClosed by remember { mutableStateOf(initialFilters.includeClosed) }

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
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Refine Properties",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = HavenNavy
                )
                Text(
                    text = "Reset All",
                    fontSize = 14.sp,
                    color = HavenGold,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .clickable {
                            listingType = "ALL"
                            minBedrooms = 0
                            minBathrooms = 0
                            sortBy = "newest"
                            verifiedOnly = false
                            includeClosed = false
                        }
                        .testTag("reset_filters_btn")
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sort By
            Text(
                text = "Sort Order",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = HavenNavy
            )
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val sortOptions = listOf(
                    "newest" to "Newest First",
                    "price_asc" to "Price: Low to High",
                    "price_desc" to "Price: High to Low"
                )
                sortOptions.forEach { (key, label) ->
                    val isSelected = sortBy == key
                    FilterChip(
                        label = label,
                        isSelected = isSelected,
                        onClick = { sortBy = key },
                        testTag = "sort_chip_$key"
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Listing Type
            Text(
                text = "Listing Type",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = HavenNavy
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val types = listOf(
                    "ALL" to "All",
                    "sale" to "For Sale",
                    "rent" to "For Rent",
                    "shortlet" to "Shortlet"
                )
                types.forEach { (key, label) ->
                    FilterChip(
                        label = label,
                        isSelected = listingType.equals(key, ignoreCase = true),
                        onClick = { listingType = key },
                        testTag = "listing_type_$key"
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Bedrooms
            Text(
                text = "Bedrooms",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = HavenNavy
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val bedOptions = listOf(0 to "Any", 2 to "2+", 3 to "3+", 4 to "4+", 5 to "5+")
                bedOptions.forEach { (count, label) ->
                    FilterChip(
                        label = label,
                        isSelected = minBedrooms == count,
                        onClick = { minBedrooms = count },
                        testTag = "bed_chip_$count"
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Bathrooms
            Text(
                text = "Bathrooms",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = HavenNavy
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val bathOptions = listOf(0 to "Any", 2 to "2+", 3 to "3+", 4 to "4+", 5 to "5+")
                bathOptions.forEach { (count, label) ->
                    FilterChip(
                        label = label,
                        isSelected = minBathrooms == count,
                        onClick = { minBathrooms = count },
                        testTag = "bath_chip_$count"
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Toggles: Verified & Include Closed
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Verified Listings Only",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = HavenNavy
                    )
                    Text(
                        text = "Show only properties with verified title deeds",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )
                }
                Switch(
                    checked = verifiedOnly,
                    onCheckedChange = { verifiedOnly = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = HavenGold)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Include Sold & Rented",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = HavenNavy
                    )
                    Text(
                        text = "Show historical closed transaction records",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )
                }
                Switch(
                    checked = includeClosed,
                    onCheckedChange = { includeClosed = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = HavenNavy)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Cancel", color = HavenNavy)
                }
                Button(
                    onClick = {
                        onApplyFilters(
                            ExploreFilterState(
                                listingType = listingType,
                                minBedrooms = minBedrooms,
                                minBathrooms = minBathrooms,
                                sortBy = sortBy,
                                verifiedOnly = verifiedOnly,
                                includeClosed = includeClosed
                            )
                        )
                        onDismiss()
                    },
                    modifier = Modifier
                        .weight(1.5f)
                        .testTag("apply_filters_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = HavenNavy),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Apply Filters", color = HavenGold, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun FilterChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) HavenNavy else Color(0xFFF1F5F9),
        modifier = Modifier
            .clickable { onClick() }
            .testTag(testTag)
    ) {
        Text(
            text = label,
            color = if (isSelected) HavenGold else Color(0xFF334155),
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        )
    }
}
