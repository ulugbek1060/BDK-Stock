package com.android.bdkstock.screens.main.menu.products

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.android.bdkstock.screens.main.base.BaseViewModel
import com.android.model.repository.account.AccountRepository
import com.android.model.repository.products.ProductsRepository
import com.android.model.repository.products.entity.IngredientItem
import com.android.model.utils.Results
import com.android.model.utils.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class DisplayProductsViewModel @Inject constructor(
   productsRepository: ProductsRepository,
   accountRepository: AccountRepository,
   savedStateHandle: SavedStateHandle
) : BaseViewModel(accountRepository) {

   private val _currentProduct = DisplayProductsFragmentArgs
         .fromSavedStateHandle(savedStateHandle)
         .product

   val ingredients: Flow<Results<List<IngredientItem>>> = productsRepository
         .getIngredientsOfProduct(_currentProduct.id)

   private val _productEntity = MutableLiveData(_currentProduct)
   val productEntity = _productEntity.asLiveData()

   fun getProductId() = _currentProduct.id

   fun getProduct() = _currentProduct.toProductSelectionItem()
}