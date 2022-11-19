package com.android.bdkstock.screens.main.menu.clients

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.android.bdkstock.screens.main.base.BaseViewModel
import com.android.model.repository.account.AccountRepository
import com.android.model.repository.clients.ClientsRepository
import com.android.model.repository.clients.entity.ClientEntity
import com.android.model.utils.MutableUnitLiveEvent
import com.android.model.utils.liveData
import com.android.model.utils.publishEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class ClientsViewModel @Inject constructor(
   private val clientsRepository: ClientsRepository,
   accountRepository: AccountRepository
) : BaseViewModel(accountRepository) {

   private val _errorEvent = MutableUnitLiveEvent()
   val errorEvent = _errorEvent.liveData()

   val clientFlow: Flow<PagingData<ClientEntity>> = clientsRepository
      .getClientsList()
      .cachedIn(viewModelScope)

   fun showAuthError() {
      _errorEvent.publishEvent()
   }
}