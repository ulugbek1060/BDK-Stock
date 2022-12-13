package com.android.bdkstock.screens.main.menu.products

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.bdkstock.R
import com.android.bdkstock.databinding.DialogAddIngredirntBinding
import com.android.bdkstock.databinding.FragmentAddProductBinding
import com.android.bdkstock.databinding.RecyclerSingleItemBinding
import com.android.bdkstock.screens.main.base.BaseFragment
import com.android.bdkstock.views.findTopNavController
import com.android.model.repository.ingredients.entity.SimpleIngredient
import com.android.model.utils.*
import com.elveum.elementadapter.simpleAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class AddProductFragment : BaseFragment<AddProductViewModel, FragmentAddProductBinding>() {

   override val viewModel by viewModels<AddProductViewModel>()
   override fun getViewBinding() = FragmentAddProductBinding.inflate(layoutInflater)
   private lateinit var layoutManager: LinearLayoutManager

   private val adapter =
      simpleAdapter<SimpleIngredient, RecyclerSingleItemBinding> {
         areItemsSame = { oldItem, newItem -> oldItem.id == newItem.id }
         bind { ingredient ->
            tvName.text = ingredient.name
            tvAmount.text = ingredient.amount
            tvUnit.text = ingredient.unit
         }
         listeners {
            buttonDelete.onClick { ingredient ->
               viewModel.removeIngredient(ingredient)
            }
         }
      }

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)

      obServeState()
      setupRecyclerView()
      observeSelectedIngredient()
      fetchIngredientsForSelection()
      observeGoBackEvent()

      binding.buttonAddIngredient.setOnClickListener { addIngredientOnClick() }
      binding.buttonSave.setOnClickListener { saveOnClick() }
   }

   private fun observeGoBackEvent() {
      viewModel.goBack.observeEvent(viewLifecycleOwner) { message ->
         findTopNavController().popBackStack()
         findTopNavController()
            .navigate(R.id.successMessageFragment, bundleOf("success_message" to message))
      }
   }

   private fun saveOnClick() {
      viewModel.createProduct(
         name = binding.inputName.text.toString(),
         price = binding.inputPrice.text.toString(),
         unit = binding.inputUnit.text.toString()
      )
   }

   private fun setupRecyclerView() {
      layoutManager = LinearLayoutManager(requireContext())
      binding.recyclerViewIngredient.layoutManager = layoutManager
      binding.recyclerViewIngredient.adapter = adapter
   }

   private fun observeSelectedIngredient() {
      viewModel.selectedIngredients.observe(viewLifecycleOwner) {
         binding.recyclerViewIngredient.adapter = adapter
         adapter.submitList(it)
      }
   }

   private fun fetchIngredientsForSelection() = lifecycleScope.launchWhenStarted {
      viewModel.ingredientsFlow.collectLatest { result ->
         when (result) {
            is Success -> {
               binding.progressbar.gone()
               binding.mainContainer.visible()
               viewModel.setAllIngredients(result.value)
            }
            is Pending -> {
               binding.mainContainer.gone()
               binding.progressbar.visible()
            }
            else -> {
               binding.mainContainer.gone()
               binding.progressbar.visible()
            }
         }
      }
   }

   private fun obServeState() {
      viewModel.state.observe(viewLifecycleOwner) { state ->
         binding.inputLayoutName.isEnabled = !state.isInProgress
         binding.inputLayoutUnit.isEnabled = !state.isInProgress
         binding.inputLayoutPrice.isEnabled = !state.isInProgress

         binding.inputLayoutName.error = state.getNameError(requireContext())
         binding.inputLayoutUnit.error = state.getUnitError(requireContext())
         binding.inputLayoutPrice.error = state.getPriceError(requireContext())

         binding.buttonSave.isVisible = !state.isInProgress
         binding.progressbar.isVisible = state.isInProgress
         binding.buttonAddIngredient.isEnabled = !state.isInProgress
      }
   }

   // -- show bottom sheet dialog for adding ingredient

   private fun addIngredientOnClick() {
      val dialog = BottomSheetDialog(requireContext())
      val dialogBinding = DialogAddIngredirntBinding.inflate(layoutInflater)
      dialog.setContentView(dialogBinding.root)

      var ingredientId: Long? = null

      viewModel.ingredientList.observe(viewLifecycleOwner) { list ->
         val adapter = ArrayAdapter(requireContext(), R.layout.spinner_item, list)
         dialogBinding.inputIngredient.setAdapter(adapter)

         dialogBinding.inputIngredient.setOnItemClickListener { _, _, position, _ ->
            ingredientId = list[position].id
            dialogBinding.inputUnit.setText(list[position].unit)
         }
      }

      dialogBinding.buttonSave.setOnClickListener {
         if (ingredientId == null) {
            Toast.makeText(requireContext(), R.string.error_empty_ingredient, Toast.LENGTH_SHORT)
               .show()
            return@setOnClickListener
         }
         if (dialogBinding.inputAmount.text.toString().isBlank()) {
            Toast.makeText(requireContext(), R.string.error_empty_amount, Toast.LENGTH_SHORT).show()
            return@setOnClickListener
         }
         val simpleIngredient = SimpleIngredient(
            id = ingredientId!!,
            name = dialogBinding.inputIngredient.text.toString(),
            unit = dialogBinding.inputUnit.text.toString()
         )
         simpleIngredient.amount = dialogBinding.inputAmount.text.toString()

         viewModel.addIngredient(simpleIngredient)
         dialog.dismiss()
      }

      dialog.show()
   }

}