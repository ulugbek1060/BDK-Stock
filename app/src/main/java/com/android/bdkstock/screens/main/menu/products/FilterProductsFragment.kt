package com.android.bdkstock.screens.main.menu.products

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.android.bdkstock.R
import com.android.bdkstock.databinding.FragmentFilterProductsBinding
import com.android.bdkstock.screens.main.base.BaseBottomSheetDialogFragment
import com.android.bdkstock.views.getDate
import com.android.model.utils.observeEvent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FilterProductsFragment :
   BaseBottomSheetDialogFragment<FragmentFilterProductsBinding>() {

   override fun getViewBinding() = FragmentFilterProductsBinding.inflate(layoutInflater)
   private val viewModel: FilterProductsViewModel by viewModels()

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)

      observeFilterData()
      observeMoveEvent()

      binding.buttonAll.setOnClickListener { viewModel.setStatus(null) }
      binding.buttonIncome.setOnClickListener { viewModel.setStatus(OPERATION_INCOME) }
      binding.buttonExpense.setOnClickListener { viewModel.setStatus(OPERATION_EXPENSE) }

      binding.inputLayoutFromDate.setEndIconOnClickListener { fromDateOnClick() }
      binding.inputLayoutToDate.setEndIconOnClickListener { toDateOnClick() }

      binding.buttonApply.setOnClickListener {
         viewModel.acceptFilterData(
            query = binding.inputQuery.text.toString(),
            fromDate = binding.inputFromDate.text.toString(),
            toDate = binding.inputToDate.text.toString()
         )
      }
      binding.buttonClear.setOnClickListener { viewModel.cancelFilterData() }

   }

   private fun observeMoveEvent() {
      viewModel.moveBack.observeEvent(viewLifecycleOwner) {
         setFragmentResult(PRODUCTS_FILTER_KEY, bundleOf(PRODUCTS_FILTER_BUNDLE_KEY to it))
         findNavController().popBackStack()
      }
   }

   private fun observeFilterData() {
      viewModel.filter.observe(viewLifecycleOwner) { filterData ->
         with(binding) {
            inputQuery.setText(filterData.query)
            toggleButton.check(filterData.getSelectedStatusId())
            inputFromDate.setText(filterData.fromDate)
            inputToDate.setText(filterData.toDate)
         }
      }
   }

   private fun toDateOnClick() {
      getDate(getString(R.string.to_date), binding.inputToDate)
   }

   private fun fromDateOnClick() {
      getDate(getString(R.string.from_date), binding.inputFromDate)
   }


   private companion object {
      const val PRODUCTS_FILTER_KEY = "ingredients_filter"
      const val PRODUCTS_FILTER_BUNDLE_KEY = "ingredients_filter_bundle"
      const val OPERATION_INCOME = 0
      const val OPERATION_EXPENSE = 1
   }
}