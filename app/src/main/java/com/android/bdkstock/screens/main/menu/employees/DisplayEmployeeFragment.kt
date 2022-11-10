package com.android.bdkstock.screens.main.menu.employees

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.android.bdkstock.R
import com.android.bdkstock.databinding.FragmentDisplayEmployeeBinding
import com.android.bdkstock.screens.main.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DisplayEmployeeFragment : BaseFragment(R.layout.fragment_display_employee) {

   private lateinit var binding: FragmentDisplayEmployeeBinding
   override val viewModel by viewModels<DisplayEmployeeViewModel>()

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)
      binding = FragmentDisplayEmployeeBinding.bind(view)


   }

}