package com.android.bdkstock.screens.main.menu.products

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.android.bdkstock.R
import com.android.bdkstock.screens.main.base.BaseViewModel
import com.android.model.repository.account.AccountRepository
import com.android.model.repository.products.ProductsRepository
import com.android.model.repository.products.entity.ProductOperationEntity
import com.android.model.utils.liveData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailOperationsViewModel @Inject constructor(
   private val productsRepository: ProductsRepository,
   accountRepository: AccountRepository,
   savedStateHandle: SavedStateHandle
) : BaseViewModel(accountRepository) {

   private val _currentOperation = DetailOperationsFragmentArgs
      .fromSavedStateHandle(savedStateHandle)
      .productOperation

   private val _state = MutableLiveData(State())
   val state = _state.liveData()

   init {
      val productOperation = _currentOperation
      _state.value = State(
         operation = productOperation,
         status = productOperation.status
      )
   }

   data class State(
      val operation: ProductOperationEntity? = null,
      val status: Int? = null
   ) {
      fun getStatusText(context: Context) =
         if (status == MANUFACTURED_PRODUCT) context.getString(R.string.income)
         else context.getString(R.string.expense)

      fun getBackgroundColor(context: Context) =
         if (status == MANUFACTURED_PRODUCT) context.getColor(R.color.green)
         else context.getColor(R.color.red)
   }

   private companion object {
      const val MANUFACTURED_PRODUCT = 0
   }
}