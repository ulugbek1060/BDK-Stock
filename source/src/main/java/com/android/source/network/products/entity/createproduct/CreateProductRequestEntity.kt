package com.android.source.network.products.entity.createproduct


import com.google.gson.annotations.SerializedName

data class CreateProductRequestEntity(
    @SerializedName("material")
    val material: List<Material>,
    @SerializedName("name")
    val name: String, // product1
    @SerializedName("price")
    val price: String, // 5600
    @SerializedName("unit")
    val unit: String // kg
)