package com.example.fitplate.data.network.model

import com.example.fitplate.data.local.model.Recipe
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Recipe(
    val id: Int,
    val name: String,
    val description: String?,
    @Json(name = "original_video_url")
    val videoUrl: String?,
    val keywords: String?,
    @Json(name = "thumbnail_url")
    val thumbnailUrl: String,
    @Json(name = "total_time_tier")
    val timeTier: TimeTier?,
    val instructions: List<Instruction>?,
    val tags: List<Tag>,
    val starRating: Double = 0.0,
    val calories: String? = null,
    val protein: String? = null,
    val carbs: String? = null,
    val fat: String? = null
) {
    fun toLocal(): com.example.fitplate.data.local.model.Recipe = com.example.fitplate.data.local.model.Recipe(
        id,
        name,
        description,
        thumbnailUrl,
        videoUrl,
        keywords,
        timeTier?.totalTimeNeeded,
        instructions?.map { it.text },
        starRating,
        calories,
        protein,
        carbs,
        fat
    )
}
