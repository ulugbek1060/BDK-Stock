package com.android.bdkstock.screens.main.profile

import androidx.lifecycle.viewModelScope
import com.android.bdkstock.screens.main.base.BaseViewModel
import com.android.model.repository.account.AccountRepository
import com.android.model.utils.MutableLiveEvent
import com.android.model.utils.asLiveData
import com.android.model.utils.publishEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
   private val repository: AccountRepository
) : BaseViewModel(repository) {

   private val _doLogoutActions = MutableLiveEvent<String>()
   val doLogoutActions = _doLogoutActions.asLiveData()

   fun logoutManually() = viewModelScope.safeLaunch {
      val message = repository.logoutManually()
      _doLogoutActions.publishEvent(message)
   }
}