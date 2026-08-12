package com.example.fitplate.data.repository.tag

import androidx.room.withTransaction
import com.example.fitplate.data.local.FitPlateDatabase
import com.example.fitplate.data.local.model.Tag
import com.example.fitplate.data.network.NetworkDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TagRepositoryImpl @Inject constructor(
    private val fitPlateDatabase: FitPlateDatabase,
    private val networkDataSource: NetworkDataSource
) : TagRepository {
    override suspend fun fetchTags() {
        val (_, remoteTags) = networkDataSource.getTags()
        fitPlateDatabase.withTransaction {
            val tags = remoteTags.map { com.example.fitplate.data.local.model.Tag(it.id, it.displayName, it.name) }
            fitPlateDatabase.tagDao().upsertAll(tags)
        }
    }

    override fun getTagForId(tagId: Int?): Flow<Tag?> =
        fitPlateDatabase.tagDao().getTagForId(tagId)

    override fun getTagsFlow(): Flow<List<Tag>> = fitPlateDatabase.tagDao().getTagsFlow()
}