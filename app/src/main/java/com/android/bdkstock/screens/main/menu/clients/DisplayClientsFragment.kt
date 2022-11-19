package com.android.bdkstock.screens.main.menu.clients

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.android.bdkstock.R
import com.android.bdkstock.databinding.FragmentDisplayClientsBinding
import com.android.bdkstock.screens.main.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DisplayClientsFragment : BaseFragment(R.layout.fragment_display_clients) {

   override val viewModel by viewModels<DisplayClientsViewModel>()
   private lateinit var binding: FragmentDisplayClientsBinding

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)
      binding = FragmentDisplayClientsBinding.bind(view)


   }
}