package com.example.fitplate.ui.component.recipe

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.example.fitplate.data.local.model.Recipe
import com.example.fitplate.ui.theme.FitPlateTheme
import kotlinx.coroutines.flow.flowOf

@Composable
fun RecipesList(
    recipesLazyPagingItems: LazyPagingItems<Recipe>,
    bookmarkedRecipes: Set<Int>,
    onRecipeClick: (Int) -> Unit,
    onBookmarkClick: (Int, Boolean) -> Unit,
    contentPadding: PaddingValues = PaddingValues(16.dp)
) {
    val state = rememberLazyGridState()

    if (recipesLazyPagingItems.loadState.refresh is LoadState.Loading && recipesLazyPagingItems.itemCount == 0) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
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

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        state = state,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = contentPadding
    ) {
        items(
            count = recipesLazyPagingItems.itemCount,
            key = recipesLazyPagingItems.itemKey { it.recipeId },
            contentType = recipesLazyPagingItems.itemContentType { "Recipes" }
        ) { index: Int ->
            val recipe = recipesLazyPagingItems[index]

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

@Preview
@Composable
private fun RecipesListPreview() {
    val testPagingData = PagingData.from(
        listOf(
            Recipe(
                1,
                "Fit Plate Recipe",
                "Delicious meal",
                "test",
                "",
                "",
                "Under 30 minutes",
                listOf(),
                4.5,
                "450kcal",
                "20g",
                "40g",
                "15g"
            ),
            Recipe(
                2,
                "Fit Plate Recipe",
                "Delicious meal",
                "test",
                "",
                "",
                "Under 30 minutes",
                listOf(),
                4.2,
                "500kcal",
                "25g",
                "50g",
                "20g"
            )
        )
    )

    FitPlateTheme {
        RecipesList(
            recipesLazyPagingItems = flowOf(testPagingData).collectAsLazyPagingItems(),
            bookmarkedRecipes = emptySet(),
            onRecipeClick = {},
            onBookmarkClick = { _, _ -> }
        )
    }
}
