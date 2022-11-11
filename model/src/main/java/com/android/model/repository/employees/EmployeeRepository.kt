package com.android.model.repository.employees

import androidx.paging.*
import com.android.model.database.employees.EmployeesDao
import com.android.model.repository.Repository
import com.android.model.repository.employees.entity.EmployeeEntity
import com.android.model.utils.AuthException
import com.android.model.utils.BackendException
import com.android.model.utils.EmptyFieldException
import com.android.model.utils.Field
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class EmployeeRepository @Inject constructor(
   private val employeesSource: EmployeesSource,
   private val employeesDao: EmployeesDao
) : Repository {

   private val TAG = this.javaClass.simpleName

   suspend fun registerEmployee(
      firstname: String,
      lastname: String,
      phoneNumber: String,
      address: String,
      jobId: Int?
   ): String {

      if (firstname.isBlank()) throw EmptyFieldException(Field.Firstname)
      if (lastname.isBlank()) throw EmptyFieldException(Field.Lastname)
      if (phoneNumber.isBlank()) throw EmptyFieldException(Field.PhoneNumber)
      if (address.isBlank()) throw EmptyFieldException(Field.Address)

      return try {
         employeesSource.registerEmployee(
            firstname = firstname,
            lastname = lastname,
            phoneNumber = phoneNumber,
            address = address,
            jobId = jobId ?: throw EmptyFieldException(Field.Job)
         )
      } catch (e: Exception) {
         if (e is BackendException && e.code == 401) {
            throw AuthException(e)
         } else {
            throw e
         }
      }
   }

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

   suspend fun updateEmployee(
      id: Long,
      firstname: String,
      lastname: String,
      phoneNumber: String,
      address: String,
      password: String,
      confirmPassword: String,
      jobId: Int?
   ): String {
      if (firstname.isBlank()) throw EmptyFieldException(Field.Firstname)
      if (lastname.isBlank()) throw EmptyFieldException(Field.Lastname)
      if (phoneNumber.isBlank()) throw EmptyFieldException(Field.PhoneNumber)
      if (address.isBlank()) throw EmptyFieldException(Field.Address)
      if (password.isBlank()) throw EmptyFieldException(Field.Password)
      if (confirmPassword != password) throw EmptyFieldException(Field.MismatchPasswordFields)

      return try {
         employeesSource.updateEmployee(
            id = id,
            firstname = firstname,
            lastname = lastname,
            phoneNumber = phoneNumber,
            address = address,
            password = password,
            job = jobId ?: throw throw EmptyFieldException(Field.Job)
         )
      } catch (e: Exception) {
         if (e is BackendException && e.code == 401) {
            throw AuthException(e)
         } else {
            throw  e
         }
      }
   }


//   fun getEmployees(
//      query: String
//   ): Flow<PagingData<EmployeeEntity>> {
//      return Pager(
//         config = PagingConfig(
//            pageSize = PAGE_SIZE,
//            enablePlaceholders = false
//         ),
//         pagingSourceFactory = {
//            EmployeesPagingSource(object : EmployeesPagingSource.EmployeesPageLoader {
//               override suspend fun getEmployees(pageIndex: Int): List<EmployeeEntity> {
//                  return employeesSource.getEmployees(
//                     query = query,
//                     pageIndex = pageIndex,
//                     pageSize = PAGE_SIZE
//                  )
//               }
//            })
//         }
//      ).flow
//   }

   private companion object {
      const val PAGE_SIZE = 10
   }
}