package com.android.bdkstock.screens.main.menu.ingredients

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.android.bdkstock.databinding.FragmentAddIngredientsBinding
import com.android.bdkstock.screens.main.base.BaseFragment
import com.android.model.utils.observeEvent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddIngredientsFragment :
   BaseFragment<AddIngredientsViewModel, FragmentAddIngredientsBinding>() {

   override val viewModel by viewModels<AddIngredientsViewModel>()
   override fun getViewBinding() = FragmentAddIngredientsBinding.inflate(layoutInflater)

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)

      observeState()

      observeNavigation()

      binding.buttonSave.setOnClickListener { saveOnClick() }
   }

   private fun saveOnClick() {
      viewModel.createIngredients(
         name = binding.inputName.text.toString(),
         unit = binding.inputUnit.text.toString()
      )
   }

   private fun observeNavigation() {
      viewModel.navigateToDisplayFrag.observeEvent(viewLifecycleOwner) {

      }
   }

   private fun observeState() {
      viewModel.state.observe(viewLifecycleOwner) { state ->
         binding.inputLayoutName.isEnabled = !state.isInProgress
         binding.inputLayoutUnit.isEnabled = !state.isInProgress

         binding.inputLayoutName.error = state.getNameErrorMessage(requireContext())
         binding.inputLayoutUnit.error = state.getUnitErrorMessage(requireContext())

         binding.buttonSave.isVisible = !state.isInProgress
         binding.progressbar.isVisible = state.isInProgress
      }
   }
}