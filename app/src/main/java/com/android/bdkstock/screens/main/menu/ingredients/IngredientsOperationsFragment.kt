package com.android.bdkstock.screens.main.menu.ingredients

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
import com.android.bdkstock.databinding.FragmentIngredientsOperationsBinding
import com.android.bdkstock.databinding.ProgressItemBiggerBinding
import com.android.bdkstock.databinding.RecyclerItemIngredientOperationBinding
import com.android.bdkstock.screens.main.ActionsFragmentDirections
import com.android.bdkstock.screens.main.base.BaseFragment
import com.android.bdkstock.screens.main.base.DefaultLoadStateAdapter
import com.android.bdkstock.screens.main.base.pagingAdapter
import com.android.bdkstock.views.findTopNavController
import com.android.model.repository.ingredients.entity.IngredientExOrInEntity
import com.android.model.utils.AuthException
import com.android.model.utils.observeEvent
import com.elveum.elementadapter.simpleAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class IngredientsOperationsFragment :
   BaseFragment<IngredientsOperationsViewModel, FragmentIngredientsOperationsBinding>() {

   override val viewModel by viewModels<IngredientsOperationsViewModel>()
   private lateinit var layoutManager: LinearLayoutManager
   override fun getViewBinding() = FragmentIngredientsOperationsBinding.inflate(layoutInflater)

   @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables")
   private val adapter =
      pagingAdapter<IngredientExOrInEntity, RecyclerItemIngredientOperationBinding> {
         areItemsSame = { oldItem, newItem -> oldItem.id == newItem.id }
         bind { ingredient ->
            // status
            val statusTextColor =
               if (ingredient.status == "1") root.context.getColor(R.color.red)
               else root.context.getColor(R.color.green)
            val statusText =
               if (ingredient.status == "1") root.context.getString(R.string.expense)
               else root.context.getString(R.string.income)
            val indicator =
               if (ingredient.status == "1") root.context.getDrawable(R.drawable.ic_export)
               else root.context.getDrawable(R.drawable.ic_import)

            ivIndicator.setImageDrawable(indicator)
            ivIndicator.setColorFilter(statusTextColor)

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

      setupProgress()
      setupRecyclerView()
      setupRefreshLayout()

      handleViewVisibility()

      observeAuthError()

      binding.buttonIncome.setOnClickListener { incomeOnClick() }
      binding.buttonExpense.setOnClickListener { expenseOnClick() }
      binding.buttonAdd.setOnClickListener { addOnClick() }

      requireActivity().addMenuProvider(menuProvider, viewLifecycleOwner, Lifecycle.State.STARTED)
   }

   private val menuProvider = object : MenuProvider {
      override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
         menuInflater.inflate(R.menu.menu_filter, menu)
      }

      override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
         if (menuItem.itemId == R.id.filter) {
//            val args =
//           findTopNavController().navigate()
         }
         return false
      }
   }

   private fun addOnClick() {
      findTopNavController()
         .navigate(R.id.action_actionsFragment_to_ingredientsTypeFragment)
   }

   private fun expenseOnClick() {
      val args = ActionsFragmentDirections
         .actionActionsFragmentToOperateIngredientsFragment(OPERATION_EXPENSE)
      findTopNavController().navigate(args)
   }

   private fun incomeOnClick() {
      val args = ActionsFragmentDirections
         .actionActionsFragmentToOperateIngredientsFragment(OPERATION_INCOME)
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

   private fun getCheckId(state: Int?) = when (state) {
      1 -> R.id.button_expense
      0 -> R.id.button_income
      else -> R.id.button_all
   }

   private companion object {
      const val OPERATION_INCOME = 0
      const val OPERATION_EXPENSE = 1
   }
}