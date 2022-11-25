package com.android.source.network.products.entity.addproduct


import com.google.gson.annotations.SerializedName

data class AddProductRequestEntity(
    @SerializedName("amount")
    val amount: String, // 12.00
    @SerializedName("comment")
    val comment: String, //
    @SerializedName("product_id")
    val productId: Int // 1
)