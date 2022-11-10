package com.android.bdkstock.screens.main.profile

import com.android.bdkstock.screens.main.base.BaseViewModel
import com.android.model.repository.account.AccountRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
   private val repository: AccountRepository
) : BaseViewModel(repository) {
}