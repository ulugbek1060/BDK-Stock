package com.android.bdkstock.screens.main.menu.products

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.android.bdkstock.R
import com.android.bdkstock.databinding.FragmentFilterProductsBinding
import com.android.model.utils.observeEvent
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class FilterProductsFragment : BottomSheetDialogFragment(R.layout.fragment_filter_products) {

   private var _binding: FragmentFilterProductsBinding? = null
   private val binding: FragmentFilterProductsBinding get() = _binding!!
   private val viewModel: FilterProductsViewModel by viewModels()

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)
      _binding = FragmentFilterProductsBinding.bind(view)

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
      getCalendarDialog(getString(R.string.to_date), binding.inputToDate)
   }

   private fun fromDateOnClick() {
      getCalendarDialog(getString(R.string.from_date), binding.inputFromDate)
   }

   @SuppressLint("SimpleDateFormat")
   private fun getCalendarDialog(title: String, inputFromDate: TextInputEditText) {
      val datePicker = MaterialDatePicker.Builder.datePicker()
         .setTitleText(title)
         .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
         .build()
      datePicker.addOnPositiveButtonClickListener { timeMiles ->
         val formatter = SimpleDateFormat("dd/MM/yyyy")
         val date = formatter.format(Date(timeMiles))
         inputFromDate.setText(date)
      }
      datePicker.addOnNegativeButtonClickListener {
         inputFromDate.setText("")
         datePicker.dismiss()
      }
      datePicker.show(parentFragmentManager, "tag")
   }

   override fun onDestroy() {
      super.onDestroy()
      _binding = null
   }

   private companion object {
      const val PRODUCTS_FILTER_KEY = "ingredients_filter"
      const val PRODUCTS_FILTER_BUNDLE_KEY = "ingredients_filter_bundle"
      const val OPERATION_INCOME = 0
      const val OPERATION_EXPENSE = 1
   }
}