package com.android.bdkstock.screens.main.menu.products

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.Serializable
import javax.inject.Inject

@HiltViewModel
class FilterProductsViewModel @Inject constructor(

) : ViewModel() {

}

data class ProductsFilterData(
   val status: Int? = null,
   val client: String? = null,
   val fromDate: String? = null,
   val toDate: String? = null,
   val driver: String? = null,
) : Serializable