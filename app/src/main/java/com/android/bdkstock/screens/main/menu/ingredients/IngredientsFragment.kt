package com.android.bdkstock.screens.main.menu.ingredients

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.bdkstock.R
import com.android.bdkstock.databinding.FragmentIngredientsBinding
import com.android.bdkstock.databinding.RecyclerItemIngredientBinding
import com.android.bdkstock.screens.main.base.BaseFragment
import com.android.bdkstock.views.DefaultLoadStateAdapter
import com.android.bdkstock.views.pagingAdapter
import com.android.model.repository.ingredients.entity.IngredientExOrInEntity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class IngredientsFragment : BaseFragment(R.layout.fragment_ingredients) {

   override val viewModel by viewModels<IngredientsViewModel>()
   private lateinit var binding: FragmentIngredientsBinding
   private lateinit var layoutManager: LinearLayoutManager

   @SuppressLint("SetTextI18n")
   private val adapter = pagingAdapter<IngredientExOrInEntity, RecyclerItemIngredientBinding> {
      areItemsSame = { oldItem, newItem -> oldItem.id == newItem.id }
      bind { ingredient ->
         // status
         val statusColor =
            if (ingredient.status == "1") root.context.getColor(R.color.white_red)
            else root.context.getColor(R.color.white_green)
         val statusTextColor =
            if (ingredient.status == "1") root.context.getColor(R.color.red)
            else root.context.getColor(R.color.green)
         val statusText =
            if (ingredient.status == "1") root.context.getString(R.string.expense)
            else root.context.getString(R.string.income)

         tvStatus.text = statusText
         tvStatus.setTextColor(statusTextColor)
         containerFrameStatus.setBackgroundColor(statusColor)

         tvIngredient.text = ingredient.name
         tvCreator.text = ingredient.creator
         tvCreatedDate.text = ingredient.createdAt.formatDate(root.context)
         tvAmount.text = "${ingredient.amount} ${ingredient.unit}"
      }
      listeners {
         root.onClick {

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

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)
      binding = FragmentIngredientsBinding.bind(view)

      setupRecyclerView()
      setupRefreshLayout()
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
}