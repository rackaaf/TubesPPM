package com.example.ewaste.data.local.dao

import androidx.room.*
import com.example.ewaste.data.local.entity.KategoriEntity

@Dao
interface KategoriDao {
    @Query("SELECT * FROM kategori")
    suspend fun getAll(): List<KategoriEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(data: List<KategoriEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(kategori: KategoriEntity)

    @Delete
    suspend fun delete(kategori: KategoriEntity)

    @Query("DELETE FROM kategori")
    suspend fun deleteAll()
}