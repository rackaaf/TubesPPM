package com.example.ewaste.data.remote

import com.example.ewaste.data.remote.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @POST("register")
    suspend fun register(@Body request: RegisterRequest): Response<BaseResponse>

    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<UserResponse>

    @POST("verify-otp")
    suspend fun verifyOtp(@Body request: OtpRequest): Response<BaseResponse>

    @POST("forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): Response<BaseResponse>

    @POST("reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<BaseResponse>

    @Multipart
    @POST("update-profile")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Part("name") name: RequestBody?,
        @Part("address") address: RequestBody?,
        @Part("birthDate") birthDate: RequestBody?,
        @Part("phoneNumber") phoneNumber: RequestBody?,
        @Part photo: MultipartBody.Part?
    ): Response<BaseResponse>

    @POST("update-password")
    suspend fun updatePassword(
        @Header("Authorization") token: String,
        @Body request: UpdatePasswordRequest
    ): Response<BaseResponse>

    @GET("profile")
    suspend fun getProfile(@Header("Authorization") token: String): Response<ProfileResponse>

    @GET("kategori")
    suspend fun getKategori(): Response<List<KategoriResponse>>

    @GET("jenis")
    suspend fun getJenis(): Response<List<JenisResponse>>

    @GET("jenis/category/{categoryId}")
    suspend fun getJenisByCategory(@Path("categoryId") categoryId: Int): Response<List<JenisResponse>>

    @POST("logout")
    suspend fun logout(@Header("Authorization") token: String): Response<BaseResponse>
}