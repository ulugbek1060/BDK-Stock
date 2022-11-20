package com.android.bdkstock.screens.main.menu.clients

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.android.bdkstock.screens.main.base.BaseViewModel
import com.android.model.repository.account.AccountRepository
import com.android.model.repository.clients.ClientsRepository
import com.android.model.utils.MutableUnitLiveEvent
import com.android.model.utils.liveData
import com.android.model.utils.publishEvent
import com.android.model.utils.requireValue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class SearchClientsViewModel @Inject constructor(
   private val clientsRepository: ClientsRepository,
   accountRepository: AccountRepository,
   savedStateHandle: SavedStateHandle
) : BaseViewModel(accountRepository) {

   private val _invalidate = MutableUnitLiveEvent()
   val invalidate = _invalidate.liveData()

   private val _errorEvent = MutableUnitLiveEvent()
   val errorEvent = _errorEvent.liveData()

   private val _query = savedStateHandle.getLiveData(CLIENTS_QUERY_KEY, "")

   @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
   val clientsFlow = _query.asFlow()
      .debounce(500)
      .flatMapLatest {
         clientsRepository.getClientsByQuery(it)
      }
      .cachedIn(viewModelScope)

   fun setQuery(searchBy: String) {
      if (searchBy.isBlank())
         _invalidate.publishEvent()

      if (_query.requireValue() != searchBy)
         _query.value = searchBy
   }

   fun showAuthError() {
      _errorEvent.publishEvent()
   }

   private companion object {
      const val CLIENTS_QUERY_KEY = "clients_query"
   }
}