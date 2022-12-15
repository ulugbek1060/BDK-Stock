package com.android.bdkstock.screens.main.menu.sales

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.android.bdkstock.R
import com.android.bdkstock.databinding.FragmentProductsForOrderBinding
import com.android.bdkstock.views.findTopNavController
import com.android.model.utils.Pending
import com.android.model.utils.Success
import com.android.model.utils.gone
import com.android.model.utils.visible
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ProductsForOrderFragment : BottomSheetDialogFragment() {

   private var _binding: FragmentProductsForOrderBinding? = null
   private val binding: FragmentProductsForOrderBinding get() = _binding!!
   private val viewModel: ProductsForOrderViewModel by viewModels()

   override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?
   ) = FragmentProductsForOrderBinding
      .inflate(layoutInflater, container, false)
      .also { _binding = it }
      .root

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)

      setupAutoComplete()
      setupAmountCalculation()

      binding.buttonSave.setOnClickListener { saveOnClick() }
   }

   private fun setupAmountCalculation() {
      binding.inputAmount.doAfterTextChanged {
         val product = viewModel.getProduct() ?: return@doAfterTextChanged

         if (it.toString().isNotBlank()) product.amount = it.toString()
         else product.amount = "0"

         binding.tvPriceTotal.text = product.calculate()
      }
   }

   private fun saveOnClick() {
      val amount = binding.inputAmount.text.toString()

      if (amount.isBlank()) return

      val product = viewModel.getProduct() ?: return
      product.amount = amount

      setFragmentResult(PRODUCT_SELECTION_KEY, bundleOf(PRODUCT_BUNDLE_KEY to product))

      findTopNavController().popBackStack()
   }

   private fun setupAutoComplete() = lifecycleScope.launchWhenStarted {
      viewModel.productsResult.collectLatest { list ->
         binding.progressbar.gone()
         binding.mainContainer.visible()
         val adapter = ArrayAdapter(requireContext(), R.layout.spinner_item, list)
         binding.autoCompleteProduct.setAdapter(adapter)

         binding.autoCompleteProduct.setOnItemClickListener { _, _, position, _ ->
            val product = list[position]
            viewModel.setProduct(product)
            binding.inputWeight.setText(product.unit)
            binding.tvProduct.text = product.name
            binding.tvPrice.text = product.price
         }
      }
   }

   override fun onDestroy() {
      super.onDestroy()
      _binding = null
   }

   private companion object {
      const val PRODUCT_SELECTION_KEY = "chosen_product"
      const val PRODUCT_BUNDLE_KEY = "bundle_product"
   }
}