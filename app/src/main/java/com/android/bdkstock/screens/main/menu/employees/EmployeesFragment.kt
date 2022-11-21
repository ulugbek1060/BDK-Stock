package com.android.bdkstock.screens.main.menu.employees

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.bdkstock.R
import com.android.bdkstock.databinding.FragmentEmployeesBinding
import com.android.bdkstock.databinding.ProgressItemSmallerBinding
import com.android.bdkstock.databinding.RecyclerItemEmployeeBinding
import com.android.bdkstock.screens.main.ActionsFragmentDirections
import com.android.bdkstock.screens.main.base.BaseFragment
import com.android.bdkstock.views.DefaultLoadStateAdapter
import com.android.bdkstock.views.findTopNavController
import com.android.bdkstock.views.pagingAdapter
import com.android.model.repository.employees.entity.EmployeeEntity
import com.android.model.utils.AuthException
import com.android.model.utils.observeEvent
import com.elveum.elementadapter.simpleAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

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
      areItemsSame = { oldItem, newItem -> oldItem.id == newItem.id }
      areContentsSame = { oldItem, newItem -> oldItem == newItem }
      bind { employee ->
         tvFullname.text = "${employee.firstname}, ${employee.lastname}"
         tvPhoneNumber.text = "+${employee.phoneNumber}"
         tvJobTitle.text = employee.job.name
      }
      listeners {
         root.onClick { employee ->
            val args =
               ActionsFragmentDirections.actionActionsFragmentToDisplayEmployeeFragment(employee)
            findTopNavController().navigate(args)
         }
      }
   }

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      observeEmployees()
   }

   private fun observeEmployees() = lifecycleScope.launch {
      viewModel.employeesFlow.collectLatest {
         adapter.submitData(it)
      }
   }

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)
      binding = FragmentEmployeesBinding.bind(view)

      setupShimmerLoading()
      setupRecyclerView()
      setupRefreshLayout()

      handleViewVisibility()

      observeAuthError()

      binding.extendedFab.setOnClickListener { fabOnClick() }

      binding.buttonSearch.setOnClickListener { searchOnClick() }
   }

   private fun searchOnClick() {
      findTopNavController().navigate(R.id.action_actionsFragment_to_searchEmployeeFragment)
   }

   private fun fabOnClick() {
      findTopNavController().navigate(R.id.action_actionsFragment_to_registerEmployeeFragment)
   }

   private fun setupRefreshLayout() {
      binding.refreshLayout.setOnRefreshListener {
         adapter.refresh()
      }
   }

   private fun setupRecyclerView() {
      layoutManager = LinearLayoutManager(requireContext())
      binding.recyclerEmployees.layoutManager = layoutManager

      binding.recyclerEmployees.adapter = adapter.withLoadStateHeaderAndFooter(
         footer = DefaultLoadStateAdapter(binding.refreshLayout) { adapter.retry() },
         header = DefaultLoadStateAdapter(binding.refreshLayout) { adapter.retry() }
      )

      binding.recyclerEmployees.itemAnimator = null

      setFabBehaviorOnRecycler()
   }

   private fun setFabBehaviorOnRecycler() {
      binding.recyclerEmployees.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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

   private fun handleViewVisibility() = lifecycleScope.launch {
      adapter
         .loadStateFlow
         .map { it.refresh }
         .collectLatest { loadState ->

            binding.recyclerProgress.isVisible = loadState == LoadState.Loading
            binding.recyclerEmployees.isVisible = loadState != LoadState.Loading

            if (loadState is LoadState.NotLoading || loadState is LoadState.Error)
               binding.refreshLayout.isRefreshing = false

            handleError(loadState)
         }
   }

   private fun handleError(refresh: LoadState) {
      if (refresh is LoadState.Error && refresh.error is AuthException) {
         viewModel.showAuthError()
      }
   }

   private fun observeAuthError() {
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

   // -- Progressbar with shimmer layout

   private val shimmerAdapter = simpleAdapter<Any, ProgressItemSmallerBinding> {}
   private fun setupShimmerLoading() {
      shimmerAdapter.submitList(listOf(1, 2, 3, 4, 5, 6, 7, 8))
      binding.recyclerProgress.layoutManager = LinearLayoutManager(requireContext())
      binding.recyclerProgress.adapter = shimmerAdapter
   }
}