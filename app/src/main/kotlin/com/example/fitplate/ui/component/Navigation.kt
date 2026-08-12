package com.example.fitplate.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.fitplate.ui.theme.FitPlateIcons
import com.example.fitplate.ui.theme.FitPlateTheme
import com.example.fitplate.ui.theme.ThemePreviews

/**
 * Navigation bar item with icon and label content slots. Wraps Material 3
 * [NavigationBarItem].
 */
@Composable
fun RowScope.FitPlateNavigationBarItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    selectedIcon: @Composable () -> Unit = icon,
    enabled: Boolean = true,
    label: @Composable (() -> Unit)? = null,
    alwaysShowLabel: Boolean = true,
) {
    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = if (selected) selectedIcon else icon,
        modifier = modifier,
        enabled = enabled,
        label = {
            if (label != null) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    label()
                    if (selected) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .size(4.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                        )
                    }
                }
            }
        },
        alwaysShowLabel = alwaysShowLabel,
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = MaterialTheme.colorScheme.primary,
            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            selectedTextColor = MaterialTheme.colorScheme.primary,
            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            indicatorColor = Color.Transparent,
        ),
    )
}

/**
 * Navigation bar with content slot. Wraps Material 3 [NavigationBar].
 */
@Composable
fun FitPlateNavigationBar(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        tonalElevation = 0.dp,
        content = content,
    )
}

@ThemePreviews
@Composable
fun FitPlateNavigationPreview() {
    val items = listOf("For You", "Saved", "AI Sous Chef")
    val icons = listOf(
        FitPlateIcons.RestaurantBorder,
        FitPlateIcons.BookmarkBorder,
        FitPlateIcons.SparklesBorder
    )
    val selectedIcons = listOf(
        FitPlateIcons.Restaurant,
        FitPlateIcons.Bookmark,
        FitPlateIcons.Sparkles
    )

    FitPlateTheme {
        FitPlateNavigationBar {
            items.forEachIndexed { index, item ->
                FitPlateNavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = icons[index],
                            contentDescription = item,
                        )
                    },
                    selectedIcon = {
                        Icon(
                            imageVector = selectedIcons[index],
                            contentDescription = item,
                        )
                    },
                    label = { Text(item) },
                    selected = index == 0,
                    onClick = { },
                )
            }
        }
    }
}
