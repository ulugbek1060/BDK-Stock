package com.android.bdkstock.screens.main.dashboard

import androidx.lifecycle.viewModelScope
import com.android.bdkstock.screens.main.base.BaseViewModel
import com.android.model.source.account.AccountRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
   private val accountRepository: AccountRepository
) : BaseViewModel(accountRepository) {

   init {
      viewModelScope.safeLaunch {

      }
   }

}