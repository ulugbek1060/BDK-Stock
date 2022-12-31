package com.android.bdkstock.screens.main.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.android.bdkstock.screens.main.base.BaseViewModel
import com.android.model.repository.account.AccountRepository
import com.android.model.repository.account.entity.AccountEntity
import com.android.model.repository.account.entity.UserPermissionEntity
import com.android.model.utils.Pending
import com.android.model.utils.Success
import com.android.model.utils.Error
import com.android.model.utils.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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

   private val _userPermissions = MutableStateFlow<List<UserPermissionEntity>>(listOf())
   val userPermissions = _userPermissions.asStateFlow()

   suspend fun getUserPermissions() = viewModelScope.safeLaunch {
      accountRepository.getUserPermissions().collectLatest { result ->
         when (result) {
            is Pending -> {
               
            }
            is Success -> {}
            is Error -> {}
            else -> {}
         }
      }
   }

   init {
      viewModelScope.safeLaunch {
         accountRepository.getAccount().collectLatest {
            _accountInfo.value = it
         }
      }
   }


   data class State(
      val isInProgress: Boolean = false,
   )
}