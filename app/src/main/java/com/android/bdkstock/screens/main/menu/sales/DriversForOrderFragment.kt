package com.android.bdkstock.screens.main.menu.sales

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.bdkstock.R
import com.android.bdkstock.databinding.FragmentDriversForOrderBinding
import com.android.bdkstock.databinding.RecyclerItemDriverBinding
import com.android.bdkstock.screens.main.base.BaseFragment
import com.android.bdkstock.screens.main.base.adapters.DefaultLoadStateAdapter
import com.android.bdkstock.screens.main.base.adapters.pagingAdapter
import com.android.bdkstock.screens.main.menu.drivers.DriversViewModel
import com.android.bdkstock.views.findTopNavController
import com.android.model.repository.drivers.entity.DriverEntity
import com.android.model.utils.AuthException
import com.android.model.utils.observeEvent
import com.elveum.elementadapter.simpleAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@FlowPreview
@ExperimentalCoroutinesApi
@AndroidEntryPoint
class DriversForOrderFragment :
   BaseFragment<DriversViewModel, FragmentDriversForOrderBinding>(),
   SearchView.OnQueryTextListener {

   override val viewModel: DriversViewModel by viewModels()
   override fun getViewBinding() = FragmentDriversForOrderBinding.inflate(layoutInflater)
   private lateinit var layoutManager: LinearLayoutManager

   @SuppressLint("SetTextI18n")
   private val adapter = pagingAdapter<DriverEntity, RecyclerItemDriverBinding> {
      areItemsSame = { oldItem, newItem -> oldItem.id == newItem.id }
      areContentsSame = { oldItem, newItem -> oldItem == newItem }
      bind { driver ->
         tvFullname.text = driver.driverFullName
         tvPhoneNumber.text = "+${driver.phoneNumber}"
      }
      listeners {
         root.onClick { driver ->
            showConfirmDialog(driver)
         }
      }
   }

   private fun showConfirmDialog(driver: DriverEntity) {
      AlertDialog.Builder(requireContext())
         .setTitle("${getString(R.string.client)}: ${driver.driverFullName}")
         .setMessage("${getString(R.string.phone_number)}: ${driver.phoneNumber}")
         .setNegativeButton(getString(R.string.close)) { _, _ -> }
         .setPositiveButton(getString(R.string.choose)) { _, _ ->
            val navController = findTopNavController()

            navController
               .previousBackStackEntry
               ?.savedStateHandle
               ?.set(DRIVER_SELECTION_KEY, driver)

            navController
               .popBackStack()

         }
         .create()
         .show()
   }

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      observeList()
   }

   private fun observeList() = lifecycleScope.launch {
      viewModel.driversFlow.collectLatest {
         adapter.submitData(it)
      }
   }

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)

      setupRecyclerView()
      observeErrorEvent()

      setupRefreshLayout()
      handleViewVisibility()

      binding.extendedFab.setOnClickListener { fabOnClick() }

      requireActivity().addMenuProvider(menuProvider, viewLifecycleOwner, Lifecycle.State.STARTED)
   }

   private val menuProvider = object : MenuProvider {
      override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
         menuInflater.inflate(R.menu.menu_search, menu)
         val myActionMenuItem = menu.findItem(R.id.search)
         val searchView = myActionMenuItem.actionView as SearchView
         searchView.setOnQueryTextListener(this@DriversForOrderFragment)
      }

      override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
         return false
      }
   }

   private fun observeErrorEvent() {
      viewModel.errorEvent.observeEvent(viewLifecycleOwner) {
         AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.user_logged_out))
            .setMessage(getString(R.string.try_again_to_sign_in))
            .setNegativeButton(getString(R.string.sign_in)) { _, _ ->
               viewModel.restart()
            }
            .setCancelable(false)
            .create()
            .show()
      }
   }

   private fun fabOnClick() {
      val args = DriversForOrderFragmentDirections
         .actionDriversForOrderFragmentToRegisterDriverFragment(false)
      findTopNavController().navigate(args)
   }

   private fun setupRecyclerView() {
      layoutManager = LinearLayoutManager(requireContext())
      binding.recyclerDrivers.layoutManager = layoutManager
      binding.recyclerDrivers.adapter = adapter.withLoadStateHeaderAndFooter(
         footer = DefaultLoadStateAdapter(binding.refreshLayout) { adapter.retry() },
         header = DefaultLoadStateAdapter(binding.refreshLayout) { adapter.retry() }
      )

      (binding.recyclerDrivers.itemAnimator as? DefaultItemAnimator)
         ?.supportsChangeAnimations = false

      setFabBehaviorOnRecycler()
   }

   private fun setFabBehaviorOnRecycler() {
      binding.recyclerDrivers.addOnScrollListener(object : RecyclerView.OnScrollListener() {
         override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            if (dy > 0 && binding.extendedFab.isVisible) {
               binding.extendedFab.hide()
            } else if (dy < 0 && !binding.extendedFab.isVisible) {
               binding.extendedFab.show()
            }
         }
      })
   }

   private fun setupRefreshLayout() {
      binding.refreshLayout.setOnRefreshListener {
         adapter.refresh()
      }
   }

   private fun handleViewVisibility() = lifecycleScope.launchWhenStarted {
      getRefreshLoadStateFlow().collectLatest { loadState ->
         binding.recyclerDrivers.isVisible = loadState != LoadState.Loading
         binding.progressbar.isVisible = loadState == LoadState.Loading

         if (loadState is LoadState.NotLoading)
            binding.ivEmpty.isVisible = adapter.snapshot().isEmpty()

         if (loadState is LoadState.NotLoading || loadState is LoadState.Error)
            binding.refreshLayout.isRefreshing = false

         handleError(loadState)
      }
   }

   private fun handleError(loadState: LoadState) {
      if (loadState is LoadState.Error && loadState.error is AuthException) {
         viewModel.showAuthError()
      }
   }

   private fun getRefreshLoadStateFlow(): Flow<LoadState> {
      return adapter.loadStateFlow
         .map { it.refresh }
   }

   override fun onQueryTextSubmit(query: String?): Boolean {
      return true
   }

   override fun onQueryTextChange(newText: String?): Boolean {
      viewModel.setQuery(newText)
      return true
   }

   private companion object {
      const val DRIVER_SELECTION_KEY = "chosen_driver"
   }
}