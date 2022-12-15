package com.android.bdkstock.screens.main.menu.ingredients

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.android.bdkstock.screens.main.base.BaseViewModel
import com.android.model.repository.account.AccountRepository
import com.android.model.repository.ingredients.IngredientsRepository
import com.android.model.repository.ingredients.entity.IngredientEntity
import com.android.model.repository.ingredients.entity.IngredientExOrInEntity
import com.android.model.utils.MutableUnitLiveEvent
import com.android.model.utils.liveData
import com.android.model.utils.publishEvent
import com.android.model.utils.requireValue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class DisplayIngredientsViewModel @Inject constructor(
   private val ingredientsRepository: IngredientsRepository,
   accountRepository: AccountRepository,
   savedStateHandle: SavedStateHandle
) : BaseViewModel(accountRepository) {

   private val _errorEvent = MutableUnitLiveEvent()
   val errorEvent = _errorEvent.liveData()

   private val _currentIngredient = DisplayIngredientsFragmentArgs
      .fromSavedStateHandle(savedStateHandle)

   private val _ingredientEntity = MutableLiveData<IngredientEntity>()
   val ingredientEntity = _ingredientEntity.liveData()

   private val _ingredientId = savedStateHandle.getLiveData<Long>(INGREDIENT_ID)

   val ingredientsOperationsFlow: Flow<PagingData<IngredientExOrInEntity>>

   init {
      _ingredientEntity.value = _currentIngredient.ingredientEntity

      _ingredientId.value = _ingredientEntity.requireValue().id

      ingredientsOperationsFlow = getOperationsByIngredientId()
   }

   fun getCurrentIngredient() = _ingredientEntity.requireValue()?.toSimpleIngredient()

   private fun getOperationsByIngredientId() = _ingredientId.asFlow()
      .flatMapLatest {
         ingredientsRepository
            .getExpensesAndIncomesOfIngredients(ingredientId = it.toInt())
      }
      .cachedIn(viewModelScope)

   fun showAuthError() {
      _errorEvent.publishEvent()
   }

   private companion object {
      const val INGREDIENT_ID = "ingredient_id"
   }
}