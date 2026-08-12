package com.example.fitplate.ui.screen.recipe

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fitplate.R
import com.example.fitplate.data.local.model.Recipe
import com.example.fitplate.data.local.model.RecipeWithTags
import com.example.fitplate.data.local.model.Tag
import com.example.fitplate.ui.screen.common.ErrorScreen
import com.example.fitplate.ui.screen.common.LoadingScreen
import com.example.fitplate.ui.theme.FitPlateIcons
import com.example.fitplate.ui.theme.FitPlateTheme
import de.charlex.compose.material3.HtmlText

@Composable
fun RecipeScreen(
    uiState: RecipeUiState,
    updateBookmarkedList: (Int, Boolean) -> Unit,
    onBackClick: () -> Unit,
    onTagClick: (Int) -> Unit,
) {
    when (uiState) {
        RecipeUiState.Error -> ErrorScreen()
        RecipeUiState.Loading -> LoadingScreen()
        is RecipeUiState.Success -> {
            RecipeScreenContent(
                recipeWithTags = uiState.recipeWithTags,
                isRecipeBookmarked = uiState.isRecipeBookmarked,
                onBackClick = onBackClick,
                onBookmarkClick = updateBookmarkedList,
                onTagClick = onTagClick
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecipeScreenContent(
    recipeWithTags: RecipeWithTags,
    isRecipeBookmarked: Boolean,
    onBackClick: () -> Unit,
    onBookmarkClick: (Int, Boolean) -> Unit,
    onTagClick: (Int) -> Unit,
) {
    val recipe = recipeWithTags.recipe
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                RecipeTutorial(recipe)

                Column(
                    modifier = Modifier
                        .offset(y = (-24).dp)
                        .background(
                            MaterialTheme.colorScheme.background,
                            RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                        )
                        .padding(horizontal = 20.dp)
                ) {
                    Spacer(modifier = Modifier.height(24.dp))

                    // Title & Bookmark
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = recipe.name,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${recipe.totalTimeNeeded ?: "30 Mins"} • ${recipe.calories ?: "0kcal"}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(
                            onClick = { onBookmarkClick(recipe.recipeId, !isRecipeBookmarked) },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceContainerHigh),
                            colors = IconButtonDefaults.iconButtonColors(
                                contentColor = if (isRecipeBookmarked) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        ) {
                            Icon(
                                imageVector = if (isRecipeBookmarked) FitPlateIcons.Bookmark else FitPlateIcons.BookmarkBorder,
                                contentDescription = stringResource(R.string.bookmark_recipe)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Nutrition Section
                    NutritionSection(
                        calories = recipeWithTags.recipe.calories ?: "0",
                        protein = recipeWithTags.recipe.protein ?: "0g",
                        carbs = recipeWithTags.recipe.carbs ?: "0g",
                        fat = recipeWithTags.recipe.fat ?: "0g"
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // AI Action Button
                    AiSubstitutionButton(onClick = { showBottomSheet = true })

                    Spacer(modifier = Modifier.height(24.dp))

                    // Description (if any)
                    recipe.description?.let {
                        HtmlText(
                            text = it,
                            modifier = Modifier.fillMaxWidth(),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (recipe.instructions?.isNotEmpty() == true) {
                        RecipeInstructions(
                            instructions = recipe.instructions,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                    }

                    RecipeTags(
                        tags = recipeWithTags.tags,
                        modifier = Modifier.padding(vertical = 16.dp),
                        onTagSelected = onTagClick
                    )

                    Spacer(modifier = Modifier.height(80.dp))
                }
            }

            // Floating Back Button
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .padding(16.dp)
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.8f))
                    .align(Alignment.TopStart)
            ) {
                Icon(
                    imageVector = FitPlateIcons.ArrowBack,
                    contentDescription = stringResource(R.string.back),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }

    if (showBottomSheet) {
        AiSubstitutionBottomSheet(
            recipeWithTags = recipeWithTags,
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState
        )
    }
}

@Composable
private fun NutritionSection(
    calories: String,
    protein: String,
    carbs: String,
    fat: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Energy Card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    MaterialTheme.colorScheme.surfaceContainerHigh,
                    RoundedCornerShape(12.dp)
                )
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.energy).uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = calories.replace(Regex("[^0-9]"), ""),
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "kcal",
                    modifier = Modifier.padding(start = 4.dp, bottom = 12.dp),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                )
            }
        }

        // Macros Card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    MaterialTheme.colorScheme.surfaceContainer,
                    RoundedCornerShape(12.dp)
                )
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.macros).uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                MacroItem(
                    label = stringResource(R.string.protein),
                    value = protein,
                    progress = 0.8f,
                    color = MaterialTheme.colorScheme.secondary
                )
                MacroItem(
                    label = stringResource(R.string.carbs),
                    value = carbs,
                    progress = 0.6f,
                    color = MaterialTheme.colorScheme.tertiary
                )
                MacroItem(
                    label = stringResource(R.string.fats),
                    value = fat,
                    progress = 0.4f,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

@Composable
private fun RowScope.MacroItem(label: String, value: String, progress: Float, color: Color) {
    var animatedProgress by remember { mutableStateOf(0f) }
    LaunchedEffect(progress) {
        animatedProgress = progress
    }
    val progressAnimation by animateFloatAsState(
        targetValue = animatedProgress,
        animationSpec = tween(durationMillis = 1000),
        label = "macroProgress"
    )

    Column(modifier = Modifier.weight(1f)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { progressAnimation },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(CircleShape),
            color = color,
            trackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
        )
    }
}

@Composable
private fun AiSubstitutionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        ),
        contentPadding = PaddingValues(16.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.align(Alignment.Center),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = FitPlateIcons.Sparkles,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.generate_ai_substitutions),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Preview
@Composable
private fun RecipeScreenContentPreview() {
    FitPlateTheme {
        RecipeScreenContent(
            recipeWithTags = RecipeWithTags(
                recipe = Recipe(
                    1,
                    "Maple Bacon Marshmallow Fluff Sweet Potato",
                    "This One-Pot Cheeseburger Pasta is a delightful twist on a classic favorite. With its savory flavors and easy clean-up, this dish is perfect for busy weeknights and satisfying comfort food cravings.",
                    "test",
                    "",
                    "buzzfeed, chicken, creamy, dinner, lemon, one-pan, quick and easy, tasty",
                    "Under 30 minutes",
                    listOf(
                        "In a large bowl, add the flour, sugar, salt, baking powder, and baking soda and whisk to combine.",
                        "In a medium bowl or liquid measuring cup, add the buttermilk, melted butter, and egg yolks and whisk to combine.",
                        "Add the buttermilk mixture to the dry ingredients and gently fold with a rubber spatula until just combined.",
                        "Add the egg whites and fold until just combined. Be sure not to overmix. Some lumps are okay.",
                        "Let the batter rest for 15-30 minutes at room temperature.",
                    ),
                    4.6,
                    "600kcal",
                    "20g",
                    "80g",
                    "25g"
                ),
                tags = listOf(Tag(1, "Serbian", "serbian"))
            ),
            isRecipeBookmarked = false,
            onBackClick = { },
            onBookmarkClick = { _, _ -> },
            onTagClick = {}
        )
    }
}
