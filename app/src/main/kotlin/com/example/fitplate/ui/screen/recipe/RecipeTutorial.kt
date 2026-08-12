package com.example.fitplate.ui.screen.recipe

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.fitplate.R
import com.example.fitplate.data.local.model.Recipe
import com.example.fitplate.ui.component.recipe.RecipeImage

@Composable
fun RecipeTutorial(
    recipe: Recipe
) {
    val context = LocalContext.current

    val infiniteTransition = rememberInfiniteTransition(label = "playPulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        contentAlignment = Alignment.Center
    ) {
        RecipeImage(
            thumbnailUrl = recipe.thumbnailUrl,
            modifier = Modifier.fillMaxSize()
        )
        
        Icon(
            imageVector = Icons.Rounded.PlayCircle,
            contentDescription = stringResource(id = R.string.play_video),
            modifier = Modifier
                .size(64.dp)
                .scale(scale)
                .clickable {
                    val intent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://www.youtube.com/results?search_query=${recipe.name} Recipe")
                    )
                    context.startActivity(intent)
                },
            tint = MaterialTheme.colorScheme.onPrimary
        )
    }
}
