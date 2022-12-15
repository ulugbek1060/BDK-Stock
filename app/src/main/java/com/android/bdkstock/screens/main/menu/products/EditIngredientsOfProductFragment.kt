package com.android.bdkstock.screens.main.menu.products

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.android.bdkstock.databinding.FragmentEditIngredientsOfProductBinding
import com.android.bdkstock.screens.main.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditIngredientsOfProductFragment :
   BaseFragment<EditIngredientsOfProductViewModel, FragmentEditIngredientsOfProductBinding>() {

   override val viewModel: EditIngredientsOfProductViewModel by viewModels()
   override fun getViewBinding() = FragmentEditIngredientsOfProductBinding.inflate(layoutInflater)

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)

   }
}