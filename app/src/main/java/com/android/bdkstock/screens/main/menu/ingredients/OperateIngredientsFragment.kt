package com.android.bdkstock.screens.main.menu.ingredients

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.android.bdkstock.R
import com.android.bdkstock.databinding.FragmentOperateIngredientsBinding
import com.android.bdkstock.screens.main.base.BaseFragment
import com.android.bdkstock.views.findTopNavController
import com.android.model.utils.Pending
import com.android.model.utils.Success
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class OperateIngredientsFragment :
   BaseFragment<OperateIngredientsViewModel, FragmentOperateIngredientsBinding>() {

   override val viewModel by viewModels<OperateIngredientsViewModel>()
   override fun getViewBinding() = FragmentOperateIngredientsBinding.inflate(layoutInflater)

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)

      observeState()
      observeIngredient()
      observeNavigateBack()

      setupIngredientsList()

      binding.buttonSave.setOnClickListener { saveOnClick() }
   }

   // TODO: setup progressbar
   private fun setupIngredientsList() = lifecycleScope.launchWhenStarted {
      viewModel.getIngredientList.collectLatest { result ->
         when (result) {
            is Success -> {
               val list = result.value
               val adapter =
                  ArrayAdapter(requireContext(), R.layout.spinner_item, list)

               binding.inputIngredient.setAdapter(adapter)

               binding.inputIngredient.setOnItemClickListener { _, _, position, _ ->
                  viewModel.setIngredient(list[position])
               }
            }
            is Pending -> {}
            else -> {}
         }
      }
   }

   private fun observeNavigateBack() {
      viewModel.navigateToDisplay.observe(viewLifecycleOwner) {
         findTopNavController().popBackStack()
      }
   }

   private fun observeIngredient() {
      viewModel.selectedIngredient.observe(viewLifecycleOwner) {
         binding.inputIngredient.setText(it.name)
         binding.inputWeight.setText(it.unit)
      }
   }

   private fun saveOnClick() {
      viewModel.operateIngredient(
         amount = binding.inputAmount.text.toString(),
         comment = binding.inputComment.text.toString()
      )
   }

   private fun observeState() {
      viewModel.state.observe(viewLifecycleOwner) { state ->

         // enable
         binding.inputLayoutIngredient.isEnabled = !state.isInProgress
         binding.inputAmount.isEnabled = !state.isInProgress
         binding.inputLayoutComment.isEnabled = !state.isInProgress

         // error
         binding.inputLayoutIngredient.error = state.getNameErrorMessage(requireContext())
         binding.inputAmount.error = state.getAmountErrorMessage(requireContext())
         binding.inputLayoutComment.error = state.getCommentErrorMessage(requireContext())

         // visibility
         binding.buttonSave.isVisible = !state.isInProgress
         binding.progress.isVisible = state.isInProgress

         binding.tvIndicator.text = state.getStatusText(requireContext())
         binding.tvIndicator.setBackgroundColor(state.getBackgroundColor(requireContext()))
         binding.tvIndicator.setCompoundDrawables(
            null,
            null,
            state.getIndicator(requireContext()),
            null
         )

      }
   }
}