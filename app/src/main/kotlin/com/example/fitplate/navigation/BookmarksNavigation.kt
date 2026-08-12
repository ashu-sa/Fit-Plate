package com.example.fitplate.navigation

import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.fitplate.ui.screen.bookmark.BookmarksScreen
import com.example.fitplate.ui.screen.bookmark.BookmarksViewModel

private const val BOOKMARKS_ROUTE = "bookmarks"

fun NavController.navigateToBookmarks(navOptions: NavOptions) =
    navigate(BOOKMARKS_ROUTE, navOptions)

fun NavGraphBuilder.bookmarksScreen(
    onRecipeClick: (Int) -> Unit
) {
    composable(
        route = BOOKMARKS_ROUTE
    ) {
        val viewModel: BookmarksViewModel = hiltViewModel()
        val recipesLazyPagingItems = viewModel.pagingDataFlow.collectAsLazyPagingItems()
        val userData by viewModel.userDataFlow.collectAsStateWithLifecycle()

        BookmarksScreen(
            recipesLazyPagingItems = recipesLazyPagingItems,
            bookmarkedRecipes = userData?.bookmarkedRecipes ?: emptySet(),
            onRecipeClick = onRecipeClick,
            onBookmarkClick = viewModel::updateBookmarkedList,
            onClearAllClick = viewModel::clearAllBookmarks
        )
    }
}
