package com.android.bdkstock.screens.main.menu.products

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.android.bdkstock.databinding.FragmentDisplayProductOperationBinding
import com.android.bdkstock.screens.main.base.BaseFragment
import com.android.bdkstock.views.getActionBar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DisplayProductOperationFragment :
   BaseFragment<DisplayProductOperationViewModel, FragmentDisplayProductOperationBinding>() {

   override val viewModel by viewModels<DisplayProductOperationViewModel>()
   override fun getViewBinding() = FragmentDisplayProductOperationBinding.inflate(layoutInflater)

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)
      observeOperation()
   }

   @SuppressLint("SetTextI18n")
   private fun observeOperation() = viewModel.state.observe(viewLifecycleOwner) { state ->

      val operation = state.operation

      getActionBar()?.title = state.getStatusText(requireContext())
      with(binding) {
         tvEmployee.text = operation?.creator
         tvProducts.text = operation?.name
         tvAmount.text = operation?.amount
         tvDate.text = operation?.createdAt
         inputComment.setText(operation?.comment)
      }
   }
}