package com.android.model.di


import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier


@Module
@InstallIn(SingletonComponent::class)
class DispatchersModule {

   @DefaultDispatcher
   @Provides
   fun providesDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

   @IoDispatcher
   @Provides
   fun providesIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

   @MainDispatcher
   @Provides
   fun providesMainDispatcher(): CoroutineDispatcher = Dispatchers.Main.immediate

}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IoDispatcher

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MainDispatcher

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DefaultDispatcher