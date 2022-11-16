package com.android.bdkstock.screens.main.menu.drivers

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.android.bdkstock.screens.main.base.BaseViewModel
import com.android.model.repository.account.AccountRepository
import com.android.model.repository.drivers.DriversRepository
import com.android.model.repository.drivers.entity.DriverEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
@HiltViewModel
class DriversViewModel @Inject constructor(
   private val driversRepository: DriversRepository,
   accountRepository: AccountRepository,
   savedStateHandle: SavedStateHandle,
) : BaseViewModel(accountRepository) {

   val driversFlow: Flow<PagingData<DriverEntity>>

   // for saving query
   private val _query = savedStateHandle.getLiveData(QUERY_DRIVER_KEY, "")

   fun setQuery(search: String) {
      _query.value = search
   }

   init {
      driversFlow = _query.asFlow()
         .debounce(500)
         .flatMapLatest {
            driversRepository.getDrivers(it)
         }
         .cachedIn(viewModelScope)
   }

   private companion object {
      const val QUERY_DRIVER_KEY = "query_driver"
   }
}