package com.example.fitplate.data.network

import com.example.fitplate.data.network.model.GroqRequest
import com.example.fitplate.data.network.model.GroqResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface GroqApi {
    @POST("openai/v1/chat/completions")
    suspend fun getChatCompletion(
        @Header("Authorization") authHeader: String,
        @Body request: GroqRequest
    ): Response<GroqResponse>
}
