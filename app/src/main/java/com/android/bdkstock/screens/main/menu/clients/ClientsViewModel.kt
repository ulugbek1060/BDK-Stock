package com.android.bdkstock.screens.main.menu.clients

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.asFlow
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
import com.android.model.utils.requireValue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
@HiltViewModel
class ClientsViewModel @Inject constructor(
   private val clientsRepository: ClientsRepository,
   accountRepository: AccountRepository,
   savedStateHandle: SavedStateHandle
) : BaseViewModel(accountRepository) {

   private val _errorEvent = MutableUnitLiveEvent()
   val errorEvent = _errorEvent.liveData()

   private val _query = savedStateHandle.getLiveData(CLIENT_QUERY_KEY, "")

   val clientFlow: Flow<PagingData<ClientEntity>> = _query.asFlow()
      .flatMapLatest {
         clientsRepository.getClientsList(it)
      }
      .cachedIn(viewModelScope)

   fun showAuthError() {
      _errorEvent.publishEvent()
   }

   fun setQuery(query: String?) {
      if (_query.value == query) return

      _query.value = query
   }

   fun getQuery(): CharSequence? = _query.requireValue()

   private companion object {
      const val CLIENT_QUERY_KEY = "client_query"
   }
}