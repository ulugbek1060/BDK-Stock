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
import com.android.bdkstock.views.getActionBar
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
      observeGoBackEvent()

      binding.buttonSave.setOnClickListener { saveOnClick() }
   }

   private fun observeGoBackEvent() {
      viewModel.goBack.observeEvent(viewLifecycleOwner) { message ->
         findTopNavController().popBackStack()
         findTopNavController().navigate(
            R.id.successMessageFragment,
            bundleOf("success_message" to message)
         )
      }
   }

   private fun saveOnClick() {
      viewModel.manufacture(
         amount = binding.inputAmount.text.toString(),
         comment = binding.inputComment.text.toString()
      )
   }

   private fun initProductsList() = lifecycleScope.launchWhenStarted {
      viewModel.productsFlow.collectLatest { list ->
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
   }

   private fun observeState() {
      viewModel.state.observe(viewLifecycleOwner) { state ->

         getActionBar()?.title = state.getStatusText(requireContext())
         with(binding) {
            inputLayoutProduct.isEnabled = !state.isInProgress
            inputLayoutAmount.isEnabled = !state.isInProgress
            inputLayoutComment.isEnabled = !state.isInProgress

            inputProduct.error = state.getProductErrorMessage(requireContext())
            inputAmount.error = state.getAmountErrorMessage(requireContext())
            inputComment.error = state.getCommentErrorMessage(requireContext())

            buttonSave.isVisible = !state.isInProgress
            progress.isVisible = state.isInProgress

            inputProduct.setText(state.defaultProduct?.name)
            inputWeight.setText(state.defaultProduct?.unit)
         }
      }
   }
}