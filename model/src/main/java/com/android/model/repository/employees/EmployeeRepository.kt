package com.android.model.repository.employees

import androidx.paging.*
import com.android.model.database.employees.EmployeesDao
import com.android.model.repository.Repository
import com.android.model.repository.employees.entity.EmployeeEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class EmployeeRepository @Inject constructor(
   private val employeesSource: EmployeesSource,
   private val employeesDao: EmployeesDao
) : Repository {

   private val TAG = this.javaClass.simpleName

   @OptIn(ExperimentalPagingApi::class)
   fun getEmployeesFromLocal(
      query: String
   ): Flow<PagingData<EmployeeEntity>> {
      val remoteLoader: EmployeesRemotePageLoader = { pageIndex ->
         employeesSource.getEmployees(query, pageIndex, PAGE_SIZE)
      }
      val localLoader: EmployeesLocalLoader = {
         employeesDao
      }
      return Pager(
         config = PagingConfig(
            pageSize = PAGE_SIZE,
            initialLoadSize = PAGE_SIZE
         ),
         remoteMediator = EmployeesRemoteMediator(localLoader, remoteLoader),
         pagingSourceFactory = { employeesDao.getEmployees() }
      )
         .flow

         .map { pagingData ->
            pagingData.map { it.toEmployeeEntity() }
         }
   }

   fun getEmployees(
      query: String
   ): Flow<PagingData<EmployeeEntity>> {
      return Pager(
         config = PagingConfig(
            pageSize = PAGE_SIZE,
            enablePlaceholders = false
         ),
         pagingSourceFactory = {
            EmployeesPagingSource(object : EmployeesPagingSource.EmployeesPageLoader {
               override suspend fun getEmployees(pageIndex: Int): List<EmployeeEntity> {
                  return employeesSource.getEmployees(
                     query = query,
                     pageIndex = pageIndex,
                     pageSize = PAGE_SIZE
                  )
               }
            })
         }
      ).flow
   }

   private companion object {
      const val PAGE_SIZE = 10
   }
}