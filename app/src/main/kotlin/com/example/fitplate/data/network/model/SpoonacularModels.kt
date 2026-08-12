package com.example.fitplate.data.network.model

import com.example.fitplate.data.local.model.Recipe
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SpoonacularSearchResponse(
    val results: List<SpoonacularRecipe>
)

@JsonClass(generateAdapter = true)
data class SpoonacularRecipe(
    val id: Int,
    val title: String,
    val summary: String?,
    val image: String,
    val readyInMinutes: Int?,
    val aggregateLikes: Int?,
    val spoonacularScore: Double?,
    val healthScore: Int?,
    val cuisines: List<String>?,
    val dishTypes: List<String>?,
    val nutrition: SpoonacularNutrition?,
    @Json(name = "analyzedInstructions")
    val analyzedInstructions: List<SpoonacularInstruction>?,
    val vegetarian: Boolean? = null,
    val vegan: Boolean? = null,
    val glutenFree: Boolean? = null,
    val dairyFree: Boolean? = null,
    val cheap: Boolean? = null,
    val veryHealthy: Boolean? = null,
    val sustainable: Boolean? = null,
    val lowFodmap: Boolean? = null,
    val weightWatcherSmartPoints: Int? = null,
    val gaps: String? = null,
    val preparationMinutes: Int? = null,
    val cookingMinutes: Int? = null,
    val creditsText: String? = null,
    val sourceName: String? = null,
    val pricePerServing: Double? = null
) {
    fun toLocal(): Recipe {
        val calories = nutrition?.nutrients?.find { it.name == "Calories" }?.let { "${it.amount.toInt()}${it.unit}" }
        val protein = nutrition?.nutrients?.find { it.name == "Protein" }?.let { "${it.amount.toInt()}${it.unit}" }
        val carbs = nutrition?.nutrients?.find { it.name == "Carbohydrates" }?.let { "${it.amount.toInt()}${it.unit}" }
        val fat = nutrition?.nutrients?.find { it.name == "Fat" }?.let { "${it.amount.toInt()}${it.unit}" }
        
        // aggregateLikes or spoonacularScore can be mapped to 0-5 star rating
        // Spoonacular score is 0-100, so / 20.0
        val rating = (spoonacularScore ?: healthScore?.toDouble() ?: 0.0) / 20.0

        return Recipe(
            recipeId = id,
            name = title,
            description = summary,
            thumbnailUrl = image,
            videoUrl = null, 
            keywords = null,
            totalTimeNeeded = readyInMinutes?.let { "$it min" },
            instructions = analyzedInstructions?.flatMap { it.steps }?.map { it.step },
            starRating = rating,
            calories = calories,
            protein = protein,
            carbs = carbs,
            fat = fat,
            isVegetarian = vegetarian ?: false,
            isVegan = vegan ?: false,
            isGlutenFree = glutenFree ?: false,
            isDairyFree = dairyFree ?: false
        )
    }
}

@JsonClass(generateAdapter = true)
data class SpoonacularNutrition(
    val nutrients: List<SpoonacularNutrient>
)

@JsonClass(generateAdapter = true)
data class SpoonacularNutrient(
    val name: String,
    val amount: Double,
    val unit: String
)

@JsonClass(generateAdapter = true)
data class SpoonacularInstruction(
    val steps: List<SpoonacularStep>
)

@JsonClass(generateAdapter = true)
data class SpoonacularStep(
    val step: String
)
