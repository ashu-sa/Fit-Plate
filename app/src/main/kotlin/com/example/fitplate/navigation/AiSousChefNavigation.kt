package com.example.fitplate.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.example.fitplate.ui.screen.aisouschef.AiSousChefScreen

const val AI_SOUS_CHEF_ROUTE = "ai_sous_chef_route"

fun NavController.navigateToAiSousChef(navOptions: NavOptions? = null) {
    this.navigate(AI_SOUS_CHEF_ROUTE, navOptions)
}

fun NavGraphBuilder.aiSousChefScreen(onRecipeClick: (Int) -> Unit) {
    composable(route = AI_SOUS_CHEF_ROUTE) {
        AiSousChefScreen(onRecipeClick = onRecipeClick)
    }
}
