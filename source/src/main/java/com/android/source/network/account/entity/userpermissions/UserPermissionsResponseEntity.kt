package com.android.source.network.account.entity.userpermissions

import com.google.gson.annotations.SerializedName

data class UserPermissionsResponseEntity(
   @SerializedName("data") val permissions: List<PermissionData>,
   @SerializedName("message") val message: String
)