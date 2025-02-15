package com.example.baseandroid.data.local.search

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

enum class SearchType {
    HISTORY, SEARCH
}

data class SearchModel(val id: Int, val type: SearchType, val title: String)

interface SearchRepository {
    suspend fun search(key: String): List<SearchModel>
    suspend fun add(model: SearchModel)
}

class SearchRepositoryImpl @Inject constructor(private val dao: SearchHistoryDao) :
    SearchRepository {
    override suspend fun search(key: String): List<SearchModel> = withContext(Dispatchers.IO) {
        return@withContext dao.findByName(key)
            .map { return@map SearchModel(it.uid, SearchType.HISTORY, it.KeySearch) }
    }

    override suspend fun add(model: SearchModel) = withContext(Dispatchers.IO)
    {
        val contain = dao.findByKey(model.title)
        if (contain.isEmpty()) {
            dao.insertAll(SearchHistoryEntity(0, model.title))
        }
    }
}