package com.android.bdkstock.screens.main.menu.drivers

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.navOptions
import androidx.paging.LoadState
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.bdkstock.R
import com.android.bdkstock.databinding.FragmentDriversBinding
import com.android.bdkstock.databinding.RecyclerItemDriverBinding
import com.android.bdkstock.databinding.RecyclerItemShimmerBinding
import com.android.bdkstock.screens.main.ActionsFragmentDirections
import com.android.bdkstock.screens.main.base.BaseFragment
import com.android.bdkstock.views.DefaultLoadStateAdapter
import com.android.bdkstock.views.findTopNavController
import com.android.bdkstock.views.pagingAdapter
import com.android.model.repository.drivers.entity.DriverEntity
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
class DriversFragment : BaseFragment(R.layout.fragment_drivers) {

   override val viewModel by viewModels<DriversViewModel>()
   private lateinit var binding: FragmentDriversBinding
   private lateinit var layoutManager: LinearLayoutManager

   @SuppressLint("SetTextI18n")
   private val adapter = pagingAdapter<DriverEntity, RecyclerItemDriverBinding> {
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
      binding = FragmentDriversBinding.bind(view)

      setupRecycler()
      setupShimmerLoading()

      setupRefreshLayout()
      handleViewVisibility()

      binding.extendedFab.setOnClickListener { fabOnClick() }
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

   private fun setupRecycler() {
      layoutManager = LinearLayoutManager(requireContext())
      binding.recyclerDrivers.layoutManager = layoutManager
      binding.recyclerDrivers.adapter = adapter.withLoadStateHeaderAndFooter(
         footer = DefaultLoadStateAdapter { adapter.retry() },
         header = DefaultLoadStateAdapter { adapter.retry() }
      )

      (binding.recyclerDrivers.itemAnimator as? DefaultItemAnimator)
         ?.supportsChangeAnimations = false
   }

   private fun setupRefreshLayout() {
      binding.refreshLayout.setOnRefreshListener {
         adapter.refresh()
      }
   }

   private fun handleViewVisibility() = lifecycleScope.launch {
      getRefreshLoadStateFlow().collectLatest { loadState ->

      }
   }

   private fun getRefreshLoadStateFlow(): Flow<LoadState> {
      return adapter.loadStateFlow
         .map { it.refresh }
   }


   // -- Progress with shimmer layout

   private val shimmerAdapter = simpleAdapter<Any, RecyclerItemShimmerBinding> {
      bind {
         root.startShimmer()
      }
   }

   private fun setupShimmerLoading() {
      shimmerAdapter.submitList(listOf(1, 2, 3, 4, 5, 6, 7, 8))
      binding.recyclerShimmerLoading.layoutManager = LinearLayoutManager(requireContext())
      binding.recyclerShimmerLoading.adapter = shimmerAdapter
   }

}