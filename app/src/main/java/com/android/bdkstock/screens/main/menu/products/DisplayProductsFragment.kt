package com.android.bdkstock.screens.main.menu.products

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.bdkstock.R
import com.android.bdkstock.databinding.FragmentDisplayProductsBinding
import com.android.bdkstock.databinding.RecyclerItemOperationBinding
import com.android.bdkstock.screens.main.base.BaseFragment
import com.android.bdkstock.screens.main.base.adapters.DefaultLoadStateAdapter
import com.android.bdkstock.screens.main.base.adapters.pagingAdapter
import com.android.bdkstock.views.findTopNavController
import com.android.model.repository.products.entity.ProductOperationEntity
import com.android.model.utils.AuthException
import com.android.model.utils.observeEvent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DisplayProductsFragment :
   BaseFragment<DisplayProductsViewModel, FragmentDisplayProductsBinding>() {

   override val viewModel by viewModels<DisplayProductsViewModel>()
   override fun getViewBinding() = FragmentDisplayProductsBinding.inflate(layoutInflater)
   private lateinit var layoutManager: LinearLayoutManager

   @SuppressLint("SetTextI18n")
   private val adapter =
      pagingAdapter<ProductOperationEntity, RecyclerItemOperationBinding> {
         areItemsSame = { oldItem, newItem -> oldItem.id == newItem.id }
         bind { products ->

            val statusColor =
               if (products.status == EXPORTED_PRODUCT) root.context.getColor(R.color.red)
               else root.context.getColor(R.color.green)

            val statusText =
               if (products.status == EXPORTED_PRODUCT) root.context.getString(R.string.expense)
               else root.context.getString(R.string.income)

            val statusIcon =
               if (products.status == EXPORTED_PRODUCT) root.context.getDrawable(R.drawable.ic_import)
               else root.context.getDrawable(R.drawable.ic_export)

            icStatus.setImageDrawable(statusIcon)
            icStatus.setColorFilter(statusColor)
            tvName.text = statusText
            tvName.setTextColor(statusColor)
            tvComment.text = products.comment
            tvAmount.text = products.amount
            tvUnit.text = products.unit
         }
         listeners {
            root.onClick {
               val args = DisplayProductsFragmentDirections
                  .actionDisplayProductsFragmentToDetailOperationsFragment(it)
               findTopNavController().navigate(args)
            }
         }
      }

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      observeIngredients()
   }

   private fun observeIngredients() = lifecycleScope.launch {
      viewModel.operationsFlow.collectLatest {
         adapter.submitData(it)
      }
   }

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)

      setupRecyclerView()
      setupRefreshLayout()
      observeIngredientFields()

      handleViewVisibility()

      observeAuthError()
   }

   private fun observeIngredientFields() {
      viewModel.ingredientEntity.observe(viewLifecycleOwner) { ingredient ->
         binding.tvName.text = ingredient.name
         binding.tvAmount.text = "${ingredient.amount} ${ingredient.unit}"
      }
   }

   private fun setupRefreshLayout() {
      binding.refreshLayout.setOnRefreshListener {
         adapter.refresh()
      }
   }

   private fun setupRecyclerView() = binding.apply {
      layoutManager = LinearLayoutManager(requireContext())
      binding.recyclerOperations.layoutManager = layoutManager

      binding.recyclerOperations.adapter = adapter.withLoadStateHeaderAndFooter(
         footer = DefaultLoadStateAdapter(binding.refreshLayout) { adapter.retry() },
         header = DefaultLoadStateAdapter(binding.refreshLayout) { adapter.retry() }
      )

      binding.recyclerOperations.itemAnimator = null
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