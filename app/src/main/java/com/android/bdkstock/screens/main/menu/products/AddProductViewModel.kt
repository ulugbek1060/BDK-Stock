package com.android.bdkstock.screens.main.menu.products

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.android.bdkstock.R
import com.android.bdkstock.screens.main.base.BaseViewModel
import com.android.model.repository.account.AccountRepository
import com.android.model.repository.ingredients.entity.SimpleIngredient
import com.android.model.repository.products.ProductsRepository
import com.android.model.repository.products.entity.SimpleIngredientItem
import com.android.model.utils.*
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddProductViewModel @Inject constructor(
   private val productsRepository: ProductsRepository, accountRepository: AccountRepository
) : BaseViewModel(accountRepository) {

   private val _state = MutableLiveData(State())
   val state = _state.liveData()

   private val _goBack = MutableLiveEvent<String>()
   val goBack = _goBack.liveData()

   private val _ingredientList = MutableLiveData<List<SimpleIngredient>>()
   val ingredientList = _ingredientList.liveData()

   private val _selectedIngredients = MutableLiveData<ArrayList<SimpleIngredient>>(arrayListOf())
   val selectedIngredients = _selectedIngredients.liveData()

   val ingredientsFlow = productsRepository
      .getIngredientsForSelect()
      .handleException()

   /**
    * Arguments:
    * 1.ingredients = [SimpleIngredientItem] -> [List]
    * 2.name = [String]
    * 2.price = [String]
    * 2.unit = [String]
    */
   fun createProduct(
      name: String, price: String, unit: String
   ) = viewModelScope.safeLaunch {
      showProgress()
      try {
         val message = productsRepository.createProduct(
            name = name,
            unit = unit,
            price = price,
            ingredients = getIngredients()
         )
         showSuccessMessage(message)
      } catch (e: EmptyFieldException) {
         showEmptyFields(e)
      } finally {
         hideProgress()
      }
   }

   private fun showSuccessMessage(message: String) {
      _goBack.publishEvent(message)
   }

   private fun showEmptyFields(e: EmptyFieldException) {
      _state.value = _state.requireValue().copy(
         emptyName = e.field == Field.NAME,
         emptyUnit = e.field == Field.UNIT,
         emptyPrice = e.field == Field.PRICE,
         emptyIngredients = e.field == Field.INGREDIENT,
      )
   }

   fun removeIngredient(ingredient: SimpleIngredient) {
      val list = _selectedIngredients.requireValue()
      list.remove(ingredient)
      _selectedIngredients.value = list
   }

   fun addIngredient(ingredient: SimpleIngredient) {
      val list = _selectedIngredients.requireValue()
      list.add(ingredient)
      _selectedIngredients.value = list
   }

   private fun getIngredients() = _selectedIngredients.requireValue().map {
      SimpleIngredientItem(
         id = it.id, amount = it.amount!!
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

   fun setAllIngredients(value: List<SimpleIngredient>) {
      _ingredientList.value = value
   }

   data class State(
      val isInProgress: Boolean = false,
      val emptyName: Boolean = false,
      val emptyUnit: Boolean = false,
      val emptyPrice: Boolean = false,
      val emptyIngredients: Boolean = false
   ) {
      fun getNameError(context: Context): String? =
         if (emptyName) context.getString(R.string.error_empty_name)
         else null

      fun getUnitError(context: Context): String? =
         if (emptyName) context.getString(R.string.error_empty_unit)
         else null

      fun getPriceError(context: Context): String? =
         if (emptyPrice) context.getString(R.string.error_empty_price)
         else null
   }
}