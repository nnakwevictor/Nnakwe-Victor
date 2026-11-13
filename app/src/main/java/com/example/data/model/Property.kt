package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.enums.PropertyStatus
import java.text.NumberFormat
import java.util.Locale

@Entity(tableName = "properties")
data class Property(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String,
    val listingType: String = "sale", // sale, rent, shortlet
    val originalPrice: Double,
    val currency: String = "NGN", // NGN, USD, GBP, AED
    val countryCode: String = "NG", // NG, US, GB, AE
    val state: String,
    val city: String,
    val lga: String = "",
    val neighbourhood: String = "",
    val address: String = "",
    val luxuryCategory: String = "Waterfront Sanctuary", // Waterfront Sanctuary, Diplomatic Enclave, Penthouse Sky-Residences, Classical Townhouse
    val propertyType: String = "Detached Villa", // Detached Villa, Sky Penthouse, Classical Townhouse, Mansion
    val titleDocument: String = "Certificate of Occupancy", // Certificate of Occupancy, Governor's Consent, Freehold Deed
    val bedrooms: Int = 0,
    val bathrooms: Int = 0,
    val toilets: Int = 0,
    val parking: Int = 0,
    val sizeSqFt: Int = 0,
    val amenities: List<String> = emptyList(),
    val images: List<String> = emptyList(),
    val videoUrl: String? = null,
    val ownerId: String = "",
    val agentId: String = "",
    val agentName: String = "",
    val agentPhone: String = "",
    val agentEmail: String = "",
    val status: String = PropertyStatus.PUBLISHED.value, // draft, pending_review, published, rejected, sold, rented
    val verificationStatus: String = "verified", // verified, unverified
    val rejectionReason: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    val formattedPrice: String
        get() {
            val format = NumberFormat.getNumberInstance(Locale.US)
            val formatted = format.format(originalPrice.toLong())
            return when (currency.uppercase()) {
                "NGN" -> "₦$formatted"
                "USD" -> "$$formatted"
                "GBP" -> "£$formatted"
                "AED" -> "AED $formatted"
                else -> "$currency $formatted"
            }
        }

    val isPublished: Boolean
        get() = status.equals(PropertyStatus.PUBLISHED.value, ignoreCase = true)

    val isVerified: Boolean
        get() = verificationStatus.equals("verified", ignoreCase = true)
}
