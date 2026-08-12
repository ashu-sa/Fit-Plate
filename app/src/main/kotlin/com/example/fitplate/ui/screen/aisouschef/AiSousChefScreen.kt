package com.example.fitplate.ui.screen.aisouschef

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.fitplate.R
import com.example.fitplate.data.local.model.Recipe
import com.example.fitplate.ui.component.MacroPill
import com.example.fitplate.ui.component.recipe.RecipeCard
import com.example.fitplate.ui.theme.FitPlateIcons
import com.example.fitplate.ui.theme.FitPlateTheme
import com.example.fitplate.ui.theme.ThemePreviews

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiSousChefScreen(
    onRecipeClick: (Int) -> Unit,
    viewModel: AiSousChefViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    val focusManager = LocalFocusManager.current
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.size - 1)
        }
    }

    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .pointerInput(Unit) {
                detectTapGestures {
                    focusManager.clearFocus()
                }
            },
        topBar = {
            LargeTopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(if (scrollBehavior.state.collapsedFraction > 0.5f) 32.dp else 48.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                                .border(
                                    1.dp,
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = FitPlateIcons.Sparkles,
                                contentDescription = null,
                                modifier = Modifier.size(if (scrollBehavior.state.collapsedFraction > 0.5f) 16.dp else 24.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "AI Sous Chef",
                                style = if (scrollBehavior.state.collapsedFraction > 0.5f)
                                    MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                else
                                    MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                                maxLines = 1
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(if (uiState.isLoading) Color.Yellow else MaterialTheme.colorScheme.primary)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (uiState.isLoading) "Typing..." else "Online",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                )
            )
        },
        bottomBar = {
            AiSousChefInput(
                isLoading = uiState.isLoading,
                onSendMessage = viewModel::sendMessage
            )
        }
    ) { padding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp * (1f - scrollBehavior.state.collapsedFraction))
                        .graphicsLayer {
                            alpha = 1f - scrollBehavior.state.collapsedFraction
                        }
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.fitplate_expanded_width_background),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        MaterialTheme.colorScheme.background
                                    )
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Your personal culinary assistant",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            item {
                Text(
                    text = "TODAY",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 2.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    ),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }

            items(uiState.messages) { message ->
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(tween(500)) + slideInVertically(tween(500)) { it / 2 }
                ) {
                    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                        if (message.isUser) {
                            UserMessage(text = message.text)
                        } else {
                            AiMessage(
                                text = message.text,
                                showRecipe = message.showRecipe,
                                onRecipeClick = onRecipeClick
                            )
                        }
                    }
                }
            }
            
            if (uiState.isLoading) {
                item {
                    val infiniteTransition = rememberInfiniteTransition(label = "thinkingPulse")
                    val alpha by infiniteTransition.animateFloat(
                        initialValue = 0.4f,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(800),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "alpha"
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Thinking...",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.background(Color.Transparent).graphicsLayer(alpha = alpha)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AiMessage(
    text: String,
    showRecipe: Boolean = false,
    onRecipeClick: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(0.9f),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
             Icon(
                imageVector = FitPlateIcons.Sparkles,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp, topEnd = 16.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.surfaceContainerHighest,
                        RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp, topEnd = 16.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            if (showRecipe) {
                MiniRecipeCard(onRecipeClick = onRecipeClick)
            }
        }
    }
}

@Composable
fun UserMessage(text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(0.9f),
            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.End)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp, topStart = 16.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                        RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp, topStart = 16.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_cooking_placeholder),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun MiniRecipeCard(onRecipeClick: (Int) -> Unit) {
    val sampleRecipe = Recipe(
        recipeId = 101,
        name = "Vibrant Salmon Grain Bowl",
        description = "A healthy and colorful grain bowl with salmon, avocado, and fresh greens.",
        thumbnailUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuBdoSKZLrz4sSuDWnmaIr-3L_REa8HJcoXuz8bD-55eP50uOof71JL9wFTOlQYF1ChkIPV-V_sKGT8JHXjj4Kb33ed80Ib86WIdaV5Na7wT5O_nHCz_flcWlAUzDa3sNpzSi3EzSqgB9hlqqo9_7HPGZFmyAIeCOiBCvd--TJEeur38tEO45toAMSdpfKnHSJTqTfAIC-Tz9TVqjVPYGp6HDkeWvCE2XLn5HBGZ5DTFNG-v5X5hDDmO",
        videoUrl = null,
        keywords = null,
        totalTimeNeeded = "25 min",
        instructions = emptyList(),
        starRating = 4.8,
        calories = "450kcal",
        protein = "42g",
        carbs = "35g",
        fat = "12g"
    )

    Box(
        modifier = Modifier
            .width(200.dp)
            .padding(top = 8.dp)
    ) {
        RecipeCard(
            recipe = sampleRecipe,
            tag = null,
            isBookmarked = false,
            onRecipeClick = onRecipeClick,
            onBookmarkClick = { _, _ -> }
        )
    }
}

@Composable
fun AiSousChefInput(
    isLoading: Boolean,
    onSendMessage: (String) -> Unit
) {
    var text by remember { mutableStateOf("") }

    val speechRecognizerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spokenText = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.get(0)
            if (!spokenText.isNullOrBlank()) {
                text = spokenText
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f))
            .padding(bottom = 16.dp)
    ) {
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(listOf("High protein breakfast", "Under 500 calories", "Vegetarian options")) { suggestion ->
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
                        .clickable { onSendMessage(suggestion) }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = suggestion,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
        Row(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF161616))
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    BasicTextField(
                        value = text,
                        onValueChange = { text = it },
                        modifier = Modifier.weight(1f),
                        textStyle = TextStyle(
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 16.sp
                        ),
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Send
                        ),
                        keyboardActions = KeyboardActions(
                            onSend = {
                                if (text.isNotBlank() && !isLoading) {
                                    onSendMessage(text)
                                    text = ""
                                }
                            }
                        ),
                        decorationBox = { innerTextField ->
                            if (text.isEmpty()) {
                                Text(
                                    text = "Type or ask...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                )
                            }
                            innerTextField()
                        }
                    )
                    if (text.isNotBlank()) {
                         IconButton(
                            onClick = {
                                if (text.isNotBlank() && !isLoading) {
                                    onSendMessage(text)
                                    text = ""
                                }
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowUpward,
                                contentDescription = "Send",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
            IconButton(
                onClick = {
                    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                        putExtra(RecognizerIntent.EXTRA_PROMPT, "Ask your AI Sous Chef...")
                    }
                    speechRecognizerLauncher.launch(intent)
                },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Voice Input",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

@ThemePreviews
@Composable
fun AiSousChefScreenPreview() {
    FitPlateTheme {
        AiSousChefScreen(onRecipeClick = {})
    }
}
