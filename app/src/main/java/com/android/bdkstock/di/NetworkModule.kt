package com.android.bdkstock.di

import android.util.Log
import com.android.model.repository.settings.AppSettings
import com.android.source.network.account.AccountApi
import com.android.source.network.employees.EmployeesApi
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {
   
   private val TAG = this.javaClass.simpleName

   @Provides
   @Singleton
   fun providesMoshi(): Gson {
      return GsonBuilder()
         .setLenient()
         .create()
   }

   @Singleton
   @Provides
   fun provideClient(appSettings: AppSettings): OkHttpClient {
      return OkHttpClient.Builder()
         .addInterceptor(createInterceptor(appSettings))
         .addInterceptor(createLoggingInterceptor())
         .build()
   }

   private fun createInterceptor(appSettings: AppSettings): Interceptor {
      return Interceptor { chain ->
         val newBuilder = chain.request().newBuilder()
         Log.d(TAG, "createInterceptor: ${chain.request().url}")
         val token = appSettings.getCurrentToken()
         if (token != null) {
            newBuilder.addHeader("Authorization", "Bearer $token")
         }
         return@Interceptor chain.proceed(newBuilder.build())
      }
   }

   private fun createLoggingInterceptor(): Interceptor {
      return HttpLoggingInterceptor()
         .setLevel(HttpLoggingInterceptor.Level.BODY)
   }

   @Provides
   @Singleton
   fun provideRetrofit(
      gson: Gson,
      okHttpClient: OkHttpClient,
   ): Retrofit {
      return Retrofit.Builder()
         .baseUrl(BASE_URL)
         .client(okHttpClient)
         .addConverterFactory(GsonConverterFactory.create(gson))
         .build()
   }

   private companion object {
      const val BASE_URL = "https://back.bdk-stock.uz/"
   }

   @Provides
   @Singleton
   fun provideAccountApi(retrofit: Retrofit): AccountApi {
      return retrofit.create(AccountApi::class.java)
   }

   @Provides
   @Singleton
   fun provideEmployeesApi(retrofit: Retrofit): EmployeesApi {
      return retrofit.create(EmployeesApi::class.java)
   }
}