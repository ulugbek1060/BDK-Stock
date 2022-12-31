package com.android.source.network.account.entity.userpermissions

import com.google.gson.annotations.SerializedName

data class PermissionSourceEntity(
   @SerializedName("id") val id: Int,
   @SerializedName("name") val name: String,
   @SerializedName("created_at") val createdAt: String,
   @SerializedName("updated_at") val updatedAt: String,
   @SerializedName("pivot") val pivot: Pivot
)

data class Pivot(
   @SerializedName("job_id") val jobId: Int,
   @SerializedName("permission_id") val permissionId: Int
)