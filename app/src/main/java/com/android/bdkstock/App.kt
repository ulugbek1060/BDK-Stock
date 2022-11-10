package com.android.bdkstock

import android.app.Application
import android.util.Log
import com.android.model.utils.Const
import com.android.source.network.employees.entity.EmployeesResponseEntity
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


fun main() {

   val call = BasicAuthClient<Api>().create(Api::class.java).getEmployees(
      "",
      1,
      10
   )
   call.enqueue(object : Callback<EmployeesResponseEntity> {
      override fun onResponse(
         call: Call<EmployeesResponseEntity>,
         response: Response<EmployeesResponseEntity>
      ) {
         if (response.isSuccessful) {
            println(response.body())
         } else {
            Log.e("EmployeesResponseEntity", "Error: ${response.code()} ${response.message()}")
         }
      }

      override fun onFailure(call: Call<EmployeesResponseEntity>, t: Throwable) {
         Log.e("EmployeesResponseEntity", t.message, t)
      }
   })

}

class BasicAuthClient<T> {

   private val gson = GsonBuilder()
      .setLenient()
      .create()

   private val retrofit = Retrofit.Builder()
      .baseUrl("https://back.bdk-stock.uz/")
      .client(provideClient(token))
      .addConverterFactory(GsonConverterFactory.create(gson))
      .build()

   fun create(service: Class<T>): T {
      return retrofit.create(service)
   }

   fun provideClient(token: String): OkHttpClient {
      return OkHttpClient.Builder()
         .addInterceptor(createInterceptor(token))
         .addInterceptor(createLoggingInterceptor())
         .build()
   }


   private fun createInterceptor(token: String): Interceptor {
      return Interceptor { chain ->
         val newBuilder = chain.request().newBuilder()
         newBuilder.addHeader("Authorization", "Bearer $token")
         return@Interceptor chain.proceed(newBuilder.build())
      }
   }

   private fun createLoggingInterceptor(): Interceptor {
      return HttpLoggingInterceptor()
         .setLevel(HttpLoggingInterceptor.Level.BODY)
   }

   companion object {
      const val token =
         "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdWQiOiIxIiwianRpIjoiYTQ2YjNkMzkxZmJkMjQ1ZmZjZGU3MGU0ZmQxZDAyZTVkODMxNzRhYzFkNzkxNTU2YmEyNmE1NzE2ODY5MjQ2ZDYxODMyMDRhY2M1Y2M3NDQiLCJpYXQiOjE2NjgwNjQ5OTQuMjUzNjcsIm5iZiI6MTY2ODA2NDk5NC4yNTM2NzIsImV4cCI6MTY2ODE1MTM5NC4yNTA0OSwic3ViIjoiMSIsInNjb3BlcyI6W119.O_awsbxT3CDjk5ttG3zAjrTByZjyla5CZ87AMiBgLIesWKXYc7ovp9TyYoaCrK3NiEgXcJVYpIAqPr6jjRLrJejMkf0tL4vQo-NCBU8aNXiNhuSGI2pDbtgn4WcpzawsM1TNIOItCLjOwOrpKkc_OmMcmi5R910De56rM-TMHDiSPZuYjDvNAAppvQnXiUdKOIv7BA1gVo_jar81-aSjOWRSCwhyWeNzx9mErxJawBrhDYDCA7sARyu0scAD4BlLSP0JMmFGzCIKDILddwC-pM9TEpWXNBIlVJHQBUxAmo_tZC0Kdsiw_EqWVEJX9DLLw5n52VMq3FEBZI3_xoK4zwi-cSuoka6Lg4K_KILYBTyzJv33UNpxRuBln4CfXmDAKpvRVxASyPRJDEvvCfi_s0r_L28vm-I3bjwJCOXzxi0K55kA7u4HIQV_lznixzgW6gETUxDicCOeoxeFDi92TdxlT0xho7dGg545cRHqK1vhoTh1cdSGQSFE5IXlGqRD7whERwtFluOI3SbWmKIBTPCSEHok3wHMAimNkLNxtHpkeLnFPnzBVFwIU9G6KkTBTIX1gqRMGj2JhV-GfApXvoymID_ISrcKv1UFsvjrMI7SynlXwsBsWnLPpHD_8nev8AtNQ4_u7Y1TAA8MyzwE2aTJwl5mwMi5SCs4MvvRANE"
   }
}

interface Api {

   @GET("api/user/get")
   fun getEmployees(
      @Query("search") query: String,
      @Query("page") pageIndex: Int,
      @Query("count") pageSize: Int,
      @Header(Const.HEADER_KEY_CONTENT_TYPE) type: String = Const.HEADER_VALUE_CONTENT_TYPE,
      @Header(Const.HEADER_KEY_ACCEPT) accept: String = Const.HEADER_VALUE_ACCEPT
   ): Call<EmployeesResponseEntity>

}


