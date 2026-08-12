package com.example.fitplate.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.fitplate.data.local.model.Tag
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for the Tag Data table.
 */
@Dao
interface TagDao {

    /**
     * Insert or update tags in the database. If a tag already exists, replace it.
     *
     * @param tags the tags to be inserted or updated.
     */
    @Upsert
    suspend fun upsertAll(tags: List<Tag>)

    @Query("SELECT * FROM recipeTag ORDER BY tagId ASC")
    fun getTagsFlow(): Flow<List<Tag>>

    @Query("SELECT * FROM recipeTag WHERE tagId = :tagId")
    fun getTagForId(tagId: Int?): Flow<Tag?>

    @Query("SELECT * FROM recipeTag WHERE name = :name LIMIT 1")
    suspend fun getTagByName(name: String): Tag?
}