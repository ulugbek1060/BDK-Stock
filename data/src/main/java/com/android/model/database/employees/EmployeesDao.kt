package com.android.model.database.employees

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.android.model.database.BaseDao
import com.android.model.database.employees.entity.EmployeeRoomEntity

@Dao
interface EmployeesDao : BaseDao<EmployeeRoomEntity> {

   @Query("SELECT * FROM employee")
   fun getEmployees(): PagingSource<Int, EmployeeRoomEntity>

   /**
    * Clear local records.
    */
   @Transaction
   @Query("DELETE FROM employee")
   suspend fun clear()

   /**
    * Clear old records and place new records from the list.
    */
   @Transaction
   suspend fun refresh(employees: List<EmployeeRoomEntity>) {
      clear()
      insert(employees)
   }
}