package com.android.bdkstock.screens.main.menu.products

import android.os.Bundle
import android.view.*
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.bdkstock.R
import com.android.bdkstock.databinding.FragmentDisplayProductsBinding
import com.android.bdkstock.databinding.RecyclerSingleItemBinding
import com.android.bdkstock.screens.main.base.BaseFragment
import com.android.bdkstock.screens.main.base.adapters.BaseAdapter
import com.android.bdkstock.screens.main.base.adapters.ViewHolderCreator
import com.android.bdkstock.views.findTopNavController
import com.android.model.repository.products.entity.IngredientItem
import com.android.model.utils.Results
import com.android.model.utils.gone
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DisplayProductsFragment :
   BaseFragment<DisplayProductsViewModel, FragmentDisplayProductsBinding>() {

   override val viewModel by viewModels<DisplayProductsViewModel>()
   override fun getViewBinding() = FragmentDisplayProductsBinding.inflate(layoutInflater)
   private lateinit var ingredientsLayoutManager: LinearLayoutManager

   private val ingredientViewHolder = object : ViewHolderCreator<RecyclerSingleItemBinding> {
      override fun inflateBinding(layoutInflater: LayoutInflater, viewGroup: ViewGroup) =
         RecyclerSingleItemBinding.inflate(layoutInflater, viewGroup, false)
   }

   private val ingredientsAdapter =
      BaseAdapter<IngredientItem, RecyclerSingleItemBinding>(ingredientViewHolder) { ingredient ->
         tvName.text = ingredient.name
         tvAmount.text = ingredient.amount
         buttonDelete.gone()
      }

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      observeIngredients()
   }

   private fun observeIngredients() = lifecycleScope.launch {
      viewModel.ingredients.collectLatest { result ->
         when (result) {
            is Results.Success -> {
               val list = result.value
               ingredientsAdapter.submitList(list)
            }
            is Results.Pending -> {}
            else -> {}
         }
      }
   }

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)
      setupIngredientsRecyclerView()
      observeProduct()


      binding.buttonList.setOnClickListener { listOnClick() }
      binding.buttonExport.setOnClickListener { exportOnClick() }
      binding.buttonImport.setOnClickListener { importOnClick() }
      binding.toggleButton.setOnClickListener { /**TODO: expand */ }

      requireActivity().addMenuProvider(menuProvider, viewLifecycleOwner, Lifecycle.State.STARTED)
   }

   private val menuProvider = object : MenuProvider {
      override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
         menuInflater.inflate(R.menu.menu_product_edit, menu)
      }

      override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
         if (menuItem.itemId == R.id.edit_product) {
            val args = DisplayProductsFragmentDirections
               .displayProductsToEditProduct(viewModel.getProductId())
            findTopNavController().navigate(args)
         } else if (menuItem.itemId == R.id.edit_ingredient_of_product) {
            val args = DisplayProductsFragmentDirections
               .displayProductsToEditIngredientsOfProduct(viewModel.getProductId())
            findTopNavController().navigate(args)
         }
         return false
      }
   }

   private fun importOnClick() {
      val args = DisplayProductsFragmentDirections
         .displayProductsToProductOperations(MANUFACTURED_PRODUCT, viewModel.getProduct())
      findTopNavController().navigate(args)
   }

   private fun exportOnClick() {
      val args = DisplayProductsFragmentDirections
         .displayProductsToProductOperations(EXPORTED_PRODUCT,viewModel.getProduct())
      findTopNavController().navigate(args)
   }

   private fun listOnClick() {
      val args = DisplayProductsFragmentDirections
         .displayProductsToOperationsOfProduct(viewModel.getProductId())
      findTopNavController().navigate(args)
   }

   private fun setupIngredientsRecyclerView() {
      ingredientsLayoutManager = LinearLayoutManager(requireContext())
      binding.recyclerIngredients.layoutManager = ingredientsLayoutManager
      binding.recyclerIngredients.adapter = ingredientsAdapter
   }

   private fun observeProduct() {
      viewModel.productEntity.observe(viewLifecycleOwner) { product ->
         with(binding) {
            tvProduct.text = product.name
            tvAmount.text = "${product.amount} ${product.unit}"
            tvDate.text = product.createdAt
         }
      }
   }

   private companion object {
      const val MANUFACTURED_PRODUCT = 0
      const val EXPORTED_PRODUCT = 1
   }
}