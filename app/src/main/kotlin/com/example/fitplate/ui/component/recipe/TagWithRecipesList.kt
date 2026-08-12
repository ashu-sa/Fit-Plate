package com.example.fitplate.ui.component.recipe

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.example.fitplate.data.local.model.Recipe

@Composable
fun TagWithRecipesList(
    recipeLazyPagingItems: LazyPagingItems<Recipe>,
    bookmarkedRecipes: Set<Int>,
    onRecipeClick: (Int) -> Unit,
    onBookmarkClick: (Int, Boolean) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(16.dp)
) {
    val state = rememberLazyGridState()
    val refreshState = recipeLazyPagingItems.loadState.refresh

    if (recipeLazyPagingItems.itemCount == 0 && (refreshState is LoadState.Loading || (refreshState is LoadState.NotLoading && !refreshState.endOfPaginationReached))) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = contentPadding,
            userScrollEnabled = false
        ) {
            items(6) {
                RecipeCardPlaceholder()
            }
        }
        return
    }

    if (recipeLazyPagingItems.itemCount == 0) return
    
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        state = state,
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = contentPadding
    ) {
        items(
            count = recipeLazyPagingItems.itemCount,
            key = { index -> recipeLazyPagingItems[index]?.recipeId ?: index },
            contentType = { "Recipe" },
        ) { index: Int ->
            val recipe = recipeLazyPagingItems[index]
            if (recipe != null) {
                RecipeCard(
                    recipe = recipe,
                    tag = null,
                    isBookmarked = bookmarkedRecipes.contains(recipe.recipeId),
                    onRecipeClick = onRecipeClick,
                    onBookmarkClick = onBookmarkClick
                )
            }
        }
    }
}
