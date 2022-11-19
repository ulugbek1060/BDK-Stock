package com.android.bdkstock.screens.main.menu.drivers

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
import com.android.bdkstock.databinding.FragmentSearchDriversBinding
import com.android.bdkstock.databinding.RecyclerItemDriverBinding
import com.android.bdkstock.databinding.RecyclerItemShimmerBinding
import com.android.bdkstock.screens.main.base.BaseFragment
import com.android.bdkstock.views.findTopNavController
import com.android.bdkstock.views.pagingAdapter
import com.android.model.repository.drivers.entity.DriverEntity
import com.android.model.utils.AuthException
import com.android.model.utils.PageNotFoundException
import com.android.model.utils.observeEvent
import com.elveum.elementadapter.simpleAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchDriversFragment : BaseFragment(R.layout.fragment_search_drivers) {

   override val viewModel by viewModels<SearchDriversViewModel>()
   private lateinit var binding: FragmentSearchDriversBinding
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
            val args =
               SearchDriversFragmentDirections.actionSearchDriverFragmentToDisplayDriverFragment(
                  driver
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
      viewModel.driversFlow.collectLatest {
         adapter.submitData(it)
      }
   }

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)
      binding = FragmentSearchDriversBinding.bind(view)

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
            binding.recyclerShimmerLoading.isVisible = loadState == LoadState.Loading
            binding.recyclerDrivers.isVisible = loadState != LoadState.Loading

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
      recyclerDrivers.layoutManager = layoutManager
      recyclerDrivers.adapter = adapter
      recyclerDrivers.itemAnimator = null
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

   private val shimmerAdapter = simpleAdapter<Any, RecyclerItemShimmerBinding> {}
   private fun setupShimmerLoading() {
      shimmerAdapter.submitList(listOf(1, 2, 3, 4, 5, 6, 7, 8))
      binding.recyclerShimmerLoading.layoutManager = LinearLayoutManager(requireContext())
      binding.recyclerShimmerLoading.adapter = shimmerAdapter
   }
}