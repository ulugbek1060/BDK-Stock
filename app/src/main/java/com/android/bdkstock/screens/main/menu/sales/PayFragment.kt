package com.android.bdkstock.screens.main.menu.sales

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.bdkstock.R
import com.android.bdkstock.databinding.FragmentPayBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class PayFragment : BottomSheetDialogFragment(R.layout.fragment_pay) {

   private var _binding: FragmentPayBinding? = null
   private val binding: FragmentPayBinding get() = _binding!!

   override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?
   ) = FragmentPayBinding
      .inflate(inflater, container, false)
      .also { _binding = it }
      .root

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)

   }

   override fun onDestroy() {
      super.onDestroy()
      _binding = null
   }
}