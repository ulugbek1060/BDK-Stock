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
import com.android.model.utils.requireValue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class SearchDriversViewModel @Inject constructor(
   private val driversRepository: DriversRepository,
   accountRepository: AccountRepository,
   savedStateHandle: SavedStateHandle
) : BaseViewModel(accountRepository) {

   private val _invalidate = MutableUnitLiveEvent()
   val invalidate = _invalidate.liveData()

   private val _errorEvent = MutableUnitLiveEvent()
   val errorEvent = _errorEvent.liveData()

   private val _query = savedStateHandle.getLiveData(QUERY_DRIVER_KEY, "")

   var driversFlow: Flow<PagingData<DriverEntity>> = _query.asFlow()
      .debounce(500)
      .flatMapLatest {
         driversRepository.getDriversByQuery(it)
      }
      .cachedIn(viewModelScope)

   init {
      _invalidate.publishEvent()
   }

   fun setQuery(searchBy: String) {

      if (searchBy.isBlank())
         _invalidate.publishEvent()

      if (_query.requireValue() != searchBy)
         _query.value = searchBy

   }

   fun showAuthError(){
      _errorEvent.publishEvent()
   }

   private companion object {
      const val QUERY_DRIVER_KEY = "query_driver"
   }
}