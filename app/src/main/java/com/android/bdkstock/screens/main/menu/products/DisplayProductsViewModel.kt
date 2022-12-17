package com.android.bdkstock.screens.main.menu.products

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.android.bdkstock.screens.main.base.BaseViewModel
import com.android.model.repository.account.AccountRepository
import com.android.model.repository.products.ProductsRepository
import com.android.model.repository.products.entity.IngredientItem
import com.android.model.repository.products.entity.ProductOperationEntity
import com.android.model.repository.products.entity.ProductSelectionItem
import com.android.model.utils.MutableUnitLiveEvent
import com.android.model.utils.Results
import com.android.model.utils.liveData
import com.android.model.utils.publishEvent
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
   val productEntity = _productEntity.liveData()

   fun getProductId() = _currentProduct.id

   fun getProduct() = _currentProduct.toProductSelectionItem()
}