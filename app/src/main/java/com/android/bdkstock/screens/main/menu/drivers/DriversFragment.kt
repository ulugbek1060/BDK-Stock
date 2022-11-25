package com.android.bdkstock.screens.main.menu.drivers

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
import androidx.navigation.navOptions
import androidx.paging.LoadState
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.bdkstock.R
import com.android.bdkstock.databinding.FragmentDriversBinding
import com.android.bdkstock.databinding.ProgressItemSmallerBinding
import com.android.bdkstock.databinding.RecyclerItemDriverBinding
import com.android.bdkstock.screens.main.ActionsFragmentDirections
import com.android.bdkstock.screens.main.base.BaseFragment
import com.android.bdkstock.views.DefaultLoadStateAdapter
import com.android.bdkstock.views.findTopNavController
import com.android.bdkstock.views.pagingAdapter
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
class DriversFragment :
   BaseFragment<DriversViewModel, FragmentDriversBinding>(),
   SearchView.OnQueryTextListener {

   override val viewModel by viewModels<DriversViewModel>()
   private lateinit var layoutManager: LinearLayoutManager
   override fun getViewBinding() = FragmentDriversBinding.inflate(layoutInflater)

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
            val arg =
               ActionsFragmentDirections.actionActionsFragmentToDisplayDriverFragment(driver)
            findTopNavController().navigate(arg)
         }
      }
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
      setupShimmerLoading()
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
         searchView.setOnQueryTextListener(this@DriversFragment)
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
      findTopNavController().navigate(R.id.registerDriverFragment, null, navOptions {
         anim {
            enter = R.anim.enter
            exit = R.anim.exit
            popEnter = R.anim.pop_enter
            popExit = R.anim.pop_exit
         }
      })
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
         binding.recyclerProgress.isVisible = loadState == LoadState.Loading

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


   // -- Progress with shimmer layout

   private val shimmerAdapter = simpleAdapter<Any, ProgressItemSmallerBinding> {}
   private fun setupShimmerLoading() {
      shimmerAdapter.submitList(listOf(1, 2, 3, 4, 5, 6, 7, 8))
      binding.recyclerProgress.layoutManager = LinearLayoutManager(requireContext())
      binding.recyclerProgress.adapter = shimmerAdapter
   }

   override fun onQueryTextSubmit(query: String?): Boolean {
      return true
   }

   override fun onQueryTextChange(newText: String?): Boolean {
      viewModel.setQuery(newText)
      return true
   }
}