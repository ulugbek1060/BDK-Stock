package com.android.model.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.android.model.database.account.AccountDao
import com.android.model.database.account.entity.AccountRoomEntity
import com.android.model.database.employees.EmployeesDao
import com.android.model.database.employees.entity.EmployeeRoomEntity
import com.android.model.database.jobs.JobsDao
import com.android.model.database.jobs.entity.JobRoomEntity

@Database(
   version = 6,
   entities = [
      AccountRoomEntity::class,
      EmployeeRoomEntity::class,
      JobRoomEntity::class
   ],
   autoMigrations = [
      AutoMigration(
         from = 5,
         to = 6,
         spec = EmployeeMigrationFrom5To6::class
      )
   ],
   exportSchema = true
)
@TypeConverters(TypeConverter::class)
abstract class AppDatabase : RoomDatabase() {

   abstract fun getAccountDao(): AccountDao

   abstract fun getEmployeesDao(): EmployeesDao

   abstract fun getJobsDao(): JobsDao
}