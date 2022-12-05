package com.android.bdkstock.screens.main.menu.sales

import com.android.bdkstock.screens.main.base.BaseViewModel
import com.android.model.repository.account.AccountRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
   accountRepository: AccountRepository
) : BaseViewModel(accountRepository) {


}