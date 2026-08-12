package com.example.fitplate.data.network

import android.util.Log
import com.example.fitplate.BuildConfig
import com.example.fitplate.data.network.model.Instruction
import com.example.fitplate.data.network.model.Recipe
import com.example.fitplate.data.network.model.SpoonacularRecipe
import com.example.fitplate.data.network.model.SpoonacularSearchResponse
import com.example.fitplate.data.network.model.Tag
import kotlinx.coroutines.coroutineScope
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Inject
import javax.inject.Singleton

const val DEFAULT_PAGE_SIZE = 20

interface FitPlateNetworkApi {
    @GET(value = "recipes/complexSearch")
    suspend fun searchRecipes(
        @Query("query") query: String?,
        @Query("cuisine") cuisine: String?,
        @Query("type") type: String?,
        @Query("diet") diet: String?,
        @Query("intolerances") intolerances: String?,
        @Query("maxReadyTime") maxReadyTime: Int?,
        @Query("addRecipeInformation") addRecipeInfo: Boolean = true,
        @Query("addRecipeNutrition") addRecipeNutrition: Boolean = true,
        @Query("number") number: Int = DEFAULT_PAGE_SIZE,
        @Query("offset") offset: Int = 0,
        @Query("apiKey") apiKey: String = BuildConfig.SPOONACULAR_API_KEY
    ): Response<SpoonacularSearchResponse>
}

/**
 * [Retrofit] backed [NetworkDataSource]
 */
@Singleton
class FitPlateRetrofit @Inject constructor(
    private val fitPlateNetworkApi: FitPlateNetworkApi
) : NetworkDataSource {

    companion object {
        private const val TAG = "Fit Plate Network Layer"
    }

    override suspend fun getRecipes(
        from: Int,
        tags: List<String>,
        diet: String?,
        intolerances: String?,
        maxReadyTime: Int?
    ): Pair<Int, List<Recipe>> = coroutineScope {
        try {
            // Separate cuisines and types
            val cuisinesList = tags.filter { isCuisine(it) }
            val typesList = tags.filter { !isCuisine(it) && it != "all" }
            
            val cuisine = if (cuisinesList.isNotEmpty()) cuisinesList.joinToString(",") else null
            val type = if (typesList.isNotEmpty()) typesList.joinToString(",") else null
            
            // If no tags, search for "healthy" as fallback
            val query = if (cuisine == null && type == null) "healthy" else null
            
            Log.d(TAG, "Searching for cuisine: $cuisine, type: $type, diet: $diet, intolerances: $intolerances, time: $maxReadyTime")
            val response = fitPlateNetworkApi.searchRecipes(
                query = query,
                cuisine = cuisine,
                type = type,
                diet = diet,
                intolerances = intolerances,
                maxReadyTime = maxReadyTime,
                offset = from
            )

            val result = response.body()
            if (response.isSuccessful && result != null) {
                Log.d(TAG, "Found ${result.results.size} recipes")
                val networkRecipes = result.results.map { it.toNetworkRecipe() }
                (networkRecipes.size to networkRecipes)
            } else {
                Log.e(TAG, "Search failed: ${response.code()} ${response.errorBody()?.string()}")
                (0 to listOf())
            }
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected exception while trying to fetch recipes", e)
            throw e
        }
    }

    override suspend fun getTags(): Pair<Int, List<Tag>> {
        val staticTags = listOf(
            "Main Course", "Side Dish", "Dessert", "Appetizer", "Salad", "Bread", 
            "Breakfast", "Soup", "Beverage", "Sauce", "Marinade", "Fingerfood", 
            "Snack", "Drink", "Indian"
        ).map { name ->
            Tag(id = name.lowercase().hashCode(), displayName = name, name = name.lowercase())
        }
        return (staticTags.size to staticTags)
    }
}

private fun isCuisine(tag: String): Boolean {
    val cuisines = listOf(
        "African", "Asian", "American", "British", "Cajun", "Caribbean", "Chinese", 
        "Eastern European", "European", "French", "German", "Greek", "Indian", 
        "Irish", "Italian", "Japanese", "Jewish", "Korean", "Latin American", 
        "Mediterranean", "Mexican", "Middle Eastern", "Nordic", "Southern", 
        "Spanish", "Thai", "Vietnamese"
    )
    return cuisines.any { it.equals(tag, ignoreCase = true) }
}

fun SpoonacularRecipe.toNetworkRecipe(): Recipe {
    val calories = nutrition?.nutrients?.find { it.name == "Calories" }?.let { "${it.amount.toInt()}${it.unit}" }
    val protein = nutrition?.nutrients?.find { it.name == "Protein" }?.let { "${it.amount.toInt()}${it.unit}" }
    val carbs = nutrition?.nutrients?.find { it.name == "Carbohydrates" }?.let { "${it.amount.toInt()}${it.unit}" }
    val fat = nutrition?.nutrients?.find { it.name == "Fat" }?.let { "${it.amount.toInt()}${it.unit}" }

    // Use healthScore if spoonacularScore is missing (often healthScore is 0-100 too)
    val score = spoonacularScore ?: healthScore?.toDouble() ?: 0.0
    val rating = if (score > 0) score / 20.0 else 4.0 + (id % 10) / 10.0 // Better fallback than 0.0

    return Recipe(
        id = id,
        name = title,
        description = summary ?: "Delicious healthy meal",
        videoUrl = null,
        keywords = null,
        thumbnailUrl = image,
        timeTier = readyInMinutes?.let { com.example.fitplate.data.network.model.TimeTier("$it min") },
        instructions = analyzedInstructions?.flatMap { it.steps }?.map { Instruction(it.step) },
        tags = (cuisines.orEmpty() + dishTypes.orEmpty()).map { tag -> 
            Tag(id = tag.lowercase().hashCode(), displayName = tag.replaceFirstChar { it.uppercase() }, name = tag.lowercase())
        },
        starRating = rating,
        calories = calories,
        protein = protein,
        carbs = carbs,
        fat = fat
    )
}
