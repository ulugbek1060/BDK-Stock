package com.android.bdkstock.screens.main.menu.products

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.android.bdkstock.R
import com.android.bdkstock.screens.main.base.BaseViewModel
import com.android.model.repository.account.AccountRepository
import com.android.model.repository.products.ProductsRepository
import com.android.model.repository.products.entity.ProductSelectionItem
import com.android.model.utils.*
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProductOperationsViewModel @Inject constructor(
   private val productsRepository: ProductsRepository,
   accountRepository: AccountRepository,
   savedStateHandle: SavedStateHandle
) : BaseViewModel(accountRepository) {

   private val _defaultProduct = ProductOperationsFragmentArgs
      .fromSavedStateHandle(savedStateHandle)
      .defaultProduct

   private val operationStatus = ProductOperationsFragmentArgs
      .fromSavedStateHandle(savedStateHandle)
      .operationsStatus

   val productsFlow = productsRepository
      .getProductsForSelect(_defaultProduct != null)
      .handleException()

   private val _goBack = MutableLiveEvent<String>()
   val goBack = _goBack.asLiveData()

   private val _state = MutableLiveData(State())
   val state = _state.asLiveData()

   private var selectedProductId: Long? = null

   init {
      _state.value = _state.requireValue().copy(
         status = operationStatus,
         defaultProduct = _defaultProduct
      )

      selectedProductId = _defaultProduct?.id
   }

   fun manufacture(amount: String, comment: String) = viewModelScope.safeLaunch {
      showProgress()
      try {
         val message = if (OPERATION_MANUFACTURE == operationStatus)
            productsRepository.manufactureProduct(
               productId = selectedProductId,
               amount = amount,
               comment = comment
            )
         else productsRepository.exportProduct(
            productId = selectedProductId,
            amount = amount,
            comment = comment
         )
         successMessage(message)
      } catch (e: EmptyFieldException) {
         showEmptyFields(e)
      } finally {
         hideProgress()
      }
   }

   private fun successMessage(message: String) = _goBack.publishEvent(message)

   private fun showEmptyFields(e: EmptyFieldException) {
      _state.value = _state.requireValue().copy(
         emptyProduct = e.field == Field.PRODUCT,
         emptyAmount = e.field == Field.AMOUNT,
         emptyComment = e.field == Field.COMMENT
      )
   }

   fun setProduct(productSelectionItem: ProductSelectionItem) {
      selectedProductId = productSelectionItem.id
   }

   fun clearProduct() {
      selectedProductId = null
   }

   fun showProgress() {
      _state.value = _state.requireValue().copy(
         isInProgress = true
      )
   }

   fun hideProgress() {
      _state.value = _state.requireValue().copy(
         isInProgress = false
      )
   }

   data class State(
      val status: Int? = null,
      val emptyProduct: Boolean = false,
      val emptyAmount: Boolean = false,
      val emptyComment: Boolean = false,
      val isInProgress: Boolean = false,
      val defaultProduct: ProductSelectionItem? = null
   ) {

      fun getStatusText(context: Context) =
         if (status == OPERATION_MANUFACTURE) context.getString(R.string.income)
         else context.getString(R.string.expense)

      fun getProductErrorMessage(context: Context) =
         if (emptyProduct) context.getString(R.string.error_empty_product)
         else null

      fun getAmountErrorMessage(context: Context) =
         if (emptyAmount) context.getString(R.string.error_empty_amount)
         else null

      fun getCommentErrorMessage(context: Context) =
         if (emptyComment) context.getString(R.string.error_empty_comment)
         else null

      fun getBackgroundColor(context: Context) =
         if (status == OPERATION_MANUFACTURE) context.getColor(R.color.green)
         else context.getColor(R.color.red)

   }

   private companion object {
      const val OPERATION_MANUFACTURE = 0
   }
}