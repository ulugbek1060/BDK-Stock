package com.android.model.repository.employees

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.android.model.database.employees.EmployeesDao
import com.android.model.database.employees.entity.EmployeeRoomEntity
import com.android.model.repository.employees.entity.EmployeeEntity

typealias EmployeesRemotePageLoader = suspend (Int) -> List<EmployeeEntity>
typealias EmployeesLocalLoader = () -> EmployeesDao

@OptIn(ExperimentalPagingApi::class)
class EmployeesRemoteMediator(
   private val localLoader: EmployeesLocalLoader,
   private val remoteLoader: EmployeesRemotePageLoader
) : RemoteMediator<Int, EmployeeRoomEntity>() {

   private var pageIndex = 1

   override suspend fun load(
      loadType: LoadType,
      state: PagingState<Int, EmployeeRoomEntity>
   ): MediatorResult {
      pageIndex =
         getPageIndex(loadType) ?: return MediatorResult.Success(endOfPaginationReached = true)

      val limit = state.config.pageSize
      val offset = pageIndex * limit

      return try {
         val employees = fetchEmployees(pageIndex)

         val employeesDao = localLoader()

         if (loadType == LoadType.REFRESH) {
            employeesDao.refresh(employees)
         } else {
            employeesDao.save(employees)
         }

         MediatorResult.Success(
            endOfPaginationReached = employees.size < limit
         )

      } catch (e: Exception) {
            MediatorResult.Error(e)
      }
   }

   private fun getPageIndex(loadType: LoadType): Int? {
      pageIndex = when (loadType) {
         LoadType.REFRESH -> 1
         LoadType.PREPEND -> return null
         LoadType.APPEND -> ++pageIndex
      }
      return pageIndex
   }

   private suspend fun fetchEmployees(
      pageIndex: Int
   ): List<EmployeeRoomEntity> {
      return remoteLoader(pageIndex).map { it.toEmployeesRoomEntity() }
   }
}