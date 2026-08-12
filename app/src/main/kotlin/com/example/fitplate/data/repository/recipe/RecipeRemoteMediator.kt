package com.example.fitplate.data.repository.recipe

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.fitplate.data.local.FitPlateDatabase
import com.example.fitplate.data.local.model.Recipe
import com.example.fitplate.data.local.model.RecipeTagCrossRef
import com.example.fitplate.data.local.model.Tag
import com.example.fitplate.data.network.NetworkDataSource
import com.example.fitplate.ui.component.FilterState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class RecipeRemoteMediator @Inject constructor(
    private val fitPlateDatabase: FitPlateDatabase,
    private val networkDataSource: NetworkDataSource
) : RemoteMediator<Int, Recipe>() {

    private var activeTags: List<Tag> = emptyList()
    private var filterState: FilterState? = null

    fun setActiveTags(tags: List<Tag>) {
        activeTags = tags
    }

    fun setFilterState(state: FilterState?) {
        filterState = state
    }

    override suspend fun initialize(): InitializeAction {
        return withContext(Dispatchers.IO) {
            if (fitPlateDatabase.recipeDao().getRecipesCount() > 0) {
                InitializeAction.SKIP_INITIAL_REFRESH
            } else {
                InitializeAction.LAUNCH_INITIAL_REFRESH
            }
        }
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Recipe>
    ): MediatorResult {
        return try {
            val offset = when (loadType) {
                LoadType.REFRESH -> 0
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val count = fitPlateDatabase.recipeDao().getRecipesCount()
                    if (count == 0) 0 else count
                }
            }

            val tagsToFetch = activeTags.mapNotNull { it.name }.toMutableList()
            filterState?.let {
                if (it.mealType != "All") {
                    tagsToFetch.add(it.mealType.lowercase())
                }
            }

            var diet = filterState?.let { if (it.dietaryPreference != "All") it.dietaryPreference.lowercase() else null }
            
            // Handle Veg/Non-Veg in network call
            if (filterState?.dietType == "Veg") {
                diet = "vegetarian"
            }

            val intolerances = filterState?.let { if (it.intolerance != "None") it.intolerance.lowercase() else null }
            val maxReadyTime = filterState?.cookingTime?.split(" ")?.firstOrNull()?.toIntOrNull()

            val (_, remoteRecipes) = networkDataSource.getRecipes(
                from = offset,
                tags = tagsToFetch,
                diet = diet,
                intolerances = intolerances,
                maxReadyTime = maxReadyTime
            )

            fitPlateDatabase.withTransaction {
                val localRecipes = remoteRecipes.map { it.toLocal() }
                fitPlateDatabase.recipeDao().upsertAll(localRecipes)

                val allCrossRefs = remoteRecipes.flatMap { recipe ->
                    val tagsToLink = mutableListOf<com.example.fitplate.data.network.model.Tag>()
                    
                    if (activeTags.isNotEmpty()) {
                        tagsToLink.addAll(activeTags.map { com.example.fitplate.data.network.model.Tag(it.tagId, it.displayName, it.name) })
                    }
                    
                    if (recipe.tags.isNotEmpty()) {
                        tagsToLink.addAll(recipe.tags)
                    }
                    
                    if (tagsToLink.isEmpty()) {
                        val indianTag = fitPlateDatabase.tagDao().getTagByName("Indian") ?: run {
                            val newTag = Tag(tagId = "indian".hashCode(), displayName = "Indian", name = "indian")
                            fitPlateDatabase.tagDao().upsertAll(listOf(newTag))
                            newTag
                        }
                        tagsToLink.add(com.example.fitplate.data.network.model.Tag(indianTag.tagId, indianTag.displayName, indianTag.name))
                    }
                    
                    tagsToLink.distinctBy { it.id }.map { tag -> RecipeTagCrossRef(recipe.id, tag.id) }
                }
                fitPlateDatabase.recipeDao().upsertRecipeTagsCrossRefs(allCrossRefs)
            }

            return MediatorResult.Success(endOfPaginationReached = true)
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }
}
