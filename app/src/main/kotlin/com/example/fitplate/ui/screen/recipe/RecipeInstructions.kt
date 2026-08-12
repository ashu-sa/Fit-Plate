package com.example.fitplate.ui.screen.recipe

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fitplate.R
import com.example.fitplate.ui.theme.FitPlateTheme

@Composable
fun RecipeInstructions(
    instructions: List<String>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        Text(
            text = stringResource(R.string.instructions),
            modifier = Modifier.padding(bottom = 16.dp),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        instructions.forEachIndexed { index, instruction ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
                    .padding(bottom = 24.dp),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier.fillMaxHeight(),
                    contentAlignment = Alignment.TopCenter
                ) {
                    if (index < instructions.size - 1) {
                        Box(
                            modifier = Modifier
                                .padding(top = 32.dp)
                                .width(1.dp)
                                .fillMaxHeight()
                                .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                            .border(
                                1.dp,
                                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = (index + 1).toString(),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Text(
                    text = instruction,
                    modifier = Modifier.padding(start = 16.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 24.sp
                )
            }
        }
    }
}

@Preview
@Composable
private fun RecipeInstructionsPreview() {
    FitPlateTheme {
        RecipeInstructions(
            instructions = listOf(
                "In a blender or food processor, combine the yogurt, lime juice, pepper, and chili powder and pulse to combine. Add ½ of the avocado and blend until creamy.",
                "In a medium bowl, combine the chicken, yogurt sauce, celery, the remaining ½ avocado, onion, and salt. Mix until well combined.",
                "Serve on low-carb bread and garnish with cilantro, or as desired.",
                "Enjoy!"
            )
        )
    }
}
