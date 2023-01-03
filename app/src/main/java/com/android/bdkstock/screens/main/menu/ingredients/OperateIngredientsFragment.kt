package com.android.bdkstock.screens.main.menu.ingredients

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.android.bdkstock.R
import com.android.bdkstock.databinding.FragmentOperateIngredientsBinding
import com.android.bdkstock.screens.main.base.BaseFragment
import com.android.bdkstock.views.findTopNavController
import com.android.bdkstock.views.getActionBar
import com.android.model.utils.*
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

   private fun setupIngredientsList() = lifecycleScope.launchWhenStarted {
      viewModel.getIngredientList.collectLatest { result ->
         when (result) {
            is Results.Success -> {
               binding.mainContainer.visible()
               binding.progressbar.gone()
               val list = result.value
               val adapter = ArrayAdapter(requireContext(), R.layout.spinner_item, list)
               binding.inputIngredient.setAdapter(adapter)

               binding.inputIngredient.setOnItemClickListener { _, _, position, _ ->
                  viewModel.setIngredient(list[position])
               }
            }
            is Results.Pending -> {
               binding.mainContainer.gone()
               binding.progressbar.visible()
            }
            else -> {
               binding.mainContainer.visible()
               binding.progressbar.gone()
            }
         }
      }
   }

   private fun observeNavigateBack() {
      viewModel.navigateToDisplay.observeEvent(viewLifecycleOwner) {
         findTopNavController().popBackStack()
         findTopNavController().navigate(
            R.id.successMessageFragment,
            bundleOf(SUCCESS_MESSAGE_BUNDLE_KEY to it)
         )
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

         getActionBar()?.title = state.getStatusText(requireContext())

         with(binding) {
            inputLayoutIngredient.isEnabled = !state.isInProgress
            inputAmount.isEnabled = !state.isInProgress
            inputLayoutComment.isEnabled = !state.isInProgress

            inputLayoutIngredient.error = state.getNameErrorMessage(requireContext())
            inputAmount.error = state.getAmountErrorMessage(requireContext())
            inputLayoutComment.error = state.getCommentErrorMessage(requireContext())

            buttonSave.isVisible = !state.isInProgress
            progressbar.isVisible = state.isInProgress

            //set default ingredient
            inputIngredient.isFocusable = state.ingredientChangeability()
            inputIngredient.isCursorVisible = state.ingredientChangeability()

            inputIngredient.setText(state.defaultIngredient?.name)
            inputWeight.setText(state.defaultIngredient?.unit)
         }
      }
   }

   private companion object {
      const val SUCCESS_MESSAGE_BUNDLE_KEY = "success_message"
   }
}