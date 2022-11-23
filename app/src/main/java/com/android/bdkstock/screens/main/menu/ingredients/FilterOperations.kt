package com.android.bdkstock.screens.main.menu.ingredients

import java.io.Serializable

data class FilterOperations(
   val status: Int? = null,
   val fromDate: String? = null,
   val toDate: String? = null
) : Serializable