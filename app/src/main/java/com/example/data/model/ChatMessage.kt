package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey
    val id: String,
    val senderId: String,
    val senderName: String,
    val recipientId: String,
    val recipientName: String,
    val propertyId: String? = null,
    val propertyTitle: String? = null,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = true
)
