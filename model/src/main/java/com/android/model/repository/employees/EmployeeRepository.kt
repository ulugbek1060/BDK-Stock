package com.android.model.repository.employees

import androidx.paging.*
import com.android.model.database.employees.EmployeesDao
import com.android.model.repository.base.BaseRepository
import com.android.model.repository.base.Repository
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
) : BaseRepository(), Repository {

   suspend fun registerEmployee(
      firstname: String,
      lastname: String,
      phoneNumber: String,
      address: String,
      jobId: Int?
   ): EmployeeEntity {

      if (firstname.isBlank()) throw EmptyFieldException(Field.Firstname)
      if (lastname.isBlank()) throw EmptyFieldException(Field.Lastname)
      if (phoneNumber.isBlank()) throw EmptyFieldException(Field.PhoneNumber)
      if (address.isBlank()) throw EmptyFieldException(Field.Address)
      if (jobId == null) throw EmptyFieldException(Field.Job)

      return wrapExceptions {
         employeesSource.registerEmployee(
            firstname = firstname,
            lastname = lastname,
            phoneNumber = phoneNumber,
            address = address,
            jobId = jobId
         )
      }
   }

   @OptIn(ExperimentalPagingApi::class)
   fun getEmployeesFromLocal(): Flow<PagingData<EmployeeEntity>> {
      val localLoader: EmployeesLocalLoader = {
         employeesDao
      }
      val remoteLoader: EmployeesRemotePageLoader = { pageIndex ->
         employeesSource.getEmployees(null, pageIndex, PAGE_SIZE)
      }
      return Pager(
         config = PagingConfig(
            pageSize = PAGE_SIZE,
            initialLoadSize = PAGE_SIZE
         ),
         remoteMediator = EmployeesRemoteMediator(
            localLoader = localLoader,
            remoteLoader = remoteLoader
         ),
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
      if (jobId == null) throw throw EmptyFieldException(Field.Job)

      return try {
         employeesSource.updateEmployee(
            id = id,
            firstname = firstname,
            lastname = lastname,
            phoneNumber = phoneNumber,
            address = address,
            password = password,
            job = jobId
         )
      } catch (e: Exception) {
         if (e is BackendException && e.code == 401) {
            throw AuthException(e)
         } else {
            throw  e
         }
      }
   }

   fun getEmployeeFromRemote(
      query: String
   ): Flow<PagingData<EmployeeEntity>> {
      val loader: EmployeesPageLoader = { pageIndex ->
         employeesSource.searchBy(query, pageIndex, PAGE_SIZE)
      }
      return Pager(
         config = PagingConfig(
            pageSize = PAGE_SIZE,
            enablePlaceholders = false
         ),
         pagingSourceFactory = {
            EmployeesPagingSource(loader)
         }
      ).flow
   }

   private companion object {
      const val PAGE_SIZE = 10
   }
}