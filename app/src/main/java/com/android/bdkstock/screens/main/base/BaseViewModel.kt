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

open class BaseViewModel(private val accountRepository: AccountRepository) : ViewModel() {

   private val TAG = this.javaClass.simpleName

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
            _showErrorMessageEvent.publishEvent(e.message ?: "")
         } catch (e: AuthException) {
            _showAuthErrorAndRestart.publishEvent()
         } catch (e: Exception) {
            e.printStackTrace()
            _showErrorMessageResEvent.publishEvent(R.string.internal_exception)
         }
      }
   }

   //uiScope.launch {
   //    dataFlow()
   //        .onEach { value -> updateUI(value) }
   //        .handleErrors()
   //        .collect()
   //}

   fun <T> Flow<T>.handleErrors(): Flow<T> =
      catch { e -> _showErrorMessageEvent.publishEvent(e.message ?: "Undefined Error!") }

   fun logout() = viewModelScope.launch {
      accountRepository.logout()
   }
}