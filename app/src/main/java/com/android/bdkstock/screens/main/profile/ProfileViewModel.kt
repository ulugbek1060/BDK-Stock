package com.android.bdkstock.screens.main.profile

import androidx.lifecycle.viewModelScope
import com.android.bdkstock.screens.main.base.BaseViewModel
import com.android.model.source.account.AccountRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
   private val repository: AccountRepository
) : BaseViewModel(repository) {

   init {
      viewModelScope.safeLaunch {

      }
   }
}