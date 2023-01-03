package com.android.bdkstock.screens.main.menu.actions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.bdkstock.R
import com.android.bdkstock.databinding.FragmentMenuBinding
import com.android.bdkstock.databinding.RecyclerItemMenuBinding
import com.android.bdkstock.screens.main.base.BaseFragment
import com.android.bdkstock.screens.main.base.adapters.BaseAdapter
import com.android.bdkstock.screens.main.base.adapters.ViewHolderCreator
import com.android.bdkstock.utils.addMenuProvider
import com.android.bdkstock.views.findTopNavController
import com.android.model.utils.observeEvent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MenuFragment : BaseFragment<MenuViewModel, FragmentMenuBinding>() {

   private lateinit var gridLayoutManager: LinearLayoutManager
   override val viewModel by viewModels<MenuViewModel>()
   override fun getViewBinding() = FragmentMenuBinding.inflate(layoutInflater)

   private val viewHolderCreator = object : ViewHolderCreator<RecyclerItemMenuBinding> {
      override fun inflateBinding(layoutInflater: LayoutInflater, viewGroup: ViewGroup) =
         RecyclerItemMenuBinding.inflate(layoutInflater, viewGroup, false)
   }

   private val adapter = BaseAdapter<MenuItem, RecyclerItemMenuBinding>(viewHolderCreator) { item ->
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

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      observePermissions()
   }

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)

      observeState()
      setUpRecyclerView()
      observeDashboard()
      observeError()

      binding.refreshLayout.setOnRefreshListener {
         viewModel.loadInitialState()
      }

      binding.card1.setOnClickListener {
         findTopNavController().navigate(R.id.action_actionsFragment_to_financialFragment)
      }

      addMenuProvider(R.menu.menu_profile, onMenuItemSelected = {
         findNavController().navigate(R.id.action_menuFragment_to_profileFragment)
      })
   }

   private fun observeError() {
      viewModel.errorMessage.observeEvent(viewLifecycleOwner) {
         findTopNavController().navigate(
            R.id.globalErrorFragment,
            bundleOf(ERROR_MESSAGE_KEY to it)
         )
      }
   }

   private fun observeState() {
      viewModel.state.observe(viewLifecycleOwner) {
         binding.refreshLayout.isRefreshing = it.isInProgress
      }
   }

   private fun observePermissions() = lifecycleScope.launch {
      viewModel.permissions.collectLatest { perms ->
         val menus = HashSet<MenuItem>()
         perms.forEach { permission ->
            //menus
            getMenus().forEach { menuItem ->
               if (permission == menuItem.permission) {
                  menus.add(menuItem)
               }
            }
            //budget Info
            if (permission == "dashboard_view") {
               viewModel.loadBudgetInfo()
            }
         }

         viewModel.setMenus(menus)
      }
   }

   private fun observeDashboard() {
      viewModel.dashboardInfo.observe(viewLifecycleOwner) {
         if (it != null) {
            binding.card1.isVisible = true
            binding.tvCard.text = it.budgetInfoEntity.totalSumInCard
            binding.tvCash.text = it.budgetInfoEntity.totalSumInCash
         } else {
            binding.card1.isVisible = false
         }
      }
   }

   private fun setUpRecyclerView() {
      viewModel.menus.observe(viewLifecycleOwner) { items ->
         adapter.submitList(items)
      }

      gridLayoutManager = LinearLayoutManager(requireContext())
      binding.recyclerMenu.layoutManager = gridLayoutManager
      binding.recyclerMenu.adapter = adapter
   }

   private fun getMenus(): List<MenuItem> {
      return listOf(
         MenuItem(
            id = 1,
            icon = R.drawable.ic_employees_bulk,
            title = getString(R.string.employees),
            fragmentId = R.id.employeesFragment,
            permission = "employee_view"
         ),
         MenuItem(
            id = 2,
            icon = R.drawable.ic_sales_bulk,
            title = getString(R.string.sales),
            fragmentId = R.id.salesFragment,
            permission = "order_view"
         ),
         MenuItem(
            id = 3,
            icon = R.drawable.ic_ingredients_bulk,
            title = getString(R.string.ingredients),
            fragmentId = R.id.ingredientsOperationsFragment,
            permission = "ingredient_view"
         ),
         MenuItem(
            id = 4,
            icon = R.drawable.ic_products_bulk,
            title = getString(R.string.products),
            fragmentId = R.id.productOperationsListFragment,
            permission = "product_view"
         ),
         MenuItem(
            id = 5,
            icon = R.drawable.ic_clients_bulk,
            title = getString(R.string.clients),
            fragmentId = R.id.clientsFragment,
            permission = "client_view"
         ),
         MenuItem(
            id = 6,
            icon = R.drawable.ic_drivers_bulk,
            title = getString(R.string.drivers),
            fragmentId = R.id.driversFragment,
            permission = "driver_view"
         ),
      )
   }

   companion object {
      const val ERROR_MESSAGE_KEY = "error_message"
   }
}

