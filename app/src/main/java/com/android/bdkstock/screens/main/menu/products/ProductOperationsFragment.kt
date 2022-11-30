package com.android.bdkstock.screens.main.menu.products

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.android.bdkstock.R
import com.android.bdkstock.databinding.FragmentProductOperationsBinding
import com.android.bdkstock.screens.main.base.BaseFragment
import com.android.bdkstock.views.findTopNavController
import com.android.model.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ProductOperationsFragment :
   BaseFragment<ProductOperationsViewModel, FragmentProductOperationsBinding>() {

   override val viewModel by viewModels<ProductOperationsViewModel>()
   override fun getViewBinding() = FragmentProductOperationsBinding.inflate(layoutInflater)

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)

      observeState()
      initProductsList()
      observeGoBack()
      binding.buttonSave.setOnClickListener { saveOnClick() }
   }

   private fun observeGoBack() {
      viewModel.goBack.observeEvent(viewLifecycleOwner) { message ->
         findTopNavController().popBackStack()
         findTopNavController().navigate(R.id.successMessageFragment, bundleOf("success_message" to message))
      }
   }

   private fun saveOnClick() {
      viewModel.manufacture(
         amount = binding.inputAmount.text.toString(),
         comment = binding.inputComment.text.toString()
      )
   }

   private fun initProductsList() = lifecycleScope.launchWhenStarted {
      viewModel.productsFlow.collectLatest { result ->
         when (result) {
            is Success -> {
               val list = result.value

               binding.mainContainer.visible()
               binding.initialProgressBar.gone()

               val adapter = ArrayAdapter(requireContext(), R.layout.spinner_item, list)
               binding.inputProduct.setAdapter(adapter)

               binding.inputProduct.setOnItemClickListener { _, _, position, _ ->
                  viewModel.setProduct(list[position])
                  binding.inputWeight.setText(list[position].unit)
               }

               // lister product input
               binding.inputProduct.doAfterTextChanged {
                  if (it.toString().isBlank()) viewModel.clearProduct()
               }
            }
            is Pending -> {
               binding.mainContainer.gone()
               binding.initialProgressBar.visible()
            }
            else -> {}
         }
      }
   }

   private fun observeState() {
      viewModel.state.observe(viewLifecycleOwner) { state ->
         binding.inputLayoutProduct.isEnabled = !state.isInProgress
         binding.inputLayoutAmount.isEnabled = !state.isInProgress
         binding.inputLayoutComment.isEnabled = !state.isInProgress

         binding.inputProduct.error = state.getProductErrorMessage(requireContext())
         binding.inputAmount.error = state.getAmountErrorMessage(requireContext())
         binding.inputComment.error = state.getCommentErrorMessage(requireContext())

         binding.buttonSave.isVisible = !state.isInProgress
         binding.progress.isVisible = state.isInProgress

         binding.tvIndicator.text = state.getStatusText(requireContext())
         binding.tvIndicator.setBackgroundColor(state.getBackgroundColor(requireContext()))
      }
   }
}