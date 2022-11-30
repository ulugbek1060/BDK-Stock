package com.android.bdkstock.screens.main.menu.products

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.android.bdkstock.screens.main.base.BaseViewModel
import com.android.model.repository.account.AccountRepository
import com.android.model.repository.products.ProductsRepository
import com.android.model.repository.products.entity.ProductEntity
import com.android.model.utils.MutableUnitLiveEvent
import com.android.model.utils.liveData
import com.android.model.utils.publishEvent
import com.android.model.utils.requireValue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class ProductsViewModel @Inject constructor(
   private val productsRepository: ProductsRepository,
   accountRepository: AccountRepository,
   savedStateHandle: SavedStateHandle
) : BaseViewModel(accountRepository) {

   private val _errorEvent = MutableUnitLiveEvent()
   val errorEvent = _errorEvent.liveData()

   private val _query = savedStateHandle.getLiveData(PRODUCTS_QUERY_KEY, "")
   val productsFlow: Flow<PagingData<ProductEntity>> = _query.asFlow()
      .flatMapLatest {
         productsRepository.getProductList(it)
      }
      .cachedIn(viewModelScope)

   fun setQuery(query: String?) {

      if (query == _query.requireValue()) return

      _query.value = query
   }

   fun showAuthError() {
      _errorEvent.publishEvent()
   }

   private companion object {
      const val PRODUCTS_QUERY_KEY = "products_query"
   }
}