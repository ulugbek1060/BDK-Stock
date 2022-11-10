package com.android.bdkstock.screens.main.menu

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.android.bdkstock.R
import com.android.bdkstock.databinding.FragmentMenuBinding
import com.android.bdkstock.databinding.RecyclerItemMenuBinding
import com.android.bdkstock.screens.main.base.BaseFragment
import com.elveum.elementadapter.simpleAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MenuFragment : BaseFragment(R.layout.fragment_menu) {

   private lateinit var binding: FragmentMenuBinding
   private lateinit var gridLayoutManager: GridLayoutManager
   override val viewModel by viewModels<MenuViewModel>()

   private val adapter = simpleAdapter<MenuItem, RecyclerItemMenuBinding> {
      areItemsSame = { oldItem, newItem -> oldItem.id == newItem.id }
      bind {
         ivItemIcon.setImageResource(it.icon)
         tvItemTitle.text = it.title
      }
      listeners {
         root.onClick {
            navigate(it.fragmentId)
         }
      }
   }

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)
      binding = FragmentMenuBinding.bind(view)

      setUpRecyclerView()
      observeMenusList()
   }

   private fun observeMenusList() {
      viewModel.menuItems.observe(viewLifecycleOwner) {
         adapter.submitList(it)
      }
   }

   private fun setUpRecyclerView() {
      gridLayoutManager = GridLayoutManager(requireContext(), 2)
      binding.recyclerMenu.layoutManager = gridLayoutManager
      binding.recyclerMenu.adapter = adapter
   }
}

