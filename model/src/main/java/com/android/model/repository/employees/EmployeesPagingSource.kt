package com.android.model.repository.employees

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.android.model.repository.employees.entity.EmployeeEntity
import okio.IOException
import retrofit2.HttpException

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
         val employees = loader.getEmployees(pageIndex)

         return LoadResult.Page(
            data = employees,
            // index of the previous page if exists
            prevKey = if (pageIndex == STARTING_PAGE_INDEX) null else pageIndex - 1,
            // index of the next page if exists;
            // please note that 'params.loadSize' may be larger for the first load (by default x3 times)
            nextKey = if (employees.isEmpty()) null else pageIndex + 1
         )
      } catch (e: IOException) {
         return LoadResult.Error(e)
      } catch (e: HttpException) {
         return LoadResult.Error(e)
      } catch (e: Exception) {
         return LoadResult.Error(e)
      }
   }

   private companion object {
      private const val STARTING_PAGE_INDEX = 1
   }

   interface EmployeesPageLoader {
      suspend fun getEmployees(pageIndex: Int): List<EmployeeEntity>
   }
}