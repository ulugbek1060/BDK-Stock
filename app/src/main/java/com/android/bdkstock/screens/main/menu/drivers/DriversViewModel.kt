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
import com.android.model.utils.MutableUnitLiveEvent
import com.android.model.utils.liveData
import com.android.model.utils.publishEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
@HiltViewModel
class DriversViewModel @Inject constructor(
   private val driversRepository: DriversRepository,
   accountRepository: AccountRepository,
   savedStateHandle: SavedStateHandle
) : BaseViewModel(accountRepository) {

   private val _errorEvent = MutableUnitLiveEvent()
   val errorEvent = _errorEvent.liveData()

   private val _query = savedStateHandle.getLiveData(DRIVERS_QUERY_KEY, "")

   val driversFlow: Flow<PagingData<DriverEntity>> = _query.asFlow()
      .flatMapLatest {
         driversRepository.getDriversList(it)
      }
      .cachedIn(viewModelScope)

   fun showAuthError() {
      _errorEvent.publishEvent()
   }

   fun setQuery(query: String?) {
      if (_query.value == query) return

      _query.value = query
   }

   private companion object {
      const val DRIVERS_QUERY_KEY = "drivers_query"
   }

}