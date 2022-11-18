package com.android.model.repository.employees

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.android.model.repository.employees.entity.EmployeeEntity
import com.android.model.utils.AuthException
import com.android.model.utils.BackendException
import com.android.model.utils.PageNotFoundException
import retrofit2.HttpException

typealias EmployeesPageLoader = suspend (pageIndex: Int) -> List<EmployeeEntity>

class EmployeesPagingSource(
   private val loader: EmployeesPageLoader
) : PagingSource<Int, EmployeeEntity>() {

   val TAG = this.javaClass.simpleName

   override fun getRefreshKey(state: PagingState<Int, EmployeeEntity>): Int? {
      return state.anchorPosition?.let { anchorPosition ->
         state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
            ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
      }
   }

   override suspend fun load(params: LoadParams<Int>): LoadResult<Int, EmployeeEntity> {
      val pageIndex = params.key ?: STARTING_PAGE_INDEX
      return try {
         val employees = loader(pageIndex)

         LoadResult.Page(
            data = employees,
            // index of the previous page if exists
            prevKey = if (pageIndex == STARTING_PAGE_INDEX) null else pageIndex - 1,
            // index of the next page if exists;
            // please note that 'params.loadSize' may be larger for the first load (by default x3 times)
            nextKey = if (employees.isEmpty()) null else pageIndex + 1
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