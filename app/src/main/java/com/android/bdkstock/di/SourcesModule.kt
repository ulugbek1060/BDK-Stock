package com.android.bdkstock.di

import com.android.model.source.account.AccountSource
import com.android.model.source.settings.AppSettings
import com.android.model.source.settings.SharedPreferencesAppSettings
import com.android.source.network.account.AccountSourceImpl
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

}