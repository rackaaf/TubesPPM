package com.example.ewaste.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "jenis")
data class JenisEntity(
    @PrimaryKey val id: Int,
    val namaJenis: String,
    val kategoriId: Int // Tidak perlu digunakan di tampilan, hanya untuk referensi database
)
