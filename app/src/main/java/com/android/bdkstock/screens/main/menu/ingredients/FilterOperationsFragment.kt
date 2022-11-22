package com.android.bdkstock.screens.main.menu.ingredients

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.android.bdkstock.R
import com.android.bdkstock.databinding.FragmentFilterOperationsBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FilterOperationsFragment :
   BottomSheetDialogFragment(R.layout.fragment_filter_operations) {

   private lateinit var binding: FragmentFilterOperationsBinding
   private val viewModel by viewModels<FilterOperationsViewModel>()

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)
      binding = FragmentFilterOperationsBinding.bind(view)



   }
}