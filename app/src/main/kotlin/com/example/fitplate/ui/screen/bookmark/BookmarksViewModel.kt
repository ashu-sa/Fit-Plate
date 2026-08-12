package com.example.fitplate.ui.screen.bookmark

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.fitplate.data.local.model.Recipe
import com.example.fitplate.data.local.model.UserData
import com.example.fitplate.data.repository.recipe.RecipeRepository
import com.example.fitplate.data.repository.userData.UserDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookmarksViewModel @Inject constructor(
    private val recipesRepository: RecipeRepository,
    private val userDataRepository: UserDataRepository
) : ViewModel() {

    val userDataFlow: StateFlow<UserData?> = userDataRepository.getUserDataFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val pagingDataFlow: Flow<PagingData<Recipe>> = userDataRepository.getUserDataFlow()
        .flatMapLatest { userData ->
            val bookmarkedRecipeIds = userData?.bookmarkedRecipes?.toList() ?: emptyList()
            recipesRepository.getRecipesPagedFlow(bookmarkedRecipeIds)
        }
        .cachedIn(viewModelScope)

    fun updateBookmarkedList(recipeId: Int, isBookmarked: Boolean) {
        viewModelScope.launch {
            val userData = userDataRepository.getUserData() ?: return@launch
            val mutableBookmarkedRecipesSet = userData.bookmarkedRecipes.toMutableSet()
            if (isBookmarked) {
                mutableBookmarkedRecipesSet.add(recipeId)
            } else {
                mutableBookmarkedRecipesSet.remove(recipeId)
            }
            userDataRepository.updateBookmarkedRecipes(
                userData.copy(
                    bookmarkedRecipes = mutableBookmarkedRecipesSet
                )
            )
        }
    }

    fun clearAllBookmarks() {
        viewModelScope.launch {
            val userData = userDataRepository.getUserData() ?: return@launch
            userDataRepository.updateBookmarkedRecipes(
                userData.copy(
                    bookmarkedRecipes = emptySet()
                )
            )
        }
    }
}
