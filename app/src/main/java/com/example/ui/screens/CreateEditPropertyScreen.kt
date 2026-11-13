package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.UploadFile
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import com.example.data.enums.PropertyStatus
import com.example.data.model.GlobalJurisdictionCatalog
import com.example.data.model.Property
import com.example.data.model.User
import com.example.ui.components.CountrySelectorSheet
import com.example.ui.theme.HavenGold
import com.example.ui.theme.HavenGoldDark
import com.example.ui.theme.HavenGreyBorder
import com.example.ui.theme.HavenGreyLight
import com.example.ui.theme.HavenGreyMuted
import com.example.ui.theme.HavenGreyText
import com.example.ui.theme.HavenNavy
import com.example.ui.theme.HavenNavyDark
import com.example.ui.theme.HavenNavyLight
import com.example.ui.theme.PlayfairFontFamily
import com.example.ui.theme.PlusJakartaFontFamily
import java.util.UUID

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CreateEditPropertyScreen(
    currentUser: User,
    onSaveProperty: (Property) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onCancel() }
    val context = LocalContext.current

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var listingType by remember { mutableStateOf("sale") }
    var priceText by remember { mutableStateOf("") }

    // International Jurisdiction & Currency State
    var countryCode by remember { mutableStateOf(currentUser.preferredCountry.ifBlank { "NG" }) }
    var currency by remember { mutableStateOf(GlobalJurisdictionCatalog.getCurrencyForCountry(countryCode)) }
    var measurementUnit by remember { mutableStateOf("sq ft") }
    var luxuryCategory by remember { mutableStateOf("Waterfront Sanctuary") }
    var propertyType by remember { mutableStateOf("Detached Villa") }
    var titleDocument by remember { mutableStateOf("Certificate of Occupancy") }

    // International Address State
    var state by remember { mutableStateOf(if (countryCode == "NG") "Lagos" else "Dubai") }
    var city by remember { mutableStateOf(if (countryCode == "NG") "Lagos" else "Dubai") }
    var lga by remember { mutableStateOf("Eti-Osa") }
    var neighbourhood by remember { mutableStateOf("Banana Island") }
    var address by remember { mutableStateOf("") }
    var postalCode by remember { mutableStateOf("") }

    var bedrooms by remember { mutableIntStateOf(4) }
    var bathrooms by remember { mutableIntStateOf(5) }
    var toilets by remember { mutableIntStateOf(5) }
    var parking by remember { mutableIntStateOf(4) }
    var sizeSqFt by remember { mutableIntStateOf(5000) }

    // Authentic Media Upload State (Zero-permission Android Photo Picker for Images & Videos)
    val uploadedImages = remember { mutableStateListOf<String>("img_haven_waterfront") }
    var videoTourUrl by remember { mutableStateOf("") }
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 10)
    ) { uris: List<Uri> ->
        uris.forEach { uri ->
            uploadedImages.add(uri.toString())
        }
    }

    val allAmenities = listOf(
        "Swimming Pool", "Waterfront View", "24/7 Power", "Smart Home Automation",
        "Elevator", "Cinema Room", "Gym & Spa", "Private Yacht Jetty", "Concierge Service", "Security Bunker"
    )
    val selectedAmenities = remember { mutableStateListOf("Swimming Pool", "24/7 Power", "Smart Home Automation") }

    var showCountrySheet by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFAFAF8))
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Top Editorial Bar
        Surface(
            color = HavenNavyDark,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onCancel) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = HavenGold)
                }
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = "NEW PROPERTY SUBMISSION",
                        fontFamily = PlusJakartaFontFamily,
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = HavenGold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Submit Prime Listing & Title Deed",
                        fontFamily = PlayfairFontFamily,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // 1. AUTHENTIC PROPERTY MEDIA UPLOAD SECTION
            Text("PHOTOGRAPHY & MEDIA FILES", fontFamily = PlusJakartaFontFamily, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = HavenGoldDark, letterSpacing = 1.5.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "Upload authentic, high-resolution architectural photographs of the estate. Real photographs uploaded by owners/brokers are audited prior to approval.",
                fontFamily = PlusJakartaFontFamily,
                fontSize = 11.5.sp,
                color = HavenGreyText
            )
            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                shape = RoundedCornerShape(2.dp),
                color = Color.White,
                border = BorderStroke(1.dp, HavenGreyBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${uploadedImages.size} Media Files Attached",
                            fontFamily = PlusJakartaFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.5.sp,
                            color = HavenNavyDark
                        )
                        Button(
                            onClick = {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = HavenNavyDark),
                            shape = RoundedCornerShape(2.dp),
                            modifier = Modifier.testTag("upload_media_btn")
                        ) {
                            Icon(imageVector = Icons.Default.AddPhotoAlternate, contentDescription = null, tint = HavenGold, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("ADD PHOTOS & VIDEOS", fontFamily = PlusJakartaFontFamily, color = HavenGold, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(uploadedImages) { mediaPath ->
                            Box(
                                modifier = Modifier
                                    .size(90.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .border(1.dp, HavenGreyBorder, RoundedCornerShape(2.dp))
                            ) {
                                val localRes = when (mediaPath) {
                                    "img_haven_waterfront" -> R.drawable.img_haven_waterfront
                                    "img_haven_estate" -> R.drawable.img_haven_estate
                                    "haven_hero_banner" -> R.drawable.haven_hero_banner
                                    "haven_penthouse" -> R.drawable.haven_penthouse
                                    else -> null
                                }
                                if (localRes != null) {
                                    Image(
                                        painter = painterResource(id = localRes),
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    AsyncImage(
                                        model = ImageRequest.Builder(context).data(mediaPath).crossfade(true).build(),
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }

                                if (mediaPath.contains("video", ignoreCase = true) || mediaPath.endsWith(".mp4", ignoreCase = true)) {
                                    Surface(
                                        shape = RoundedCornerShape(1.dp),
                                        color = HavenNavyDark.copy(alpha = 0.85f),
                                        modifier = Modifier.align(Alignment.BottomStart).padding(4.dp)
                                    ) {
                                        Text(
                                            text = "VIDEO",
                                            fontFamily = PlusJakartaFontFamily,
                                            fontSize = 8.sp,
                                            color = HavenGold,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                if (uploadedImages.size > 1) {
                                    Surface(
                                        shape = CircleShape,
                                        color = Color.Black.copy(alpha = 0.7f),
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(4.dp)
                                            .size(20.dp)
                                            .clickable { uploadedImages.remove(mediaPath) }
                                    ) {
                                        Icon(imageVector = Icons.Default.Close, contentDescription = "Remove", tint = Color.White, modifier = Modifier.size(12.dp))
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = HavenGreyBorder, thickness = 0.8.dp)
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "VIRTUAL WALKTHROUGH / VIDEO TOUR LINK (OPTIONAL)",
                        fontFamily = PlusJakartaFontFamily,
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = HavenNavyDark,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = videoTourUrl,
                        onValueChange = { videoTourUrl = it },
                        placeholder = { Text("https://youtu.be/... or Vimeo / Cloud 4K Tour", fontFamily = PlusJakartaFontFamily, fontSize = 11.5.sp, color = HavenGreyMuted) },
                        modifier = Modifier.fillMaxWidth().testTag("video_tour_url_input"),
                        shape = RoundedCornerShape(2.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = HavenNavyDark,
                            unfocusedBorderColor = HavenGreyBorder
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 2. INTERNATIONAL JURISDICTION & CURRENCY CONSOLE
            Text("INTERNATIONAL JURISDICTION & CURRENCY", fontFamily = PlusJakartaFontFamily, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = HavenGoldDark, letterSpacing = 1.5.sp)
            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                shape = RoundedCornerShape(2.dp),
                color = Color.White,
                border = BorderStroke(1.dp, HavenGreyBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showCountrySheet = true }
                    .testTag("select_country_in_form")
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val c = GlobalJurisdictionCatalog.getCountry(countryCode)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = c?.flagEmoji ?: "🌐", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "${c?.name ?: countryCode} (${c?.code ?: countryCode})",
                                fontFamily = PlusJakartaFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.5.sp,
                                color = HavenNavyDark
                            )
                            Text(
                                text = "Currency: $currency • Calling: ${c?.callingCode ?: "+1"} • Units: $measurementUnit",
                                fontFamily = PlusJakartaFontFamily,
                                fontSize = 11.sp,
                                color = HavenGreyText
                            )
                        }
                    }
                    Text("CHANGE", fontFamily = PlusJakartaFontFamily, color = HavenGoldDark, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 3. TITLE & DESCRIPTION
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Listing Title", fontFamily = PlusJakartaFontFamily, fontSize = 12.sp) },
                placeholder = { Text("E.g., The Grand Waterfront Villa", fontFamily = PlusJakartaFontFamily) },
                modifier = Modifier.fillMaxWidth().testTag("create_title_input"),
                shape = RoundedCornerShape(2.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = HavenNavyDark,
                    unfocusedBorderColor = HavenGreyBorder,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Architectural Description & Details", fontFamily = PlusJakartaFontFamily, fontSize = 12.sp) },
                modifier = Modifier.fillMaxWidth().testTag("create_desc_input"),
                shape = RoundedCornerShape(2.dp),
                minLines = 3,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = HavenNavyDark,
                    unfocusedBorderColor = HavenGreyBorder,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 4. TRANSACTION TYPE & ASKING PRICE
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val types = listOf("sale" to "Buy / Sale", "rent" to "Lease / Rent", "shortlet" to "Prime Stay")
                types.forEach { (tKey, tLabel) ->
                    val isSel = listingType == tKey
                    Surface(
                        shape = RoundedCornerShape(2.dp),
                        color = if (isSel) HavenNavyDark else Color.White,
                        border = BorderStroke(1.dp, if (isSel) HavenNavyDark else HavenGreyBorder),
                        modifier = Modifier.weight(1f).clickable { listingType = tKey }
                    ) {
                        Text(
                            text = tLabel,
                            fontFamily = PlusJakartaFontFamily,
                            color = if (isSel) HavenGold else HavenNavyDark,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 10.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = priceText,
                onValueChange = { priceText = it.filter { char -> char.isDigit() } },
                label = { Text("Original Asking Price ($currency)", fontFamily = PlusJakartaFontFamily, fontSize = 12.sp) },
                placeholder = { Text("1850000000", fontFamily = PlusJakartaFontFamily) },
                modifier = Modifier.fillMaxWidth().testTag("create_price_input"),
                shape = RoundedCornerShape(2.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = HavenNavyDark,
                    unfocusedBorderColor = HavenGreyBorder,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 5. FLEXIBLE INTERNATIONAL ADDRESS SYSTEM
            Text("LOCATION & ADDRESS", fontFamily = PlusJakartaFontFamily, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = HavenGoldDark, letterSpacing = 1.5.sp)
            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = city,
                    onValueChange = { city = it },
                    label = { Text("City / Town", fontFamily = PlusJakartaFontFamily, fontSize = 12.sp) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(2.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = HavenNavyDark, unfocusedBorderColor = HavenGreyBorder, focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)
                )
                OutlinedTextField(
                    value = state,
                    onValueChange = { state = it },
                    label = { Text("State / Province / Region", fontFamily = PlusJakartaFontFamily, fontSize = 12.sp) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(2.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = HavenNavyDark, unfocusedBorderColor = HavenGreyBorder, focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = neighbourhood,
                    onValueChange = { neighbourhood = it },
                    label = { Text("Neighbourhood / District", fontFamily = PlusJakartaFontFamily, fontSize = 12.sp) },
                    modifier = Modifier.weight(1.3f),
                    shape = RoundedCornerShape(2.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = HavenNavyDark, unfocusedBorderColor = HavenGreyBorder, focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)
                )
                OutlinedTextField(
                    value = postalCode,
                    onValueChange = { postalCode = it },
                    label = { Text("Postal / ZIP Code", fontFamily = PlusJakartaFontFamily, fontSize = 12.sp) },
                    modifier = Modifier.weight(0.9f),
                    shape = RoundedCornerShape(2.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = HavenNavyDark, unfocusedBorderColor = HavenGreyBorder, focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Street Address / Development", fontFamily = PlusJakartaFontFamily, fontSize = 12.sp) },
                placeholder = { Text("14 Ocean Crest Way, Banana Island", fontFamily = PlusJakartaFontFamily) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(2.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = HavenNavyDark, unfocusedBorderColor = HavenGreyBorder, focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 6. SPECIFICATIONS & LEGAL TITLE
            Text("SPECIFICATIONS & TITLE DEED", fontFamily = PlusJakartaFontFamily, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = HavenGoldDark, letterSpacing = 1.5.sp)
            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CounterInput(label = "Beds", count = bedrooms, onCountChange = { bedrooms = it }, modifier = Modifier.weight(1f))
                CounterInput(label = "Baths", count = bathrooms, onCountChange = { bathrooms = it }, modifier = Modifier.weight(1f))
                CounterInput(label = "Parking", count = parking, onCountChange = { parking = it }, modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = titleDocument,
                    onValueChange = { titleDocument = it },
                    label = { Text("Legal Title Document", fontFamily = PlusJakartaFontFamily, fontSize = 12.sp) },
                    placeholder = { Text("Certificate of Occupancy, Freehold Deed", fontFamily = PlusJakartaFontFamily) },
                    modifier = Modifier.weight(1.3f),
                    shape = RoundedCornerShape(2.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = HavenNavyDark, unfocusedBorderColor = HavenGreyBorder, focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)
                )
                OutlinedTextField(
                    value = propertyType,
                    onValueChange = { propertyType = it },
                    label = { Text("Property Type", fontFamily = PlusJakartaFontFamily, fontSize = 12.sp) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(2.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = HavenNavyDark, unfocusedBorderColor = HavenGreyBorder, focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 7. AMENITIES SELECTION
            Text("CURATED AMENITIES", fontFamily = PlusJakartaFontFamily, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = HavenGoldDark, letterSpacing = 1.5.sp)
            Spacer(modifier = Modifier.height(8.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                allAmenities.forEach { amenity ->
                    val isChecked = selectedAmenities.contains(amenity)
                    Surface(
                        shape = RoundedCornerShape(2.dp),
                        color = if (isChecked) HavenNavyDark else Color.White,
                        border = BorderStroke(1.dp, if (isChecked) HavenNavyDark else HavenGreyBorder),
                        modifier = Modifier.clickable {
                            if (isChecked) selectedAmenities.remove(amenity)
                            else selectedAmenities.add(amenity)
                        }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            if (isChecked) {
                                Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = HavenGold, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                            }
                            Text(
                                text = amenity,
                                color = if (isChecked) HavenGold else HavenNavyDark,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 8. SUBMISSION ACTIONS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        val price = priceText.toDoubleOrNull() ?: 50000000.0
                        val newProp = Property(
                            id = "prop_${UUID.randomUUID().toString().take(8)}",
                            title = title.ifBlank { "Uncaptioned Project X Residence" },
                            description = description.ifBlank { "Luxury property curated by PROJECT X — GLOBAL PROPERTY GROUP." },
                            listingType = listingType,
                            originalPrice = price,
                            currency = currency,
                            countryCode = countryCode,
                            state = state,
                            city = city,
                            lga = lga,
                            neighbourhood = neighbourhood,
                            address = address,
                            luxuryCategory = luxuryCategory,
                            propertyType = propertyType,
                            titleDocument = titleDocument,
                            bedrooms = bedrooms,
                            bathrooms = bathrooms,
                            toilets = toilets,
                            parking = parking,
                            sizeSqFt = sizeSqFt,
                            amenities = selectedAmenities.toList(),
                            images = uploadedImages.toList(),
                            videoUrl = videoTourUrl.ifBlank { null },
                            ownerId = if (currentUser.isOwner) currentUser.id else "",
                            agentId = currentUser.id,
                            agentName = currentUser.name,
                            agentPhone = currentUser.phone,
                            agentEmail = currentUser.email,
                            status = PropertyStatus.DRAFT.value,
                            verificationStatus = "unverified"
                        )
                        onSaveProperty(newProp)
                    },
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(2.dp),
                    border = BorderStroke(1.dp, HavenGreyBorder)
                ) {
                    Text("SAVE DRAFT", fontFamily = PlusJakartaFontFamily, color = HavenNavyDark, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }

                Button(
                    onClick = {
                        val price = priceText.toDoubleOrNull() ?: 150000000.0
                        val newProp = Property(
                            id = "prop_${UUID.randomUUID().toString().take(8)}",
                            title = title.ifBlank { "Curated Project X Estate" },
                            description = description.ifBlank { "Prime architectural residence offering luxury living standards." },
                            listingType = listingType,
                            originalPrice = price,
                            currency = currency,
                            countryCode = countryCode,
                            state = state,
                            city = city,
                            lga = lga,
                            neighbourhood = neighbourhood,
                            address = address,
                            luxuryCategory = luxuryCategory,
                            propertyType = propertyType,
                            titleDocument = titleDocument,
                            bedrooms = bedrooms,
                            bathrooms = bathrooms,
                            toilets = toilets,
                            parking = parking,
                            sizeSqFt = sizeSqFt,
                            amenities = selectedAmenities.toList(),
                            images = uploadedImages.toList(),
                            videoUrl = videoTourUrl.ifBlank { null },
                            ownerId = if (currentUser.isOwner) currentUser.id else "",
                            agentId = currentUser.id,
                            agentName = currentUser.name,
                            agentPhone = currentUser.phone,
                            agentEmail = currentUser.email,
                            status = PropertyStatus.PENDING_REVIEW.value,
                            verificationStatus = "unverified"
                        )
                        onSaveProperty(newProp)
                    },
                    modifier = Modifier.weight(1.3f).height(48.dp).testTag("submit_for_review_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = HavenNavyDark),
                    shape = RoundedCornerShape(2.dp)
                ) {
                    Text("SUBMIT FOR AUDIT", fontFamily = PlusJakartaFontFamily, color = HavenGold, fontWeight = FontWeight.Bold, fontSize = 11.sp, letterSpacing = 0.5.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showCountrySheet) {
        CountrySelectorSheet(
            selectedCountryCode = countryCode,
            onCountrySelected = { newCode ->
                countryCode = newCode
                currency = GlobalJurisdictionCatalog.getCurrencyForCountry(newCode)
                val c = GlobalJurisdictionCatalog.getCountry(newCode)
                if (c != null) {
                    measurementUnit = c.defaultUnit
                }
            },
            onDismiss = { showCountrySheet = false }
        )
    }
}

@Composable
private fun CounterInput(
    label: String,
    count: Int,
    onCountChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(2.dp),
        color = Color.White,
        border = BorderStroke(1.dp, HavenGreyBorder),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, fontFamily = PlusJakartaFontFamily, fontSize = 10.sp, color = HavenGreyText)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "-",
                    fontFamily = PlusJakartaFontFamily,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = HavenNavyDark,
                    modifier = Modifier
                        .clickable { if (count > 0) onCountChange(count - 1) }
                        .padding(horizontal = 6.dp)
                )
                Text(
                    text = "$count",
                    fontFamily = PlusJakartaFontFamily,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = HavenNavyDark
                )
                Text(
                    text = "+",
                    fontFamily = PlusJakartaFontFamily,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = HavenNavyDark,
                    modifier = Modifier
                        .clickable { onCountChange(count + 1) }
                        .padding(horizontal = 6.dp)
                )
            }
        }
    }
}
