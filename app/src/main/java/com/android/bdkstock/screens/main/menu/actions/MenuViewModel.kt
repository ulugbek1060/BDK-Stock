package com.android.bdkstock.screens.main.menu.actions

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.android.bdkstock.screens.main.base.BaseViewModel
import com.android.model.repository.account.AccountRepository
import com.android.model.repository.dashboard.DashboardRepository
import com.android.model.repository.dashboard.entity.BudgetAndChart
import com.android.model.utils.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "MenuViewModel"

@HiltViewModel
class MenuViewModel @Inject constructor(
   private val dashboardRepository: DashboardRepository,
   private val accountRepository: AccountRepository
) : BaseViewModel(accountRepository) {

   private val _permissions = MutableStateFlow<List<String>>(emptyList())
   val permissions = _permissions.asStateFlow()

   private val _menus = MutableLiveData<List<MenuItem>>()
   val menus = _menus.asLiveData()

   private val _dashboardInfo = MutableLiveData<BudgetAndChart?>(null)
   val dashboardInfo = _dashboardInfo.asLiveData()

   private val _errorMessage = MutableLiveEvent<String?>()
   val errorMessage = _errorMessage.asLiveData()

   private val _state = MutableLiveData(State())
   val state = _state.asLiveData()

   init {
      loadInitialState()
   }

   fun loadInitialState() {
      viewModelScope.launch(Dispatchers.IO) {
         accountRepository.getUserPermissions().collectLatest { result ->
            when (result) {
               is Results.Success -> {
                  hideProgress()
                  result.value.let { _permissions.value = it }
               }
               is Results.Pending -> {
                  showProgress()
               }
               is Results.Error -> {
                  hideProgress()
                  _errorMessage.publishEventOnBackground(
                     result.error.localizedMessage
                  )
               }
               else -> {
                  hideProgress()
               }
            }
         }
      }
   }

   fun setMenus(menus: HashSet<MenuItem>) {
      _menus.value = menus.toList()
   }

   fun loadBudgetInfo() = viewModelScope.launch(Dispatchers.IO) {
      dashboardRepository.getBudgetInfo("2022").collectLatest { result ->
         when (result) {
            is Results.Pending -> {
               showProgress()
            }
            is Results.Success -> {
               hideProgress()
               result.value.let { _dashboardInfo.postValue(it) }
            }
            is Results.Error -> {
               hideProgress()
               _errorMessage.publishEventOnBackground(
                  result.error.localizedMessage
               )
            }
            else -> {
               hideProgress()
            }
         }
      }
   }

   private fun showProgress() {
      _state.postValue(
         _state.requireValue().copy(
            isInProgress = true
         )
      )
   }

   private fun hideProgress() {
      _state.postValue(
         _state.requireValue().copy(
            isInProgress = false
         )
      )
   }

   data class State(
      val isInProgress: Boolean = false
   )
}