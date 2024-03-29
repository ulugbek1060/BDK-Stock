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
import androidx.recyclerview.widget.RecyclerView
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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@AndroidEntryPoint
class IngredientsTypeFragment :
   BaseFragment<IngredientsTypeViewModel, FragmentIngredientsTypeBinding>(),
   androidx.appcompat.widget.SearchView.OnQueryTextListener {

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
      setFabBehaviorOnRecycler()

      handleViewVisibility()

      observeAuthError()

      binding.extendedFab.setOnClickListener { addOnClick() }

      requireActivity().addMenuProvider(menuProvider, viewLifecycleOwner, Lifecycle.State.STARTED)
   }

   private val menuProvider = object : MenuProvider {
      override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
         menuInflater.inflate(R.menu.menu_search, menu)
         val myActionMenuItem = menu.findItem(R.id.search)
         val searchView = myActionMenuItem.actionView as androidx.appcompat.widget.SearchView
         searchView.setOnQueryTextListener(this@IngredientsTypeFragment)
      }

      override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
         return false
      }
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

   private fun setFabBehaviorOnRecycler() {
      binding.recyclerIngredients.addOnScrollListener(object : RecyclerView.OnScrollListener() {
         override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            if (dy > 0 && binding.extendedFab.isVisible) {
               binding.extendedFab.hide()
            } else if (dy < 0 && !binding.extendedFab.isVisible) {
               binding.extendedFab.show()
            }
         }
      })
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

   override fun onQueryTextSubmit(query: String?): Boolean {
      return false
   }

   override fun onQueryTextChange(newText: String?): Boolean {
      return viewModel.setQuery(newText)
   }
}