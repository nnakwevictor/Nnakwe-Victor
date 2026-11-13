package com.example.data.enums

enum class UserRole(val value: String, val displayName: String) {
    CUSTOMER("customer", "Customer / Buyer"),
    AGENT("agent", "Licensed Agent / Broker"),
    OWNER("owner", "Property Owner / Landlord"),
    DEVELOPER("developer", "Property Developer"),
    ADMIN("admin", "Platform Administrator");

    companion object {
        fun fromValue(value: String): UserRole =
            entries.find { it.value.equals(value, ignoreCase = true) } ?: CUSTOMER
    }
}

enum class PropertyStatus(val value: String, val label: String) {
    DRAFT("draft", "Draft"),
    PENDING_REVIEW("pending_review", "Pending Review"),
    PUBLISHED("published", "Published"),
    REJECTED("rejected", "Rejected"),
    SOLD("sold", "Sold"),
    RENTED("rented", "Rented");

    companion object {
        fun fromValue(value: String): PropertyStatus =
            entries.find { it.value.equals(value, ignoreCase = true) } ?: PUBLISHED
    }
}

enum class EnquiryStatus(val value: String, val label: String) {
    UNREAD("unread", "Unread"),
    CONTACTED("contacted", "Contacted"),
    RESOLVED("resolved", "Resolved");

    companion object {
        fun fromValue(value: String): EnquiryStatus =
            entries.find { it.value.equals(value, ignoreCase = true) } ?: UNREAD
    }
}

enum class InspectionStatus(val value: String, val label: String) {
    PENDING("pending", "Pending Confirmation"),
    CONFIRMED("confirmed", "Confirmed"),
    COMPLETED("completed", "Completed"),
    CANCELLED("cancelled", "Cancelled");

    companion object {
        fun fromValue(value: String): InspectionStatus =
            entries.find { it.value.equals(value, ignoreCase = true) } ?: PENDING
    }
}

enum class ListingType(val value: String, val label: String) {
    SALE("sale", "For Sale"),
    RENT("rent", "For Rent"),
    SHORTLET("shortlet", "Short Let");

    companion object {
        fun fromValue(value: String): ListingType =
            entries.find { it.value.equals(value, ignoreCase = true) } ?: SALE
    }
}
