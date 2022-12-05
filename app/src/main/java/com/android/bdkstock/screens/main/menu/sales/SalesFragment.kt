package com.android.bdkstock.screens.main.menu.sales

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.bdkstock.R
import com.android.bdkstock.databinding.FragmentSalesBinding
import com.android.bdkstock.databinding.ProgressItemBiggerBinding
import com.android.bdkstock.databinding.RecyclerItemOrderBinding
import com.android.bdkstock.screens.main.base.BaseFragment
import com.android.bdkstock.views.DefaultLoadStateAdapter
import com.android.bdkstock.views.pagingAdapter
import com.android.model.repository.sales.entity.OrderListItem
import com.android.model.utils.AuthException
import com.android.model.utils.observeEvent
import com.elveum.elementadapter.simpleAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map

@AndroidEntryPoint
class SalesFragment : BaseFragment<SalesViewModel, FragmentSalesBinding>() {

   override val viewModel by viewModels<SalesViewModel>()
   override fun getViewBinding() = FragmentSalesBinding.inflate(layoutInflater)
   private lateinit var layoutManager: LinearLayoutManager

   @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables")
   private val adapter = pagingAdapter<OrderListItem, RecyclerItemOrderBinding> {
      areItemsSame = { oldItem, newItem -> oldItem.id == newItem.id }
      bind { order ->
         val statusColor = when (order.status) {
               1 -> root.context.getColor(R.color.green)
               0 -> root.context.getColor(R.color.yellow)
               else -> root.context.getColor(R.color.red)
            }

         val statusText =
            if (order.status == 1) root.context.getString(R.string.expense)
            else root.context.getString(R.string.income)

         val indicatorIcon = when (order.status) {
               1 -> root.context.getDrawable(R.drawable.ic_sales)
               0 -> root.context.getDrawable(R.drawable.ic_time)
               else -> root.context.getDrawable(R.drawable.ic_cancel)
            }

         tvStatus.text = statusText
         tvStatus.setTextColor(statusColor)

         ivIndicator.setImageDrawable(indicatorIcon)
         ivIndicator.setColorFilter(statusColor)

         tvClient.text = order.client
         tvCreatedDate.text = order.createdAt
         tvSum.text = order.summa
         }
         listeners {
            root.onClick {

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
      setupProgress()
      setupRecyclerView()
      setupRefreshLayout()

      handleViewVisibility()

      observeAuthError()

      binding.buttonManufacture.setCompoundDrawablesWithIntrinsicBounds(
         requireContext().getDrawable(R.drawable.ic_add),
         null,
         null,
         null
      )
      binding.buttonExport.setCompoundDrawablesWithIntrinsicBounds(
         requireContext().getDrawable(R.drawable.ic_remove),
         null,
         null,
         null
      )

//      binding.buttonManufacture.setOnClickListener { manufactureOnClick() }
//      binding.buttonExport.setOnClickListener { exportOnclick() }
//      binding.buttonShow.setOnClickListener { showOnClick() }

      requireActivity().addMenuProvider(menuProvider, viewLifecycleOwner, Lifecycle.State.STARTED)
   }


   private val menuProvider = object : MenuProvider {
      override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
         menuInflater.inflate(R.menu.menu_filter, menu)
      }

      override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
         if (menuItem.itemId == R.id.filter) showFilterDialog()
         return false
      }
   }

   private fun showFilterDialog() {
//      val dialog = AlertDialog.Builder(requireContext()).create()
//      dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
//
//      val dialogBinding = DialogFilterOperationsBinding.inflate(layoutInflater)
//      dialog.setView(dialogBinding.root)
//
//      var status: Int? = null
//
//      dialogBinding.buttonClear.setOnClickListener {
//         viewModel.clearFilter()
//         dialog.dismiss()
//      }
//
//      dialogBinding.buttonApply.setOnClickListener {
//         viewModel.setFilterData(
//            query = dialogBinding.inputQuery.text.toString(),
//            fromDate = dialogBinding.inputFromDate.text.toString(),
//            toDate = dialogBinding.inputToDate.text.toString(),
//            status = status,
//         )
//         dialog.dismiss()
//      }
//
//      dialogBinding.buttonAll.setOnClickListener { status = null }
//      dialogBinding.buttonExpense.setOnClickListener { status =
//         ProductOperationsListFragment.EXPORTED_PRODUCT
//      }
//      dialogBinding.buttonIncome.setOnClickListener { status =
//         ProductOperationsListFragment.MANUFACTURED_PRODUCT
//      }
//
//      dialogBinding.inputLayoutFromDate.setEndIconOnClickListener {
//         getCalendarDialog(
//            requireContext().getString(R.string.from_date),
//            dialogBinding.inputFromDate
//         )
//      }
//
//      dialogBinding.inputLayoutToDate.setEndIconOnClickListener {
//         getCalendarDialog(
//            requireContext().getString(R.string.to_date),
//            dialogBinding.inputToDate
//         )
//      }
//
//      viewModel.query.observe(viewLifecycleOwner) { filterData ->
//
//         status = filterData.status
//
//         dialogBinding.radioGroup.check(getCheckId(filterData.status))
//         dialogBinding.inputQuery.setText(filterData.query)
//         dialogBinding.inputFromDate.setText(filterData.fromDate)
//         dialogBinding.inputToDate.setText(filterData.toDate)
//      }
//
//      dialog.show()
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
      progressAdapter.submitList(listOf(1, 2, 3, 4, 5, 6, 7, 8))
      binding.recyclerProgress.layoutManager = LinearLayoutManager(requireContext())
      binding.recyclerProgress.adapter = progressAdapter
   }

   private companion object {
      const val MANUFACTURED_PRODUCT = 0
      const val EXPORTED_PRODUCT = 1
   }
}
