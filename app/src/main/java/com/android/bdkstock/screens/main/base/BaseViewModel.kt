package com.android.bdkstock.screens.main.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.bdkstock.R
import com.android.model.repository.account.AccountRepository
import com.android.model.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

open class BaseViewModel(private val accountRepository: AccountRepository) : ViewModel() {

   private val _showErrorMessageResEvent = MutableLiveEvent<Int>()
   val showErrorMessageRes = _showErrorMessageResEvent.liveData()

   private val _showErrorMessageEvent = MutableLiveEvent<String>()
   val showErrorMessageEvent = _showErrorMessageEvent.liveData()

   private val _showAuthErrorAndRestart = MutableUnitLiveEvent()
   val showAuthErrorAndRestart = _showAuthErrorAndRestart.liveData()

   fun CoroutineScope.safeLaunch(block: suspend CoroutineScope.() -> Unit) {
      viewModelScope.launch {
         try {
            block()
         } catch (e: ConnectionException) {
            _showErrorMessageResEvent.publishEvent(R.string.connection_error)
         } catch (e: BackendException) {
            _showErrorMessageEvent.publishEvent(e.message ?: "Undefined Message from Backend")
         } catch (e: AuthException) {
            _showAuthErrorAndRestart.publishEvent()
         } catch (e: Exception) {
            _showErrorMessageResEvent.publishEvent(R.string.internal_exception)
         }
      }
   }

   fun logout() = viewModelScope.launch {
      accountRepository.logout()
   }

   fun restart() {
      _showAuthErrorAndRestart.publishEvent()
   }

}

//fun <T> Flow<T>.handleException(): Flow<T> = flow {
//      try {
//         collectLatest { emit(it) }
//      } catch (e: AuthException) {
//         e.printStackTrace()
//         _showAuthErrorAndRestart.publishEvent()
//      } catch (e: Throwable) {
//         e.printStackTrace()
//         _showErrorMessageResEvent.publishEvent(R.string.internal_exception)
//      }
//   }