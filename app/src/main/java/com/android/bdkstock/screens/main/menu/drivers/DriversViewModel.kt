package com.android.bdkstock.screens.main.menu.drivers

import androidx.lifecycle.SavedStateHandle
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
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
@HiltViewModel
class DriversViewModel @Inject constructor(
   private val driversRepository: DriversRepository,
   accountRepository: AccountRepository
) : BaseViewModel(accountRepository) {

   private val _errorEvent = MutableUnitLiveEvent()
   val errorEvent = _errorEvent.liveData()

   val driversFlow: Flow<PagingData<DriverEntity>> = driversRepository
      .getDriversList()
      .cachedIn(viewModelScope)

   fun showAuthError() {
      _errorEvent.publishEvent()
   }

}