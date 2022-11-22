package com.android.bdkstock.screens.main.menu.ingredients

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.android.bdkstock.R
import com.android.bdkstock.databinding.FragmentOperateIngredientsBinding
import com.android.bdkstock.screens.main.base.BaseFragment
import com.android.bdkstock.views.findTopNavController
import com.android.model.repository.ingredients.entity.IngredientEntity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OperateIngredientsFragment : BaseFragment(R.layout.fragment_operate_ingredients) {

   override val viewModel by viewModels<OperateIngredientsViewModel>()
   private lateinit var binding: FragmentOperateIngredientsBinding

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)
      binding = FragmentOperateIngredientsBinding.bind(view)

      observeState()
      observeIngredient()

      fetchResultFromSelection()

      observeNavigateBack()

      binding.buttonSave.setOnClickListener { saveOnClick() }
      binding.inputIngredient.setOnClickListener { ingredientOnClick() }
      binding.back.setOnClickListener { findTopNavController().popBackStack() }
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

   private fun fetchResultFromSelection() {
      findTopNavController().currentBackStackEntry
         ?.savedStateHandle
         ?.getLiveData<IngredientEntity>(IngredientSelectionFragment.SELECTED_INGREDIENT_KEY)
         ?.observe(viewLifecycleOwner) {
            viewModel.setIngredient(it)
         }
   }

   private fun ingredientOnClick() {
      findTopNavController()
         .navigate(R.id.action_operateIngredientsFragment_to_ingredientSelectionFragment)
   }

   private fun saveOnClick() {
      viewModel.operateIngredient(
         amount = binding.inputAmount.text.toString(),
         comment = binding.inputComment.text.toString()
      )
   }

   private fun observeState() {
      viewModel.state.observe(viewLifecycleOwner) { state ->
         // set title
         binding.tvTitle.text = state.getTitle(requireContext())

         // enable
         binding.inputLayoutIngredient.isEnabled = !state.isInProgress
         binding.inputLayoutWeight.isEnabled = !state.isInProgress
         binding.inputLayoutComment.isEnabled = !state.isInProgress

         // error
         binding.inputLayoutIngredient.error = state.getNameErrorMessage(requireContext())
         binding.inputLayoutWeight.error = state.getAmountErrorMessage(requireContext())
         binding.inputLayoutComment.error = state.getCommentErrorMessage(requireContext())

         // visibility
         binding.buttonSave.isVisible = !state.isInProgress
         binding.lottiProgress.isVisible = state.isInProgress
      }
   }
}