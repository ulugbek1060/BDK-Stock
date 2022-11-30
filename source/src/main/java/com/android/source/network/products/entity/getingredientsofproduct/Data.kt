package com.android.source.network.products.entity.getingredientsofproduct


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("amount")
    val amount: String, // 32.38
    @SerializedName("material_id")
    val materialId: Long, // 3
    @SerializedName("name")
    val name: String, // KOST
    @SerializedName("unit")
    val unit: String // kg
)