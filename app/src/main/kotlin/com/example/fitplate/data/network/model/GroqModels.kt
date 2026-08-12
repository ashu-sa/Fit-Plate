package com.example.fitplate.data.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GroqRequest(
    val model: String = "llama-3.3-70b-versatile",
    val messages: List<GroqMessage>,
    val temperature: Double = 0.7
)

@JsonClass(generateAdapter = true)
data class GroqMessage(
    val role: String,
    val content: String
)

@JsonClass(generateAdapter = true)
data class GroqResponse(
    val choices: List<GroqChoice>
)

@JsonClass(generateAdapter = true)
data class GroqChoice(
    val message: GroqMessage
)
