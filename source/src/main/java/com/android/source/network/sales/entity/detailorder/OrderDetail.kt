package com.android.source.network.sales.entity.detailorder


import com.google.gson.annotations.SerializedName

data class OrderDetail(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val identification: String,
    @SerializedName("status") val status: Int,
    @SerializedName("summa") val summa: String,
    @SerializedName("paid") val paid: String,
    @SerializedName("loan") val debit: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("client") val client: ClientOrderDetail,
    @SerializedName("driver") val driver: DriverOrderDetail,
    @SerializedName("product") val products: List<ProductOrderDetail>,
)