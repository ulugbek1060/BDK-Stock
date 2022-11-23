package com.android.bdkstock.screens.main.menu.ingredients

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import com.android.bdkstock.R
import com.android.bdkstock.databinding.FragmentFilterOperationsBinding
import com.android.bdkstock.views.findTopNavController
import com.android.model.utils.observeEvent
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import java.io.Serializable

@AndroidEntryPoint
class FilterOperationsFragment :
   BottomSheetDialogFragment(R.layout.fragment_filter_operations) {

   private lateinit var binding: FragmentFilterOperationsBinding
   private val viewModel by viewModels<FilterOperationsViewModel>()
   override fun getTheme(): Int = R.style.CustomBottomSheetDialogTheme

   private  val TAG = "FilterOperationsFragmen"

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)
      binding = FragmentFilterOperationsBinding.bind(view)

      observeFilterData()
      radioButtonListener()
      navigateBack()

      binding.buttonApply.setOnClickListener { filterOnClick() }
      binding.buttonClear.setOnClickListener { clearOnClick() }
   }

   private fun observeFilterData() {
      viewModel.filterData.observe(viewLifecycleOwner) { filter ->
         binding.tvFromDate.text = filter.fromDate
         binding.tvToDate.text = filter.toDate
         binding.radioGroup.check(getCheckedButtonId(filter.status))
      }
   }

   private fun getCheckedButtonId(status: Int?): Int = when (status) {
      1 -> R.id.button_expense
      0 -> R.id.button_income
      else -> R.id.button_all
   }

   private fun navigateBack() {
      viewModel.goBack.observeEvent(viewLifecycleOwner) { filter ->
         findTopNavController()
            .previousBackStackEntry
            ?.savedStateHandle
            ?.set(FILTER_DATA_KEY, filter)
         findTopNavController().popBackStack()
      }
   }

   private fun radioButtonListener() {
      binding.buttonAll.setOnClickListener { viewModel.setStatus(null) }
      binding.buttonIncome.setOnClickListener { viewModel.setStatus(0) }
      binding.buttonExpense.setOnClickListener { viewModel.setStatus(1) }
   }

   private fun filterOnClick() {
      viewModel.setData(
         fromDate = binding.tvFromDate.text.toString(),
         toDate = binding.tvToDate.text.toString(),
      )
   }

   private fun clearOnClick() {
      viewModel.clearFilter()
   }

   override fun onDestroy() {
      super.onDestroy()
      Log.d(TAG, "onDestroy: ")
   }

   private companion object {
      const val FILTER_DATA_KEY = "filter_data"
   }
}