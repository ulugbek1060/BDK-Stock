package com.android.bdkstock.screens.main.menu.ingredients

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.bdkstock.R
import com.android.bdkstock.databinding.FragmentDisplayIngredientsBinding
import com.android.bdkstock.databinding.RecyclerItemIngredientOperationBinding
import com.android.bdkstock.screens.main.base.BaseFragment
import com.android.bdkstock.views.DefaultLoadStateAdapter
import com.android.bdkstock.views.findTopNavController
import com.android.bdkstock.views.pagingAdapter
import com.android.model.repository.ingredients.entity.IngredientExOrInEntity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DisplayIngredientsFragment :
   BaseFragment<DisplayIngredientsViewModel, FragmentDisplayIngredientsBinding>() {


   override val viewModel by viewModels<DisplayIngredientsViewModel>()
   private lateinit var layoutManager: LinearLayoutManager
   override fun getViewBinding() = FragmentDisplayIngredientsBinding.inflate(layoutInflater)

   @SuppressLint("SetTextI18n")
   private val adapter =
      pagingAdapter<IngredientExOrInEntity, RecyclerItemIngredientOperationBinding> {
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
            tvCreatedDate.text = ingredient.createdAt
            tvAmount.text = "${ingredient.amount} ${ingredient.unit}"
         }
         listeners {
            root.onClick {
               val args = DisplayIngredientsFragmentDirections
                  .actionDisplayIngredientsFragmentToDisplayOperationsFragment(it)
               findTopNavController().navigate(args)
            }
         }
      }

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)

      setupRecyclerView()
      observeIngredients()
      observeIngredientFields()
   }

   private fun observeIngredients() = lifecycleScope.launch {
      viewModel.ingredientsOperationsFlow.collectLatest {
         adapter.submitData(it)
      }
   }

   private fun observeIngredientFields() {
      viewModel.ingredientEntity.observe(viewLifecycleOwner) { ingredient ->
         binding.tvName.text = ingredient.name
         binding.tvAmount.text = "${ingredient.amount} ${ingredient.unit}"
      }
   }

   private fun setupRecyclerView() = binding.apply {
      layoutManager = LinearLayoutManager(requireContext())
      binding.recyclerIngredients.layoutManager = layoutManager

      binding.recyclerIngredients.adapter = adapter.withLoadStateHeaderAndFooter(
         footer = DefaultLoadStateAdapter() { adapter.retry() },
         header = DefaultLoadStateAdapter() { adapter.retry() }
      )

      binding.recyclerIngredients.itemAnimator = null
   }
}