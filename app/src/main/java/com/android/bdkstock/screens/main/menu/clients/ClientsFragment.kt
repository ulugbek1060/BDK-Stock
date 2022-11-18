package com.android.bdkstock.screens.main.menu.clients

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.bdkstock.R
import com.android.bdkstock.databinding.FragmentClientsBinding
import com.android.bdkstock.databinding.RecyclerItemClientBinding
import com.android.bdkstock.databinding.RecyclerItemShimmerBinding
import com.android.bdkstock.screens.main.base.BaseFragment
import com.android.bdkstock.views.DefaultLoadStateAdapter
import com.android.bdkstock.views.pagingAdapter
import com.android.model.repository.clients.entity.ClientEntity
import com.android.model.utils.AuthException
import com.android.model.utils.gone
import com.android.model.utils.visible
import com.elveum.elementadapter.simpleAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ClientsFragment : BaseFragment(R.layout.fragment_clients) {

   override val viewModel by viewModels<ClientsViewModel>()
   private lateinit var binding: FragmentClientsBinding
   private lateinit var layoutManager: LinearLayoutManager

   private val TAG = this.javaClass.simpleName

   private val clientsAdapter = pagingAdapter<ClientEntity, RecyclerItemClientBinding> {
      bind { client ->
         tvFullname.text = client.fullName
         tvPhoneNumber.text = client.phoneNumber
      }
      listeners {
         root.onClick {

         }
      }
   }

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      observeClients()
   }

   private fun observeClients() = lifecycleScope.launch {
      viewModel.clientFlow.collectLatest {
         clientsAdapter.submitData(it)
      }
   }

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)
      binding = FragmentClientsBinding.bind(view)

      setupRecyclerView()
      setupShimmerLoading()

      handleLoadStateAndUiVisibility()
   }

   private fun handleLoadStateAndUiVisibility() = lifecycleScope.launch {
      clientsAdapter.loadStateFlow.collectLatest { loadState ->

      }
   }

   private fun setupRecyclerView() = binding.apply {
      layoutManager = LinearLayoutManager(requireContext())
      recyclerClients.layoutManager = layoutManager
      recyclerClients.adapter = clientsAdapter.withLoadStateHeaderAndFooter(
         header = DefaultLoadStateAdapter { clientsAdapter.retry() },
         footer = DefaultLoadStateAdapter { clientsAdapter.retry() }
      )
      recyclerClients.setHasFixedSize(true)
   }

   // -- Progress with shimmer layout

   private val shimmerAdapter =
      simpleAdapter<Any, RecyclerItemShimmerBinding> {
         bind {
            root.startShimmer()
         }
      }

   private fun setupShimmerLoading() {
      shimmerAdapter.submitList(listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10))
      binding.recyclerShimmerLoading.layoutManager = LinearLayoutManager(requireContext())
      binding.recyclerShimmerLoading.adapter = shimmerAdapter
   }
}