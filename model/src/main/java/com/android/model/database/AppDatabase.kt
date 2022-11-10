package com.android.model.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.android.model.database.account.AccountDao
import com.android.model.database.account.entity.AccountRoomEntity
import com.android.model.database.employees.EmployeesDao
import com.android.model.database.employees.entity.EmployeeRoomEntity

@Database(
   version = 2,
   entities = [
      AccountRoomEntity::class,
      EmployeeRoomEntity::class
   ]
)
abstract class AppDatabase : RoomDatabase() {

   abstract fun getAccountDao(): AccountDao

   abstract fun getEmployeesDao(): EmployeesDao
}