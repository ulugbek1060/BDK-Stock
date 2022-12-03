package com.android.bdkstock.screens.main.menu.sales

import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.android.bdkstock.screens.main.base.BaseViewModel
import com.android.model.repository.account.AccountRepository
import com.android.model.repository.sales.SalesRepository
import com.android.model.utils.MutableUnitLiveEvent
import com.android.model.utils.liveData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SalesViewModel @Inject constructor(
   private val salesRepository: SalesRepository,
   accountRepository: AccountRepository
) : BaseViewModel(accountRepository) {

   private val _errorEvent = MutableUnitLiveEvent()
   val errorEvent = _errorEvent.liveData()

   fun showAuthError() {

   }

   val ordersFlow = salesRepository.getOrdersList().cachedIn(viewModelScope)

}