package com.android.source.network.job.entity


import com.google.gson.annotations.SerializedName

data class JobsResponseEntity(
   @SerializedName("job") val jobs: Jobs
)