package com.android.bdkstock.screens.main.menu.ingredients

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.android.bdkstock.screens.main.base.BaseViewModel
import com.android.model.repository.account.AccountRepository
import com.android.model.repository.ingredients.IngredientsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class IngredientsViewModel @Inject constructor(
   private val ingredientsRepository: IngredientsRepository,
   accountRepository: AccountRepository,
   savedStateHandle: SavedStateHandle
) : BaseViewModel(accountRepository) {

   val ingredientsExAndInFlow = ingredientsRepository
      .getExpensesAndIncomesOfIngredients()
      .cachedIn(viewModelScope)

}