package com.example.ewaste.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "kategori")
data class KategoriEntity(
    @PrimaryKey val id: Int,
    val namaKategori: String
)
