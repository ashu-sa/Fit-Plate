package com.example.fitplate.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.fitplate.data.local.converter.BookmarkedRecipesConverter
import com.example.fitplate.data.local.converter.InstructionsConverter
import com.example.fitplate.data.local.dao.RecipeDao
import com.example.fitplate.data.local.dao.TagDao
import com.example.fitplate.data.local.dao.UserDataDao
import com.example.fitplate.data.local.model.Recipe
import com.example.fitplate.data.local.model.RecipeTagCrossRef
import com.example.fitplate.data.local.model.Tag
import com.example.fitplate.data.local.model.UserData

/**
 * The Room Database that contains the Recipe table.
 *
 * Note that exportSchema should be true in production databases.
 */
@TypeConverters(InstructionsConverter::class, BookmarkedRecipesConverter::class)
@Database(
    entities = [Recipe::class, UserData::class, Tag::class, RecipeTagCrossRef::class],
    version = 12,
    exportSchema = false
)
abstract class FitPlateDatabase : RoomDatabase() {

    abstract fun recipeDao(): RecipeDao

    abstract fun tagDao(): TagDao

    abstract fun userDataDao(): UserDataDao
}
