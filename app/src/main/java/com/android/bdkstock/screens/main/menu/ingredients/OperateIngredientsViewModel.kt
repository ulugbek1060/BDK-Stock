package com.android.bdkstock.screens.main.menu.ingredients

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.android.bdkstock.R
import com.android.bdkstock.screens.main.base.BaseViewModel
import com.android.model.repository.account.AccountRepository
import com.android.model.repository.ingredients.IngredientsRepository
import com.android.model.repository.ingredients.entity.IngredientEntity
import com.android.model.repository.ingredients.entity.IngredientExOrInEntity
import com.android.model.utils.*
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OperateIngredientsViewModel @Inject constructor(
   private val ingredientsRepository: IngredientsRepository,
   accountRepository: AccountRepository,
   savedStateHandle: SavedStateHandle,
) : BaseViewModel(accountRepository) {

   private val _operationId = OperateIngredientsFragmentArgs
      .fromSavedStateHandle(savedStateHandle).operationId

   private val _state = MutableLiveData(State())
   val state = _state.liveData()

   private val _navigateToDisplay = MutableLiveEvent<IngredientExOrInEntity>()
   val navigateToDisplay = _navigateToDisplay.liveData()

   private val _selectedIngredient = MutableLiveData<IngredientEntity>()
   val selectedIngredient = _selectedIngredient.liveData()

   init {
      _state.value = _state.requireValue().copy(
         operationId = _operationId
      )
   }

   fun operateIngredient(
      amount: String,
      comment: String
   ) = viewModelScope.safeLaunch {
      showProgress()
      try {
         val importedIngredient =
            ingredientsRepository.addExpensesAndIncomesOfIngredient(
               statusOfActions = _operationId,
               ingredientId = getIngredientId(),
               amount = amount,
               comments = comment
            )
         successAndNavigateToDisplay(importedIngredient)
      } catch (e: EmptyFieldException) {
         showEmptyFields(e)
      } finally {
         hideProgress()
      }
   }

   fun setIngredient(ingredientEntity: IngredientEntity) {
      _selectedIngredient.value = ingredientEntity
   }

   private fun successAndNavigateToDisplay(importedIngredient: IngredientExOrInEntity) {
      _navigateToDisplay.publishEvent(importedIngredient)
   }

   private fun showEmptyFields(e: EmptyFieldException) {
      _state.value = _state.requireValue().copy(
         emptyName = e.field == Field.IngredientName,
         emptyAmount = e.field == Field.Amount,
         emptyComment = e.field == Field.Comments
      )
   }

   private fun showProgress() {
      _state.value = _state.requireValue().copy(
         isInProgress = true
      )
   }

   private fun hideProgress() {
      _state.value = _state.requireValue().copy(
         isInProgress = false
      )
   }

   private fun getIngredientId() = _selectedIngredient.requireValue().id.toInt()

   data class State(
      val isInProgress: Boolean = false,
      val emptyName: Boolean = false,
      val emptyAmount: Boolean = false,
      val emptyComment: Boolean = false,
      val operationId: Int? = null
   ) {

      fun getTitle(context: Context) =
         if (operationId == OPERATION_INCOME)
            context.getString(R.string.income)
         else
            context.getString(R.string.expense)

      fun getNameErrorMessage(context: Context) =
         if (emptyName) context.getString(R.string.error_empty_name)
         else null

      fun getAmountErrorMessage(context: Context) =
         if (emptyAmount) context.getString(R.string.error_empty_amount)
         else null

      fun getCommentErrorMessage(context: Context) =
         if (emptyComment) context.getString(R.string.error_empty_comment)
         else null
   }

   private companion object {
      const val OPERATION_INCOME = 0
   }
}