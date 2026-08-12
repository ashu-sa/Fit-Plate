package com.example.fitplate.data.repository.recipe

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.room.withTransaction
import com.example.fitplate.data.local.FitPlateDatabase
import com.example.fitplate.data.local.dao.RecipeDao
import com.example.fitplate.data.local.model.Recipe
import com.example.fitplate.data.local.model.RecipeTagCrossRef
import com.example.fitplate.data.local.model.RecipeWithTags
import com.example.fitplate.data.local.model.Tag
import com.example.fitplate.data.network.DEFAULT_PAGE_SIZE
import com.example.fitplate.data.network.NetworkDataSource
import com.example.fitplate.di.DefaultDispatcher
import com.example.fitplate.ui.component.FilterState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RecipeRepositoryImpl @Inject constructor(
    private val fitPlateDatabase: FitPlateDatabase,
    private val localDataSource: RecipeDao,
    private val remoteMediator: RecipeRemoteMediator,
    private val networkDataSource: NetworkDataSource,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher
) : RecipeRepository {

    override suspend fun fetchInitialRecipes() {
        withContext(dispatcher) {
            // Force refresh if we don't have enough recipes OR if they are missing nutrition data
            val existingRecipes = localDataSource.searchRecipesPaged("").load(
                androidx.paging.PagingSource.LoadParams.Refresh(null, 1, false)
            ) as? androidx.paging.PagingSource.LoadResult.Page
            
            val needsRefresh = (localDataSource.getRecipesCount() < 10) || 
                (existingRecipes?.data?.any { it.protein == null } == true)

            if (!needsRefresh) return@withContext

            try {
                val (_, remoteRecipes) = networkDataSource.getRecipes(0, listOf("Indian"))
                if (remoteRecipes.isEmpty()) return@withContext
                
                val limitedRecipes = remoteRecipes.take(15)

                fitPlateDatabase.withTransaction {
                    val tagToUse = fitPlateDatabase.tagDao().getTagByName("Indian") ?: run {
                        val newTag = Tag(tagId = "indian".hashCode(), displayName = "Indian", name = "indian")
                        fitPlateDatabase.tagDao().upsertAll(listOf(newTag))
                        newTag
                    }

                    val localRecipes = limitedRecipes.map { it.toLocal() }
                    localDataSource.upsertAll(localRecipes)

                    val allCrossRefs = limitedRecipes.flatMap { recipe ->
                        val recipeTags = recipe.tags.ifEmpty {
                            listOf(com.example.fitplate.data.network.model.Tag(tagToUse.tagId, tagToUse.displayName, tagToUse.name))
                        }
                        recipeTags.map { tag -> RecipeTagCrossRef(recipe.id, tag.id) }
                    }
                    localDataSource.upsertRecipeTagsCrossRefs(allCrossRefs)
                }
            } catch (e: Exception) {
                Log.e("RecipeRepository", "Initial fetch failed", e)
            }
        }
    }

    override fun getRecipesPagedFlow(recipeIds: List<Int>): Flow<PagingData<Recipe>> = Pager(
        config = PagingConfig(pageSize = DEFAULT_PAGE_SIZE)
    ) {
        localDataSource.loadRecipesPaged(recipeIds)
    }.flow

    override fun searchRecipesPagedFlow(query: String): Flow<PagingData<Recipe>> = Pager(
        config = PagingConfig(pageSize = DEFAULT_PAGE_SIZE)
    ) {
        localDataSource.searchRecipesPaged(query)
    }.flow

    @OptIn(ExperimentalPagingApi::class)
    override fun getRecipesForTagsPagedFlow(
        tags: List<Tag>,
        filterState: FilterState?
    ): Flow<PagingData<Recipe>> {
        remoteMediator.setActiveTags(tags)
        remoteMediator.setFilterState(filterState)
        
        val tagIds = tags.map { it.tagId }.ifEmpty { null }
        
        val pager = Pager(
            config = PagingConfig(pageSize = DEFAULT_PAGE_SIZE),
            remoteMediator = remoteMediator,
            pagingSourceFactory = { 
                localDataSource.loadFilteredRecipes(
                    tagIds = tagIds,
                    diet = if (filterState?.dietaryPreference != "All") filterState?.dietaryPreference else null,
                    mealType = if (filterState?.mealType != "All") filterState?.mealType else null,
                    intolerance = if (filterState?.intolerance != "None") filterState?.intolerance else null,
                    isVeg = when (filterState?.dietType) {
                        "Veg" -> true
                        "Non-Veg" -> false
                        else -> null
                    }
                )
            }
        )
        return pager.flow
    }

    override fun getRecipeFlow(recipeId: Int): Flow<RecipeWithTags> =
        localDataSource.observeById(recipeId).map { withContext(dispatcher) { it } }
}
