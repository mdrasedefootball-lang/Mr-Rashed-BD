package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.QuarantinedAppDao
import com.example.data.dao.SecurityEventDao
import com.example.data.entity.QuarantinedAppEntity
import com.example.data.entity.SecurityEventEntity

@Database(
    entities = [SecurityEventEntity::class, QuarantinedAppEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun securityEventDao(): SecurityEventDao
    abstract fun quarantinedAppDao(): QuarantinedAppDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mrrashed_security_db"
                ).fallbackToDestructiveMigration()
                 .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
