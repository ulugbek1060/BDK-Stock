package com.android.model.repository.drivers

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.android.model.repository.drivers.entity.DriverEntity
import com.android.model.utils.AuthException
import com.android.model.utils.BackendException
import com.android.model.utils.PageNotFoundException

typealias DriverPageLoader = suspend (pageIndex: Int) -> List<DriverEntity>

class DriverPagingSource(
   private val loader: DriverPageLoader
) : PagingSource<Int, DriverEntity>() {

   override fun getRefreshKey(state: PagingState<Int, DriverEntity>): Int? {
      return state.anchorPosition?.let { anchorPosition ->
         state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
            ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
      }
   }

   override suspend fun load(params: LoadParams<Int>): LoadResult<Int, DriverEntity> {
      val pageIndex = params.key ?: INITIAL_PAGE_INDEX
      return try {
         val list = loader(pageIndex)
         LoadResult.Page(
            data = list,
            prevKey = if (pageIndex == INITIAL_PAGE_INDEX) null else pageIndex - 1,
            nextKey = if (list.isEmpty()) null else pageIndex + 1
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
      const val INITIAL_PAGE_INDEX = 1
   }
}