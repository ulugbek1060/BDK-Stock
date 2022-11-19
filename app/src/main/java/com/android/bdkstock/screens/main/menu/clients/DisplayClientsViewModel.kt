package com.android.bdkstock.screens.main.menu.clients

import com.android.bdkstock.screens.main.base.BaseViewModel
import com.android.model.repository.account.AccountRepository
import com.android.model.repository.clients.ClientsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DisplayClientsViewModel @Inject constructor(
   private val clientsRepository: ClientsRepository,
   accountRepository: AccountRepository
) : BaseViewModel(accountRepository) {


}