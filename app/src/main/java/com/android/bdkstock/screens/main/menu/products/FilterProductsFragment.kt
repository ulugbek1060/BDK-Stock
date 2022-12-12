package com.android.bdkstock.screens.main.menu.products

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import com.android.bdkstock.R
import com.android.bdkstock.databinding.FragmentFilterProductsBinding
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

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)
      _binding = FragmentFilterProductsBinding.bind(view)



   }


   // -- calendar dialog

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

   private fun getCheckId(state: Int?) = when (state) {
      1 -> R.id.button_expense
      0 -> R.id.button_income
      else -> R.id.button_all
   }

   override fun onDestroy() {
      super.onDestroy()
      _binding = null
   }

}