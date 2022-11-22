package com.android.bdkstock.screens.main.menu.ingredients

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.android.bdkstock.R
import com.android.bdkstock.databinding.FragmentDisplayOperationsBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DisplayOperationsFragment :
   BottomSheetDialogFragment(R.layout.fragment_display_operations) {

   private lateinit var binding: FragmentDisplayOperationsBinding
   private val viewModel by viewModels<DisplayOperationsViewModel>()

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)
      binding = FragmentDisplayOperationsBinding.bind(view)

   }
}