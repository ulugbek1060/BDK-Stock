package com.android.bdkstock.di

import android.content.Context
import androidx.room.Room
import com.android.model.database.AppDatabase
import com.android.model.database.account.AccountDao
import com.android.model.database.employees.EmployeesDao
import com.android.model.database.jobs.JobsDao
import com.android.model.database.vehicles.VehiclesDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

   @Provides
   @Singleton
   fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
      return Room.databaseBuilder(context, AppDatabase::class.java, DB_NAME)
//         .fallbackToDestructiveMigration()
         .build()
   }

   @Provides
   @Singleton
   fun provideAccountDao(appDatabase: AppDatabase): AccountDao {
      return appDatabase.getAccountDao()
   }

   @Provides
   @Singleton
   fun provideEmployeesDao(appDatabase: AppDatabase): EmployeesDao {
      return appDatabase.getEmployeesDao()
   }

   @Provides
   @Singleton
   fun provideJobsDao(appDatabase: AppDatabase): JobsDao {
      return appDatabase.getJobsDao()
   }

   @Provides
   @Singleton
   fun provideVehicleDao(appDatabase: AppDatabase): VehiclesDao {
      return appDatabase.getVehiclesDao()
   }

   private companion object {
      const val DB_NAME = "bdk_stock_db"
   }
}