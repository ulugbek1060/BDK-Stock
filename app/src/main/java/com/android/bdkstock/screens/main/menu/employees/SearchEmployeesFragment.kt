package com.android.bdkstock.screens.main.menu.employees

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.bdkstock.R
import com.android.bdkstock.databinding.FragmentSearchEmployeesBinding
import com.android.bdkstock.databinding.ProgressItemSmallerBinding
import com.android.bdkstock.databinding.RecyclerItemEmployeeBinding
import com.android.bdkstock.screens.main.base.BaseFragment
import com.android.bdkstock.views.findTopNavController
import com.android.bdkstock.views.pagingAdapter
import com.android.model.repository.employees.entity.EmployeeEntity
import com.android.model.utils.AuthException
import com.android.model.utils.PageNotFoundException
import com.android.model.utils.observeEvent
import com.elveum.elementadapter.simpleAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchEmployeesFragment : BaseFragment(R.layout.fragment_search_employees) {

   private val TAG = "SearchEmployeeFragment"

   override val viewModel by viewModels<SearchEmployeeViewModel>()
   private lateinit var binding: FragmentSearchEmployeesBinding
   private lateinit var layoutManager: LinearLayoutManager

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
               SearchEmployeesFragmentDirections.actionSearchEmployeeFragmentToDisplayEmployeeFragment(
                  employee
               )
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
      binding = FragmentSearchEmployeesBinding.bind(view)


      setupRecycler()
      setupShimmerLoading()
      handleUiVisibility()

      observeAuthError()
      invalidateAdapter()

      setupSearchBar()
   }

   private fun invalidateAdapter() {
      viewModel.invalidate.observeEvent(viewLifecycleOwner) {
         adapter.submitData(viewLifecycleOwner.lifecycle, PagingData.empty())
      }
   }

   private fun handleUiVisibility() = lifecycleScope.launch {
      adapter.loadStateFlow.map { it.refresh }
         .collectLatest { loadState ->
            binding.recyclerProgress.isVisible = loadState == LoadState.Loading
            binding.recyclerEmployees.isVisible = loadState != LoadState.Loading

            handleErrorMessage(loadState)
         }
   }

   private fun handleErrorMessage(loadState: LoadState) = lifecycleScope.launch {
      if (loadState is LoadState.Error && loadState.error is PageNotFoundException) {
         Toast.makeText(requireContext(), loadState.error.message, Toast.LENGTH_SHORT).show()
      } else if (loadState is LoadState.Error && loadState.error is AuthException) {
         viewModel.showAuthError()
      }
   }

   private fun setupSearchBar() {
      binding.searchBar.apply {
         textListener = {
            viewModel.setQuery(it)
         }
         backListener = {
            findNavController().popBackStack()
         }
      }
   }

   private fun setupRecycler() = binding.apply {
      layoutManager = LinearLayoutManager(requireContext())
      recyclerEmployees.layoutManager = layoutManager
      recyclerEmployees.adapter = adapter
      recyclerEmployees.itemAnimator = null
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