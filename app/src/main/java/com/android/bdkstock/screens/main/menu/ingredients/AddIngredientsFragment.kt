package com.android.bdkstock.screens.main.menu.ingredients

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.android.bdkstock.R
import com.android.bdkstock.databinding.FragmentAddIngredientsBinding
import com.android.bdkstock.screens.main.base.BaseFragment
import com.android.bdkstock.views.findTopNavController
import com.android.model.utils.observeEvent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddIngredientsFragment : BaseFragment(R.layout.fragment_add_ingredients) {

   override val viewModel by viewModels<AddIngredientsViewModel>()
   private lateinit var binding: FragmentAddIngredientsBinding

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)
      binding = FragmentAddIngredientsBinding.bind(view)

      observeState()

      observeNavigation()

      binding.buttonSave.setOnClickListener { saveOnClick() }
      binding.back.setOnClickListener { findTopNavController().popBackStack() }
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
         // enable
         binding.inputLayoutName.isEnabled = !state.isInProgress
         binding.inputLayoutUnit.isEnabled = !state.isInProgress

         // error
         binding.inputLayoutName.error = state.getNameErrorMessage(requireContext())
         binding.inputLayoutUnit.error = state.getUnitErrorMessage(requireContext())

         // visibility
         binding.buttonSave.isVisible = !state.isInProgress
         binding.lottiProgress.isVisible = state.isInProgress
      }
   }
}