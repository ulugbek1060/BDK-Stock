package com.android.bdkstock.screens.main.menu.ingredients

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
import com.android.bdkstock.databinding.FragmentIngredientsTypeBinding
import com.android.bdkstock.databinding.RecyclerSingleItemBinding
import com.android.bdkstock.screens.main.base.BaseFragment
import com.android.bdkstock.screens.main.base.adapters.DefaultLoadStateAdapter
import com.android.bdkstock.screens.main.base.adapters.pagingAdapter
import com.android.bdkstock.views.findTopNavController
import com.android.model.repository.ingredients.entity.IngredientEntity
import com.android.model.utils.AuthException
import com.android.model.utils.gone
import com.android.model.utils.observeEvent
import com.elveum.elementadapter.simpleAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@AndroidEntryPoint
class IngredientsTypeFragment :
   BaseFragment<IngredientsTypeViewModel, FragmentIngredientsTypeBinding>() {

   override val viewModel by viewModels<IngredientsTypeViewModel>()
   private lateinit var layoutManager: LinearLayoutManager
   override fun getViewBinding() = FragmentIngredientsTypeBinding.inflate(layoutInflater)

   @SuppressLint("SetTextI18n")
   private val adapter = pagingAdapter<IngredientEntity, RecyclerSingleItemBinding> {
      areItemsSame = { oldItem, newItem -> oldItem.id == newItem.id }
      bind { ingredient ->
         tvName.text = ingredient.name
         tvAmount.text = ingredient.amount
         tvUnit.text = ingredient.unit
         buttonDelete.gone()
      }
      listeners {
         root.onClick {
            val args = IngredientsTypeFragmentDirections
               .actionIngredientsTypeFragmentToDisplayIngredientsFragment(it)
            findTopNavController().navigate(args)
         }
      }
   }

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      observeIngredients()
   }

   private fun observeIngredients() = lifecycleScope.launch {
      viewModel.ingredientsFlow.collectLatest {
         adapter.submitData(it)
      }
   }

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)

      setupRecyclerView()
      setupRefreshLayout()

      handleViewVisibility()

      observeAuthError()

      binding.extendedFab.setOnClickListener { addOnClick() }
   }

   private fun addOnClick() {
      findTopNavController().navigate(R.id.action_ingredientsTypeFragment_to_addIngredientsFragment)
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

   private fun handleViewVisibility() = lifecycleScope.launch {
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
}