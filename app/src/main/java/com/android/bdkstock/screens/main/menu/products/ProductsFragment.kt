package com.android.bdkstock.screens.main.menu.products

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
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.bdkstock.R
import com.android.bdkstock.databinding.FragmentProductsBinding
import com.android.bdkstock.databinding.ProgressItemSmallerBinding
import com.android.bdkstock.databinding.RecyclerItemIngredientBinding
import com.android.bdkstock.screens.main.base.BaseFragment
import com.android.bdkstock.screens.main.base.adapters.DefaultLoadStateAdapter
import com.android.bdkstock.screens.main.base.adapters.pagingAdapter
import com.android.bdkstock.views.findTopNavController
import com.android.model.repository.products.entity.ProductEntity
import com.android.model.utils.AuthException
import com.android.model.utils.observeEvent
import com.elveum.elementadapter.simpleAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class ProductsFragment :
   BaseFragment<ProductsViewModel, FragmentProductsBinding>(),
   SearchView.OnQueryTextListener {

   override val viewModel: ProductsViewModel by viewModels()
   override fun getViewBinding() = FragmentProductsBinding.inflate(layoutInflater)
   private lateinit var layoutManager: LinearLayoutManager

   @SuppressLint("SetTextI18n")
   private val adapter = pagingAdapter<ProductEntity, RecyclerItemIngredientBinding> {
      areItemsSame = { oldItem, newItem -> oldItem.id == newItem.id }
      bind { ingredient ->
         tvName.text = ingredient.name
         tvAmount.text = "${ingredient.amount} ${ingredient.unit}"
      }
      listeners {
         root.onClick {
            val args = ProductsFragmentDirections
               .actionProductsFragmentToDisplayProductsFragment(it)
            findTopNavController().navigate(args)
         }
      }
   }

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      observeIngredients()
   }


   private fun observeIngredients() = lifecycleScope.launch {
      viewModel.productsFlow.collectLatest {
         adapter.submitData(it)
      }
   }

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)

      setupProgress()
      setupRecyclerView()
      setupRefreshLayout()

      handleViewVisibility()

      observeAuthError()

      binding.extendedFab.setOnClickListener { addOnClick() }

      requireActivity().addMenuProvider(menuProvider, viewLifecycleOwner, Lifecycle.State.STARTED)
   }

   private val menuProvider = object : MenuProvider {
      override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
         menuInflater.inflate(R.menu.menu_search, menu)
         val myActionMenuItem = menu.findItem(R.id.search)
         val searchView = myActionMenuItem.actionView as SearchView
         searchView.setOnQueryTextListener(this@ProductsFragment)
      }

      override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
         return false
      }
   }

   private fun addOnClick() {
      findTopNavController().navigate(R.id.action_productsFragment_to_addProductFragment)
   }

   private fun setupRecyclerView() = binding.apply {
      layoutManager = LinearLayoutManager(requireContext())
      binding.recyclerProducts.layoutManager = layoutManager

      binding.recyclerProducts.adapter = adapter.withLoadStateHeaderAndFooter(
         footer = DefaultLoadStateAdapter(binding.refreshLayout) { adapter.retry() },
         header = DefaultLoadStateAdapter(binding.refreshLayout) { adapter.retry() }
      )

      binding.recyclerProducts.itemAnimator = null
   }

   private fun setupRefreshLayout() {
      binding.refreshLayout.setOnRefreshListener {
         adapter.refresh()
      }
   }

   private fun handleViewVisibility() = lifecycleScope.launch {
      adapter
         .loadStateFlow
         .map { it.refresh }
         .collectLatest { loadState ->

            binding.recyclerProgress.isVisible = loadState == LoadState.Loading
            binding.refreshLayout.isVisible = loadState != LoadState.Loading

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

   private val progressAdapter = simpleAdapter<Any, ProgressItemSmallerBinding> {}
   private fun setupProgress() {
      progressAdapter.submitList(listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10))
      binding.recyclerProgress.layoutManager = LinearLayoutManager(requireContext())
      binding.recyclerProgress.adapter = progressAdapter
   }

   override fun onQueryTextSubmit(query: String?): Boolean {
      return true
   }

   override fun onQueryTextChange(newText: String?): Boolean {
      viewModel.setQuery(newText)
      return true
   }
}