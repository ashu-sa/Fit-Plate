package com.example.fitplate.ui.screen.onboarding

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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fitplate.R
import com.example.fitplate.ui.theme.FitPlateTheme
import com.example.fitplate.ui.theme.ThemePreviews

@Composable
fun OnboardingScreen(
    isLoading: Boolean,
    onContinueClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF131313)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Blurred Background Layer
            Image(
                painter = painterResource(id = R.drawable.fitplate_onboarding),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .scale(1.1f)
                    .blur(20.dp),
                contentScale = ContentScale.Crop,
                alpha = 0.4f
            )
            
            // Dark Overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.7f)
                            )
                        )
                    )
            )

            Greeting(isLoading, onContinueClick)
        }
    }
}

@Composable
private fun Greeting(
    isLoading: Boolean,
    onContinueClick: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        visible = true
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1f))

        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(1000)) + slideInVertically(tween(1000)) { it / 2 }
        ) {
            // Logo with Pulse Animation
            Image(
                painter = painterResource(id = R.drawable.ic_cooking_placeholder),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(280.dp)
                    .scale(scale),
                contentScale = ContentScale.Fit
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(1000, 300)) + slideInVertically(tween(1000, 300)) { it / 2 }
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Brand Name
                Text(
                    text = stringResource(R.string.fit_plate_with_emoji),
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    ),
                    textAlign = TextAlign.Center
                )

                // Tagline
                Text(
                    text = stringResource(R.string.smart_recipes_perfect_macros),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Light,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(1000, 600)) + slideInVertically(tween(1000, 600)) { it / 2 }
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            } else {
                // Get Started Button
                GetStartedButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    onContinueClick = onContinueClick
                )
            }
        }
    }
}

@Composable
private fun GetStartedButton(
    modifier: Modifier = Modifier,
    onContinueClick: () -> Unit
) {
    Button(
        onClick = onContinueClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        shape = CircleShape
    ) {
        Text(
            text = stringResource(R.string.get_started).uppercase(),
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        )
    }
}

@ThemePreviews
@Composable
private fun GreetingPreview() {
    FitPlateTheme {
        OnboardingScreen(isLoading = false) {}
    }
}
