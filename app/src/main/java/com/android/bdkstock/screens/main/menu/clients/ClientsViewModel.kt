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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class ClientsViewModel @Inject constructor(
   private val clientsRepository: ClientsRepository,
   accountRepository: AccountRepository,
   savedStateHandle: SavedStateHandle
) : BaseViewModel(accountRepository) {

   val clientFlow: Flow<PagingData<ClientEntity>>

   private val query = savedStateHandle.getLiveData(DEFAULT_CLIENT_QUERY, "")

   init {
      clientFlow = query.asFlow()
         .debounce(500)
         .flatMapLatest {
            clientsRepository.getClients(it)
         }
         .cachedIn(viewModelScope)
   }

   fun setQuery(search: String) {
      query.value = search
   }

   private companion object {
      const val DEFAULT_CLIENT_QUERY = "client_query"
   }
}