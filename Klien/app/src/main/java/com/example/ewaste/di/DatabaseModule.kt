package com.example.ewaste.di

import android.content.Context
import androidx.room.Room
import com.example.ewaste.data.local.EwasteDatabase // Update import ini
import com.example.ewaste.data.local.dao.KategoriDao
import com.example.ewaste.data.local.dao.JenisDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideEwasteDatabase(@ApplicationContext appContext: Context): EwasteDatabase {
        return Room.databaseBuilder(
            appContext,
            EwasteDatabase::class.java,
            "ewaste_database"
        )
            .fallbackToDestructiveMigration() // Penting: ini akan handle schema changes
            .build()
    }

    @Provides
    fun provideKategoriDao(database: EwasteDatabase): KategoriDao {
        return database.kategoriDao()
    }

    @Provides
    fun provideJenisDao(database: EwasteDatabase): JenisDao {
        return database.jenisDao()
    }
}