package com.example.fitplate.ui.screen.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitplate.data.repository.recipe.RecipeRepository
import com.example.fitplate.data.repository.tag.TagRepository
import com.example.fitplate.data.repository.userData.UserDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository,
    private val tagRepository: TagRepository,
    private val recipeRepository: RecipeRepository
) : ViewModel() {

    private var _shouldProceedToHome: MutableStateFlow<Boolean?> = MutableStateFlow(null)
    val shouldProceedToHome: StateFlow<Boolean?> = _shouldProceedToHome.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        updateShouldProceedToHomeBasedOnUserData()

        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Fetch tags and initial recipes
                tagRepository.fetchTags()
                recipeRepository.fetchInitialRecipes()
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun updateShouldProceedToHomeBasedOnUserData() {
        viewModelScope.launch {
            _shouldProceedToHome.update {
                userDataRepository.getUserData()?.shouldHideOnboarding == true
            }
        }
    }
}
