package com.example.fitplate.ui.screen.foryou

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.fitplate.data.local.model.Recipe
import com.example.fitplate.data.local.model.Tag
import com.example.fitplate.data.local.model.UserData
import com.example.fitplate.data.repository.recipe.RecipeRepository
import com.example.fitplate.data.repository.tag.TagRepository
import com.example.fitplate.data.repository.userData.UserDataRepository
import com.example.fitplate.ui.component.FilterState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForYouViewModel @Inject constructor(
    private val recipesRepository: RecipeRepository,
    private val userDataRepository: UserDataRepository,
    private val tagRepository: TagRepository
) : ViewModel() {

    private val _selectedTagIds = MutableStateFlow<Set<Int>>(emptySet())
    val selectedTagIds = _selectedTagIds.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _filterState = MutableStateFlow(FilterState())
    val filterState = _filterState.asStateFlow()

    private val _isFilterDialogOpen = MutableStateFlow(false)
    val isFilterDialogOpen = _isFilterDialogOpen.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val selectedTagsFlow: StateFlow<List<Tag>> = _selectedTagIds
        .flatMapLatest { ids ->
            tagRepository.getTagsFlow().map { allTags ->
                allTags.filter { it.tagId in ids }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

    val userDataFlow: StateFlow<UserData?> = userDataRepository.getUserDataFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    init {
        viewModelScope.launch {
            userDataRepository.updateOnboardingShownFlag()
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun toggleFilterDialog(isOpen: Boolean) {
        _isFilterDialogOpen.value = isOpen
    }

    fun onApplyFilters(filterState: FilterState) {
        _filterState.value = filterState
        _isFilterDialogOpen.value = false
    }

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

    fun setInitialActiveTag(tagId: Int) {
        if (_selectedTagIds.value.isEmpty()) {
            _selectedTagIds.update {
                setOf(tagId)
            }
        }
    }

    fun updateActiveTag(tagId: Int) {
        _selectedTagIds.update { currentIds ->
            if (tagId == -1) {
                emptySet()
            } else if (currentIds.contains(tagId)) {
                currentIds - tagId
            } else {
                currentIds + tagId
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val pagingDataFlow: Flow<PagingData<Recipe>> = combine(
        selectedTagsFlow,
        _searchQuery,
        _filterState
    ) { tags, query, filter ->
        Triple(tags, query, filter)
    }.flatMapLatest { (tags, query, filter) ->
        if (query.isNotBlank()) {
            recipesRepository.searchRecipesPagedFlow(query)
        } else {
            recipesRepository.getRecipesForTagsPagedFlow(tags, filter)
        }
    }.cachedIn(viewModelScope)

    val tagsFlow = tagRepository.getTagsFlow()
}
