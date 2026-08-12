package com.example.fitplate.data.repository.tag

import com.example.fitplate.data.local.model.Tag
import kotlinx.coroutines.flow.Flow

/**
 * Interface to the tag data layer.
 */
interface TagRepository {
    suspend fun fetchTags()

    fun getTagForId(tagId: Int?): Flow<Tag?>

    fun getTagsFlow(): Flow<List<Tag>>
}