package com.example.fitplate.ui.component.recipe

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.fitplate.data.local.model.Recipe
import com.example.fitplate.data.local.model.Tag
import com.example.fitplate.ui.component.MacroPill
import com.example.fitplate.ui.component.shimmerEffect
import com.example.fitplate.ui.theme.FitPlateTheme

import java.util.Locale

@Composable
fun RecipeCardPlaceholder(
    modifier: Modifier = Modifier
) {
    val cornerRadius = 24.dp
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(cornerRadius))
            .background(Color(0xFF1E1E1E))
            .border(
                width = 1.dp,
                color = Color(0xFF2A2A2A),
                shape = RoundedCornerShape(cornerRadius)
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .shimmerEffect()
        )

        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(20.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .shimmerEffect()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp, 14.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .shimmerEffect()
                )
                Box(
                    modifier = Modifier
                        .size(40.dp, 14.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .shimmerEffect()
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                repeat(3) {
                    Box(
                        modifier = Modifier
                            .size(50.dp, 24.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .shimmerEffect()
                    )
                }
            }
        }
    }
}

@Composable
fun RecipeCard(
    recipe: Recipe,
    tag: Tag?,
    isBookmarked: Boolean,
    onRecipeClick: (Int) -> Unit,
    onBookmarkClick: (Int, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val cornerRadius = 24.dp
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        label = "cardScale"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .clip(RoundedCornerShape(cornerRadius))
            .background(Color(0xFF1E1E1E))
            .border(
                width = 1.dp,
                color = Color(0xFF2A2A2A),
                shape = RoundedCornerShape(cornerRadius)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = { onRecipeClick(recipe.recipeId) }
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .background(Color(0xFF2A2A2A))
        ) {
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(recipe.thumbnailUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                loading = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .shimmerEffect()
                    )
                },
                error = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .shimmerEffect()
                    )
                }
            )
            // Gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color(0xFF1E1E1E).copy(alpha = 0.8f)
                            ),
                            startY = 100f
                        )
                    )
            )
            // Bookmark button
            val bookmarkTint by animateColorAsState(
                targetValue = if (isBookmarked) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurface,
                label = "bookmarkTint"
            )
            val bookmarkScale by animateFloatAsState(
                targetValue = if (isBookmarked) 1.2f else 1f,
                animationSpec = spring(dampingRatio = 0.5f, stiffness = 300f),
                label = "bookmarkScale"
            )

            IconButton(
                onClick = { onBookmarkClick(recipe.recipeId, !isBookmarked) },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
            ) {
                Icon(
                    imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                    contentDescription = "Bookmark",
                    modifier = Modifier
                        .size(20.dp)
                        .scale(bookmarkScale),
                    tint = bookmarkTint
                )
            }
        }

        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = recipe.name,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 22.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AccessTime,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = recipe.totalTimeNeeded ?: "20 min",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "•",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = String.format(Locale.getDefault(), "%.1f", recipe.starRating),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                recipe.calories?.let { calories ->
                    MacroPill(
                        label = "Cal",
                        value = calories.replace("kcal", ""),
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                }
                MacroPill(
                    label = "P",
                    value = recipe.protein ?: "0g",
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.secondaryContainer
                )
                MacroPill(
                    label = "C",
                    value = recipe.carbs ?: "0g",
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.tertiaryContainer
                )
            }
        }
    }
}

@Preview
@Composable
private fun RecipeCardPlaceholderPreview() {
    FitPlateTheme {
        Box(modifier = Modifier
            .padding(16.dp)
            .width(200.dp)) {
            RecipeCardPlaceholder()
        }
    }
}

@Preview
@Composable
private fun RecipeCardPreview() {
    FitPlateTheme {
        Box(modifier = Modifier
            .padding(16.dp)
            .width(200.dp)) {
            RecipeCard(
                recipe = Recipe(
                    1,
                    "Quinoa Power Bowl",
                    "Delicious meal",
                    "https://lh3.googleusercontent.com/aida-public/AB6AXuBdoSKZLrz4sSuDWnmaIr-3L_REa8HJcoXuz8bD-55eP50uOof71JL9wFTOlQYF1ChkIPV-V_sKGT8JHXjj4Kb33ed80Ib86WIdaV5Na7wT5O_nHCz_flcWlAUzDa3sNpzSi3EzSqgB9hlqqo9_7HPGZFmyAIeCOiBCvd--TJEeur38tEO45toAMSdpfKnHSJTqTfAIC-Tz9TVqjVPYGp6HDkeWvCE2XLn5HBGZ5DTFNG-v5X5hDDmO",
                    "",
                    "",
                    "15 min",
                    listOf(),
                    4.8,
                    "350kcal",
                    "15g",
                    "50g",
                    "10g"
                ),
                tag = Tag(1, "Fast", "fast"),
                isBookmarked = false,
                onRecipeClick = {},
                onBookmarkClick = { _, _ -> }
            )
        }
    }
}
