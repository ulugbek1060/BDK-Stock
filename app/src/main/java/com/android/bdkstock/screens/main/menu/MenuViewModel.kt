package com.android.bdkstock.screens.main.menu

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
         add(MenuItem(1, R.drawable.ic_employees, "Employees", R.id.employeesFragment))
         add(MenuItem(1, R.drawable.ic_sales, "Sales", R.id.salesFragment))
         add(MenuItem(1, R.drawable.ic_financial, "Financial", R.id.financialFragment))
         add(MenuItem(1, R.drawable.ic_materials, "Ingredients", R.id.ingredientsFragment))
         add(MenuItem(1, R.drawable.ic_products, "Products", R.id.productsFragment))
         add(MenuItem(1, R.drawable.ic_clients, "Clients", R.id.clientsFragment))
         add(MenuItem(1, R.drawable.ic_drivers, "Drivers", R.id.driversFragment))
      }

   init {
      _menuItems.postValue(this.listMenus)
   }
}