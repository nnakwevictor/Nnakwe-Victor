package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.ChatMessage
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatMessageDao {
    @Query("SELECT * FROM chat_messages WHERE (senderId = :userId1 AND recipientId = :userId2) OR (senderId = :userId2 AND recipientId = :userId1) ORDER BY timestamp ASC")
    fun getMessagesBetween(userId1: String, userId2: String): Flow<List<ChatMessage>>

    @Query("SELECT * FROM chat_messages WHERE senderId = :userId OR recipientId = :userId ORDER BY timestamp DESC")
    fun getAllMessagesForUser(userId: String): Flow<List<ChatMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessage)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<ChatMessage>)

    @Query("SELECT COUNT(*) FROM chat_messages")
    suspend fun getCount(): Int
}
