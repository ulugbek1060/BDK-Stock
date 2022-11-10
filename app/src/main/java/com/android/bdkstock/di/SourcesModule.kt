package com.android.bdkstock.di

import com.android.model.repository.account.AccountSource
import com.android.model.repository.employees.EmployeesSource
import com.android.model.repository.settings.AppSettings
import com.android.model.repository.settings.SharedPreferencesAppSettings
import com.android.source.network.account.AccountSourceImpl
import com.android.source.network.employees.EmployeesSourceIml
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class SourcesModule {

   @Binds
   abstract fun bindsAccountSource(
      accountSourceImpl: AccountSourceImpl
   ): AccountSource

   @Binds
   abstract fun bindsAppSettings(
      sharedPreferencesAppSettings: SharedPreferencesAppSettings
   ): AppSettings


   @Binds
   abstract fun bindsEmployeesSource(
      employeesSourceIml: EmployeesSourceIml
   ): EmployeesSource

}