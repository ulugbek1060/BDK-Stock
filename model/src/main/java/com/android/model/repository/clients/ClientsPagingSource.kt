package com.android.model.repository.clients

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.android.model.repository.clients.entity.ClientEntity

typealias ClientLoader = suspend (pageIndex: Int) -> List<ClientEntity>

class ClientsPagingSource(
   private val loader: ClientLoader
) : PagingSource<Int, ClientEntity>() {

   override fun getRefreshKey(state: PagingState<Int, ClientEntity>): Int? {
      return state.anchorPosition?.let { anchorPosition ->
         state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
            ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
      }
   }

   override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ClientEntity> {
      val pageIndex = params.key ?: INITIAL_PAGE_INDEX
      return try {
         val list = loader.invoke(pageIndex)
         LoadResult.Page(
            data = list,
            prevKey = if (pageIndex == INITIAL_PAGE_INDEX) null else pageIndex - 1,
            nextKey = if (list.isEmpty()) null else pageIndex + 1
         )
      } catch (e: Exception) {
         LoadResult.Error(e)
      }
   }

   private companion object {
      const val INITIAL_PAGE_INDEX = 1
   }
}