package com.android.source.network.account.entity.logout


import com.google.gson.annotations.SerializedName

data class LogoutSuccessResponse(
    @SerializedName("success")
    val success: String // User logged out successfully!
)