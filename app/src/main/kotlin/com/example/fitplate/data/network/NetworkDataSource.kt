package com.example.fitplate.data.network

import com.example.fitplate.data.network.model.Recipe
import com.example.fitplate.data.network.model.Tag

/**
 * Interface representing network calls to the Fit Plate backend
 */
interface NetworkDataSource {
    suspend fun getRecipes(
        from: Int,
        tags: List<String>,
        diet: String? = null,
        intolerances: String? = null,
        maxReadyTime: Int? = null
    ): Pair<Int, List<Recipe>>

    suspend fun getTags(): Pair<Int, List<Tag>>
}
