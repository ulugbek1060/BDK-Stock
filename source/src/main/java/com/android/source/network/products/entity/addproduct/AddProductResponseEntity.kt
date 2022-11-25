package com.android.source.network.products.entity.addproduct


import com.google.gson.annotations.SerializedName

data class AddProductResponseEntity(
    @SerializedName("data")
   val product: AddProductData,
    @SerializedName("message")
   val message: String // product muvaffaqiyatli qo\shildi
)