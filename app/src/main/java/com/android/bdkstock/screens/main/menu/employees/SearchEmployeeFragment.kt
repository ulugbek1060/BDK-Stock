package com.android.bdkstock.screens.main.menu.employees

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.bdkstock.R
import com.android.bdkstock.databinding.FragmentSearchEmployeeBinding
import com.android.bdkstock.databinding.RecyclerItemEmployeeBinding
import com.android.bdkstock.databinding.RecyclerItemShimmerBinding
import com.android.bdkstock.screens.main.base.BaseFragment
import com.android.bdkstock.views.findTopNavController
import com.android.bdkstock.views.pagingAdapter
import com.android.bdkstock.views.textChanges
import com.android.model.repository.employees.entity.EmployeeEntity
import com.android.model.utils.AuthException
import com.android.model.utils.PageNotFoundException
import com.android.model.utils.observeEvent
import com.elveum.elementadapter.simpleAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchEmployeeFragment : BaseFragment(R.layout.fragment_search_employee) {

   private val TAG = "SearchEmployeeFragment"

   override val viewModel by viewModels<SearchEmployeeViewModel>()
   private lateinit var binding: FragmentSearchEmployeeBinding
   private lateinit var layoutManager: LinearLayoutManager

   @SuppressLint("SetTextI18n")
   private val adapter = pagingAdapter<EmployeeEntity, RecyclerItemEmployeeBinding> {
      areItemsSame = { oldCat, newCat -> oldCat.id == newCat.id }
      areContentsSame = { oldItem, newItem -> oldItem == newItem }
      bind { employee ->
         tvFullname.text = "${employee.firstname}, ${employee.lastname}"
         tvPhoneNumber.text = "+${employee.phoneNumber}"
         tvJobTitle.text = employee.job.name
      }
      listeners {
         root.onClick { employee ->
            val args =
               SearchEmployeeFragmentDirections.actionSearchEmployeeFragmentToDisplayEmployeeFragment(
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
      binding = FragmentSearchEmployeeBinding.bind(view)


      setupRecycler()
      setupShimmerLoading()
      handleUiVisibility()

      observeAuthError()
      invalidateAdapter()

      addTextWatchListener()
   }

   private fun invalidateAdapter() {
      viewModel.invalidate.observeEvent(viewLifecycleOwner) {
         adapter.submitData(viewLifecycleOwner.lifecycle, PagingData.empty())
      }
   }

   private fun handleUiVisibility() = lifecycleScope.launch {
      adapter.loadStateFlow.map { it.refresh }
         .collectLatest { loadState ->
            binding.recyclerShimmerLoading.isVisible = loadState == LoadState.Loading
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

   @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
   private fun addTextWatchListener() {
      binding.inputQuery
         .textChanges()
         .debounce(500)
         .onEach { viewModel.setQuery(it.toString()) }
         .launchIn(lifecycleScope)
   }

   private fun setupRecycler() = binding.apply {
      layoutManager = LinearLayoutManager(requireContext())
      recyclerEmployees.layoutManager = layoutManager
      recyclerEmployees.adapter = adapter
      recyclerEmployees.itemAnimator = null
   }

   private fun observeAuthError() {
      viewModel.showAuthError.observeEvent(viewLifecycleOwner) {
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