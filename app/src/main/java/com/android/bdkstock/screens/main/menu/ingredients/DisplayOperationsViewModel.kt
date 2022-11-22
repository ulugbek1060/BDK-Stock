package com.android.bdkstock.screens.main.menu.ingredients

import com.android.bdkstock.screens.main.base.BaseViewModel
import com.android.model.repository.account.AccountRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DisplayOperationsViewModel @Inject constructor(
   accountRepository: AccountRepository
) : BaseViewModel(accountRepository) {

}