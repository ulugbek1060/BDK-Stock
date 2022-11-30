package com.android.bdkstock

import android.app.Application
import android.util.Log
import com.android.model.utils.Const
import com.android.source.network.employees.entity.employeeslist.EmployeesResponseEntity
import com.google.gson.GsonBuilder
import dagger.hilt.android.HiltAndroidApp
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

@HiltAndroidApp
class App : Application()

