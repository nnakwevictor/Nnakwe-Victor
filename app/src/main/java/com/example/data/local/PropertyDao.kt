package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Property
import kotlinx.coroutines.flow.Flow

@Dao
interface PropertyDao {
    @Query("SELECT * FROM properties ORDER BY createdAt DESC")
    fun getAllProperties(): Flow<List<Property>>

    @Query("SELECT * FROM properties WHERE status = 'published' ORDER BY createdAt DESC")
    fun getPublishedProperties(): Flow<List<Property>>

    @Query("SELECT * FROM properties WHERE id = :id")
    suspend fun getPropertyById(id: String): Property?

    @Query("SELECT * FROM properties WHERE id = :id")
    fun observePropertyById(id: String): Flow<Property?>

    @Query("SELECT * FROM properties WHERE agentId = :agentId ORDER BY createdAt DESC")
    fun getPropertiesByAgent(agentId: String): Flow<List<Property>>

    @Query("SELECT * FROM properties WHERE status = 'pending_review' ORDER BY createdAt DESC")
    fun getPendingReviewProperties(): Flow<List<Property>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProperty(property: Property)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProperties(properties: List<Property>)

    @Update
    suspend fun updateProperty(property: Property)

    @Query("UPDATE properties SET status = :newStatus, updatedAt = :timestamp WHERE id = :propertyId")
    suspend fun updateStatus(propertyId: String, newStatus: String, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE properties SET status = 'rejected', rejectionReason = :reason, updatedAt = :timestamp WHERE id = :propertyId")
    suspend fun rejectProperty(propertyId: String, reason: String, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE properties SET verificationStatus = :verificationStatus, updatedAt = :timestamp WHERE id = :propertyId")
    suspend fun updateVerificationStatus(propertyId: String, verificationStatus: String, timestamp: Long = System.currentTimeMillis())

    @Delete
    suspend fun deleteProperty(property: Property)

    @Query("SELECT COUNT(*) FROM properties")
    suspend fun getCount(): Int
}
