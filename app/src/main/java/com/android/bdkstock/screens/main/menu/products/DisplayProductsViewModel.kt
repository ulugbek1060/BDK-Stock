package com.android.bdkstock.screens.main.menu.products

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.android.bdkstock.screens.main.base.BaseViewModel
import com.android.model.repository.account.AccountRepository
import com.android.model.repository.ingredients.entity.IngredientEntity
import com.android.model.repository.products.ProductsRepository
import com.android.model.repository.products.entity.ProductEntity
import com.android.model.repository.products.entity.ProductOperationEntity
import com.android.model.utils.MutableUnitLiveEvent
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

   val operationsFlow: Flow<PagingData<ProductOperationEntity>> = productsRepository
      .getOperationsList(productId = _currentProduct.id)
      .cachedIn(viewModelScope)

   private val _errorEvent = MutableUnitLiveEvent()
   val errorEvent = _errorEvent.liveData()

   private val _ingredientEntity = MutableLiveData(_currentProduct)
   val ingredientEntity = _ingredientEntity.liveData()

   fun showAuthError() {
      _errorEvent.publishEvent()
   }
}