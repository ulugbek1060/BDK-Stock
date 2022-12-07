package com.android.bdkstock.screens.main.menu.sales

import com.android.bdkstock.screens.main.base.BaseViewModel
import com.android.model.repository.account.AccountRepository
import com.android.model.repository.products.ProductsRepository
import com.android.model.repository.products.entity.ProductSelectionItem
import com.android.model.utils.Results
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class ProductsForOrderViewModel @Inject constructor(
   productsRepository: ProductsRepository,
   accountRepository: AccountRepository
) : BaseViewModel(accountRepository) {

   private var chosenProduct: ProductSelectionItem? = null

   val productsResult: Flow<Results<List<ProductSelectionItem>>> = productsRepository
      .getProductsForSelect()
      .handleException()

   fun getProduct() = chosenProduct

   fun setProduct(product: ProductSelectionItem) {
      chosenProduct = product
   }
}