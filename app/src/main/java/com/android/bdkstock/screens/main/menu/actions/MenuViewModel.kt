package com.android.bdkstock.screens.main.menu.actions

import android.app.Application
import com.android.bdkstock.screens.main.base.BaseViewModel
import com.android.model.repository.account.AccountRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class MenuViewModel @Inject constructor(
   accountRepository: AccountRepository
) : BaseViewModel(accountRepository) {


}