package com.android.bdkstock.screens.main.menu

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.bdkstock.R
import com.android.bdkstock.databinding.FragmentSalesBinding
import com.android.bdkstock.databinding.RecyclerItemShimmerBinding
import com.elveum.elementadapter.simpleAdapter

class SalesFragment : Fragment(R.layout.fragment_sales) {

   private lateinit var binding: FragmentSalesBinding
   private lateinit var layoutManager: LinearLayoutManager

   private val shimmerAdapter = simpleAdapter<Any, RecyclerItemShimmerBinding> {
      areItemsSame = { oldItem, newItem -> oldItem == newItem }
   }

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)
      binding = FragmentSalesBinding.bind(view)

      layoutManager = LinearLayoutManager(requireContext())
      binding.recyclerShimmerLoading.layoutManager = layoutManager
      binding.recyclerShimmerLoading.adapter = shimmerAdapter
      val list = listOf(1, 2, 3, 4, 5, 6, 7, 8)
      shimmerAdapter.submitList(list)
      binding.recyclerShimmerLoading.setItemViewCacheSize(list.size)

   }
}
