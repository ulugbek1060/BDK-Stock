package com.android.bdkstock.screens.main.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.bdkstock.R
import com.android.model.repository.account.AccountRepository
import com.android.model.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

open class BaseViewModel(
   private val accountRepository: AccountRepository? = null
) : ViewModel() {

   private val _showErrorMessageResEvent = MutableLiveEvent<Int>()
   val showErrorMessageRes = _showErrorMessageResEvent.asLiveData()

   private val _showErrorMessageEvent = MutableLiveEvent<String>()
   val showErrorMessageEvent = _showErrorMessageEvent.asLiveData()

   private val _showAuthErrorAndRestart = MutableUnitLiveEvent()
   val showAuthErrorAndRestart = _showAuthErrorAndRestart.asLiveData()

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
            e.printStackTrace()
            _showErrorMessageResEvent.publishEvent(R.string.internal_exception)
         }
      }
   }

   fun logout() = viewModelScope.launch {
      accountRepository?.logout()
   }

   fun restart() {
      _showAuthErrorAndRestart.publishEvent()
   }

   fun <T> Flow<T>.handleException(): Flow<T> = catch { e ->
      if (e is AuthException) {
         _showAuthErrorAndRestart.publishEvent()
      } else {
         e.printStackTrace()
         _showErrorMessageEvent.publishEvent(e.message ?: "Unknown error!")
      }
   }
}
