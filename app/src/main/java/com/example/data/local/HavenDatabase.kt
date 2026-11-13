package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.data.model.ChatMessage
import com.example.data.model.Enquiry
import com.example.data.model.Favourite
import com.example.data.model.Inspection
import com.example.data.model.Property
import com.example.data.model.User

@Database(
    entities = [
        Property::class,
        User::class,
        Enquiry::class,
        Inspection::class,
        Favourite::class,
        ChatMessage::class
    ],
    version = 4,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class HavenDatabase : RoomDatabase() {
    abstract fun propertyDao(): PropertyDao
    abstract fun userDao(): UserDao
    abstract fun enquiryDao(): EnquiryDao
    abstract fun inspectionDao(): InspectionDao
    abstract fun favouriteDao(): FavouriteDao
    abstract fun chatMessageDao(): ChatMessageDao

    companion object {
        @Volatile
        private var INSTANCE: HavenDatabase? = null

        fun getDatabase(context: Context): HavenDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HavenDatabase::class.java,
                    "haven_real_estate.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
