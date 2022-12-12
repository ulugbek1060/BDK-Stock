package com.android.bdkstock.screens.main.menu.products

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.bdkstock.R
import com.android.bdkstock.databinding.FragmentProductOperationsListBinding
import com.android.bdkstock.databinding.ProgressItemBiggerBinding
import com.android.bdkstock.databinding.RecyclerItemIngredientOperationBinding
import com.android.bdkstock.screens.main.ActionsFragmentDirections
import com.android.bdkstock.screens.main.base.BaseFragment
import com.android.bdkstock.screens.main.base.DefaultLoadStateAdapter
import com.android.bdkstock.screens.main.base.pagingAdapter
import com.android.bdkstock.views.findTopNavController
import com.android.model.repository.products.entity.ProductOperationEntity
import com.android.model.utils.AuthException
import com.android.model.utils.observeEvent
import com.elveum.elementadapter.simpleAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import java.util.*

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class ProductOperationsListFragment :
   BaseFragment<ProductOperationsListViewModel, FragmentProductOperationsListBinding>() {

   override val viewModel: ProductOperationsListViewModel by viewModels()
   override fun getViewBinding() = FragmentProductOperationsListBinding.inflate(layoutInflater)
   private lateinit var layoutManager: LinearLayoutManager

   @SuppressLint("SetTextI18n")
   private val adapter =
      pagingAdapter<ProductOperationEntity, RecyclerItemIngredientOperationBinding> {
         areItemsSame = { oldItem, newItem -> oldItem.id == newItem.id }
         bind { ingredient ->
            // status
            val statusTextColor =
               if (ingredient.status == 1) root.context.getColor(R.color.red)
               else root.context.getColor(R.color.green)
            val statusText =
               if (ingredient.status == 1) root.context.getString(R.string.expense)
               else root.context.getString(R.string.income)

            tvStatus.text = statusText
            tvStatus.setTextColor(statusTextColor)

            tvIngredient.text = ingredient.name
            tvCreator.text = ingredient.creator
            tvCreatedDate.text = ingredient.createdAt.formatDate(root.context)
            tvAmount.text = ": ${ingredient.amount} ${ingredient.unit}"
         }
         listeners {
            root.onClick {
               val args = ActionsFragmentDirections
                  .actionActionsFragmentToDetailOperationsFragment(it)
               findTopNavController().navigate(args)
            }
         }
      }

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      observeProducts()
   }

   private fun observeProducts() = lifecycleScope.launchWhenStarted {
      viewModel.operationsFlow.collectLatest {
         adapter.submitData(it)
      }
   }

   @SuppressLint("UseCompatLoadingForDrawables")
   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)

      setupProgress()
      setupRecyclerView()
      setupRefreshLayout()

      handleViewVisibility()

      observeAuthError()
      getFilterResult()

      binding.buttonManufacture.setOnClickListener { manufactureOnClick() }
      binding.buttonExport.setOnClickListener { exportOnclick() }
      binding.buttonShow.setOnClickListener { showOnClick() }

      requireActivity().addMenuProvider(menuProvider, viewLifecycleOwner, Lifecycle.State.STARTED)
   }

   private fun showOnClick() {
      findTopNavController().navigate(R.id.action_actionsFragment_to_productsFragment)
   }

   private fun exportOnclick() {
      val args = ActionsFragmentDirections
         .actionActionsFragmentToProductOperationsFragment(EXPORTED_PRODUCT)
      findTopNavController().navigate(args)
   }

   private fun manufactureOnClick() {
      val args = ActionsFragmentDirections
         .actionActionsFragmentToProductOperationsFragment(MANUFACTURED_PRODUCT)
      findTopNavController().navigate(args)
   }

   private val menuProvider = object : MenuProvider {
      override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
         menuInflater.inflate(R.menu.menu_filter, menu)
      }

      override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
         if (menuItem.itemId == R.id.filter) {
            val args = ProductOperationsListFragmentDirections
               .actionToProductsFilter(viewModel.getFilter())
            findNavController().navigate(args)
         }
         return false
      }
   }

   private fun getFilterResult() {
      setFragmentResultListener(PRODUCTS_FILTER_KEY) { _, bundle ->
         val filterData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            bundle.getSerializable(PRODUCTS_FILTER_BUNDLE_KEY, ProductsFilterData::class.java)
         } else {
            bundle.getSerializable(PRODUCTS_FILTER_BUNDLE_KEY) as ProductsFilterData
         } ?: ProductsFilterData()
         viewModel.setFilterData(filterData)
      }
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

   private fun handleViewVisibility() = lifecycleScope.launchWhenStarted {
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

   private val progressAdapter = simpleAdapter<Any, ProgressItemBiggerBinding> {}
   private fun setupProgress() {
      progressAdapter.submitList(listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10))
      binding.recyclerProgress.layoutManager = LinearLayoutManager(requireContext())
      binding.recyclerProgress.adapter = progressAdapter
   }

   private companion object {
      const val PRODUCTS_FILTER_KEY = "ingredients_filter"
      const val PRODUCTS_FILTER_BUNDLE_KEY = "ingredients_filter_bundle"
      const val MANUFACTURED_PRODUCT = 0
      const val EXPORTED_PRODUCT = 1
   }
}