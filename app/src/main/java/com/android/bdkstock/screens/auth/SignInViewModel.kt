package com.android.bdkstock.screens.auth

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.bdkstock.R
import com.android.model.repository.account.AccountRepository
import com.android.model.utils.*
import com.google.gson.JsonParseException
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
   private val accountRepository: AccountRepository,
   @ApplicationContext private val context: Context
) : ViewModel() {

   private val _state = MutableLiveData(State())
   val state = _state.asLiveData()

   private val _showAuthError = MutableUnitLiveEvent()
   val showAuthError = _showAuthError.asLiveData()

   private val _errorParseJson = MutableUnitLiveEvent()
   val errorParseJson = _errorParseJson.asLiveData()

   private val _showMessageFromBackend = MutableLiveEvent<String>()
   val showCauseMessage = _showMessageFromBackend.asLiveData()

   private val _clearFields = MutableUnitLiveEvent()
   val clearFields = _clearFields.asLiveData()

   private val _navigate = MutableUnitLiveEvent()
   val navigate = _navigate.asLiveData()

   fun signIn(phoneNumber: String, password: String) = viewModelScope.launch {
      showProgress()
      try {
         accountRepository.signIn(phoneNumber, password)
         launchActivities()
      } catch (e: BackendException) {
         showBackendMessage(e.message ?: context.getString(R.string.unknown_error))
      } catch (e: JsonParseException) {
         e.printStackTrace()
      } catch (e: EmptyFieldException) {
         processEmptyFieldException(e)
      } catch (e: AuthException) {
         processAuthException()
      } catch (e: ConnectionException) {
         // TODO: need to initialize connection exception
      } catch (e: ParseBackendResponseException) {
         _errorParseJson.publishEvent()
      } finally {
         hideProgress()
      }
   }

   private fun processAuthException() {
      _showAuthError.publishEvent()
      _clearFields.publishEvent()
   }

   private fun processEmptyFieldException(e: EmptyFieldException) {
      _state.value = _state.requireValue().copy(
         emptyPasswordError = e.field == Field.PASSWORD,
         emptyPhoneNumberError = e.field == Field.PHONE_NUMBER
      )
   }

   private fun launchActivities() = _navigate.publishEvent()

   private fun showBackendMessage(
      message: String
   ) = _showMessageFromBackend.publishEvent(message)

   private fun showProgress() {
      _state.value = _state.requireValue().copy(signInInProgress = true)
   }

   private fun hideProgress() {
      _state.value = _state.requireValue().copy(signInInProgress = false)
   }

   data class State(
      val emptyPhoneNumberError: Boolean = false,
      val emptyPasswordError: Boolean = false,
      val signInInProgress: Boolean = false
   ) {
      val showProgress = signInInProgress
      val enableViews = !signInInProgress
   }
}