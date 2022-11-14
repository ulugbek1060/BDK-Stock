package com.android.source.network.job.entity


import com.google.gson.annotations.SerializedName

data class Jobs(
   @SerializedName("data") val listJobs: List<Job>
)