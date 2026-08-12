package com.example.fitplate.data.network.model

import com.example.fitplate.data.local.model.Recipe
import com.example.fitplate.data.local.model.Tag
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MealResponse<T>(
    val meals: List<T>?
)

@JsonClass(generateAdapter = true)
data class MealSummary(
    @Json(name = "idMeal") val id: String,
    @Json(name = "strMeal") val name: String,
    @Json(name = "strMealThumb") val thumbnailUrl: String
) {
    fun toNetworkRecipe(): com.example.fitplate.data.network.model.Recipe {
        return com.example.fitplate.data.network.model.Recipe(
            id = id.toIntOrNull() ?: 0,
            name = name,
            description = null,
            videoUrl = null,
            keywords = null,
            thumbnailUrl = thumbnailUrl,
            timeTier = null,
            instructions = null,
            tags = emptyList()
        )
    }
}

@JsonClass(generateAdapter = true)
data class MealDetail(
    @Json(name = "idMeal") val id: String,
    @Json(name = "strMeal") val name: String,
    @Json(name = "strInstructions") val instructions: String?,
    @Json(name = "strMealThumb") val thumbnailUrl: String,
    @Json(name = "strYoutube") val videoUrl: String?,
    @Json(name = "strTags") val tags: String?,
    @Json(name = "strCategory") val category: String?,
    @Json(name = "strArea") val area: String?
) {
    fun toNetworkRecipe(): com.example.fitplate.data.network.model.Recipe {
        val tagList = mutableListOf<com.example.fitplate.data.network.model.Tag>()
        tags?.split(",")?.filter { it.isNotBlank() }?.forEachIndexed { index, tagName ->
            tagList.add(com.example.fitplate.data.network.model.Tag(id = index + 1000, displayName = tagName.trim(), name = tagName.trim()))
        }
        
        val instructionList = instructions?.split(Regex("(\\r\\n|\\n|\\r)"))
            ?.filter { it.isNotBlank() }
            ?.map { Instruction(it.trim()) }

        return com.example.fitplate.data.network.model.Recipe(
            id = id.toIntOrNull() ?: 0,
            name = name,
            description = instructions,
            videoUrl = videoUrl,
            keywords = listOfNotNull(category, area).joinToString(", "),
            thumbnailUrl = thumbnailUrl,
            timeTier = null,
            instructions = instructionList,
            tags = tagList
        )
    }
}

@JsonClass(generateAdapter = true)
data class CategoryResponse(
    val categories: List<CategorySummary>
)

@JsonClass(generateAdapter = true)
data class CategorySummary(
    @Json(name = "idCategory") val id: String,
    @Json(name = "strCategory") val name: String
) {
    fun toNetworkTag(): com.example.fitplate.data.network.model.Tag {
        return com.example.fitplate.data.network.model.Tag(
            id = id.toIntOrNull() ?: 0,
            displayName = name,
            name = name
        )
    }
}
