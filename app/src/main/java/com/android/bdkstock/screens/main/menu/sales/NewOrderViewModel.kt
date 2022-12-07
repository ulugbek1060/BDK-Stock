package com.android.bdkstock.screens.main.menu.sales

import androidx.lifecycle.MutableLiveData
import com.android.bdkstock.screens.main.base.BaseViewModel
import com.android.model.repository.account.AccountRepository
import com.android.model.repository.products.entity.ProductSelectionItem
import com.android.model.utils.liveData
import com.android.model.utils.requireValue
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NewOrderViewModel @Inject constructor(
   accountRepository: AccountRepository
) : BaseViewModel(accountRepository) {

   private val _productsList = MutableLiveData<MutableList<ProductSelectionItem>>(mutableListOf())
   val productsList = _productsList.liveData()


   fun setProduct(product: ProductSelectionItem?) {
      if (product == null) return
      val list = _productsList.requireValue()
      list.add(product)
      _productsList.postValue(list)
   }
}