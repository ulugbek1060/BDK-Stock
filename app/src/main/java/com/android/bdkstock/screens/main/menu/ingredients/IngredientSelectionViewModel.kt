package com.android.bdkstock.screens.main.menu.ingredients

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.android.bdkstock.screens.main.base.BaseViewModel
import com.android.model.repository.account.AccountRepository
import com.android.model.repository.ingredients.IngredientsRepository
import com.android.model.repository.ingredients.entity.IngredientEntity
import com.android.model.utils.MutableUnitLiveEvent
import com.android.model.utils.liveData
import com.android.model.utils.publishEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class IngredientSelectionViewModel @Inject constructor(
   private val ingredientsRepository: IngredientsRepository,
   accountRepository: AccountRepository,
   savedStateHandle: SavedStateHandle
) : BaseViewModel(accountRepository) {

   private val _errorEvent = MutableUnitLiveEvent()
   val errorEvent = _errorEvent.liveData()

   private val _query = savedStateHandle.getLiveData(INGREDIENTS_QUERY, "")

   @OptIn(ExperimentalCoroutinesApi::class)
   val ingredientsFlow: Flow<PagingData<IngredientEntity>> = _query.asFlow()
      .debounce(500)
      .flatMapLatest {
         ingredientsRepository.getIngredientsList(it)
      }
      .cachedIn(viewModelScope)

   fun setQuery(query: String) {
      _query.value = query
   }

   fun showAuthError() {
      _errorEvent.publishEvent()
   }

   private companion object {
      const val INGREDIENTS_QUERY = "ingredient_query"
   }
}