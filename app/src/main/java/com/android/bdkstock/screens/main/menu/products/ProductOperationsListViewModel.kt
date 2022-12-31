package com.android.bdkstock.screens.main.menu.products

import androidx.lifecycle.MutableLiveData
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
import com.android.model.utils.asLiveData
import com.android.model.utils.publishEvent
import com.android.model.utils.requireValue
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
   val errorEvent = _errorEvent.asLiveData()

   private val _filter = MutableLiveData(ProductsFilterData())
   val filter = _filter.asLiveData()

   val operationsFlow: Flow<PagingData<ProductOperationEntity>> = _filter.asFlow()
      .flatMapLatest { filter ->
         productsRepository
            .getOperationsList(
               query = filter.query,
               status = filter.status,
               fromDate = filter.fromDate,
               toDate = filter.toDate
            )
      }
      .cachedIn(viewModelScope)


   fun showAuthError() {
      _errorEvent.publishEvent()
   }

   fun getFilter() = _filter.requireValue()

   fun setFilterData(filterData: ProductsFilterData) {
      _filter.value = filterData
   }

}