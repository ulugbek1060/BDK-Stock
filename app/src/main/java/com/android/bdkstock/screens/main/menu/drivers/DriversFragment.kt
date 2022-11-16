package com.android.bdkstock.screens.main.menu.drivers

import android.os.Bundle
import android.view.View
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.bdkstock.R
import com.android.bdkstock.databinding.FragmentDriversBinding
import com.android.bdkstock.databinding.RecyclerItemDriverBinding
import com.android.bdkstock.databinding.RecyclerItemShimmerBinding
import com.android.bdkstock.screens.main.ActionsFragmentDirections
import com.android.bdkstock.screens.main.base.BaseFragment
import com.android.bdkstock.screens.main.base.simpleScan
import com.android.bdkstock.views.DefaultLoadStateAdapter
import com.android.bdkstock.views.pagingAdapter
import com.android.model.repository.drivers.entity.DriverEntity
import com.elveum.elementadapter.simpleAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@FlowPreview
@ExperimentalCoroutinesApi
@AndroidEntryPoint
class DriversFragment : BaseFragment(R.layout.fragment_drivers) {

   override val viewModel by viewModels<DriversViewModel>()
   private lateinit var binding: FragmentDriversBinding
   private lateinit var layoutManager: LinearLayoutManager

   private val adapter = pagingAdapter<DriverEntity, RecyclerItemDriverBinding> {
      bind { driver ->
         tvFullname.text = driver.driverFullName
         tvPhoneNumber.text = "+${driver.phoneNumber}"
      }
      listeners {
         root.onClick { driver ->
            val arg =
               ActionsFragmentDirections.actionActivityFragmentToDisplayDriverFragment(driver)
            findTopNavController().navigate(arg)
         }
      }
   }

   private val shimmerAdapter = simpleAdapter<Any, RecyclerItemShimmerBinding> {
      bind {
         root.startShimmer()
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
      binding = FragmentDriversBinding.bind(view)

      setupRecycler()
      setupShimmerLoading()

      setupRefreshLayout()
      handleViewVisibility()

      handleScrollingTop()

      binding.extendedFab.setOnClickListener { navigateToRegisterFrag() }
   }

   private fun navigateToRegisterFrag() {
      navigateFromTopNavController(R.id.registerDriverFragment)
   }

   private fun setupShimmerLoading() {
      shimmerAdapter.submitList(listOf(1, 2, 3, 4, 5, 6, 7, 8))
      binding.recyclerShimmerLoading.layoutManager = LinearLayoutManager(requireContext())
      binding.recyclerShimmerLoading.adapter = shimmerAdapter
   }

   private fun setupRecycler() {
      layoutManager = LinearLayoutManager(requireContext())
      binding.recyclerDrivers.layoutManager = layoutManager
      binding.recyclerDrivers.adapter = adapter

      (binding.recyclerDrivers.itemAnimator as? DefaultItemAnimator)
         ?.supportsChangeAnimations = false

      lifecycleScope.launchWhenStarted {
         waitForLoad()
         val footerAdapter = DefaultLoadStateAdapter(binding.refreshLayout) { adapter.retry() }
         val adapterWithLoadState = adapter.withLoadStateFooter(footerAdapter)
         binding.recyclerDrivers.adapter = adapterWithLoadState
      }
   }

   private suspend fun waitForLoad() {
      adapter.onPagesUpdatedFlow
         .map { adapter.itemCount }
         .first { it > 0 }
   }

   private fun setupRefreshLayout() {
      binding.refreshLayout.setOnRefreshListener {
         adapter.refresh()
      }
   }

   private fun handleViewVisibility() = lifecycleScope.launch {
      getRefreshLoadStateFlow()
         .simpleScan(count = 3)
         .collectLatest { (beforePrevious, previous, current) ->
            binding.recyclerDrivers.isInvisible = current is LoadState.Error
                || previous is LoadState.Error
                || (beforePrevious is LoadState.Error
                && previous is LoadState.NotLoading
                && current is LoadState.Loading)

            binding.recyclerShimmerLoading.isVisible = current is LoadState.Loading
                || previous is LoadState.Loading
                || (beforePrevious is LoadState.Loading
                && previous is LoadState.NotLoading
                && current is LoadState.Loading)

            if (binding.refreshLayout.isRefreshing && (current is LoadState.NotLoading || current is LoadState.Error)) {
               binding.refreshLayout.isRefreshing = false
            }
         }
   }

   private fun handleScrollingTop() = lifecycleScope.launch {
      getRefreshLoadStateFlow()
         .simpleScan(count = 2)
         .collect { (previousState, currentState) ->
            if (previousState is LoadState.Loading
               && currentState is LoadState.NotLoading
            ) {
               delay(200)
               binding.recyclerDrivers.scrollToPosition(0)
            }
         }
   }

   private fun getRefreshLoadStateFlow(): Flow<LoadState> {
      return adapter.loadStateFlow
         .map { it.refresh }
   }

}