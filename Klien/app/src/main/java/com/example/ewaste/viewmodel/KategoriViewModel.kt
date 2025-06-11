package com.example.ewaste.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ewaste.data.local.entity.KategoriEntity
import com.example.ewaste.data.local.entity.JenisEntity
import com.example.ewaste.data.repository.WasteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class KategoriViewModel @Inject constructor(
    private val repository: WasteRepository
) : ViewModel() {

    private val _kategoriList = MutableStateFlow<List<KategoriEntity>>(emptyList())
    val kategoriList: StateFlow<List<KategoriEntity>> = _kategoriList.asStateFlow()

    private val _jenisList = MutableStateFlow<List<JenisEntity>>(emptyList())
    val jenisList: StateFlow<List<JenisEntity>> = _jenisList.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun loadKategori() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                Log.d("KategoriViewModel", "Loading kategori...")
                val result = repository.fetchKategori()
                Log.d("KategoriViewModel", "Loaded ${result.size} kategori")
                _kategoriList.value = result
            } catch (e: Exception) {
                Log.e("KategoriViewModel", "Error loading kategori: ${e.message}")
                _errorMessage.value = "Error loading kategori: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadJenis(kategoriId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                Log.d("KategoriViewModel", "Loading jenis for kategori: $kategoriId")
                val result = repository.fetchJenis(kategoriId)
                Log.d("KategoriViewModel", "Loaded ${result.size} jenis")
                _jenisList.value = result
            } catch (e: Exception) {
                Log.e("KategoriViewModel", "Error loading jenis: ${e.message}")
                _errorMessage.value = "Error loading jenis: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadAllJenis() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                Log.d("KategoriViewModel", "Loading all jenis...")
                val result = repository.fetchAllJenis()
                Log.d("KategoriViewModel", "Loaded ${result.size} total jenis")
                _jenisList.value = result
            } catch (e: Exception) {
                Log.e("KategoriViewModel", "Error loading all jenis: ${e.message}")
                _errorMessage.value = "Error loading jenis: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}