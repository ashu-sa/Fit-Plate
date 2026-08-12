package com.example.fitplate.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.example.fitplate.ui.FitPlateAppState

/**
 * Top-level navigation graph. Navigation is organized as explained at
 * https://d.android.com/jetpack/compose/nav-adaptive
 *
 * The navigation graph defined in this file defines the different top level routes. Navigation
 * within each route is handled using state and Back Handlers.
 */
@Composable
fun FitPlateNavHost(
    appState: FitPlateAppState,
    modifier: Modifier = Modifier,
    startDestination: String = ONBOARDING_ROUTE,
) {
    val navController = appState.navController
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
        enterTransition = {
            fadeIn(animationSpec = tween(300, easing = LinearEasing)) +
                    slideIntoContainer(
                        animationSpec = tween(300, easing = EaseIn),
                        towards = AnimatedContentTransitionScope.SlideDirection.Start
                    )
        },
        exitTransition = {
            fadeOut(animationSpec = tween(300, easing = LinearEasing)) +
                    slideOutOfContainer(
                        animationSpec = tween(300, easing = EaseOut),
                        towards = AnimatedContentTransitionScope.SlideDirection.Start
                    )
        },
        popEnterTransition = {
            fadeIn(animationSpec = tween(300, easing = LinearEasing)) +
                    slideIntoContainer(
                        animationSpec = tween(300, easing = EaseIn),
                        towards = AnimatedContentTransitionScope.SlideDirection.End
                    )
        },
        popExitTransition = {
            fadeOut(animationSpec = tween(300, easing = LinearEasing)) +
                    slideOutOfContainer(
                        animationSpec = tween(300, easing = EaseOut),
                        towards = AnimatedContentTransitionScope.SlideDirection.End
                    )
        }
    ) {
        onboardingScreen { appState.navigateToTopLevelDestination(TopLevelDestination.FOR_YOU) }
        forYouScreen(
            onRecipeClick = navController::navigateToRecipe
        )
        bookmarksScreen(
            onRecipeClick = navController::navigateToRecipe
        )
        aiSousChefScreen(
            onRecipeClick = navController::navigateToRecipe
        )
        recipeScreen(
            onBackClick = navController::popBackStack,
            onTagClick = appState::navigateToForYouWithTag
        )
    }
}
