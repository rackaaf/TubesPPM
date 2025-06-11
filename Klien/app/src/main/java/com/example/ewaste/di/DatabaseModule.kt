package com.example.ewaste.di

import android.content.Context
import androidx.room.Room
import com.example.ewaste.data.local.EWasteDatabase
import com.example.ewaste.data.local.dao.JenisDao
import com.example.ewaste.data.local.dao.KategoriDao
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
    fun provideDatabase(@ApplicationContext context: Context): EWasteDatabase {
        return Room.databaseBuilder(
            context,
            EWasteDatabase::class.java,
            "ewaste.db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideKategoriDao(db: EWasteDatabase): KategoriDao = db.kategoriDao()

    @Provides
    fun provideJenisDao(db: EWasteDatabase): JenisDao = db.jenisDao()
}
