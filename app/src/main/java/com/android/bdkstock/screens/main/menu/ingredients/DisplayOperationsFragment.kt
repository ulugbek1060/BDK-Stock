package com.android.bdkstock.screens.main.menu.ingredients

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.android.bdkstock.R
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
   private fun observeOperation() {
      viewModel.operationEntity.observe(viewLifecycleOwner) {

         binding.tvOperation.text = getStatusText(it.status.toInt())
         binding.backgroundOperation.setBackgroundColor(getStatusBackground(it.status.toInt()))
         binding.tvOperation.setTextColor(getTextColor(it.status.toInt()))

         binding.tvCreator.text = it.creator
         binding.tvIngredient.text = it.name
         binding.tvAmount.text = "${it.amount} ${it.unit}"
         binding.tvCreatedDate.text = it.createdAt
         binding.tvComment.text = it.comment
      }
   }

   private fun getStatusText(status: Int) =
      if (status == OPERATION_INCOME) requireContext().getString(R.string.income)
      else requireContext().getString(R.string.expense)

   private fun getStatusBackground(status: Int) =
      if (status == OPERATION_INCOME) requireContext().getColor(R.color.white_green)
      else requireContext().getColor(R.color.white_red)

   private fun getTextColor(status: Int) =
      if (status == OPERATION_INCOME) requireContext().getColor(R.color.green)
      else requireContext().getColor(R.color.red)

   private companion object {
      const val OPERATION_INCOME = 0
   }
}