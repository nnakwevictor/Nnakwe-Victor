package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.Enquiry
import kotlinx.coroutines.flow.Flow

@Dao
interface EnquiryDao {
    @Query("SELECT * FROM enquiries ORDER BY createdAt DESC")
    fun getAllEnquiries(): Flow<List<Enquiry>>

    @Query("SELECT * FROM enquiries WHERE customerId = :customerId ORDER BY createdAt DESC")
    fun getEnquiriesByCustomer(customerId: String): Flow<List<Enquiry>>

    @Query("SELECT * FROM enquiries WHERE agentId = :agentId ORDER BY createdAt DESC")
    fun getEnquiriesByAgent(agentId: String): Flow<List<Enquiry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEnquiry(enquiry: Enquiry)

    @Query("UPDATE enquiries SET status = :status WHERE id = :id")
    suspend fun updateEnquiryStatus(id: String, status: String)

    @Query("SELECT COUNT(*) FROM enquiries WHERE agentId = :agentId AND status = 'unread'")
    fun getUnreadCountForAgent(agentId: String): Flow<Int>
}
