package com.example.ewaste.di

import com.example.ewaste.BuildConfig
import com.example.ewaste.data.remote.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // Ganti BASE_URL dengan IP khusus untuk emulator
    private const val BASE_URL = "http://192.168.1.9/Rest-API/public/api/"

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)

        if (BuildConfig.DEBUG) {
            builder.addInterceptor(loggingInterceptor)

            // Custom debug interceptor
            builder.addInterceptor { chain ->
                val request = chain.request()
                android.util.Log.d("NetworkDebug", "🌐 Request URL: ${request.url}")
                try {
                    val response = chain.proceed(request)
                    android.util.Log.d("NetworkDebug", "✅ Response: ${response.code}")
                    response
                } catch (e: Exception) {
                    android.util.Log.e("NetworkDebug", "❌ Network Error: ${e.message}")
                    throw e
                }
            }
        }

        return builder.build()
    }

    @Provides
    @Singleton
    fun provideApiService(okHttpClient: OkHttpClient): ApiService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL) // Menggunakan BASE_URL baru
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
