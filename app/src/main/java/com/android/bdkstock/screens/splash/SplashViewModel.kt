package com.android.bdkstock.screens.splash

import androidx.lifecycle.ViewModel
import com.android.model.repository.account.AccountRepository
import com.android.model.utils.MutableLiveEvent
import com.android.model.utils.asLiveData
import com.android.model.utils.publishEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
   private val repository: AccountRepository
) : ViewModel() {

   private val _launchMainScreen = MutableLiveEvent<Boolean>()
   val launchMainScreen = _launchMainScreen.asLiveData()

   init {
      _launchMainScreen.publishEvent(repository.isSignedIn())
   }
}