package com.android.bdkstock.screens.main.menu.sales

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.android.bdkstock.R
import com.android.bdkstock.databinding.FragmentOrdersFilterBinding
import com.android.model.utils.Const
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class FilterOrdersFragment : BottomSheetDialogFragment(R.layout.fragment_orders_filter) {

   private var _binding: FragmentOrdersFilterBinding? = null
   private val binding: FragmentOrdersFilterBinding get() = _binding!!
   private val viewModel: FilterOrderViewModel by viewModels()

   private val TAG = "FilterOrdersFragment"

   override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?
   ) = FragmentOrdersFilterBinding
      .inflate(layoutInflater, container, false)
      .also { _binding = it }
      .root

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)

      observeFilterData()

      binding.buttonApply.setOnClickListener { applyOnClick() }
      binding.buttonClear.setOnClickListener { clearOnClick() }
      binding.inputLayoutFromDate.setEndIconOnClickListener { fromDateOnClick() }
      binding.inputLayoutToDate.setEndIconOnClickListener { toDateOnClick() }
   }

   private fun clearOnClick() {
      viewModel.clearData()
      back()
   }

   private fun applyOnClick() {
      viewModel.setFilterDate(
         status = 0,
         client = binding.inputQueryClient.text.toString(),
         fromDate = binding.inputFromDate.text.toString(),
         toDate = binding.inputToDate.text.toString(),
         driver = binding.inputQueryDriver.text.toString()
      )
      back()
   }

   private fun back(){
      setFragmentResult(
         ORDER_FILTER_DATA_KEY,
         bundleOf(ORDER_FILTER_DATA_BUNDLE_KEY to viewModel.getFilterData())
      )
      findNavController().popBackStack()
   }

   private fun toDateOnClick() {
      getCalendarDialog(getString(R.string.to_date), binding.inputToDate)
   }

   private fun fromDateOnClick() {
      getCalendarDialog(getString(R.string.from_date), binding.inputFromDate)
   }

   private fun observeFilterData() {
      viewModel.filterData.observe(viewLifecycleOwner) { filter ->

         binding.toggleButton.check(getCheckedId(filter.status))

         binding.inputQueryClient.setText(filter.client)
         binding.inputQueryDriver.setText(filter.driver)
         binding.inputFromDate.setText(filter.fromDate)
         binding.inputToDate.setText(filter.toDate)
      }
   }

   private fun getCheckedId(status: Int?): Int {
      return when (status) {
         1 -> R.id.button_expense
         0 -> R.id.button_income
         else -> R.id.button_all
      }
   }

   @SuppressLint("SimpleDateFormat")
   private fun getCalendarDialog(title: String, inputFromDate: TextInputEditText) {
      val datePicker = MaterialDatePicker
         .Builder
         .datePicker()
         .setTitleText(title)
         .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
         .build()

      datePicker.addOnPositiveButtonClickListener { timeMiles ->
         val formatter = SimpleDateFormat(Const.DATE_TIME_INPUT_FORMAT)
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
      const val ORDER_FILTER_DATA_KEY = "order_filter_data"
      const val ORDER_FILTER_DATA_BUNDLE_KEY = "order_filter_data_bundle"
   }

}

