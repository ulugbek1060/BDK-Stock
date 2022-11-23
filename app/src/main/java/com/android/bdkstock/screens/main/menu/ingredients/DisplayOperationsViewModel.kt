package com.android.bdkstock.screens.main.menu.ingredients

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.android.bdkstock.screens.main.base.BaseViewModel
import com.android.model.repository.account.AccountRepository
import com.android.model.repository.ingredients.entity.IngredientExOrInEntity
import com.android.model.utils.liveData
import com.android.source.network.ingredients.entity.expenseandincomelist.ExOrInIngredient
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DisplayOperationsViewModel @Inject constructor(
   accountRepository: AccountRepository,
   savedStateHandle: SavedStateHandle
) : BaseViewModel(accountRepository) {

   private val _currentOperations = DisplayOperationsFragmentArgs
      .fromSavedStateHandle(savedStateHandle).operationIngredient

   private val _operationEntity = MutableLiveData<IngredientExOrInEntity>()
   val operationEntity = _operationEntity.liveData()

   init {
      _operationEntity.value = _currentOperations
   }
}