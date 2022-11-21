package com.android.bdkstock.screens.main.menu.ingredients

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.android.bdkstock.R
import com.android.bdkstock.databinding.FragmentImportIngredientBinding
import com.android.bdkstock.screens.main.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ImportIngredientFragment : BaseFragment(R.layout.fragment_import_ingredient) {

   override val viewModel by viewModels<ImportIngredientViewModel>()
   private lateinit var binding: FragmentImportIngredientBinding

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)
      binding = FragmentImportIngredientBinding.bind(view)

   }
}