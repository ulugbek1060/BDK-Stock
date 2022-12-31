package com.android.source.network.account.entity.userpermissions

import com.google.gson.annotations.SerializedName

data class UpdatePermsRequestEntity(
   @SerializedName("job_id") val jobId: Int,
   @SerializedName("permission") val permissions: List<Int>
)