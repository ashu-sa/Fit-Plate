package com.example.fitplate.data.repository.userData

import com.example.fitplate.data.local.model.UserData
import kotlinx.coroutines.flow.Flow

/**
 * Interface to the user data layer.
 */
interface UserDataRepository {

    suspend fun updateOnboardingShownFlag()

    fun getUserDataFlow(): Flow<UserData?>

    suspend fun getUserData(): UserData?

    suspend fun updateBookmarkedRecipes(userData: UserData)
}
