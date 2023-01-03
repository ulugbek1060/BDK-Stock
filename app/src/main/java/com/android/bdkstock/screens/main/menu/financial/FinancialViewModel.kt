package com.android.bdkstock.screens.main.menu.financial

import com.android.bdkstock.screens.main.base.BaseViewModel
import com.android.model.repository.account.AccountRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FinancialViewModel @Inject constructor(
   private val accountRepository: AccountRepository
) : BaseViewModel(accountRepository) {
}