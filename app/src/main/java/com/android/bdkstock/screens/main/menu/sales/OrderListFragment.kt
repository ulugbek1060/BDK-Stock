package com.android.bdkstock.screens.main.menu.sales

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
import com.android.bdkstock.databinding.FragmentOrderListBinding
import com.android.bdkstock.databinding.RecyclerItemOrderBinding
import com.android.bdkstock.screens.main.ActionsFragmentDirections
import com.android.bdkstock.screens.main.base.BaseFragment
import com.android.bdkstock.screens.main.base.adapters.DefaultLoadStateAdapter
import com.android.bdkstock.screens.main.base.adapters.pagingAdapter
import com.android.bdkstock.views.findTopNavController
import com.android.model.repository.sales.entity.OrderListItem
import com.android.model.utils.AuthException
import com.android.model.utils.observeEvent
import com.elveum.elementadapter.simpleAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map

@AndroidEntryPoint
class OrderListFragment : BaseFragment<OrderListViewModel, FragmentOrderListBinding>() {

   override val viewModel by viewModels<OrderListViewModel>()
   override fun getViewBinding() = FragmentOrderListBinding.inflate(layoutInflater)
   private lateinit var layoutManager: LinearLayoutManager

   @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables")
   private val adapter = pagingAdapter<OrderListItem, RecyclerItemOrderBinding> {
      areItemsSame = { oldItem, newItem -> oldItem.id == newItem.id }
      bind { order ->
         val statusColor = when (order.status) {
            1 -> root.context.getColor(R.color.green)
            0 -> root.context.getColor(R.color.orange)
            else -> root.context.getColor(R.color.red)
         }

         val statusText = when (order.status) {
            1 -> root.context.getString(R.string.sale_status_1)
            0 -> root.context.getString(R.string.sale_status_0)
            else -> root.context.getString(R.string.cancel)
         }

         val indicatorIcon = when (order.status) {
            1 -> root.context.getDrawable(R.drawable.ic_sales)
            0 -> root.context.getDrawable(R.drawable.ic_time)
            else -> root.context.getDrawable(R.drawable.ic_cancel)
         }

         icStatus.setImageDrawable(indicatorIcon)
         icStatus.setColorFilter(statusColor)
         tvStatus.text = statusText
         tvStatus.setTextColor(statusColor)
         tvName.text = order.identification
         tvBuyerName.text = order.client
         tvBuyerPhoneNumber.text = "no phone number"
         tvSum.text = order.summa
         tvUnit.text = "UZS"
      }
      listeners {
         root.onClick {
            val args = ActionsFragmentDirections
               .actionActionsFragmentToOrderDetailFragment(it.id)
            findTopNavController().navigate(args)
         }
      }
   }

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      observeProducts()
   }

   private fun observeProducts() = lifecycleScope.launchWhenStarted {
      viewModel.ordersFlow.collectLatest {
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

      getFilterDataResult()

      binding.extendedFab.setOnClickListener { fabOnClick() }

      requireActivity().addMenuProvider(menuProvider, viewLifecycleOwner, Lifecycle.State.STARTED)
   }

   private fun getFilterDataResult() {
      setFragmentResultListener(ORDER_FILTER_DATA_KEY) { _, bundle ->
         val result = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            bundle.getSerializable(ORDER_FILTER_DATA_BUNDLE_KEY, OrdersFilterData::class.java)
         } else {
            bundle.getSerializable(ORDER_FILTER_DATA_BUNDLE_KEY) as OrdersFilterData
         } ?: OrdersFilterData()
         viewModel.setFilterData(result)
      }
   }

   private fun fabOnClick() {
      findTopNavController().navigate(R.id.action_actionsFragment_to_newOrderFragment)
   }

   private val menuProvider = object : MenuProvider {
      override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
         menuInflater.inflate(R.menu.menu_filter, menu)
      }

      override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
         if (menuItem.itemId == R.id.filter) {
            val args = OrderListFragmentDirections
               .actionSalesFragmentToFilterOrdersFragment(viewModel.getFilterData())
            findNavController().navigate(args)
         }
         return false
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
            if (dy > 0 && binding.extendedFab.isVisible) {
               binding.extendedFab.hide()
            } else if (dy < 0 && !binding.extendedFab.isVisible) {
               binding.extendedFab.show()
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
      const val MANUFACTURED_PRODUCT = 0
      const val EXPORTED_PRODUCT = 1
      const val ORDER_FILTER_DATA_KEY = "order_filter_data"
      const val ORDER_FILTER_DATA_BUNDLE_KEY = "order_filter_data_bundle"
   }
}
