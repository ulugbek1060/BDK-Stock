package com.android.bdkstock.screens.main.menu.sales

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.android.bdkstock.databinding.FragmentOrderBinding
import com.android.bdkstock.screens.main.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrderFragment : BaseFragment<OrderViewModel, FragmentOrderBinding>() {

   override val viewModel by viewModels<OrderViewModel>()
   override fun getViewBinding() = FragmentOrderBinding.inflate(layoutInflater)

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)

   }
}