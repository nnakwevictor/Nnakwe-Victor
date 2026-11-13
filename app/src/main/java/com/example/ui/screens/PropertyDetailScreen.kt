package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bathtub
import androidx.compose.material.icons.filled.Bed
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SquareFoot
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.R
import com.example.data.model.Property
import com.example.data.model.User
import com.example.ui.components.EnquiryDialog
import com.example.ui.components.InspectionBookingDialog
import com.example.ui.components.MortgageCalculatorDialog
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
import com.example.ui.theme.StatusSuccess

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PropertyDetailScreen(
    property: Property,
    currentUser: User?,
    isFavourite: Boolean,
    onBack: () -> Unit,
    onToggleFavourite: (Property) -> Unit,
    onBookInspection: (date: String, timeSlot: String, notes: String) -> Unit,
    onSendEnquiry: (name: String, email: String, phone: String, message: String) -> Unit,
    onStartChat: ((agentId: String, agentName: String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }
    val context = LocalContext.current

    var showBookingDialog by remember { mutableStateOf(false) }
    var showEnquiryDialog by remember { mutableStateOf(false) }
    var showMortgageCalc by remember { mutableStateOf(false) }
    var descriptionExpanded by remember { mutableStateOf(false) }

    val images = remember(property) {
        if (property.images.isNotEmpty()) property.images
        else listOf("img_haven_estate", "img_haven_waterfront", "haven_penthouse")
    }
    val pagerState = rememberPagerState(pageCount = { images.size })

    Box(modifier = modifier.fillMaxSize().background(Color(0xFFFAFAF8))) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 90.dp)
        ) {
            // 1. EDITORIAL FULL-BLEED GALLERY
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(340.dp)
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    val img = images[page]
                    val localRes = when (img) {
                        "img_haven_estate" -> R.drawable.img_haven_estate
                        "img_haven_waterfront" -> R.drawable.img_haven_waterfront
                        "haven_hero_banner" -> R.drawable.haven_hero_banner
                        "haven_penthouse" -> R.drawable.haven_penthouse
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
                            model = ImageRequest.Builder(context)
                                .data(img)
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

                // Scrim
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    HavenNavyDark.copy(alpha = 0.65f),
                                    Color.Transparent,
                                    HavenNavyDark.copy(alpha = 0.85f)
                                )
                            )
                        )
                )

                // Top Navigation Actions
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(2.dp),
                        color = HavenNavyDark.copy(alpha = 0.9f),
                        border = BorderStroke(0.8.dp, HavenGold.copy(alpha = 0.5f))
                    ) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier.testTag("detail_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = HavenGold
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Surface(
                            shape = RoundedCornerShape(2.dp),
                            color = HavenNavyDark.copy(alpha = 0.9f),
                            border = BorderStroke(0.8.dp, HavenGold.copy(alpha = 0.5f))
                        ) {
                            IconButton(onClick = {
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, "Explore ${property.title} on PROJECT X — GLOBAL PROPERTY GROUP: ${property.formattedPrice}")
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "Share Residence"))
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Share",
                                    tint = Color.White
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(2.dp),
                            color = HavenNavyDark.copy(alpha = 0.9f),
                            border = BorderStroke(0.8.dp, HavenGold.copy(alpha = 0.5f))
                        ) {
                            IconButton(
                                onClick = { onToggleFavourite(property) },
                                modifier = Modifier.testTag("detail_fav_button")
                            ) {
                                Icon(
                                    imageVector = if (isFavourite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = "Favourite",
                                    tint = if (isFavourite) Color(0xFFB91C1C) else Color.White
                                )
                            }
                        }
                    }
                }

                // Video Tour Link & Image Pager Index Pill
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (!property.videoUrl.isNullOrBlank()) {
                        Surface(
                            shape = RoundedCornerShape(2.dp),
                            color = HavenNavyDark.copy(alpha = 0.95f),
                            border = BorderStroke(1.dp, HavenGold),
                            modifier = Modifier.clickable {
                                try {
                                    val videoIntent = Intent(Intent.ACTION_VIEW, Uri.parse(property.videoUrl))
                                    context.startActivity(videoIntent)
                                } catch (e: Exception) {
                                    // Fallback handled gracefully
                                }
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "🎥 WATCH VIDEO TOUR",
                                    fontFamily = PlusJakartaFontFamily,
                                    color = HavenGold,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            }
                        }
                    } else {
                        Spacer(modifier = Modifier.width(1.dp))
                    }

                    Surface(
                        shape = RoundedCornerShape(2.dp),
                        color = HavenNavyDark.copy(alpha = 0.9f),
                        border = BorderStroke(0.5.dp, HavenGold.copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = "${pagerState.currentPage + 1} OF ${images.size}",
                            fontFamily = PlusJakartaFontFamily,
                            color = HavenGold,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // 2. EDITORIAL PRICING & IDENTITY
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val typeLabel = when (property.listingType.lowercase()) {
                        "sale" -> "FOR SALE"
                        "rent" -> "FOR LEASE"
                        "shortlet" -> "PRIME STAY"
                        else -> property.listingType.uppercase()
                    }

                    Surface(
                        shape = RoundedCornerShape(1.dp),
                        color = HavenGoldContainer,
                        border = BorderStroke(0.8.dp, HavenGold.copy(alpha = 0.6f))
                    ) {
                        Text(
                            text = typeLabel,
                            fontFamily = PlusJakartaFontFamily,
                            color = HavenGold,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(1.dp),
                        color = HavenGreyLight
                    ) {
                        Text(
                            text = "DEMONSTRATION PROPERTY",
                            fontFamily = PlusJakartaFontFamily,
                            color = HavenGreyText,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = property.formattedPrice,
                    fontFamily = PlayfairFontFamily,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = HavenNavyDark,
                    letterSpacing = (-0.5).sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = property.title,
                    fontFamily = PlayfairFontFamily,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Medium,
                    color = HavenNavyDark,
                    lineHeight = 27.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Address",
                        tint = HavenGoldDark,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = property.address.ifBlank { "${property.neighbourhood}, ${property.city}, ${property.state}, ${property.countryCode}" },
                        fontFamily = PlusJakartaFontFamily,
                        fontSize = 13.sp,
                        color = HavenGreyText
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // 3. INSTITUTIONAL TITLE VERIFICATION BANNER
                if (property.isVerified) {
                    Surface(
                        shape = RoundedCornerShape(2.dp),
                        color = HavenNavyDark,
                        border = BorderStroke(1.dp, HavenGold.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.VerifiedUser,
                                contentDescription = "Verified Title",
                                tint = HavenGold,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "GOVERNMENT TITLE DEED VERIFIED",
                                    fontFamily = PlusJakartaFontFamily,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = HavenGold,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Certificate of Occupancy, Governor's Consent & Land Registry deed audited by Haven legal partners.",
                                    fontFamily = PlusJakartaFontFamily,
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.8f),
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(18.dp))
                }

                // 4. ARCHITECTURAL SPECIFICATIONS TABLE
                Text(
                    text = "SPECIFICATIONS",
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
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            SpecTableItem("Bedrooms", "${property.bedrooms} Ensuite")
                            SpecTableItem("Bathrooms", "${property.bathrooms} Bath")
                            SpecTableItem("Interior Size", "${property.sizeSqFt} SQ FT")
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = HavenGreyBorder, thickness = 0.8.dp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            SpecTableItem("Legal Title", if (property.titleDocument.isNotBlank()) property.titleDocument else "C of O")
                            SpecTableItem("Property Type", property.propertyType.uppercase())
                            SpecTableItem("Territory", property.countryCode)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 5. RESIDENCE DESCRIPTION
                Text(
                    text = "THE RESIDENCE",
                    fontFamily = PlusJakartaFontFamily,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = HavenGoldDark,
                    letterSpacing = 1.5.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = property.description,
                    fontFamily = PlusJakartaFontFamily,
                    fontSize = 13.5.sp,
                    color = HavenGreyDark,
                    lineHeight = 22.sp,
                    maxLines = if (descriptionExpanded) Int.MAX_VALUE else 4
                )
                if (property.description.length > 180) {
                    Text(
                        text = if (descriptionExpanded) "READ LESS" else "READ FULL OVERVIEW",
                        fontFamily = PlusJakartaFontFamily,
                        color = HavenGoldDark,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        letterSpacing = 1.sp,
                        modifier = Modifier
                            .clickable { descriptionExpanded = !descriptionExpanded }
                            .padding(top = 6.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 6. CURATED AMENITIES
                if (property.amenities.isNotEmpty()) {
                    Text(
                        text = "AMENITIES & FEATURES",
                        fontFamily = PlusJakartaFontFamily,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = HavenGoldDark,
                        letterSpacing = 1.5.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        property.amenities.forEach { amenity ->
                            Surface(
                                shape = RoundedCornerShape(2.dp),
                                color = Color.White,
                                border = BorderStroke(1.dp, HavenGreyBorder)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = HavenGoldDark,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = amenity,
                                        fontFamily = PlusJakartaFontFamily,
                                        fontSize = 11.5.sp,
                                        color = HavenNavyDark,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }

                // 7. CERTIFIED LISTING ADVISOR CARD
                Text(
                    text = "EXCLUSIVE LISTING ADVISOR",
                    fontFamily = PlusJakartaFontFamily,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = HavenGoldDark,
                    letterSpacing = 1.5.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(2.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, HavenGreyBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .background(HavenNavyDark),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = property.agentName.take(1).ifBlank { "H" },
                                    fontFamily = PlayfairFontFamily,
                                    color = HavenGold,
                                    fontSize = 19.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = property.agentName.ifBlank { "PROJECT X Executive Advisory" },
                                    fontFamily = PlusJakartaFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.5.sp,
                                    color = HavenNavyDark
                                )
                                Text(
                                    text = "PROJECT X — GLOBAL PROPERTY GROUP • Certified Partner",
                                    fontFamily = PlusJakartaFontFamily,
                                    fontSize = 11.5.sp,
                                    color = HavenGreyText
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    onStartChat?.invoke(property.agentId, property.agentName)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = HavenNavyDark),
                                modifier = Modifier
                                    .weight(1.3f)
                                    .height(44.dp)
                                    .testTag("detail_chat_agent_btn"),
                                shape = RoundedCornerShape(2.dp)
                            ) {
                                Icon(imageVector = Icons.Default.ChatBubble, contentDescription = "Chat", tint = HavenGold, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("DIRECT MESSAGE", fontFamily = PlusJakartaFontFamily, color = HavenGold, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                            }

                            OutlinedButton(
                                onClick = {
                                    val callIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${property.agentPhone}"))
                                    context.startActivity(callIntent)
                                },
                                modifier = Modifier
                                    .weight(0.9f)
                                    .height(44.dp),
                                shape = RoundedCornerShape(2.dp),
                                border = BorderStroke(1.dp, HavenGreyBorder)
                            ) {
                                Icon(imageVector = Icons.Default.Call, contentDescription = "Call", tint = HavenNavyDark, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("CALL", fontFamily = PlusJakartaFontFamily, color = HavenNavyDark, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }

                            OutlinedButton(
                                onClick = {
                                    val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:${property.agentEmail}"))
                                    context.startActivity(emailIntent)
                                },
                                modifier = Modifier
                                    .weight(0.9f)
                                    .height(44.dp),
                                shape = RoundedCornerShape(2.dp),
                                border = BorderStroke(1.dp, HavenGreyBorder)
                            ) {
                                Icon(imageVector = Icons.Default.Mail, contentDescription = "Email", tint = HavenNavyDark, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("EMAIL", fontFamily = PlusJakartaFontFamily, color = HavenNavyDark, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // 8. INTERACTIVE FINANCING & MORTGAGE CALCULATOR BANNER
                Surface(
                    shape = RoundedCornerShape(2.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, HavenGold.copy(alpha = 0.6f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showMortgageCalc = true }
                        .testTag("open_mortgage_calc_card")
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(HavenNavyDark),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Calculate,
                                    contentDescription = null,
                                    tint = HavenGold,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Mortgage & Acquisition Financing",
                                    fontFamily = PlayfairFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = HavenNavyDark
                                )
                                Text(
                                    text = "Simulate equity down payment, tenure & monthly yield",
                                    fontFamily = PlusJakartaFontFamily,
                                    fontSize = 11.sp,
                                    color = HavenGreyText
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = HavenGoldDark
                        )
                    }
                }
            }
        }

        // STICKY BOTTOM ACTIONS (Inspection & Confidential Enquiry)
        Surface(
            color = Color.White,
            shadowElevation = 8.dp,
            border = BorderStroke(1.dp, HavenGreyBorder),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = { showEnquiryDialog = true },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("detail_enquire_button"),
                    shape = RoundedCornerShape(2.dp),
                    border = BorderStroke(1.2.dp, HavenNavyDark)
                ) {
                    Icon(imageVector = Icons.Default.Message, contentDescription = null, tint = HavenNavyDark, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("ENQUIRE", fontFamily = PlusJakartaFontFamily, color = HavenNavyDark, fontWeight = FontWeight.Bold, fontSize = 12.sp, letterSpacing = 0.5.sp)
                }

                Button(
                    onClick = { showBookingDialog = true },
                    modifier = Modifier
                        .weight(1.3f)
                        .height(48.dp)
                        .testTag("detail_book_inspection_button"),
                    shape = RoundedCornerShape(2.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = HavenNavyDark)
                ) {
                    Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = null, tint = HavenGold, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("SCHEDULE VIEWING", fontFamily = PlusJakartaFontFamily, color = HavenGold, fontWeight = FontWeight.Bold, fontSize = 12.sp, letterSpacing = 0.5.sp)
                }
            }
        }

        // Dialogs
        if (showBookingDialog) {
            InspectionBookingDialog(
                property = property,
                onConfirm = { date, time, notes ->
                    onBookInspection(date, time, notes)
                    showBookingDialog = false
                },
                onDismiss = { showBookingDialog = false }
            )
        }

        if (showEnquiryDialog) {
            EnquiryDialog(
                property = property,
                currentUser = currentUser,
                onSend = { name, email, phone, msg ->
                    onSendEnquiry(name, email, phone, msg)
                    showEnquiryDialog = false
                },
                onDismiss = { showEnquiryDialog = false }
            )
        }

        if (showMortgageCalc) {
            MortgageCalculatorDialog(
                property = property,
                onDismiss = { showMortgageCalc = false }
            )
        }
    }
}

@Composable
private fun SpecTableItem(
    label: String,
    value: String
) {
    Column {
        Text(
            text = label.uppercase(),
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
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = HavenNavyDark
        )
    }
}
