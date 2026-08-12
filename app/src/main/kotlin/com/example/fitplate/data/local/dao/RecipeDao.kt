package com.example.fitplate.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.example.fitplate.data.local.model.Recipe
import com.example.fitplate.data.local.model.RecipeTagCrossRef
import com.example.fitplate.data.local.model.RecipeWithTags
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for the Recipe table.
 */
@Dao
interface RecipeDao {

    /**
     * Observes a single recipe.
     *
     * @param recipeId the recipe id.
     * @return the recipe with recipeId.
     */
    @Transaction
    @Query("SELECT * FROM recipe WHERE recipeId = :recipeId")
    fun observeById(recipeId: Int): Flow<RecipeWithTags>

    /**
     * Load all recipes paged
     */
    @Query("SELECT * FROM recipe WHERE recipeId IN (:recipeIds)")
    fun loadRecipesPaged(recipeIds: List<Int>): PagingSource<Int, Recipe>

    @Query("SELECT * FROM recipe WHERE name LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%'")
    fun searchRecipesPaged(query: String): PagingSource<Int, Recipe>

    @Transaction
    @Query("""
        SELECT DISTINCT r.* FROM recipe r
        LEFT JOIN RecipeTagCrossRef crossRef ON r.recipeId = crossRef.recipeId
        LEFT JOIN recipeTag tag ON crossRef.tagId = tag.tagId
        WHERE (:tagIds IS NULL OR tag.tagId IN (:tagIds))
        AND (:diet IS NULL OR r.isVegetarian = 1 OR r.isVegan = 1 OR LOWER(tag.name) = LOWER(:diet))
        AND (:mealType IS NULL OR :mealType = 'All' OR LOWER(tag.name) = LOWER(:mealType))
        AND (:intolerance IS NULL OR :intolerance = 'None' OR (
            (:intolerance = 'Dairy' AND r.isDairyFree = 1) OR
            (:intolerance = 'Gluten' AND r.isGlutenFree = 1) OR
            (LOWER(tag.name) != LOWER(:intolerance))
        ))
        AND (:isVeg IS NULL OR r.isVegetarian = :isVeg)
    """)
    fun loadFilteredRecipes(
        tagIds: List<Int>?,
        diet: String?,
        mealType: String?,
        intolerance: String?,
        isVeg: Boolean?
    ): PagingSource<Int, Recipe>

    /**
     * Insert or update recipes in the database. If a recipe already exists, replace it.
     *
     * @param recipes the recipes to be inserted or updated.
     */
    @Upsert
    suspend fun upsertAll(recipes: List<Recipe>)

    /**
     * Insert or update recipes tags cross references in the database.
     *
     * @param recipesTags the recipes tags cross references to be inserted or updated.
     */
    @Upsert
    suspend fun upsertRecipeTagsCrossRefs(recipesTags: List<RecipeTagCrossRef>)

    @Query("SELECT COUNT(recipeId) FROM recipe")
    fun getRecipesCount(): Int
}
