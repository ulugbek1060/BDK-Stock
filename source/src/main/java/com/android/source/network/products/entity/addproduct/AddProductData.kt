package com.android.source.network.products.entity.addproduct


import com.google.gson.annotations.SerializedName

data class AddProductData(
    @SerializedName("amount")
    val amount: Int, // 100
    @SerializedName("comment")
    val comment: String, // My initial created product
    @SerializedName("created_at")
    val createdAt: String, // 2022-11-24T17:49:05.000000Z
    @SerializedName("id")
    val id: Int, // 21
    @SerializedName("product_id")
    val productId: Int, // 12
    @SerializedName("requestId")
    val requestId: Int, // 1669312145
    @SerializedName("status")
    val status: String, // 0
    @SerializedName("updated_at")
    val updatedAt: String, // 2022-11-24T17:49:05.000000Z
    @SerializedName("user_id")
    val userId: Int // 1
)