package com.android.bdkstock.screens.main.menu.products

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.android.bdkstock.databinding.FragmentOperationsDetailBinding
import com.android.bdkstock.screens.main.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailOperationsFragment :
   BaseFragment<DetailOperationsViewModel, FragmentOperationsDetailBinding>() {

   override val viewModel by viewModels<DetailOperationsViewModel>()
   override fun getViewBinding() = FragmentOperationsDetailBinding.inflate(layoutInflater)

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)
      observeOperation()
   }

   @SuppressLint("SetTextI18n")
   private fun observeOperation() = viewModel.state.observe(viewLifecycleOwner) { state ->
    binding.tvCreator.setText(state.operation?.creator)
      binding.inputProduct.setText(state.operation?.name)
      binding.inputAmount.setText(state.operation?.amount)
      binding.inputWeight.setText(state.operation?.unit)
      binding.inputComment.setText(state.operation?.comment)
      binding.tvCreatedDate.setText(state.operation?.createdAt)

      binding.tvIndicator.text = state.getStatusText(requireContext())
      binding.tvIndicator.setBackgroundColor(state.getBackgroundColor(requireContext()))
   }
}