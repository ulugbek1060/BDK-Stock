package com.android.bdkstock.screens.main.menu.products

import com.android.bdkstock.screens.main.base.BaseViewModel
import com.android.model.repository.account.AccountRepository
import com.android.model.repository.products.ProductsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EditIngredientsOfProductViewModel @Inject constructor(
   private val productsRepository: ProductsRepository,
   accountRepository: AccountRepository
) : BaseViewModel(accountRepository) {


}