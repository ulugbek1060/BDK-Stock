package com.android.bdkstock.screens.main.menu.actions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.GridLayoutManager
import com.android.bdkstock.R
import com.android.bdkstock.databinding.FragmentMenuBinding
import com.android.bdkstock.databinding.RecyclerItemMenuBinding
import com.android.bdkstock.screens.main.base.BaseAdapter
import com.android.bdkstock.screens.main.base.BaseFragment
import com.android.bdkstock.screens.main.base.ViewHolderCreator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MenuFragment : BaseFragment<MenuViewModel, FragmentMenuBinding>() {

   private lateinit var gridLayoutManager: GridLayoutManager
   override val viewModel by viewModels<MenuViewModel>()
   override fun getViewBinding() = FragmentMenuBinding.inflate(layoutInflater)

   private val viewHolderCreator = object : ViewHolderCreator<RecyclerItemMenuBinding> {
      override fun inflateBinding(layoutInflater: LayoutInflater, viewGroup: ViewGroup) =
         RecyclerItemMenuBinding.inflate(layoutInflater, viewGroup, false)
   }

   private val adapter =
      BaseAdapter<MenuItem, RecyclerItemMenuBinding>(viewHolderCreator) { item ->
         ivItemIcon.setImageResource(item.icon)
         tvItemTitle.text = item.title
         root.setOnClickListener {
            findNavController().navigate(item.fragmentId, null, navOptions {
               anim {
                  enter = R.anim.enter
                  exit = R.anim.exit
                  popExit = R.anim.pop_enter
                  popExit = R.anim.pop_exit
               }
            })
         }
      }

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)
      setUpRecyclerView()
   }

   private fun setUpRecyclerView() {
      adapter.submitList(getMenus())
      gridLayoutManager = GridLayoutManager(requireContext(), 2)
      binding.recyclerMenu.layoutManager = gridLayoutManager
      binding.recyclerMenu.adapter = adapter
   }

   private fun getMenus(): List<MenuItem> {
      return listOf(
         MenuItem(
            1,
            R.drawable.ic_employees_bulk,
            getString(R.string.employees),
            R.id.employeesFragment
         ),
         MenuItem(
            2,
            R.drawable.ic_sales_bulk,
            getString(R.string.sales),
            R.id.salesFragment
         ),
         MenuItem(
            3,
            R.drawable.ic_ingredients_bulk,
            getString(R.string.ingredients),
            R.id.ingredientsOperationsFragment
         ),
         MenuItem(
            4,
            R.drawable.ic_products_bulk,
            getString(R.string.products),
            R.id.productOperationsListFragment
         ),
         MenuItem(
            5,
            R.drawable.ic_clients_bulk,
            getString(R.string.clients),
            R.id.clientsFragment
         ),
         MenuItem(
            6,
            R.drawable.ic_drivers_bulk,
            getString(R.string.drivers),
            R.id.driversFragment
         ),
      )
   }
}

