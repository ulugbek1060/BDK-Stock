package com.android.bdkstock.screens.main.menu.actions

import androidx.lifecycle.MutableLiveData
import com.android.bdkstock.R
import com.android.bdkstock.screens.main.base.BaseViewModel
import com.android.model.repository.account.AccountRepository
import com.android.model.utils.liveData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MenuViewModel @Inject constructor(
   accountRepository: AccountRepository
) : BaseViewModel(accountRepository) {


}