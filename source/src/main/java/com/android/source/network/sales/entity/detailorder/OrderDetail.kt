package com.android.source.network.sales.entity.detailorder


import com.google.gson.annotations.SerializedName

data class OrderDetail(
    @SerializedName("client") val client: ClientOrderDetail,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("driver") val driver: DriverOrderDetail,
    @SerializedName("id") val id: Long,
    @SerializedName("loan") val debit: String,
    @SerializedName("name") val identification: String,
    @SerializedName("paid") val paid: String,
    @SerializedName("product") val products: List<ProductOrderDetail>,
    @SerializedName("status") val status: Int,
    @SerializedName("summa") val summa: String
)