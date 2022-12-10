package com.android.bdkstock.screens.main.menu.sales

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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.bdkstock.R
import com.android.bdkstock.databinding.FragmentClientsForOrderBinding
import com.android.bdkstock.databinding.ProgressItemSmallerBinding
import com.android.bdkstock.databinding.RecyclerItemClientBinding
import com.android.bdkstock.screens.main.base.BaseFragment
import com.android.bdkstock.screens.main.menu.clients.ClientsViewModel
import com.android.bdkstock.screens.main.base.DefaultLoadStateAdapter
import com.android.bdkstock.views.findTopNavController
import com.android.bdkstock.screens.main.base.pagingAdapter
import com.android.model.repository.clients.entity.ClientEntity
import com.android.model.utils.AuthException
import com.android.model.utils.observeEvent
import com.elveum.elementadapter.simpleAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@FlowPreview
@ExperimentalCoroutinesApi
@AndroidEntryPoint
class ClientsForOrderFragment :
   BaseFragment<ClientsViewModel, FragmentClientsForOrderBinding>(),
   SearchView.OnQueryTextListener {

   override val viewModel: ClientsViewModel by viewModels()
   override fun getViewBinding() = FragmentClientsForOrderBinding.inflate(layoutInflater)
   private lateinit var layoutManager: LinearLayoutManager
   private lateinit var searchView: SearchView

   private val adapter = pagingAdapter<ClientEntity, RecyclerItemClientBinding> {
      areItemsSame = { oldItem, newItem -> oldItem.clientId == newItem.clientId }
      areContentsSame = { oldItem, newItem -> oldItem == newItem }
      bind { client ->
         tvFullname.text = client.fullName
         tvPhoneNumber.text = client.phoneNumber
      }
      listeners {
         root.onClick {
            showConfirmDialog(it)
         }
      }
   }

   private fun showConfirmDialog(client: ClientEntity) {
      AlertDialog.Builder(requireContext())
         .setTitle("${getString(R.string.client)}: ${client.fullName}")
         .setMessage("${getString(R.string.phone_number)}: ${client.phoneNumber}")
         .setNegativeButton(getString(R.string.close)) { _, _ -> }
         .setPositiveButton(getString(R.string.choose)) { _, _ ->
            val navController = findTopNavController()

            navController
               .previousBackStackEntry
               ?.savedStateHandle
               ?.set(CLIENT_SELECTION_KEY, client)

            navController
               .popBackStack()

         }
         .create()
         .show()
   }

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      observeClients()
   }

   private fun observeClients() = lifecycleScope.launch {
      viewModel.clientFlow.collectLatest {
         adapter.submitData(it)
      }
   }

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)

      setupShimmerLoading()
      setupRecyclerView()
      setupRefreshLayout()

      handleViewVisibility()

      observeAuthError()

      binding.extendedFab.setOnClickListener { fabOnClick() }

      requireActivity().addMenuProvider(menuProvider, viewLifecycleOwner, Lifecycle.State.STARTED)
   }

   private val menuProvider = object : MenuProvider {
      override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
         menuInflater.inflate(R.menu.menu_search, menu)
         val myActionMenuItem = menu.findItem(R.id.search)
         searchView = myActionMenuItem.actionView as SearchView
         searchView.setOnQueryTextListener(this@ClientsForOrderFragment)
      }

      override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
         return false
      }
   }

   private fun fabOnClick() {
      val args = ClientsForOrderFragmentDirections
         .actionClientForOrderFragmentToRegisterClientsFragment(false)
      findTopNavController().navigate(args)
   }

   private fun setupRefreshLayout() {
      binding.refreshLayout.setOnRefreshListener {
         adapter.refresh()
      }
   }

   private fun setupRecyclerView() = binding.apply {
      layoutManager = LinearLayoutManager(requireContext())
      binding.recyclerClients.layoutManager = layoutManager

      binding.recyclerClients.adapter = adapter.withLoadStateHeaderAndFooter(
         footer = DefaultLoadStateAdapter(binding.refreshLayout) { adapter.retry() },
         header = DefaultLoadStateAdapter(binding.refreshLayout) { adapter.retry() }
      )

      binding.recyclerClients.itemAnimator = null

      setFabBehaviorOnRecycler()
   }

   private fun setFabBehaviorOnRecycler() {
      binding.recyclerClients.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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

   private fun handleViewVisibility() = lifecycleScope.launchWhenStarted {
      adapter
         .loadStateFlow
         .map { it.refresh }
         .collectLatest { loadState ->

            binding.recyclerProgress.isVisible = loadState == LoadState.Loading
            binding.recyclerClients.isVisible = loadState != LoadState.Loading

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

   // -- Progress with shimmer layout

   private val shimmerAdapter = simpleAdapter<Any, ProgressItemSmallerBinding> {}
   private fun setupShimmerLoading() {
      shimmerAdapter.submitList(listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10))
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

   private companion object {
      const val CLIENT_SELECTION_KEY = "chosen_client"
   }
}