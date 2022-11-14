package com.android.bdkstock.screens.main.menu.employees

import android.annotation.SuppressLint
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
import com.android.bdkstock.databinding.FragmentEmployeesBinding
import com.android.bdkstock.databinding.RecyclerItemEmployeeBinding
import com.android.bdkstock.databinding.RecyclerItemShimmerBinding
import com.android.bdkstock.screens.main.ActivityFragmentDirections
import com.android.bdkstock.screens.main.base.BaseFragment
import com.android.model.repository.employees.entity.EmployeeEntity
import com.elveum.elementadapter.simpleAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

@ExperimentalCoroutinesApi
@FlowPreview
@AndroidEntryPoint
class EmployeesFragment : BaseFragment(R.layout.fragment_employees) {

   private lateinit var binding: FragmentEmployeesBinding
   override val viewModel by viewModels<EmployeesViewModel>()
   private lateinit var layoutManager: LinearLayoutManager

   private val TAG = this.javaClass.simpleName

   @SuppressLint("SetTextI18n")
   private val adapter = pagingAdapter<EmployeeEntity, RecyclerItemEmployeeBinding> {
      areItemsSame = { oldCat, newCat -> oldCat.id == newCat.id }
      bind { employee ->
         tvFullname.text = "${employee.firstname}, ${employee.lastname}"
         tvPhoneNumber.text = "+${employee.phoneNumber}"
         tvJobTitle.text = employee.job.name
      }
      listeners {
         root.onClick { employee ->
            val args =
               ActivityFragmentDirections.actionActivityFragmentToDisplayEmployeeFragment(employee)
            findTopNavController().navigate(args)
         }
      }
   }

   private val shimmerAdapter = simpleAdapter<Any, RecyclerItemShimmerBinding> {
      bind {
         root.startShimmer()
      }
   }

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)
      binding = FragmentEmployeesBinding.bind(view)

      setupShimmerLoading()
      setupList()
      setupRefreshLayout()

      observeEmployees()
      observeToast()

      handleViewVisibility()

      binding.extendedFab.setOnClickListener { navigateToRegisterFrag() }
   }

   private fun navigateToRegisterFrag() {
      findTopNavController().navigate(
         R.id.action_activityFragment_to_registerEmployeeFragment,
      )
   }

   private fun setupShimmerLoading() {
      shimmerAdapter.submitList(listOf(1, 2, 3, 4, 5, 6, 7, 8))
      binding.recyclerShimmerLoading.layoutManager = LinearLayoutManager(requireContext())
      binding.recyclerShimmerLoading.adapter = shimmerAdapter
   }

   private fun handleViewVisibility() = lifecycleScope.launchWhenStarted {
      getRefreshLoadStateFlow()
         .collectLatest { current ->
            binding.recyclerEmployees.isInvisible =
               current is LoadState.Error || current is LoadState.Loading
            binding.recyclerShimmerLoading.isVisible =
               current is LoadState.Loading
            if (
               binding.refreshLayout.isRefreshing
               && (current is LoadState.NotLoading
                   || current is LoadState.Error)
            ) {
               binding.refreshLayout.isRefreshing = false
            }
         }
   }

   private fun getRefreshLoadStateFlow(): Flow<LoadState> {
      return adapter.loadStateFlow
         .map { it.refresh }
   }

   private fun observeToast() {

   }

   private fun observeEmployees() = lifecycleScope.launchWhenStarted {
      viewModel.employeesFlow.collectLatest {
         adapter.submitData(it)
      }
   }

   private fun setupRefreshLayout() {
      binding.refreshLayout.setOnRefreshListener {
         adapter.refresh()
      }
   }

   private fun setupList() {
      layoutManager = LinearLayoutManager(requireContext())
      binding.recyclerEmployees.layoutManager = layoutManager

      binding.recyclerEmployees.adapter = adapter

      (binding.recyclerEmployees.itemAnimator as? DefaultItemAnimator)
         ?.supportsChangeAnimations = false

      lifecycleScope.launchWhenStarted {
         waitForLoad()
         val footerAdapter = DefaultLoadStateAdapter(binding.refreshLayout) {
            adapter.retry()
         }
         val adapterWithLoadState = adapter.withLoadStateFooter(footerAdapter)
         binding.recyclerEmployees.adapter = adapterWithLoadState
      }
   }

   private suspend fun waitForLoad() {
      adapter.onPagesUpdatedFlow
         .map { adapter.itemCount }
         .first { it > 0 }
   }
}