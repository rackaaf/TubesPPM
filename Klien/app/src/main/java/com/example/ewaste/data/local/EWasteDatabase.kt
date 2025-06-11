package com.example.ewaste.data.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.example.ewaste.data.local.dao.KategoriDao
import com.example.ewaste.data.local.dao.JenisDao
import com.example.ewaste.data.local.entity.KategoriEntity
import com.example.ewaste.data.local.entity.JenisEntity

@Database(
    entities = [KategoriEntity::class, JenisEntity::class],
    version = 3, // Update version dari 1 ke 3
    exportSchema = false
)
abstract class EwasteDatabase : RoomDatabase() {
    abstract fun kategoriDao(): KategoriDao
    abstract fun jenisDao(): JenisDao

    companion object {
        @Volatile
        private var INSTANCE: EwasteDatabase? = null

        fun getDatabase(context: Context): EwasteDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EwasteDatabase::class.java,
                    "ewaste_database"
                )
                    .fallbackToDestructiveMigration() // Ini akan recreate DB ketika schema berubah
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}