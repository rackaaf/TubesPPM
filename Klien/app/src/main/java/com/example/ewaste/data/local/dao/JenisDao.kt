package com.example.ewaste.data.local.dao

import androidx.room.*
import com.example.ewaste.data.local.entity.JenisEntity

@Dao
interface JenisDao {
    @Query("SELECT * FROM jenis WHERE kategoriId = :kategoriId")
    suspend fun getByKategori(kategoriId: Int): List<JenisEntity>

    @Query("SELECT * FROM jenis")
    suspend fun getAll(): List<JenisEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(data: List<JenisEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(jenis: JenisEntity)

    @Delete
    suspend fun delete(jenis: JenisEntity)

    @Query("DELETE FROM jenis")
    suspend fun deleteAll()
}