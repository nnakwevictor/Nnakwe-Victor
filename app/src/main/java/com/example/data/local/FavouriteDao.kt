package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.Favourite
import com.example.data.model.Property
import kotlinx.coroutines.flow.Flow

@Dao
interface FavouriteDao {
    @Query("SELECT * FROM favourites WHERE userId = :userId ORDER BY createdAt DESC")
    fun getFavourites(userId: String): Flow<List<Favourite>>

    @Query("SELECT p.* FROM properties p INNER JOIN favourites f ON p.id = f.propertyId WHERE f.userId = :userId ORDER BY f.createdAt DESC")
    fun getFavouriteProperties(userId: String): Flow<List<Property>>

    @Query("SELECT EXISTS(SELECT 1 FROM favourites WHERE userId = :userId AND propertyId = :propertyId)")
    fun isFavourite(userId: String, propertyId: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addFavourite(favourite: Favourite)

    @Query("DELETE FROM favourites WHERE userId = :userId AND propertyId = :propertyId")
    suspend fun removeFavourite(userId: String, propertyId: String)
}
