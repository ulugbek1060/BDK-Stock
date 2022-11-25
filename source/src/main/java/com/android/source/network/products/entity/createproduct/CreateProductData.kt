package com.android.source.network.products.entity.createproduct


import com.google.gson.annotations.SerializedName

data class CreateProductData(
    @SerializedName("amount")
    val amount: String, // 0
    @SerializedName("created_at")
    val createdAt: String, // 2022-11-24T17:45:38.000000Z
    @SerializedName("id")
    val id: Int, // 12
    @SerializedName("name")
    val name: String, // MyFirstProduct
    @SerializedName("price")
    val price: String, // 2000
    @SerializedName("summa")
    val summa: String, // 0
    @SerializedName("unit")
    val unit: String, // kg
    @SerializedName("updated_at")
    val updatedAt: String // 2022-11-24T17:45:38.000000Z
)