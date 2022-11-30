package com.android.bdkstock.screens.main.menu.products

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.android.bdkstock.screens.main.base.BaseViewModel
import com.android.model.repository.account.AccountRepository
import com.android.model.repository.products.ProductsRepository
import com.android.model.repository.products.entity.ProductOperationEntity
import com.android.model.utils.MutableUnitLiveEvent
import com.android.model.utils.liveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class ProductOperationsListViewModel @Inject constructor(
   private val productsRepository: ProductsRepository,
   accountRepository: AccountRepository,
   savedStateHandle: SavedStateHandle
) : BaseViewModel(accountRepository) {

   private val _errorEvent = MutableUnitLiveEvent()
   val errorEvent = _errorEvent.liveData()

   private val _query = savedStateHandle.getLiveData(PRODUCTS_FILTER_DATA, ProductsFilterState())
   val operationsFlow: Flow<PagingData<ProductOperationEntity>> = _query.asFlow()
      .flatMapLatest { filter ->
         productsRepository
            .getOperationsList(
               productId = filter.productId,
               query = filter.query,
               status = filter.status,
               fromDate = filter.fromDate,
               toDate = filter.toDate
            )
      }
      .cachedIn(viewModelScope)

   fun setFilterData(
      productId: Long,
      query: String,
      status: Int,
      fromDate: String,
      toDate: String
   ) {
//      _query.value =
   }

   fun showAuthError() {

   }

   data class ProductsFilterState(
      val productId: Long? = null,
      val query: String? = null,
      val status: Int? = null,
      val fromDate: String? = null,
      val toDate: String? = null
   )

   private companion object {
      const val PRODUCTS_FILTER_DATA = "products_filter"
   }
}