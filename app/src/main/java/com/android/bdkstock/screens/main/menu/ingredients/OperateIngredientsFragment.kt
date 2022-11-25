package com.android.bdkstock.screens.main.menu.ingredients

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.android.bdkstock.R
import com.android.bdkstock.databinding.FragmentIngredientsTypeBinding
import com.android.bdkstock.databinding.FragmentOperateIngredientsBinding
import com.android.bdkstock.screens.main.base.BaseFragment
import com.android.bdkstock.views.findTopNavController
import com.android.model.repository.ingredients.entity.IngredientEntity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OperateIngredientsFragment :
   BaseFragment<OperateIngredientsViewModel, FragmentOperateIngredientsBinding>() {

   // TODO: 1. initialize title 2.ingredients lis for select

   override val viewModel by viewModels<OperateIngredientsViewModel>()
   override fun getViewBinding() = FragmentOperateIngredientsBinding.inflate(layoutInflater)

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)

      observeState()
      observeIngredient()

      observeNavigateBack()

      binding.buttonSave.setOnClickListener { saveOnClick() }
      binding.inputIngredient.setOnClickListener { ingredientOnClick() }
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

   private fun ingredientOnClick() {

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
         binding.lottiProgress.isVisible = state.isInProgress
      }
   }
}