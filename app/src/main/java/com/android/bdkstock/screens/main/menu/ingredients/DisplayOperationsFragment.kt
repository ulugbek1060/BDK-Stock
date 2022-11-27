package com.android.bdkstock.screens.main.menu.ingredients

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.android.bdkstock.databinding.FragmentDisplayOperationsBinding
import com.android.bdkstock.screens.main.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DisplayOperationsFragment :
   BaseFragment<DisplayOperationsViewModel, FragmentDisplayOperationsBinding>() {

   override fun getViewBinding() = FragmentDisplayOperationsBinding.inflate(layoutInflater)
   override val viewModel by viewModels<DisplayOperationsViewModel>()

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)

      observeOperation()
   }

   @SuppressLint("SetTextI18n")
   private fun observeOperation() = viewModel.state.observe(viewLifecycleOwner) { state ->
      binding.tvCreator.setText(state.ingredient?.creator)
      binding.inputIngredient.setText(state.ingredient?.name)
      binding.inputAmount.setText(state.ingredient?.amount)
      binding.inputWeight.setText(state.ingredient?.unit)
      binding.inputComment.setText(state.ingredient?.comment)
      binding.tvCreatedDate.setText(state.ingredient?.createdAt)

      binding.tvIndicator.text = state.getStatusText(requireContext())
      binding.tvIndicator.setBackgroundColor(state.getBackgroundColor(requireContext()))
   }
}