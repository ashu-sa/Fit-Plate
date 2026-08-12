package com.example.fitplate.ui.screen.recipe

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.fitplate.R
import com.example.fitplate.ui.theme.FitPlateIcons
import com.example.fitplate.ui.theme.FitPlateTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailsTopBar(
    isRecipeBookmarked: Boolean,
    onBackClick: () -> Unit,
    onBookmarkClick: (Boolean) -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(id = R.string.recipe_details),
                maxLines = 1
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    FitPlateIcons.ArrowBack,
                    stringResource(id = R.string.back),
                )
            }
        },
        actions = {
            IconButton(onClick = { onBookmarkClick(!isRecipeBookmarked) }) {
                Icon(
                    imageVector = if (isRecipeBookmarked) FitPlateIcons.Bookmark else FitPlateIcons.BookmarkBorder,
                    contentDescription = stringResource(R.string.bookmark_recipe)
                )
            }
        },
    )
}

@Preview
@Composable
private fun RecipeDetailsTopBarPreview() {
    FitPlateTheme {
        RecipeDetailsTopBar(false, {}) {}
    }
}
