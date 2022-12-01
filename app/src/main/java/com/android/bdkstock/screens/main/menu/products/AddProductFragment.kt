package com.android.bdkstock.screens.main.menu.products

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.android.bdkstock.databinding.FragmentAddProductBinding
import com.android.bdkstock.screens.main.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddProductFragment : BaseFragment<AddProductViewModel, FragmentAddProductBinding>() {

   override val viewModel by viewModels<AddProductViewModel>()
   override fun getViewBinding() = FragmentAddProductBinding.inflate(layoutInflater)

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)

   }
}