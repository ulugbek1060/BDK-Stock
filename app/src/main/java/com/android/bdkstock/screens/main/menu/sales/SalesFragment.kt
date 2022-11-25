package com.android.bdkstock.screens.main.menu.sales

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.android.bdkstock.R
import com.android.bdkstock.databinding.FragmentSalesBinding

class SalesFragment : Fragment(R.layout.fragment_sales) {

   private lateinit var binding: FragmentSalesBinding

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)
      binding = FragmentSalesBinding.bind(view)

   }
}
