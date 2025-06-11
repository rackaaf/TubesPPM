package com.example.ewaste.data.repository

import android.util.Log
import com.example.ewaste.data.local.dao.KategoriDao
import com.example.ewaste.data.local.dao.JenisDao
import com.example.ewaste.data.local.entity.KategoriEntity
import com.example.ewaste.data.local.entity.JenisEntity
import com.example.ewaste.data.remote.ApiService
import javax.inject.Inject

class WasteRepository @Inject constructor(
    private val api: ApiService,
    private val kategoriDao: KategoriDao,
    private val jenisDao: JenisDao
) {
    // Fetch kategori dari API dan cache ke Room
    suspend fun fetchKategori(): List<KategoriEntity> {
        return try {
            Log.d("WasteRepository", "Fetching kategori from API...")
            val response = api.getKategori()
            Log.d("WasteRepository", "API Response: ${response.code()}")

            if (response.isSuccessful && response.body() != null) {
                val responseBody = response.body()!!
                Log.d("WasteRepository", "API returned ${responseBody.size} categories")

                val apiData = responseBody.map { kategoriResponse ->
                    KategoriEntity(
                        id = kategoriResponse.id,
                        namaKategori = kategoriResponse.nama,
                        deskripsi = kategoriResponse.deskripsi
                    )
                }

                // Save to local database
                kategoriDao.deleteAll()
                kategoriDao.insertAll(apiData)
                Log.d("WasteRepository", "Saved ${apiData.size} categories to local DB")
                return apiData
            } else {
                Log.w("WasteRepository", "API call failed, using local data. Response: ${response.errorBody()?.string()}")
                // Fallback to local data if API fails
                val localData = kategoriDao.getAll()
                Log.d("WasteRepository", "Local DB returned ${localData.size} categories")
                localData
            }
        } catch (e: Exception) {
            Log.e("WasteRepository", "Network error: ${e.message}", e)
            // Fallback to local data if network error
            val localData = kategoriDao.getAll()
            Log.d("WasteRepository", "Fallback: Local DB returned ${localData.size} categories")
            localData
        }
    }

    // Fetch jenis dari API dan cache ke Room
    suspend fun fetchJenis(kategoriId: Int): List<JenisEntity> {
        return try {
            Log.d("WasteRepository", "Fetching jenis for category $kategoriId from API...")
            val response = api.getJenisByCategory(kategoriId)
            Log.d("WasteRepository", "API Response: ${response.code()}")

            if (response.isSuccessful && response.body() != null) {
                val responseBody = response.body()!!
                Log.d("WasteRepository", "API returned ${responseBody.size} jenis for category $kategoriId")

                val apiData = responseBody.map { jenisResponse ->
                    JenisEntity(
                        id = jenisResponse.id,
                        namaJenis = jenisResponse.namaJenis,
                        kategoriId = jenisResponse.kategoriId
                    )
                }

                // Save to local database (replace existing for this category)
                jenisDao.insertAll(apiData)
                Log.d("WasteRepository", "Saved ${apiData.size} jenis to local DB")
                return apiData
            } else {
                Log.w("WasteRepository", "API call failed, using local data. Response: ${response.errorBody()?.string()}")
                // Fallback to local data if API fails
                val localData = jenisDao.getByKategori(kategoriId)
                Log.d("WasteRepository", "Local DB returned ${localData.size} jenis for category $kategoriId")
                localData
            }
        } catch (e: Exception) {
            Log.e("WasteRepository", "Network error: ${e.message}", e)
            // Fallback to local data if network error
            val localData = jenisDao.getByKategori(kategoriId)
            Log.d("WasteRepository", "Fallback: Local DB returned ${localData.size} jenis for category $kategoriId")
            localData
        }
    }

    // Get all jenis dari API
    suspend fun fetchAllJenis(): List<JenisEntity> {
        return try {
            Log.d("WasteRepository", "Fetching all jenis from API...")
            val response = api.getJenis()
            Log.d("WasteRepository", "API Response: ${response.code()}")

            if (response.isSuccessful && response.body() != null) {
                val responseBody = response.body()!!
                Log.d("WasteRepository", "API returned ${responseBody.size} total jenis")

                val apiData = responseBody.map { jenisResponse ->
                    JenisEntity(
                        id = jenisResponse.id,
                        namaJenis = jenisResponse.namaJenis,
                        kategoriId = jenisResponse.kategoriId
                    )
                }

                // Save to local database
                jenisDao.deleteAll()
                jenisDao.insertAll(apiData)
                Log.d("WasteRepository", "Saved ${apiData.size} jenis to local DB")
                return apiData
            } else {
                Log.w("WasteRepository", "API call failed, using local data. Response: ${response.errorBody()?.string()}")
                // Fallback to local data if API fails
                val localData = jenisDao.getAll()
                Log.d("WasteRepository", "Local DB returned ${localData.size} total jenis")
                localData
            }
        } catch (e: Exception) {
            Log.e("WasteRepository", "Network error: ${e.message}", e)
            // Fallback to local data if network error
            val localData = jenisDao.getAll()
            Log.d("WasteRepository", "Fallback: Local DB returned ${localData.size} total jenis")
            localData
        }
    }
}