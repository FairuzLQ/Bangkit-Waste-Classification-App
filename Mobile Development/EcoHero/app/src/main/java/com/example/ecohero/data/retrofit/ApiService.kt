package com.example.ecohero.data.retrofit

import com.example.ecohero.data.response.NewsResponse
import com.example.ecohero.data.response.UploadResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*


interface ApiService {
    @GET("everything")
    fun searchNews(
        @Query("q") query: String,
        @Query("apiKey") apiKey: String
    ): Call<NewsResponse>
}

interface ApiServceUpload {
    @Multipart
    @POST("upload")
    fun uploadImage(
        @Part imagePart: MultipartBody.Part,
        @Part descriptionPart: MultipartBody.Part
    ): Call<UploadResponse>
}

