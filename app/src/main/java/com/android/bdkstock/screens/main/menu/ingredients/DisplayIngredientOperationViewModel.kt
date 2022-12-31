package com.android.bdkstock.screens.main.menu.ingredients

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.android.bdkstock.R
import com.android.bdkstock.screens.main.base.BaseViewModel
import com.android.model.repository.account.AccountRepository
import com.android.model.repository.ingredients.entity.IngredientExOrInEntity
import com.android.model.utils.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DisplayIngredientOperationViewModel @Inject constructor(
   accountRepository: AccountRepository,
   savedStateHandle: SavedStateHandle
) : BaseViewModel(accountRepository) {

   private val _currentOperations = DisplayIngredientOperationFragmentArgs
      .fromSavedStateHandle(savedStateHandle)
      .operationIngredient

   private val _state = MutableLiveData(State())
   val state = _state.asLiveData()

   init {
      val ingredient = _currentOperations
      _state.value = State(
         ingredient = ingredient,
         status = ingredient.status.toInt()
      )
   }

   data class State(
      val ingredient: IngredientExOrInEntity? = null,
      val status: Int? = null
   ) {
      fun getStatusText(context: Context) =
         if (status == OPERATION_INCOME) context.getString(R.string.income)
         else context.getString(R.string.expense)

      fun getBackgroundColor(context: Context) =
         if (status == OPERATION_INCOME) context.getColor(R.color.green)
         else context.getColor(R.color.red)
   }

   private companion object {
      const val OPERATION_INCOME = 0
   }
}