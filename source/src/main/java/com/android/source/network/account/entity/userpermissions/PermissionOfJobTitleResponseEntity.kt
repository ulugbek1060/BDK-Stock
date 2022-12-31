package com.android.source.network.account.entity.userpermissions

import com.google.gson.annotations.SerializedName

data class PermissionOfJobTitleResponseEntity(
   @SerializedName("message") val message: String,
   @SerializedName("data") val permissions: Set<PermissionSourceEntity>
)