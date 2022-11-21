package com.android.bdkstock.screens.main.menu.actions

import androidx.lifecycle.MutableLiveData
import com.android.bdkstock.R
import com.android.bdkstock.screens.main.base.BaseViewModel
import com.android.model.repository.account.AccountRepository
import com.android.model.utils.liveData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MenuViewModel @Inject constructor(
   private val accountRepository: AccountRepository
) : BaseViewModel(accountRepository) {

   private val _menuItems = MutableLiveData<ArrayList<MenuItem>>()
   val menuItems = _menuItems.liveData()

   private val listMenus = ArrayList<MenuItem>()
      .apply {
         add(MenuItem(1, R.drawable.ic_employees_bulk, "Employees", R.id.employeesFragment))
         add(MenuItem(2, R.drawable.ic_sales_bulk, "Sales", R.id.salesFragment))
         add(MenuItem(3, R.drawable.ic_ingredients_bulk, "Ingredients", R.id.ingredientsOperationsFragment))
         add(MenuItem(4, R.drawable.ic_products_bulk, "Products", R.id.productsFragment))
         add(MenuItem(5, R.drawable.ic_clients_bulk, "Clients", R.id.clientsFragment))
         add(MenuItem(6, R.drawable.ic_drivers_bulk, "Drivers", R.id.driversFragment))
      }

   init {
      _menuItems.postValue(this.listMenus)
   }
}