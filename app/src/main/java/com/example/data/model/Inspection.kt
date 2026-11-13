package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.enums.InspectionStatus

@Entity(tableName = "inspections")
data class Inspection(
    @PrimaryKey
    val id: String,
    val propertyId: String,
    val propertyTitle: String,
    val propertyAddress: String,
    val customerId: String,
    val customerName: String,
    val customerPhone: String = "",
    val agentId: String,
    val date: String, // e.g. "2026-11-15"
    val timeSlot: String, // e.g. "10:00 AM - 11:30 AM"
    val notes: String = "",
    val status: String = InspectionStatus.PENDING.value, // pending, confirmed, completed, cancelled
    val createdAt: Long = System.currentTimeMillis()
) {
    val inspectionStatus: InspectionStatus
        get() = InspectionStatus.fromValue(status)
}
