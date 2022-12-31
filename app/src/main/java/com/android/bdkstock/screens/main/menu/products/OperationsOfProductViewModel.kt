package com.android.bdkstock.screens.main.menu.products

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.android.bdkstock.screens.main.base.BaseViewModel
import com.android.model.repository.account.AccountRepository
import com.android.model.repository.products.ProductsRepository
import com.android.model.repository.products.entity.ProductOperationEntity
import com.android.model.utils.MutableUnitLiveEvent
import com.android.model.utils.asLiveData
import com.android.model.utils.publishEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class OperationsOfProductViewModel @Inject constructor(
   productsRepository: ProductsRepository,
   accountRepository: AccountRepository,
   savedStateHandle: SavedStateHandle
) : BaseViewModel(accountRepository) {

   private val _productId = OperationsOfProductFragmentArgs
      .fromSavedStateHandle(savedStateHandle)
      .productId

   val operationsFlow: Flow<PagingData<ProductOperationEntity>> =
      productsRepository.getOperationsList(productId = _productId)
         .cachedIn(viewModelScope)

   private val _errorEvent = MutableUnitLiveEvent()
   val errorEvent = _errorEvent.asLiveData()

   fun showAuthError() {
      _errorEvent.publishEvent()
   }
}