package com.example.fitplate.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.compose.runtime.getValue
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.fitplate.ui.screen.onboarding.OnboardingScreen
import com.example.fitplate.ui.screen.onboarding.OnboardingViewModel

const val ONBOARDING_ROUTE = "onboarding_route"

fun NavGraphBuilder.onboardingScreen(onContinueClick: () -> Unit) {
    composable(
        route = ONBOARDING_ROUTE
    ) {
        val viewModel: OnboardingViewModel = hiltViewModel()
        val shouldProceedToHome by viewModel.shouldProceedToHome.collectAsStateWithLifecycle()
        val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

        LaunchedEffect(shouldProceedToHome) {
            if (shouldProceedToHome == true) {
                onContinueClick()
            }
        }

        when (shouldProceedToHome) {
            false -> {
                OnboardingScreen(
                    isLoading = isLoading,
                    onContinueClick = onContinueClick
                )
            }
            else -> {
                // While determining state or after deciding to proceed, 
                // show a blank background matching the app's theme to avoid blinks.
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF131313))
                )
            }
        }
    }
}
