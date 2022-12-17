package com.android.source.network.dashboard

import com.android.source.network.dashboard.entity.GetPaysResponseEntity
import retrofit2.http.GET

interface DashboardApi {

   @GET("api/dashboard/pays")
   suspend fun getPays(): GetPaysResponseEntity



}