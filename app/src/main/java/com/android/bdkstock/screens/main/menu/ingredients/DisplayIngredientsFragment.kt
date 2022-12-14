package com.android.bdkstock.screens.main.menu.ingredients

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.bdkstock.R
import com.android.bdkstock.databinding.FragmentDisplayIngredientsBinding
import com.android.bdkstock.databinding.RecyclerItemOperationBinding
import com.android.bdkstock.screens.main.base.BaseWithFabFragment
import com.android.bdkstock.screens.main.base.adapters.DefaultLoadStateAdapter
import com.android.bdkstock.screens.main.base.adapters.pagingAdapter
import com.android.bdkstock.views.findTopNavController
import com.android.bdkstock.views.getActionBar
import com.android.model.repository.ingredients.entity.IngredientExOrInEntity
import com.android.model.utils.AuthException
import com.android.model.utils.observeEvent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DisplayIngredientsFragment :
   BaseWithFabFragment<DisplayIngredientsViewModel, FragmentDisplayIngredientsBinding>() {

   override val viewModel by viewModels<DisplayIngredientsViewModel>()
   override fun getViewBinding() = FragmentDisplayIngredientsBinding.inflate(layoutInflater)
   private lateinit var layoutManager: LinearLayoutManager

   @SuppressLint("SetTextI18n")
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
            tvName.text = statusText
            tvName.setTextColor(statusColor)
            tvComment.text = ingredient.comment
            tvAmount.text = ingredient.amount
            tvUnit.text = ingredient.unit
         }
         listeners {
            root.onClick {
               val args = DisplayIngredientsFragmentDirections
                  .actionDisplayIngredientsFragmentToDisplayOperationsFragment(it)
               findTopNavController().navigate(args)
            }
         }
      }

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      observeIngredients()
   }

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)

      setupRecyclerView()
      setupRefreshLayout()
      observeIngredientFields()
      setFabBehaviorOnMotion()

      handleViewVisibility()

      observeAuthError()

      binding.fabOptions.setOnClickListener { fabOperationsOnClick() }
      binding.fabImport.setOnClickListener { }
      binding.fabExport.setOnClickListener { }
   }

   private fun fabOperationsOnClick() = onAddButtonClicked(
      binding.fabOptions,
      listOf(
         binding.fabExport,
         binding.fabImport
      ),
      listOf(
         binding.tvExport,
         binding.tvImport
      )
   )


   private fun observeIngredients() = lifecycleScope.launch {
      viewModel.ingredientsOperationsFlow.collectLatest {
         adapter.submitData(it)
      }
   }

   private fun observeIngredientFields() {
      viewModel.ingredientEntity.observe(viewLifecycleOwner) { ingredient ->

         getActionBar()?.title = ingredient.name

         with(binding) {
            tvIngredient.text = ingredient.name
            tvAmount.text = "${ingredient.amount} ${ingredient.unit}"
            tvDate.text = ingredient.createdAt
         }
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

   private fun setFabBehaviorOnMotion() {
      binding.motionLayout.addTransitionListener(object : MotionLayout.TransitionListener {
         override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) = Unit
         override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) = Unit
         override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) = Unit

         override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
            if (currentId == R.id.autoCompleteToEnd && binding.fabOptions.isVisible) {
               if (isFabChildesVisible()) fabOperationsOnClick()
               binding.fabOptions.hide()
            } else if (currentId == R.id.start && !binding.fabOptions.isVisible) {
               binding.fabOptions.show()
            }
         }

      })
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
      const val OPERATION_IMPORT = 0
      const val OPERATION_EXPORT = 1
   }
}