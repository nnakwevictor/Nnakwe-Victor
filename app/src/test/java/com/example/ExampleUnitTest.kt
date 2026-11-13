package com.example

import com.example.data.enums.EnquiryStatus
import com.example.data.enums.InspectionStatus
import com.example.data.enums.PropertyStatus
import com.example.data.enums.UserRole
import com.example.data.model.Property
import com.example.data.model.User
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun `verify property price formatting for NGN and USD`() {
        val propNgn = Property(
            id = "prop_1",
            title = "Banana Island Luxury Villa",
            description = "Waterfront luxury",
            originalPrice = 1850000000.0,
            currency = "NGN",
            countryCode = "NG",
            state = "Lagos",
            city = "Lagos",
            neighbourhood = "Banana Island",
            bedrooms = 6,
            bathrooms = 7,
            status = PropertyStatus.PUBLISHED.value,
            verificationStatus = "verified"
        )
        assertTrue(propNgn.formattedPrice.contains("₦"))
        assertTrue(propNgn.formattedPrice.contains("1,850,000,000"))
        assertTrue(propNgn.isPublished)
        assertTrue(propNgn.isVerified)

        val propUsd = Property(
            id = "prop_2",
            title = "Miami Waterfront Mansion",
            description = "Biscayne Bay",
            originalPrice = 12000000.0,
            currency = "USD",
            countryCode = "US",
            state = "Florida",
            city = "Miami",
            status = PropertyStatus.PENDING_REVIEW.value,
            verificationStatus = "unverified"
        )
        assertTrue(propUsd.formattedPrice.startsWith("$"))
        assertFalse(propUsd.isPublished)
        assertFalse(propUsd.isVerified)
    }

    @Test
    fun `verify standardized enums`() {
        assertEquals(UserRole.CUSTOMER, UserRole.fromValue("customer"))
        assertEquals(UserRole.AGENT, UserRole.fromValue("agent"))
        assertEquals(UserRole.ADMIN, UserRole.fromValue("admin"))

        assertEquals(PropertyStatus.DRAFT, PropertyStatus.fromValue("draft"))
        assertEquals(PropertyStatus.PENDING_REVIEW, PropertyStatus.fromValue("pending_review"))
        assertEquals(PropertyStatus.PUBLISHED, PropertyStatus.fromValue("published"))
        assertEquals(PropertyStatus.REJECTED, PropertyStatus.fromValue("rejected"))
        assertEquals(PropertyStatus.SOLD, PropertyStatus.fromValue("sold"))
        assertEquals(PropertyStatus.RENTED, PropertyStatus.fromValue("rented"))

        assertEquals(EnquiryStatus.UNREAD, EnquiryStatus.fromValue("unread"))
        assertEquals(EnquiryStatus.CONTACTED, EnquiryStatus.fromValue("contacted"))
        assertEquals(EnquiryStatus.RESOLVED, EnquiryStatus.fromValue("resolved"))

        assertEquals(InspectionStatus.PENDING, InspectionStatus.fromValue("pending"))
        assertEquals(InspectionStatus.CONFIRMED, InspectionStatus.fromValue("confirmed"))
        assertEquals(InspectionStatus.COMPLETED, InspectionStatus.fromValue("completed"))
        assertEquals(InspectionStatus.CANCELLED, InspectionStatus.fromValue("cancelled"))
    }

    @Test
    fun `verify user role attributes`() {
        val customer = User(
            id = "c1",
            name = "Chidi Okonkwo",
            email = "chidi@test.com",
            role = "customer"
        )
        assertFalse(customer.isAgent)
        assertFalse(customer.isAdmin)

        val agent = User(
            id = "a1",
            name = "Amina Bello",
            email = "amina@test.com",
            role = "agent",
            isVerified = true
        )
        assertTrue(agent.isAgent)
        assertFalse(agent.isAdmin)

        val admin = User(
            id = "adm1",
            name = "Victor Nnakwe",
            email = "admin@haven.ng",
            role = "admin",
            isVerified = true
        )
        assertTrue(admin.isAdmin)
        assertFalse(admin.isAgent)
    }
}
