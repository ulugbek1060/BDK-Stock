package com.android.model.repository.employees

import androidx.paging.PagingData
import com.android.model.database.employees.EmployeesDao
import com.android.model.repository.base.BasePageSource
import com.android.model.repository.base.BaseRepository
import com.android.model.repository.base.DataLoader
import com.android.model.repository.base.Repository
import com.android.model.repository.employees.entity.EmployeeEntity
import com.android.model.utils.AuthException
import com.android.model.utils.BackendException
import com.android.model.utils.EmptyFieldException
import com.android.model.utils.Field
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class EmployeeRepository @Inject constructor(
   private val employeesSource: EmployeesSource
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

   fun getEmployeeFromRemote(query: String? = null): Flow<PagingData<EmployeeEntity>> =
      getPagerData {
         val loader: DataLoader<EmployeeEntity> = { pageIndex ->
            employeesSource.getEmployees(query, pageIndex, PAGE_SIZE)
         }
         BasePageSource(loader = loader, defaultPageSize = DEFAULT_PAGE_SIZE)
      }

   private companion object {
      const val PAGE_SIZE = 10
   }
}