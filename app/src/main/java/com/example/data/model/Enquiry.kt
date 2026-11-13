package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.enums.EnquiryStatus

@Entity(tableName = "enquiries")
data class Enquiry(
    @PrimaryKey
    val id: String,
    val propertyId: String,
    val propertyTitle: String,
    val customerId: String,
    val customerName: String,
    val customerEmail: String,
    val customerPhone: String = "",
    val agentId: String,
    val message: String,
    val status: String = EnquiryStatus.UNREAD.value, // unread, contacted, resolved
    val createdAt: Long = System.currentTimeMillis()
) {
    val enquiryStatus: EnquiryStatus
        get() = EnquiryStatus.fromValue(status)
}
