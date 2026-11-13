package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bathtub
import androidx.compose.material.icons.filled.Bed
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.SquareFoot
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.R
import com.example.data.enums.PropertyStatus
import com.example.data.model.Property
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
import com.example.ui.theme.StatusSuccess

@Composable
fun PropertyCard(
    property: Property,
    isFavourite: Boolean,
    onToggleFavourite: (Property) -> Unit,
    onClick: (Property) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick(property) }
            .testTag("property_card_${property.id}"),
        shape = RoundedCornerShape(2.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, HavenGreyBorder)
    ) {
        Column {
            // Authentic architectural photo frame
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 10f)
                    .background(HavenGreyLight)
            ) {
                PropertyThumbnail(property = property)

                // Top Tag Bar: Listing Type & Legal Status
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        // Listing Type Badge (Editorial Minimalist)
                        val typeLabel = when (property.listingType.lowercase()) {
                            "sale" -> "FOR SALE"
                            "rent" -> "FOR LEASE"
                            "shortlet" -> "PRIME STAY"
                            else -> property.listingType.uppercase()
                        }
                        Surface(
                            shape = RoundedCornerShape(1.dp),
                            color = HavenNavyDark.copy(alpha = 0.92f),
                            modifier = Modifier.border(0.5.dp, HavenGold.copy(alpha = 0.5f), RoundedCornerShape(1.dp))
                        ) {
                            Text(
                                text = typeLabel,
                                fontFamily = PlusJakartaFontFamily,
                                color = HavenGold,
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        // Verified Title Badge
                        if (property.isVerified) {
                            Surface(
                                shape = RoundedCornerShape(1.dp),
                                color = HavenNavyDark.copy(alpha = 0.92f),
                                modifier = Modifier.border(0.5.dp, Color(0xFF15803D).copy(alpha = 0.6f), RoundedCornerShape(1.dp))
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.VerifiedUser,
                                        contentDescription = "Title Verified",
                                        tint = Color(0xFF22C55E),
                                        modifier = Modifier.size(11.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "TITLE VERIFIED",
                                        fontFamily = PlusJakartaFontFamily,
                                        color = Color.White,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.8.sp
                                    )
                                }
                            }
                        }

                        // DEMONSTRATION PROPERTY label to strictly avoid misleading buyers
                        Surface(
                            shape = RoundedCornerShape(1.dp),
                            color = Color(0xCC000000)
                        ) {
                            Text(
                                text = "DEMONSTRATION PROPERTY",
                                fontFamily = PlusJakartaFontFamily,
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                            )
                        }
                    }

                    // Save / Favourite button
                    Surface(
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.94f),
                        shadowElevation = 2.dp,
                        modifier = Modifier.size(34.dp)
                    ) {
                        IconButton(
                            onClick = { onToggleFavourite(property) },
                            modifier = Modifier
                                .fillMaxSize()
                                .testTag("fav_button_${property.id}")
                        ) {
                            Icon(
                                imageVector = if (isFavourite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = if (isFavourite) "Remove from saved" else "Save residence",
                                tint = if (isFavourite) Color(0xFFB91C1C) else HavenNavy,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            // Editorial Listing Details
            Column(modifier = Modifier.padding(16.dp)) {
                // Price in Editorial Serif
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = property.formattedPrice,
                        fontFamily = PlayfairFontFamily,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = HavenNavyDark,
                        letterSpacing = (-0.2).sp
                    )

                    // Price per sqft or sqm if applicable
                    if (property.sizeSqFt > 0) {
                        Text(
                            text = "${property.sizeSqFt.toString()} SQ FT",
                            fontFamily = PlusJakartaFontFamily,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = HavenGreyMuted,
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Title
                Text(
                    text = property.title,
                    fontFamily = PlusJakartaFontFamily,
                    fontSize = 14.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = HavenNavy,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Location line
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location",
                        tint = HavenGoldDark,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    val locDisplay = listOfNotNull(
                        property.neighbourhood.ifBlank { null },
                        property.city.ifBlank { null },
                        property.countryCode
                    ).joinToString(", ")
                    Text(
                        text = locDisplay,
                        fontFamily = PlusJakartaFontFamily,
                        fontSize = 12.sp,
                        color = HavenGreyText,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = HavenGreyBorder, thickness = 0.8.dp)
                Spacer(modifier = Modifier.height(10.dp))

                // Specifications: Bed • Bath • Title
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (property.bedrooms > 0) {
                        EditorialSpecItem(icon = Icons.Default.Bed, label = "${property.bedrooms} Beds")
                    }
                    if (property.bathrooms > 0) {
                        EditorialSpecItem(icon = Icons.Default.Bathtub, label = "${property.bathrooms} Baths")
                    }
                    if (property.titleDocument.isNotBlank()) {
                        Text(
                            text = property.titleDocument.uppercase(),
                            fontFamily = PlusJakartaFontFamily,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = HavenGoldDark,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PropertyCardHorizontal(
    property: Property,
    isFavourite: Boolean,
    onToggleFavourite: (Property) -> Unit,
    onClick: (Property) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick(property) }
            .testTag("property_card_h_${property.id}"),
        shape = RoundedCornerShape(2.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, HavenGreyBorder)
    ) {
        Row(modifier = Modifier.height(130.dp)) {
            // Left Image
            Box(
                modifier = Modifier
                    .width(135.dp)
                    .fillMaxHeight()
                    .background(HavenGreyLight)
            ) {
                PropertyThumbnail(property = property)

                Surface(
                    shape = RoundedCornerShape(1.dp),
                    color = HavenNavyDark.copy(alpha = 0.85f),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(6.dp)
                ) {
                    Text(
                        text = "DEMONSTRATION PROPERTY",
                        fontFamily = PlusJakartaFontFamily,
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 7.5.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }

            // Right Details
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = property.formattedPrice,
                        fontFamily = PlayfairFontFamily,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = HavenNavyDark
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = property.title,
                        fontFamily = PlusJakartaFontFamily,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = HavenNavy,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${property.neighbourhood}, ${property.city}",
                        fontFamily = PlusJakartaFontFamily,
                        fontSize = 11.sp,
                        color = HavenGreyMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${property.bedrooms} Beds • ${property.bathrooms} Baths",
                        fontFamily = PlusJakartaFontFamily,
                        fontSize = 11.sp,
                        color = HavenGreyText
                    )

                    IconButton(
                        onClick = { onToggleFavourite(property) },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = if (isFavourite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Save",
                            tint = if (isFavourite) Color(0xFFB91C1C) else HavenGreyMuted,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PropertyThumbnail(property: Property) {
    val firstImage = property.images.firstOrNull() ?: "img_haven_estate"
    val localRes = when (firstImage) {
        "haven_hero_banner" -> R.drawable.haven_hero_banner
        "haven_penthouse" -> R.drawable.haven_penthouse
        "img_haven_estate" -> R.drawable.img_haven_estate
        "img_haven_waterfront" -> R.drawable.img_haven_waterfront
        else -> null
    }

    if (localRes != null) {
        Image(
            painter = painterResource(id = localRes),
            contentDescription = property.title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    } else {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(firstImage)
                .crossfade(true)
                .build(),
            placeholder = painterResource(id = R.drawable.img_haven_estate),
            error = painterResource(id = R.drawable.img_haven_estate),
            contentDescription = property.title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
private fun EditorialSpecItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = HavenGoldDark,
            modifier = Modifier.size(13.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            fontFamily = PlusJakartaFontFamily,
            fontSize = 11.5.sp,
            fontWeight = FontWeight.Medium,
            color = HavenGreyText
        )
    }
}

