package com.android.bdkstock.screens.main.menu.products

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.android.bdkstock.databinding.FragmentEditProductBinding
import com.android.bdkstock.screens.main.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditProductFragment : BaseFragment<EditProductViewModel, FragmentEditProductBinding>() {
   override val viewModel: EditProductViewModel by viewModels()
   override fun getViewBinding() = FragmentEditProductBinding.inflate(layoutInflater)

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)

   }
}