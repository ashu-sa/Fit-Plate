package com.example.fitplate.ui.screen.bookmark

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.example.fitplate.data.local.model.Recipe
import com.example.fitplate.ui.theme.FitPlateTheme
import com.example.fitplate.ui.theme.ThemePreviews
import java.util.Locale
import kotlinx.coroutines.flow.flowOf

@Composable
fun BookmarksScreen(
    recipesLazyPagingItems: LazyPagingItems<Recipe>,
    bookmarkedRecipes: Set<Int>,
    onRecipeClick: (Int) -> Unit,
    onBookmarkClick: (Int, Boolean) -> Unit,
    onClearAllClick: () -> Unit
) {
    Scaffold(
        topBar = {
            BookmarksTopBar()
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Filters Row
            BookmarksFiltersRow(onClearAllClick)

            // Recipe List
            if (recipesLazyPagingItems.loadState.refresh is LoadState.Loading && recipesLazyPagingItems.itemCount == 0) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else if (recipesLazyPagingItems.itemCount == 0) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "No saved recipes yet",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(recipesLazyPagingItems.itemCount) { index ->
                        val recipe = recipesLazyPagingItems[index]
                        if (recipe != null) {
                            SavedRecipeItem(
                                recipe = recipe,
                                onRecipeClick = onRecipeClick,
                                onBookmarkClick = onBookmarkClick
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BookmarksTopBar() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Saved Recipes",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.01).sp
            ),
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun BookmarksFiltersRow(
    onClearAllClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterChip(label = "All Saved", isSelected = true)
            }
            items(listOf("Breakfast", "Dinner", "Snacks")) { label ->
                FilterChip(label = label, isSelected = false)
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .clickable(onClick = onClearAllClick),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Clear All",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
private fun FilterChip(label: String, isSelected: Boolean) {
    val containerColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceContainer,
        label = "chipContainerColor"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outlineVariant,
        label = "chipBorderColor"
    )
    val textColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
        label = "chipTextColor"
    )
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = tween(durationMillis = 200),
        label = "chipScale"
    )

    Text(
        text = label,
        modifier = Modifier
            .scale(scale)
            .clip(CircleShape)
            .background(containerColor)
            .border(1.dp, borderColor, CircleShape)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        color = textColor,
        style = MaterialTheme.typography.labelMedium
    )
}

@Composable
private fun SavedRecipeItem(
    recipe: Recipe,
    onRecipeClick: (Int) -> Unit,
    onBookmarkClick: (Int, Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF1E1E1E).copy(alpha = 0.6f))
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
            .clickable { onRecipeClick(recipe.recipeId) }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(RoundedCornerShape(12.dp))
        ) {
            AsyncImage(
                model = recipe.thumbnailUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f)),
                            startY = 50f
                        )
                    ),
                contentAlignment = Alignment.BottomStart
            ) {
                Row(
                    modifier = Modifier.padding(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = String.format(Locale.getDefault(), "%.1f", recipe.starRating),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White
                    )
                }
            }
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = recipe.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Default.Bookmark,
                    contentDescription = "Unsave",
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { onBookmarkClick(recipe.recipeId, false) },
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Text(
                text = recipe.description ?: "Quick healthy meal",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Row(
                modifier = Modifier.padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MacroTag(label = "${recipe.carbs ?: "0g"} C", color = MaterialTheme.colorScheme.primary)
                MacroTag(label = "${recipe.protein ?: "0g"} P", color = MaterialTheme.colorScheme.secondary)
                MacroTag(label = "${recipe.fat ?: "0g"} F", color = MaterialTheme.colorScheme.tertiary)
            }
        }
    }
}

@Composable
private fun MacroTag(label: String, color: Color) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHighest)
            .border(1.dp, color.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}

@ThemePreviews
@Composable
private fun BookmarksScreenPreview() {
    val recipes = listOf(
        Recipe(
            1,
            "Avocado Power Toast",
            "Quick morning energy boost with complex carbs.",
            "test",
            "",
            "",
            "10 min",
            listOf(),
            4.8,
            "250kcal",
            "8g",
            "30g",
            "15g"
        ),
        Recipe(
            2,
            "Roasted Quinoa Bowl",
            "Nutrient-dense dinner option loaded with fiber.",
            "test",
            "",
            "",
            "20 min",
            listOf(),
            4.5,
            "400kcal",
            "12g",
            "60g",
            "10g"
        )
    )
    val testPagingData = PagingData.from(recipes)
    FitPlateTheme {
        BookmarksScreen(
            recipesLazyPagingItems = flowOf(testPagingData).collectAsLazyPagingItems(),
            bookmarkedRecipes = setOf(1, 2),
            onRecipeClick = {},
            onBookmarkClick = { _, _ -> },
            onClearAllClick = {}
        )
    }
}
