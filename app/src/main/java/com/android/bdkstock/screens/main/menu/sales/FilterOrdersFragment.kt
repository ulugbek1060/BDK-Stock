package com.android.bdkstock.screens.main.menu.sales

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.bdkstock.R
import com.android.bdkstock.databinding.FragmentOrdersFilterBinding
import com.android.bdkstock.databinding.FragmentProductsForOrderBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FilterOrdersFragment : BottomSheetDialogFragment(R.layout.fragment_orders_filter) {

   private var _binding: FragmentOrdersFilterBinding? = null
   private val binding: FragmentOrdersFilterBinding get() = _binding!!


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
      binding
   }


   override fun onDestroy() {
      super.onDestroy()
      _binding = null
   }

}