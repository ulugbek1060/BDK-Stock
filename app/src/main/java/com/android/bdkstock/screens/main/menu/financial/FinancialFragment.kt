package com.android.bdkstock.screens.main.menu.financial

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.android.bdkstock.databinding.FragmentFinancialBinding
import com.android.bdkstock.screens.main.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FinancialFragment : BaseFragment<FinancialViewModel, FragmentFinancialBinding>() {
   override val viewModel: FinancialViewModel by viewModels()
   override fun getViewBinding() = FragmentFinancialBinding.inflate(layoutInflater)

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)
      binding
   }
}