package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.Inspection
import kotlinx.coroutines.flow.Flow

@Dao
interface InspectionDao {
    @Query("SELECT * FROM inspections ORDER BY createdAt DESC")
    fun getAllInspections(): Flow<List<Inspection>>

    @Query("SELECT * FROM inspections WHERE customerId = :customerId ORDER BY createdAt DESC")
    fun getInspectionsByCustomer(customerId: String): Flow<List<Inspection>>

    @Query("SELECT * FROM inspections WHERE agentId = :agentId ORDER BY createdAt DESC")
    fun getInspectionsByAgent(agentId: String): Flow<List<Inspection>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInspection(inspection: Inspection)

    @Query("UPDATE inspections SET status = :status WHERE id = :id")
    suspend fun updateInspectionStatus(id: String, status: String)

    @Query("UPDATE inspections SET date = :date, timeSlot = :timeSlot, status = 'confirmed' WHERE id = :id")
    suspend fun rescheduleInspection(id: String, date: String, timeSlot: String)
}
