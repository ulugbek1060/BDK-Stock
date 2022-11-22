package com.android.bdkstock.screens.main.menu.ingredients

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.bdkstock.R
import com.android.bdkstock.databinding.FragmentIngredientSelectionBinding
import com.android.bdkstock.databinding.RecyclerItemIngredientSelectionBinding
import com.android.bdkstock.views.DefaultLoadStateAdapter
import com.android.bdkstock.views.findTopNavController
import com.android.bdkstock.views.pagingAdapter
import com.android.bdkstock.views.setWidthAndHeightPercent
import com.android.model.repository.ingredients.entity.IngredientEntity
import com.android.model.utils.AuthException
import com.android.model.utils.observeEvent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@AndroidEntryPoint
class IngredientSelectionFragment : DialogFragment(R.layout.fragment_ingredient_selection) {

   private val viewModel by viewModels<IngredientSelectionViewModel>()
   private lateinit var binding: FragmentIngredientSelectionBinding
   private lateinit var layoutManager: LinearLayoutManager

   @SuppressLint("SetTextI18n")
   private val adapter = pagingAdapter<IngredientEntity, RecyclerItemIngredientSelectionBinding> {
      areItemsSame = { oldItem, newItem -> oldItem.id == newItem.id }
      bind { ingredient ->
         tvName.text = ingredient.name
         tvAmount.text = "${ingredient.amount} ${ingredient.unit}"
      }
      listeners {
         root.onClick {
            findTopNavController().previousBackStackEntry?.savedStateHandle?.set(SELECTED_INGREDIENT_KEY, it)
            findTopNavController().popBackStack()
         }
      }
   }

   override fun getTheme(): Int = R.style.MyDialog

   private fun observeIngredients() = lifecycleScope.launch {
      viewModel.ingredientsFlow.collectLatest {
         adapter.submitData(it)
      }
   }

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)
      binding = FragmentIngredientSelectionBinding.bind(view)

      setWidthAndHeightPercent(90, 60)
      observeIngredients()

      binding.searchText.doAfterTextChanged {
         viewModel.setQuery(it.toString())
      }

      setupRecycler()
      setupRefreshLayout()

      handleViewVisibility()
      observeAuthError()

   }

   private fun setupRecycler() = binding.apply {
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

   private fun handleViewVisibility() = lifecycleScope.launch {
      adapter
         .loadStateFlow
         .map { it.refresh }
         .collectLatest { loadState ->

            binding.lottiProgress.isVisible = loadState == LoadState.Loading
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

   companion object{
      const val SELECTED_INGREDIENT_KEY = "ingredient_key"
   }
}