package com.android.bdkstock.screens.main.menu.products

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.AbsListView
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.bdkstock.R
import com.android.bdkstock.databinding.FragmentProductOperationsListBinding
import com.android.bdkstock.databinding.RecyclerItemOperationBinding
import com.android.bdkstock.screens.main.ActionsFragmentDirections
import com.android.bdkstock.screens.main.base.BaseWithFabFragment
import com.android.bdkstock.screens.main.base.adapters.DefaultLoadStateAdapter
import com.android.bdkstock.screens.main.base.adapters.pagingAdapter
import com.android.bdkstock.views.findTopNavController
import com.android.model.repository.products.entity.ProductOperationEntity
import com.android.model.utils.AuthException
import com.android.model.utils.observeEvent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import java.util.*

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class ProductOperationsListFragment :
   BaseWithFabFragment<ProductOperationsListViewModel, FragmentProductOperationsListBinding>() {

   override val viewModel: ProductOperationsListViewModel by viewModels()
   override fun getViewBinding() = FragmentProductOperationsListBinding.inflate(layoutInflater)
   private lateinit var layoutManager: LinearLayoutManager

   private val TAG = "ProductOperationsListFr"

   @SuppressLint("SetTextI18n")
   private val adapter =
      pagingAdapter<ProductOperationEntity, RecyclerItemOperationBinding> {
         areItemsSame = { oldItem, newItem -> oldItem.id == newItem.id }
         bind { products ->

            val statusColor =
               if (products.status == EXPORTED_PRODUCT) root.context.getColor(R.color.orange)
               else root.context.getColor(R.color.green)

            val statusText =
               if (products.status == EXPORTED_PRODUCT) root.context.getString(R.string.expense)
               else root.context.getString(R.string.income)

            val statusIcon =
               if (products.status == EXPORTED_PRODUCT) root.context.getDrawable(R.drawable.ic_export)
               else root.context.getDrawable(R.drawable.ic_import)

            icStatus.setImageDrawable(statusIcon)
            icStatus.setColorFilter(statusColor)
            tvStatus.text = statusText
            tvStatus.setTextColor(statusColor)
            tvName.text = products.name
            tvComment.text = products.comment
            tvAmount.text = products.amount
            tvUnit.text = products.unit

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
      setupRecyclerView()
      setupRefreshLayout()
      setFabBehaviorOnRecycler()

      handleViewVisibility()

      observeAuthError()
      getFilterResult()

      binding.fabManufacture.setOnClickListener { manufactureOnClick() }
      binding.fabExport.setOnClickListener { exportOnclick() }
      binding.fabProducts.setOnClickListener { showOnClick() }
      binding.fabOptions.setOnClickListener { fabOperationsOnClick() }

      requireActivity().addMenuProvider(menuProvider, viewLifecycleOwner, Lifecycle.State.STARTED)
   }

   private fun fabOperationsOnClick() = onAddButtonClicked(
      binding.fabOptions,
      listOf(
         binding.fabProducts,
         binding.fabManufacture,
         binding.fabExport
      ),
      listOf(
         binding.tvProducts,
         binding.tvManufacture,
         binding.tvExport
      )
   )

   private fun showOnClick() {
      findTopNavController().navigate(R.id.action_actionsFragment_to_productsFragment)
   }

   private fun exportOnclick() {
      val args = ActionsFragmentDirections
         .actionsFragmentToProductOperations(EXPORTED_PRODUCT)
      findTopNavController().navigate(args)
   }

   private fun manufactureOnClick() {
      val args = ActionsFragmentDirections
         .actionsFragmentToProductOperations(MANUFACTURED_PRODUCT)
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

   private fun setFabBehaviorOnRecycler() {
      binding.recyclerProducts.addOnScrollListener(object : RecyclerView.OnScrollListener() {
         override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            if (dy > 0 && binding.fabOptions.isVisible) {
               if (isFabChildesVisible()) fabOperationsOnClick()
               binding.fabOptions.hide()
            } else if (dy < 0 && !binding.fabOptions.isVisible) {
               binding.fabOptions.show()
            }
         }

         override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            when (newState) {
               AbsListView.OnScrollListener.SCROLL_STATE_FLING -> {}
               AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL -> {}
               else -> {}
            }
         }
      })
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

            binding.progressbar.isVisible = loadState == LoadState.Loading
            binding.refreshLayout.isVisible = loadState != LoadState.Loading

            if (loadState is LoadState.NotLoading)
               binding.ivEmpty.isVisible = adapter.snapshot().isEmpty()

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

   private companion object {
      const val PRODUCTS_FILTER_KEY = "ingredients_filter"
      const val PRODUCTS_FILTER_BUNDLE_KEY = "ingredients_filter_bundle"
      const val MANUFACTURED_PRODUCT = 0
      const val EXPORTED_PRODUCT = 1
   }
}