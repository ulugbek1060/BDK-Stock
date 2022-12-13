package com.android.bdkstock.screens.main.menu.ingredients

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
import com.android.bdkstock.databinding.FragmentIngredientsOperationsBinding
import com.android.bdkstock.databinding.RecyclerItemOperationBinding
import com.android.bdkstock.screens.main.ActionsFragmentDirections
import com.android.bdkstock.screens.main.base.BaseWithFabFragment
import com.android.bdkstock.screens.main.base.adapters.DefaultLoadStateAdapter
import com.android.bdkstock.screens.main.base.adapters.pagingAdapter
import com.android.bdkstock.views.findTopNavController
import com.android.model.repository.ingredients.entity.IngredientExOrInEntity
import com.android.model.utils.AuthException
import com.android.model.utils.observeEvent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class IngredientsOperationsFragment :
   BaseWithFabFragment<IngredientsOperationsViewModel, FragmentIngredientsOperationsBinding>() {

   override val viewModel by viewModels<IngredientsOperationsViewModel>()
   private lateinit var layoutManager: LinearLayoutManager
   override fun getViewBinding() = FragmentIngredientsOperationsBinding.inflate(layoutInflater)

   private val TAG = "IngredientsOperationsFr"

   @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables")
   private val adapter =
      pagingAdapter<IngredientExOrInEntity, RecyclerItemOperationBinding> {
         areItemsSame = { oldItem, newItem -> oldItem.id == newItem.id }
         bind { ingredient ->

            val statusColor =
               if (ingredient.status == OPERATION_EXPORT) root.context.getColor(R.color.red)
               else root.context.getColor(R.color.green)

            val statusText =
               if (ingredient.status == OPERATION_EXPORT) root.context.getString(R.string.expense)
               else root.context.getString(R.string.income)

            val statusIcon =
               if (ingredient.status == OPERATION_EXPORT) root.context.getDrawable(R.drawable.ic_import)
               else root.context.getDrawable(R.drawable.ic_export)

            icStatus.setImageDrawable(statusIcon)
            icStatus.setColorFilter(statusColor)
            tvStatus.text = statusText
            tvStatus.setTextColor(statusColor)
            tvName.text = ingredient.name
            tvComment.text = ingredient.comment
            tvAmount.text = ingredient.amount
            tvUnit.text = ingredient.unit
         }
         listeners {
            root.onClick {
               val args = ActionsFragmentDirections
                  .actionActionsFragmentToDisplayOperationsFragment(it)
               findTopNavController().navigate(args)
            }
         }
      }

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      observeClients()
   }

   private fun observeClients() = lifecycleScope.launch {
      viewModel.ingredientsExAndInFlow.collectLatest {
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

      binding.fabIncome.setOnClickListener { incomeOnClick() }
      binding.fabExpense.setOnClickListener { expenseOnClick() }
      binding.fabIngredients.setOnClickListener { addOnClick() }
      binding.fabOptions.setOnClickListener { fabOperationsOnClick() }

      requireActivity().addMenuProvider(menuProvider, viewLifecycleOwner, Lifecycle.State.STARTED)
   }

   private fun fabOperationsOnClick() = onAddButtonClicked(
      binding.fabOptions,
      listOf(
         binding.fabIngredients,
         binding.fabIncome,
         binding.fabExpense
      ),
      listOf(
         binding.tvIngredients,
         binding.tvIncome,
         binding.tvExpense
      )
   )

   private val menuProvider = object : MenuProvider {
      override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
         menuInflater.inflate(R.menu.menu_filter, menu)
      }

      override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
         if (menuItem.itemId == R.id.filter) {
            val args = IngredientsOperationsFragmentDirections
               .actionToIngredientsFilter(viewModel.getFilter())
            findNavController().navigate(args)
         }
         return false
      }
   }

   private fun getFilterResult() {
      setFragmentResultListener(INGREDIENTS_FILTER_KEY) { _, bundle ->
         val filterData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            bundle.getSerializable(INGREDIENTS_FILTER_BUNDLE_KEY, IngredientsFilterData::class.java)
         } else {
            bundle.getSerializable(INGREDIENTS_FILTER_BUNDLE_KEY) as IngredientsFilterData
         } ?: IngredientsFilterData()
         viewModel.setFilterData(filterData)
      }
   }

   private fun addOnClick() {
      findTopNavController()
         .navigate(R.id.action_actionsFragment_to_ingredientsTypeFragment)
   }

   private fun expenseOnClick() {
      val args = ActionsFragmentDirections
         .actionActionsFragmentToOperateIngredientsFragment(OPERATION_EXPORT)
      findTopNavController().navigate(args)
   }

   private fun incomeOnClick() {
      val args = ActionsFragmentDirections
         .actionActionsFragmentToOperateIngredientsFragment(OPERATION_IMPORT)
      findTopNavController().navigate(args)
   }

   private fun setupRecyclerView() = binding.apply {
      layoutManager = LinearLayoutManager(requireContext())
      binding.recyclerIngredients.layoutManager = layoutManager

      binding.recyclerIngredients.adapter = adapter.withLoadStateHeaderAndFooter(
         footer = DefaultLoadStateAdapter(binding.refreshLayout) { adapter.retry() },
         header = DefaultLoadStateAdapter(binding.refreshLayout) { adapter.retry() }
      )

      binding.recyclerIngredients.itemAnimator = null
   }

   private fun setFabBehaviorOnRecycler() {
      binding.recyclerIngredients.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
      const val INGREDIENTS_FILTER_KEY = "ingredients_filter"
      const val INGREDIENTS_FILTER_BUNDLE_KEY = "ingredients_filter_bundle"
      const val OPERATION_IMPORT = 0
      const val OPERATION_EXPORT = 1
   }
}