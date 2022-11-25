package com.android.model.repository.base

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.android.model.utils.AuthException
import com.android.model.utils.BackendException
import com.android.model.utils.PageNotFoundException

typealias DataLoader<T> = suspend (pageIndex: Int) -> List<T>

class BasePageSource<T : Any>(
   private val loader: DataLoader<T>,
   private val defaultPageSize: Int
) : PagingSource<Int, T>() {

   private val TAG = "BasePageSource"

   override fun getRefreshKey(state: PagingState<Int, T>): Int? {
      return state.anchorPosition?.let { anchorPosition ->
         state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
            ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
      }
   }

   override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
      val pageIndex = params.key ?: STARTING_PAGE_INDEX
      return try {
         val list = loader.invoke(pageIndex)
         Log.d(TAG, "load: ${list.size}")

         LoadResult.Page(
            data = list,
            // index of the previous page if exists
            prevKey = if (pageIndex == STARTING_PAGE_INDEX) null else pageIndex - 1,
            // index of the next page if exists;
            // please note that 'params.loadSize' may be larger for the first load (by default x3 times)
            nextKey = if (list.isEmpty() || list.size < defaultPageSize) null else pageIndex + 1
         )
      } catch (e: Exception) {
         if (e is BackendException && e.code == 401) {
            LoadResult.Error(AuthException(e))
         } else if (e is BackendException && e.code == 404) {
            LoadResult.Error(PageNotFoundException(e))
         } else {
            LoadResult.Error(e)
         }
      }
   }

   private companion object {
      private const val STARTING_PAGE_INDEX = 1
   }

}