package com.example.fitplate.data.local.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation

/**
 * Internal model used to represent a recipe stored locally in a Room database.
 * This is used inside the data layer only.
 */
@Entity(tableName = "recipe")
data class Recipe(
    @PrimaryKey
    val recipeId: Int,
    val name: String,
    val description: String?,
    val thumbnailUrl: String,
    val videoUrl: String?,
    val keywords: String?,
    val totalTimeNeeded: String?,
    val instructions: List<String>?,
    val starRating: Double = 0.0,
    val calories: String? = null,
    val protein: String? = null,
    val carbs: String? = null,
    val fat: String? = null,
    val isVegetarian: Boolean = false,
    val isVegan: Boolean = false,
    val isGlutenFree: Boolean = false,
    val isDairyFree: Boolean = false
)

@Entity(
    primaryKeys = ["recipeId", "tagId"],
    indices = [Index(value = ["tagId"])]
)
data class RecipeTagCrossRef(
    val recipeId: Int,
    val tagId: Int
)

data class RecipeWithTags(
    @Embedded val recipe: Recipe,
    @Relation(
        parentColumn = "recipeId",
        entityColumn = "tagId",
        associateBy = Junction(RecipeTagCrossRef::class)
    )
    val tags: List<Tag>
)
