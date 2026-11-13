package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "favourites",
    indices = [Index(value = ["userId", "propertyId"], unique = true)]
)
data class Favourite(
    @PrimaryKey
    val id: String,
    val userId: String,
    val propertyId: String,
    val createdAt: Long = System.currentTimeMillis()
)
