package com.example.fitplate.di

import android.content.Context
import androidx.room.Room
import com.example.fitplate.data.local.FitPlateDatabase
import com.example.fitplate.data.local.dao.RecipeDao
import com.example.fitplate.data.local.dao.TagDao
import com.example.fitplate.data.local.dao.UserDataDao
import com.example.fitplate.data.network.NetworkDataSource
import com.example.fitplate.data.network.NetworkMonitor
import com.example.fitplate.data.network.FitPlateRetrofit
import com.example.fitplate.data.repository.recipe.RecipeRepository
import com.example.fitplate.data.repository.recipe.RecipeRepositoryImpl
import com.example.fitplate.data.repository.tag.TagRepository
import com.example.fitplate.data.repository.tag.TagRepositoryImpl
import com.example.fitplate.data.repository.userData.UserDataRepository
import com.example.fitplate.data.repository.userData.UserDataRepositoryImpl
import com.example.fitplate.util.ConnectivityManagerNetworkMonitor
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {

    @Singleton
    @Binds
    abstract fun bindNetworkDataSource(dataSource: FitPlateRetrofit): NetworkDataSource

    @Singleton
    @Binds
    abstract fun bindNetworkMonitor(networkMonitor: ConnectivityManagerNetworkMonitor): NetworkMonitor
}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): FitPlateDatabase = Room.databaseBuilder(
        context,
        FitPlateDatabase::class.java,
        "FitPlate.db"
    ).fallbackToDestructiveMigration().build()

    @Provides
    fun provideRecipeDao(database: FitPlateDatabase): RecipeDao = database.recipeDao()

    @Provides
    fun provideTagDao(database: FitPlateDatabase): TagDao = database.tagDao()

    @Provides
    fun provideUserDataDao(database: FitPlateDatabase): UserDataDao = database.userDataDao()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Singleton
    @Binds
    abstract fun bindRecipeRepository(repository: RecipeRepositoryImpl): RecipeRepository

    @Singleton
    @Binds
    abstract fun bindTagRepository(repository: TagRepositoryImpl): TagRepository

    @Singleton
    @Binds
    abstract fun bindUserDataRepository(repository: UserDataRepositoryImpl): UserDataRepository
}
