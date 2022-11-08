package com.android.bdkstock.di

import com.android.model.source.settings.AppSettings
import com.android.source.network.account.AccountApi
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
         val token = appSettings.getCurrentToken()
         if (token != null) {
            newBuilder.addHeader("Authorization", token)
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
}