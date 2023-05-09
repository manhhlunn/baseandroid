package com.example.baseandroid.application.base

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.baseandroid.resource.utils.ResultResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import retrofit2.HttpException

class BasePagingSource<V : Any>(
    private val request: (page: Int) -> Flow<ResultResponse<List<V>>>,
    private val onDefaultError: ((HttpException) -> Unit)? = null
) :
    PagingSource<Int, V>() {

    companion object {
        const val START_PAGE = 1
        const val ITEM_PER_PAGE = 20
    }

    override fun getRefreshKey(state: PagingState<Int, V>): Int = START_PAGE

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, V> {
        val page = params.key ?: START_PAGE
        return when (val result = request(page).first()) {
            is ResultResponse.Error -> LoadResult.Error(result.exception)
            is ResultResponse.DefaultError -> {
                onDefaultError?.invoke(result.httpException)
                LoadResult.Error(result.httpException)
            }

            is ResultResponse.Success -> {
                val prevKey = if (page == START_PAGE) null else page - 1
                val nextKey = if (result.value.size < ITEM_PER_PAGE) null else page + 1
                LoadResult.Page(
                    data = result.value,
                    prevKey = prevKey,
                    nextKey = nextKey
                )
            }
        }
    }
}