package com.android.bdkstock.screens.main.menu

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.android.bdkstock.R
import com.android.bdkstock.databinding.FragmentMenuBinding
import com.android.bdkstock.screens.main.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MenuFragment : BaseFragment(R.layout.fragment_menu) {

   private lateinit var binding: FragmentMenuBinding
   override val viewModel by viewModels<MenuViewModel>()

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)
      binding = FragmentMenuBinding.bind(view)

   }
}

