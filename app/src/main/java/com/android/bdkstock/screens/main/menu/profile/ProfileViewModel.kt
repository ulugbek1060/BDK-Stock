package com.android.bdkstock.screens.main.menu.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.android.bdkstock.screens.main.base.BaseViewModel
import com.android.model.repository.account.AccountRepository
import com.android.model.repository.account.entity.AccountEntity
import com.android.model.utils.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
   private val accountRepository: AccountRepository
) : BaseViewModel(accountRepository) {

   private val _state = MutableLiveData(State())
   val state = _state.asLiveData()

   private val _accountInfo = MutableLiveData<AccountEntity>()
   val accountInfo = _accountInfo.asLiveData()

   init {
      viewModelScope.safeLaunch {
         accountRepository.getAccount().collectLatest {
            _accountInfo.value = it
         }
      }
   }

   fun logoutManually() = viewModelScope.safeLaunch {
      accountRepository.logoutManually()
   }

   data class State(
      val isInProgress: Boolean = false,
   )
}