package com.android.bdkstock.screens.main.menu.sales

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.android.bdkstock.R
import com.android.bdkstock.databinding.FragmentOrdersFilterBinding
import com.android.bdkstock.screens.main.base.BaseBottomSheetDialogFragment
import com.android.bdkstock.views.getDate
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FilterOrdersFragment : BaseBottomSheetDialogFragment<FragmentOrdersFilterBinding>() {

   override fun getViewBinding() = FragmentOrdersFilterBinding.inflate(layoutInflater)
   private val viewModel: FilterOrderViewModel by viewModels()

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)

      observeFilterData()

      binding.buttonApply.setOnClickListener { applyOnClick(view) }
      binding.buttonClear.setOnClickListener { clearOnClick() }
      binding.inputLayoutFromDate.setEndIconOnClickListener { fromDateOnClick() }
      binding.inputLayoutToDate.setEndIconOnClickListener { toDateOnClick() }
   }

   private fun clearOnClick() {
      viewModel.clearData()
      back()
   }

   private fun applyOnClick(view: View) {
      viewModel.setFilterDate(
         status = getButtonTag(view, binding.toggleButton.checkedButtonId),
         client = binding.inputQueryClient.text.toString(),
         fromDate = binding.inputFromDate.text.toString(),
         toDate = binding.inputToDate.text.toString(),
         driver = binding.inputQueryDriver.text.toString()
      )
      back()
   }

   private fun getButtonTag(view: View, id: Int): Int {
      return view.findViewById<Button>(id).tag.toString().toInt()
   }

   private fun back() {
      setFragmentResult(
         ORDER_FILTER_DATA_KEY, bundleOf(ORDER_FILTER_DATA_BUNDLE_KEY to viewModel.getFilterData())
      )
      findNavController().popBackStack()
   }

   private fun toDateOnClick() {
      getDate(getString(R.string.to_date), binding.inputToDate)
   }

   private fun fromDateOnClick() {
      getDate(getString(R.string.from_date), binding.inputFromDate)
   }

   private fun observeFilterData() {
      viewModel.filterData.observe(viewLifecycleOwner) { filter ->
         binding.toggleButton.check(filter.getSelectedStatusId())
         binding.inputQueryClient.setText(filter.client)
         binding.inputQueryDriver.setText(filter.driver)
         binding.inputFromDate.setText(filter.fromDate)
         binding.inputToDate.setText(filter.toDate)
      }
   }

   private companion object {
      const val ORDER_FILTER_DATA_KEY = "order_filter_data"
      const val ORDER_FILTER_DATA_BUNDLE_KEY = "order_filter_data_bundle"
   }
}

