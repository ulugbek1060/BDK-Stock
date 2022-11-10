package com.android.bdkstock.screens.main.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.android.bdkstock.screens.main.base.BaseViewModel
import com.android.model.repository.account.AccountRepository
import com.android.model.repository.account.entity.AccountEntity
import com.android.model.utils.liveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
   private val repository: AccountRepository
) : BaseViewModel(repository) {

   private val _accountInfo = MutableLiveData<AccountEntity>()
   val accountInfo = _accountInfo.liveData()

   init {
      viewModelScope.safeLaunch {
//         _accountInfo.value = repository.getAccount().first()
         repository.getAccount().collect {
            _accountInfo.value = it
         }
      }
   }

   data class State(
      val accountInProcess:Boolean = false
   )
}