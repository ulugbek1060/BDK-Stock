package com.android.bdkstock.screens.main.menu.sales

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.android.bdkstock.R
import com.android.bdkstock.screens.main.base.BaseViewModel
import com.android.model.repository.account.AccountRepository
import com.android.model.repository.products.entity.ProductSelectionItem
import com.android.model.repository.sales.SalesRepository
import com.android.model.repository.sales.entity.OrderEntity
import com.android.model.repository.sales.entity.SimpleProduct
import com.android.model.utils.*
import dagger.hilt.android.lifecycle.HiltViewModel
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class NewOrderViewModel @Inject constructor(
   private val salesRepository: SalesRepository,
   accountRepository: AccountRepository
) : BaseViewModel(accountRepository) {

   private var clientId: Long? = null
   private var driverId: Long? = null

   private val _goBack = MutableLiveEvent<OrderEntity>()
   val goBack = _goBack.liveData()

   private val _state = MutableLiveData(State())
   val state = _state.liveData()

   private val _productsList = MutableLiveData<MutableList<ProductSelectionItem>>(mutableListOf())
   val productsList = _productsList.liveData()

   fun createOrder() = viewModelScope.safeLaunch {
      showProgress()
      try {
         val orderEntity = salesRepository.createOrder(
            clientId = clientId,
            driverId = driverId,
            products = getProducts()
         )
         successAndGoBack(orderEntity)
      } catch (e: EmptyFieldException) {
         showEmptyFields(e)
      } finally {
         hideProgress()
      }
   }

   private fun successAndGoBack(orderEntity: OrderEntity) {
      _goBack.publishEvent(orderEntity)
   }

   private fun showEmptyFields(e: EmptyFieldException) {
      _state.value = _state.requireValue().copy(
         isEmptyClient = e.field == Field.EMPTY_CLIENT,
         isEmptyDriver = e.field == Field.EMPTY_DRIVER,
         isEmptyProduct = e.field == Field.PRODUCT
      )
   }

   fun setClientId(chosenClientId: Long) {
      clientId = chosenClientId
   }

   fun setDriverId(chosenDriverId: Long) {
      driverId = chosenDriverId
   }

   private fun calculateTotalSum() {
      _state.value = _state.requireValue().copy(
         totalSum = getTotalSum()
      )
   }

   fun setProduct(product: ProductSelectionItem?) {
      if (product == null) return
      val list = _productsList.value ?: arrayListOf()
      list.add(product)
      _productsList.value = list

      calculateTotalSum()
   }

   fun removeProduct(product: ProductSelectionItem) {
      val list = _productsList.value ?: arrayListOf()
      list.remove(product)
      _productsList.value = list

      calculateTotalSum()
   }

   private fun getProducts() = _productsList.requireValue().map {
      SimpleProduct(
         id = it.id,
         amount = it.amount
      )
   }

   private fun getTotalSum(): String {
      var result = "0"
      try {
         _productsList.requireValue().map {
            result = BigDecimal(result).plus(BigDecimal(it.calculate())).toString()
         }
      } catch (e: Exception) {
         result = "invalid value!!"
      }
      return result
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

   fun disableClientErrorMessage(clientState: Boolean) {
      if (_state.requireValue().isEmptyClient)
         _state.value = _state.requireValue().copy(
            isEmptyClient = clientState
         )
   }

   fun disableDriverErrorMessage(driverState: Boolean) {
      if (_state.requireValue().isEmptyDriver)
         _state.value = _state.requireValue().copy(
            isEmptyDriver = driverState
         )
   }

   data class State(
      val isInProgress: Boolean = false,
      val isEmptyClient: Boolean = false,
      val isEmptyDriver: Boolean = false,
      val isEmptyProduct: Boolean = false,
      val totalSum: String = "0"
   ) {

      fun getClientErrorMessage(context: Context) =
         if (isEmptyClient) context.getString(R.string.error_empty_client)
         else null

      fun getDriverErrorMessage(context: Context) =
         if (isEmptyDriver) context.getString(R.string.error_empty_driver)
         else null

      fun getProductErrorMessage(context: Context) =
         if (isEmptyProduct) context.getString(R.string.error_empty_product)
         else null
   }
}