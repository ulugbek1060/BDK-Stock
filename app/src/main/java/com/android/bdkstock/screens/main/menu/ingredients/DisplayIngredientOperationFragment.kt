package com.android.bdkstock.screens.main.menu.ingredients

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.android.bdkstock.databinding.FragmentDisplayIngredientOperationBinding
import com.android.bdkstock.screens.main.base.BaseFragment
import com.android.bdkstock.views.getActionBar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DisplayIngredientOperationFragment :
   BaseFragment<DisplayIngredientOperationViewModel, FragmentDisplayIngredientOperationBinding>() {

   override fun getViewBinding() =
      FragmentDisplayIngredientOperationBinding.inflate(layoutInflater)

   override val viewModel by viewModels<DisplayIngredientOperationViewModel>()

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)

      observeOperation()
   }

   @SuppressLint("SetTextI18n")
   private fun observeOperation() = viewModel.state.observe(viewLifecycleOwner) { state ->
      val ingredient = state.ingredient
      getActionBar()?.title = state.getStatusText(requireContext())
      with(binding) {
         tvEmployee.text = ingredient?.creator
         tvIngredients.text = ingredient?.name
         tvAmount.text = "${ingredient?.amount} ${ingredient?.unit}"
         tvDate.text = ingredient?.createdAt
         inputComment.setText(state.ingredient?.comment)
      }
   }
}