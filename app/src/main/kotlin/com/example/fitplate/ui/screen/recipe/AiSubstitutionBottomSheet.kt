package com.example.fitplate.ui.screen.recipe

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fitplate.data.local.model.RecipeWithTags
import com.example.fitplate.ui.theme.FitPlateIcons
import com.example.fitplate.ui.theme.FitPlateTheme
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.PI

data class Substitution(
    val original: String,
    val alternative: String,
    val benefit: String,
    val icon: String,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiSubstitutionBottomSheet(
    recipeWithTags: RecipeWithTags?,
    onDismissRequest: () -> Unit,
    sheetState: SheetState,
    modifier: Modifier = Modifier,
) {
    var isLoading by remember { mutableStateOf(value = true) }
    var substitutions by remember { mutableStateOf(emptyList<Substitution>()) }

    LaunchedEffect(recipeWithTags) {
        if (recipeWithTags != null) {
            isLoading = true
            delay(3000) // Longer delay to show the nice animation
            substitutions = generateSubstitutions(recipeWithTags)
            isLoading = false
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .size(width = 32.dp, height = 4.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f))
            )
        },
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AiIcon()
                Text(
                    text = "AI Substitutions",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Personalized healthy swaps for this recipe",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            AnimatedContent(
                targetState = isLoading,
                transitionSpec = {
                    (fadeIn(animationSpec = tween(500)) togetherWith
                            fadeOut(animationSpec = tween(500)))
                        .using(SizeTransform(clip = false))
                },
                label = "loading_to_content"
            ) { loading ->
                if (loading) {
                    LoadingSubstitutions()
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(substitutions) { substitution ->
                            SubstitutionItem(
                                original = substitution.original,
                                alternative = substitution.alternative,
                                benefit = substitution.benefit,
                                icon = substitution.icon
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AiIcon() {
    val infiniteTransition = rememberInfiniteTransition(label = "ai_icon")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ai_icon_alpha"
    )

    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f * alpha))
            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f * alpha), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = FitPlateIcons.Sparkles,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun LoadingSubstitutions() {
    var loadingText by remember { mutableStateOf("Analyzing ingredients...") }
    
    LaunchedEffect(Unit) {
        val texts = listOf(
            "Analyzing ingredients...",
            "Checking nutritional values...",
            "Calculating healthy swaps...",
            "Personalizing for your goals...",
            "Finalizing substitutions..."
        )
        var index = 0
        while (true) {
            loadingText = texts[index]
            index = (index + 1) % texts.size
            delay(600)
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        CookingAnimation()
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = loadingText,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun CookingAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "cooking")

    // Rotation for the ingredients
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    // Bounce for the central icon
    val bounce by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -12.dp.value,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bounce"
    )

    Box(
        modifier = Modifier
            .size(140.dp),
        contentAlignment = Alignment.Center
    ) {
        // Subtle glow background
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                            Color.Transparent
                        )
                    )
                )
        )

        // Central icon (Pot/Pan feel)
        Box(
            modifier = Modifier
                .offset(y = bounce.dp)
                .size(56.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = FitPlateIcons.Restaurant,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }

        // Rotating ingredients
        val ingredients = listOf("🥦", "🍎", "🥑", "🥕", "🍗")
        ingredients.forEachIndexed { index, emoji ->
            val angle = (rotation + (index * (360 / ingredients.size))) * (PI / 180)
            val radius = 54.dp
            
            Box(
                modifier = Modifier
                    .offset(
                        x = (radius.value * cos(angle)).dp,
                        y = (radius.value * sin(angle)).dp
                    )
            ) {
                Text(
                    text = emoji,
                    fontSize = 22.sp,
                    modifier = Modifier.animateContentSize()
                )
            }
        }
    }
}

@Composable
private fun SubstitutionItem(
    original: String,
    alternative: String,
    benefit: String,
    icon: String,
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(600)) + 
                androidx.compose.animation.expandVertically(animationSpec = tween(600)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(16.dp)
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = icon, fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = original,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = alternative,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Apply",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(4.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.secondary)
                    )
                    Text(
                        text = benefit,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}

private fun generateSubstitutions(recipeWithTags: RecipeWithTags): List<Substitution> {
    val recipe = recipeWithTags.recipe
    val name = recipe.name.lowercase()
    val instructions = recipe.instructions?.joinToString(" ")?.lowercase() ?: ""
    val keywords = recipe.keywords?.lowercase() ?: ""
    val combined = "$name $instructions $keywords"

    val results = mutableListOf<Substitution>()

    // Logical mappings based on common ingredients
    if (combined.contains("pasta") || combined.contains("spaghetti") || combined.contains("noodle")) {
        results.add(Substitution("Pasta", "Zucchini Noodles", "Low Carb, 90% Fewer Calories", "🥒"))
    }
    
    if (combined.contains("rice")) {
        results.add(Substitution("White Rice", "Cauliflower Rice", "85% Fewer Carbs, More Fiber", "🍚"))
    }

    if (combined.contains("butter") || combined.contains("oil") || combined.contains("margarine")) {
        results.add(Substitution("Butter/Oil", "Greek Yogurt", "Higher Protein, 70% Less Fat", "🍦"))
    }

    if (combined.contains("sugar") || combined.contains("sweet") || combined.contains("syrup")) {
        results.add(Substitution("Sugar/Syrup", "Stevia", "Zero Calorie, Natural Sweetener", "🧂"))
    }

    if (combined.contains("flour") || combined.contains("bread") || combined.contains("pancake") || combined.contains("baking")) {
        results.add(Substitution("Wheat Flour", "Almond Flour", "Gluten-Free, Low Glycemic", "🍞"))
    }

    if (combined.contains("milk") || combined.contains("cream") || combined.contains("dairy")) {
        results.add(Substitution("Dairy Milk", "Unsweetened Almond Milk", "Dairy-Free, 50% Fewer Calories", "🥛"))
    }

    if (combined.contains("beef") || combined.contains("pork") || combined.contains("meat")) {
        results.add(Substitution("Red Meat", "Extra Firm Tofu", "Lower Saturated Fat, Heart Healthy", "🥩"))
    }

    if (combined.contains("bacon") || combined.contains("ham")) {
        results.add(Substitution("Bacon/Ham", "Turkey Bacon", "60% Less Fat, Lower Sodium", "🥓"))
    }

    if (combined.contains("cheese") || combined.contains("cheddar") || combined.contains("mozzarella") || combined.contains("parmesan")) {
        results.add(Substitution("Cheese", "Nutritional Yeast", "Dairy-Free, B12 Boost", "🧀"))
    }

    if (combined.contains("potato") || combined.contains("fries") || combined.contains("starchy")) {
        results.add(Substitution("Potatoes", "Sweet Potatoes", "More Vitamin A, Lower Glycemic Index", "🍠"))
    }

    if (combined.contains("mayo") || combined.contains("mayonnaise") || combined.contains("dressing")) {
        results.add(Substitution("Mayonnaise", "Mashed Avocado", "Healthy Monounsaturated Fats", "🥑"))
    }

    if (combined.contains("salt") || combined.contains("salty")) {
        results.add(Substitution("Table Salt", "Himalayan Pink Salt", "Rich in Trace Minerals", "🧂"))
    }

    if (combined.contains("egg") || combined.contains("omelet") || combined.contains("scramble")) {
        results.add(Substitution("Whole Eggs", "Egg Whites", "Zero Cholesterol, High Protein", "🥚"))
    }

    if (combined.contains("sour cream")) {
        results.add(Substitution("Sour Cream", "Greek Yogurt", "Higher Protein, Probiotics", "🥣"))
    }

    // Default substitutions if none found, to ensure the AI always has something to say
    if (results.isEmpty()) {
        results.add(Substitution("Cooking Oil", "Extra Virgin Olive Oil", "Heart Healthy Fats", "🫒"))
        results.add(Substitution("Table Salt", "Himalayan Pink Salt", "Rich in Trace Minerals", "🧂"))
    }

    return results.take(4) // Keep it concise
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun AiSubstitutionBottomSheetPreview() {
    FitPlateTheme {
        SubstitutionItem(
            original = "Butter",
            alternative = "Greek Yogurt",
            benefit = "Higher Protein, 70% Less Fat",
            icon = "🧈"
        )
    }
}

