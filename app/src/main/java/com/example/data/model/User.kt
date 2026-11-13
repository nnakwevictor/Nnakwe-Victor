package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.enums.UserRole

@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val id: String,
    val name: String,
    val email: String,
    val phone: String = "",
    val role: String = UserRole.CUSTOMER.value, // customer, agent, admin
    val agencyName: String? = null,
    val avatarUrl: String = "",
    val isVerified: Boolean = false,
    val preferredCountry: String = "NG",
    val preferredCurrency: String = "NGN"
) {
    val userRole: UserRole
        get() = UserRole.fromValue(role)

    val isAgent: Boolean
        get() = userRole == UserRole.AGENT

    val isOwner: Boolean
        get() = userRole == UserRole.OWNER || role.equals("owner", ignoreCase = true) || role.equals("landlord", ignoreCase = true)

    val isDeveloper: Boolean
        get() = userRole == UserRole.DEVELOPER || role.equals("developer", ignoreCase = true)

    val isAdmin: Boolean
        get() = userRole == UserRole.ADMIN
}
