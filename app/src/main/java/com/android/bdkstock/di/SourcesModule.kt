package com.android.bdkstock.di

import com.android.model.repository.account.AccountSource
import com.android.model.repository.clients.ClientSource
import com.android.model.repository.drivers.DriversSource
import com.android.model.repository.employees.EmployeesSource
import com.android.model.repository.ingredients.IngredientsSource
import com.android.model.repository.jobs.JobSource
import com.android.model.repository.settings.AppSettings
import com.android.model.repository.settings.SharedPreferencesAppSettings
import com.android.source.network.account.AccountSourceImpl
import com.android.source.network.clients.ClientsSourceImpl
import com.android.source.network.drivers.DriversSourceImpl
import com.android.source.network.employees.EmployeesSourceIml
import com.android.source.network.ingredients.IngredientsSourceImpl
import com.android.source.network.job.JobSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.FragmentComponent
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

   @Binds
   abstract fun bindsJobsSource(
      jobSourceImp: JobSourceImpl
   ): JobSource

   @Binds
   abstract fun bindsDriversSource(
      driversSource: DriversSourceImpl
   ): DriversSource

   @Binds
   abstract fun bindsClientsSource(
      clientsSource: ClientsSourceImpl
   ): ClientSource

   @Binds
   abstract fun bindsIngredientsSource(
      ingredientsImpl: IngredientsSourceImpl
   ): IngredientsSource

}