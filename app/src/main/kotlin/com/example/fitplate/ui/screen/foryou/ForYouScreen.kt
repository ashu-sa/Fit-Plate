package com.example.fitplate.ui.screen.foryou

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.fitplate.data.local.model.Recipe
import com.example.fitplate.data.local.model.Tag
import com.example.fitplate.ui.component.FilterDialog
import com.example.fitplate.ui.component.FilterState
import com.example.fitplate.ui.component.FitPlateSearchBar
import com.example.fitplate.ui.component.FitPlateTopBar
import com.example.fitplate.ui.component.recipe.TagWithRecipesList
import com.example.fitplate.ui.theme.FitPlateTheme
import com.example.fitplate.ui.theme.ThemePreviews
import kotlinx.coroutines.flow.flowOf

@Composable
fun ForYouScreen(
    tags: List<Tag>,
    selectedTagIds: Set<Int>,
    searchQuery: String,
    isFilterDialogOpen: Boolean,
    filterState: FilterState,
    recipeLazyPagingItems: LazyPagingItems<Recipe>,
    bookmarkedRecipes: Set<Int>,
    onRecipeClick: (Int) -> Unit,
    onTagSelected: (Int) -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onFilterClick: () -> Unit,
    onDismissFilter: () -> Unit,
    onApplyFilters: (FilterState) -> Unit,
    onBookmarkClick: (Int, Boolean) -> Unit
) {
    val focusManager = LocalFocusManager.current

    if (isFilterDialogOpen) {
        FilterDialog(
            initialFilterState = filterState,
            onDismiss = onDismissFilter,
            onApply = onApplyFilters
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .pointerInput(Unit) {
                detectTapGestures {
                    focusManager.clearFocus()
                }
            }
    ) {
        // Top Bar
        FitPlateTopBar(
            onFilterClick = onFilterClick,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
        )

        // Search Bar
        FitPlateSearchBar(
            query = searchQuery,
            onQueryChanged = onSearchQueryChanged,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
        )

        // Categories / Chips (Horizontal Scrollable)
        TagsRow(
            tags = tags,
            selectedTagIds = selectedTagIds,
            onTagSelected = onTagSelected
        )

        // Recipe Grid
        TagWithRecipesList(
            recipeLazyPagingItems = recipeLazyPagingItems,
            bookmarkedRecipes = bookmarkedRecipes,
            onRecipeClick = onRecipeClick,
            onBookmarkClick = onBookmarkClick,
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp)
        )
    }
}

@Composable
private fun TagsRow(
    tags: List<Tag>,
    selectedTagIds: Set<Int>,
    onTagSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // "All" chip if not present in tags
        if (tags.none { it.name == "all" }) {
             item {
                CategoryChip(
                    label = "All",
                    isSelected = selectedTagIds.isEmpty(),
                    onClick = { onTagSelected(-1) }
                )
            }
        }

        items(tags, key = { it.tagId }) { tag ->
            CategoryChip(
                label = tag.displayName,
                isSelected = selectedTagIds.contains(tag.tagId),
                onClick = { onTagSelected(tag.tagId) }
            )
        }
    }
}

@Composable
private fun CategoryChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val containerColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else Color(0xFF1E1E1E),
        label = "containerColor"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
        label = "borderColor"
    )
    val textColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
        label = "textColor"
    )
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = tween(durationMillis = 200),
        label = "scale"
    )

    Text(
        text = label,
        modifier = Modifier
            .scale(scale)
            .clip(CircleShape)
            .background(containerColor)
            .border(1.dp, borderColor, CircleShape)
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 10.dp),
        color = textColor,
        style = MaterialTheme.typography.labelLarge.copy(fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium)
    )
}

@ThemePreviews
@Composable
private fun ForYouScreenPreview() {
    val tags = listOf(
        Tag(1, "Breakfast", "breakfast"),
        Tag(2, "High Protein", "high-protein"),
        Tag(3, "Vegan", "vegan"),
    )
    val testPagingData = PagingData.from(
        listOf(
            Recipe(
                1,
                "Quinoa Power Bowl",
                "Delicious meal",
                "test",
                "",
                "",
                "15 min",
                listOf(),
                4.7,
                "350kcal",
                "15g",
                "45g",
                "12g"
            ),
            Recipe(
                2,
                "Grilled Salmon",
                "Delicious meal",
                "test",
                "",
                "",
                "25 min",
                listOf(),
                4.9,
                "450kcal",
                "35g",
                "0g",
                "25g"
            )
        )
    )

    FitPlateTheme {
        ForYouScreen(
            tags = tags,
            selectedTagIds = setOf(tags.first().tagId),
            searchQuery = "",
            isFilterDialogOpen = false,
            filterState = FilterState(),
            recipeLazyPagingItems = flowOf(testPagingData).collectAsLazyPagingItems(),
            bookmarkedRecipes = emptySet(),
            onRecipeClick = {},
            onTagSelected = {},
            onSearchQueryChanged = {},
            onFilterClick = {},
            onDismissFilter = {},
            onApplyFilters = {},
            onBookmarkClick = { _, _ -> }
        )
    }
}
