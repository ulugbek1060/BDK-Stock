package com.android.bdkstock.screens.main.menu.actions

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.GridLayoutManager
import com.android.bdkstock.R
import com.android.bdkstock.databinding.FragmentMenuBinding
import com.android.bdkstock.databinding.RecyclerItemMenuBinding
import com.android.bdkstock.screens.main.base.BaseFragment
import com.elveum.elementadapter.simpleAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MenuFragment : BaseFragment<MenuViewModel, FragmentMenuBinding>() {

   private lateinit var gridLayoutManager: GridLayoutManager
   override val viewModel by viewModels<MenuViewModel>()
   override fun getViewBinding() = FragmentMenuBinding.inflate(layoutInflater)

   private val adapter = simpleAdapter<MenuItem, RecyclerItemMenuBinding> {
      areItemsSame = { oldItem, newItem -> oldItem.id == newItem.id }
      bind {
         ivItemIcon.setImageResource(it.icon)
         tvItemTitle.text = it.title
      }
      listeners {
         root.onClick {
            findNavController().navigate(it.fragmentId, null, navOptions {
               anim {
                  enter = R.anim.enter
                  exit = R.anim.exit
                  popExit = R.anim.pop_enter
                  popExit = R.anim.pop_exit
               }
            })
         }
      }
   }

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)

      setUpRecyclerView()
      observeMenusList()
   }

   private fun observeMenusList() {

   }

   private fun setUpRecyclerView() {
      adapter.submitList(listMenus)
      gridLayoutManager = GridLayoutManager(requireContext(), 2)
      binding.recyclerMenu.layoutManager = gridLayoutManager
      binding.recyclerMenu.adapter = adapter
   }

   private val listMenus = ArrayList<MenuItem>()
      .apply {
         add(
            MenuItem(
               1,
               R.drawable.ic_employees_bulk,
               requireContext().getString(R.string.employees),
               R.id.employeesFragment
            )
         )
         add(
            MenuItem(
               2,
               R.drawable.ic_sales_bulk,
               "Sales",
               R.id.salesFragment
            )
         )
         add(
            MenuItem(
               3,
               R.drawable.ic_ingredients_bulk,
               requireContext().getString(R.string.ingredients),
               R.id.ingredientsOperationsFragment
            )
         )
         add(
            MenuItem(
               4,
               R.drawable.ic_products_bulk,
               "Products",
               R.id.productsFragment
            )
         )
         add(
            MenuItem(
               5,
               R.drawable.ic_clients_bulk,
               requireContext().getString(R.string.clients),
               R.id.clientsFragment
            )
         )
         add(
            MenuItem(
               6,
               R.drawable.ic_drivers_bulk,
               requireContext().getString(R.string.drivers),
               R.id.driversFragment
            )
         )
      }

}

