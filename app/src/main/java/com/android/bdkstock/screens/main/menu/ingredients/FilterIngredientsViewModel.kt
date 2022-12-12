package com.android.bdkstock.screens.main.menu.ingredients

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FilterIngredientsViewModel @Inject constructor(

) : ViewModel() {


}

data class IngredientsFilterData(
   val query: String? = null,
   val status: Int? = null,
   val fromDate: String? = null,
   val toDate: String? = null
) : java.io.Serializable