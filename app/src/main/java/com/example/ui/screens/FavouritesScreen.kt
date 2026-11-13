package com.example.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Property
import com.example.data.model.User
import com.example.ui.components.PropertyCard
import com.example.ui.theme.HavenGold
import com.example.ui.theme.HavenNavy

@Composable
fun FavouritesScreen(
    favouriteProperties: List<Property>,
    currentUser: User?,
    onPropertyClick: (Property) -> Unit,
    onToggleFavourite: (Property) -> Unit,
    onExploreClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Saved Portfolios",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = HavenNavy
        )
        Text(
            text = "${favouriteProperties.size} luxury properties bookmarked",
            fontSize = 13.sp,
            color = Color(0xFF64748B),
            modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
        )

        if (favouriteProperties.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.FavoriteBorder,
                        contentDescription = null,
                        tint = Color(0xFFCBD5E1),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No saved properties yet",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = HavenNavy
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Tap the heart icon on any estate across Nigeria, USA, UK, or UAE to curate your private shortlist.",
                        fontSize = 13.sp,
                        color = Color(0xFF64748B),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = onExploreClick,
                        colors = ButtonDefaults.buttonColors(containerColor = HavenNavy),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("explore_from_favs_btn")
                    ) {
                        Text("Explore Marketplace", color = HavenGold, fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 96.dp)
            ) {
                items(favouriteProperties, key = { it.id }) { prop ->
                    Box(modifier = Modifier.padding(vertical = 8.dp)) {
                        PropertyCard(
                            property = prop,
                            isFavourite = true,
                            onToggleFavourite = onToggleFavourite,
                            onClick = onPropertyClick
                        )
                    }
                }
            }
        }
    }
}
