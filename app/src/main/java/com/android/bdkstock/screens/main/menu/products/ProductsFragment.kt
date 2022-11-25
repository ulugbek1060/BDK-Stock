package com.android.bdkstock.screens.main.menu.products

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.android.bdkstock.databinding.FragmentProductsBinding
import com.android.bdkstock.screens.main.base.BaseFragment
import com.android.bdkstock.screens.main.base.BaseViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductsFragment : BaseFragment<ProductsViewModel, FragmentProductsBinding>() {

   override val viewModel:ProductsViewModel by viewModels()
   override fun getViewBinding() = FragmentProductsBinding.inflate(layoutInflater)

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)

   }


}