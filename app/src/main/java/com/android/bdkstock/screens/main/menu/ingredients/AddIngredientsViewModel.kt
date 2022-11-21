package com.android.bdkstock.screens.main.menu.ingredients

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.android.bdkstock.R
import com.android.bdkstock.screens.main.base.BaseViewModel
import com.android.model.repository.account.AccountRepository
import com.android.model.repository.ingredients.IngredientsRepository
import com.android.model.repository.ingredients.entity.IngredientEntity
import com.android.model.utils.*
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddIngredientsViewModel @Inject constructor(
   private val ingredientsRepository: IngredientsRepository,
   accountRepository: AccountRepository
) : BaseViewModel(accountRepository) {

   private val _state = MutableLiveData(State())
   val state = _state.liveData()

   private val _navigateToDisplayFrag = MutableLiveEvent<IngredientEntity>()
   val navigateToDisplayFrag = _navigateToDisplayFrag.liveData()

   fun createIngredients(
      name: String,
      unit: String
   ) = viewModelScope.safeLaunch {
      showProgress()
      try {
         val ingredient = ingredientsRepository.createIngredient(name, unit)
         successAndNavigateToDisplay(ingredient)
      } catch (e: EmptyFieldException) {
         publishEmptyFields(e)
      } finally {
         hideProgress()
      }
   }

   private fun successAndNavigateToDisplay(ingredient: IngredientEntity) {
      _navigateToDisplayFrag.publishEvent(ingredient)
   }

   private fun publishEmptyFields(e: EmptyFieldException) {
      _state.value = _state.requireValue().copy(
         emptyNameError = e.field == Field.IngredientName,
         emptyUnitError = e.field == Field.IngredientUnit
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

   data class State(
      val isInProgress: Boolean = false,
      val emptyNameError: Boolean = false,
      val emptyUnitError: Boolean = false
   ) {
      fun getNameErrorMessage(context: Context) =
         if (emptyNameError) context.getString(R.string.error_empty_name)
         else null

      fun getUnitErrorMessage(context: Context) =
         if (emptyUnitError) context.getString(R.string.error_empty_unit)
         else null
   }
}