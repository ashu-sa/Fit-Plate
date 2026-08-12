package com.example.fitplate.data.repository.recipe

import androidx.paging.PagingData
import com.example.fitplate.data.local.model.Recipe
import com.example.fitplate.data.local.model.RecipeWithTags
import com.example.fitplate.data.local.model.Tag
import com.example.fitplate.ui.component.FilterState
import kotlinx.coroutines.flow.Flow

/**
 * Interface to the recipe data layer.
 */
interface RecipeRepository {

    suspend fun fetchInitialRecipes()

    fun getRecipesPagedFlow(recipeIds: List<Int>): Flow<PagingData<Recipe>>

    fun searchRecipesPagedFlow(query: String): Flow<PagingData<Recipe>>

    fun getRecipesForTagsPagedFlow(
        tags: List<Tag>,
        filterState: FilterState? = null
    ): Flow<PagingData<Recipe>>

    fun getRecipeFlow(recipeId: Int): Flow<RecipeWithTags>
}
