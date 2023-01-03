package com.android.bdkstock.di

import com.android.model.repository.account.AccountSource
import com.android.model.repository.clients.ClientSource
import com.android.model.repository.dashboard.DashboardSource
import com.android.model.repository.drivers.DriversSource
import com.android.model.repository.employees.EmployeesSource
import com.android.model.repository.ingredients.IngredientsSource
import com.android.model.repository.jobs.JobSource
import com.android.model.repository.products.ProductsSource
import com.android.model.repository.sales.SalesSource
import com.android.model.repository.settings.AppSettings
import com.android.model.repository.settings.SharedPreferencesAppSettings
import com.android.model.repository.settings.UserPermissions
import com.android.model.repository.settings.UserPermissionsPreferences
import com.android.source.network.account.AccountSourceImpl
import com.android.source.network.clients.ClientsSourceImpl
import com.android.source.network.dashboard.DashboardSourceImpl
import com.android.source.network.drivers.DriversSourceImpl
import com.android.source.network.employees.EmployeesSourceIml
import com.android.source.network.ingredients.IngredientsSourceImpl
import com.android.source.network.job.JobSourceImpl
import com.android.source.network.products.ProductsSourceImpl
import com.android.source.network.sales.SalesSourceImpl
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
   abstract fun bindsPermissionsPref(
      userPermissionsPreferences: UserPermissionsPreferences
   ): UserPermissions

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

   @Binds
   abstract fun bindsProductsSource(
      productsSourceImpl: ProductsSourceImpl
   ): ProductsSource

   @Binds
   abstract fun bindsSalesSource(
      salesSourceImpl: SalesSourceImpl
   ): SalesSource

   @Binds
   abstract fun bindsDashboardSource(
      dashboardSourceImpl: DashboardSourceImpl
   ): DashboardSource
}