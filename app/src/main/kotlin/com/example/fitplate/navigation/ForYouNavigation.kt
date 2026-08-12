package com.example.fitplate.navigation

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.fitplate.data.local.model.Tag
import com.example.fitplate.ui.screen.foryou.ForYouScreen
import com.example.fitplate.ui.screen.foryou.ForYouViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

private const val TAG_ID_ARGUMENT = "tagId"
private const val FOR_YOU_ROUTE = "for_you_route"
private const val FOR_YOU_ROUTE_WITH_TAG_ID = "$FOR_YOU_ROUTE/$TAG_ID_ARGUMENT"

fun NavController.navigateToForYou(navOptions: NavOptions, tagId: Int?) = navigate(
    if (tagId != null) FOR_YOU_ROUTE_WITH_TAG_ID.replace(TAG_ID_ARGUMENT, tagId.toString()) else FOR_YOU_ROUTE,
    navOptions
)

fun NavGraphBuilder.forYouScreen(onRecipeClick: (Int) -> Unit) {
    val forYouComposable: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit = { backStackEntry ->
        val tagId = backStackEntry.arguments?.getString(TAG_ID_ARGUMENT)?.toIntOrNull()
        val viewModel = hiltViewModel<ForYouViewModel>().apply { if (tagId != null) setInitialActiveTag(tagId) }

        ForYouComposableScreen(
            viewModel = viewModel,
            onRecipeClick = onRecipeClick
        )
    }

    composable(route = FOR_YOU_ROUTE, content = forYouComposable)
    composable(route = "$FOR_YOU_ROUTE/{$TAG_ID_ARGUMENT}", content = forYouComposable)
}

@Composable
private fun ForYouComposableScreen(
    viewModel: ForYouViewModel,
    onRecipeClick: (Int) -> Unit
) {
    val tags by viewModel.tagsFlow.collectAsStateWithLifecycle(initialValue = emptyList())
    val selectedTagIds by viewModel.selectedTagIds.collectAsStateWithLifecycle()
    val recipeLazyPagingItems = viewModel.pagingDataFlow.collectAsLazyPagingItems()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val isFilterDialogOpen by viewModel.isFilterDialogOpen.collectAsStateWithLifecycle()
    val filterState by viewModel.filterState.collectAsStateWithLifecycle()
    val userData by viewModel.userDataFlow.collectAsStateWithLifecycle()

    ForYouScreen(
        tags = tags,
        selectedTagIds = selectedTagIds,
        searchQuery = searchQuery,
        isFilterDialogOpen = isFilterDialogOpen,
        filterState = filterState,
        recipeLazyPagingItems = recipeLazyPagingItems,
        bookmarkedRecipes = userData?.bookmarkedRecipes ?: emptySet(),
        onRecipeClick = onRecipeClick,
        onTagSelected = viewModel::updateActiveTag,
        onSearchQueryChanged = viewModel::onSearchQueryChanged,
        onFilterClick = { viewModel.toggleFilterDialog(true) },
        onDismissFilter = { viewModel.toggleFilterDialog(false) },
        onApplyFilters = viewModel::onApplyFilters,
        onBookmarkClick = viewModel::updateBookmarkedList
    )
}
