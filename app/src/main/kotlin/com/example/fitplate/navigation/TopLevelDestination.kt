package com.example.fitplate.navigation

import androidx.compose.ui.graphics.vector.ImageVector
import com.example.fitplate.R
import com.example.fitplate.ui.theme.FitPlateIcons

/**
 * Type for the top level destinations in the application. Each of these destinations
 * can contain one or more screens (based on the window size). Navigation from one screen to the
 * next within a single destination will be handled directly in composables.
 */
enum class TopLevelDestination(
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val iconTextId: Int
) {
    FOR_YOU(
        selectedIcon = FitPlateIcons.Restaurant,
        unselectedIcon = FitPlateIcons.RestaurantBorder,
        iconTextId = R.string.feature_for_you_title
    ),
    BOOKMARKS(
        selectedIcon = FitPlateIcons.Bookmark,
        unselectedIcon = FitPlateIcons.BookmarkBorder,
        iconTextId = R.string.feature_bookmarks_title
    ),
    AI_SOUS_CHEF(
        selectedIcon = FitPlateIcons.Sparkles,
        unselectedIcon = FitPlateIcons.SparklesBorder,
        iconTextId = R.string.feature_ai_sous_chef_title
    )
}
