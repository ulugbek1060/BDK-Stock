package com.android.bdkstock.screens.main.menu.sales

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.SavedStateHandle
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.bdkstock.databinding.FragmentNewOrderBinding
import com.android.bdkstock.databinding.RecyclerItemOrderProductBinding
import com.android.bdkstock.screens.main.base.BaseFragment
import com.android.bdkstock.views.findTopNavController
import com.android.model.repository.clients.entity.ClientEntity
import com.android.model.repository.drivers.entity.DriverEntity
import com.android.model.repository.products.entity.ProductSelectionItem
import com.elveum.elementadapter.simpleAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewOrderFragment : BaseFragment<NewOrderViewModel, FragmentNewOrderBinding>() {

   override val viewModel by viewModels<NewOrderViewModel>()
   override fun getViewBinding() = FragmentNewOrderBinding.inflate(layoutInflater)

   private val adapter = simpleAdapter<ProductSelectionItem, RecyclerItemOrderProductBinding> {
      areItemsSame = { oldItem, newItem -> oldItem.id == newItem.id }
      bind {
         tvProductName.text = it.name
         tvWeight.text = "${it.amount} ${it.unit}"
         tvTotalSum.text = it.calculate()
      }
      listeners {
         root.onClick {
            // TODO: need
         }
      }
   }

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)

      val savedStateHandle = findTopNavController()
         .currentBackStackEntry
         ?.savedStateHandle

      observeClientData(savedStateHandle)
      observeDriverData(savedStateHandle)
      observeProductData(savedStateHandle)

      binding.buttonAddClient.setOnClickListener { addClientButtonClick() }
      binding.buttonAddDriver.setOnClickListener { addDriverOnClick() }
      binding.buttonAddProduct.setOnClickListener { addProductOnClick() }

      setupRecyclerView()
      observeProductList()

      requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
   }

   private fun observeProductList() = viewModel.productsList.observe(viewLifecycleOwner) {
      adapter.submitList(it)
   }

   private fun observeProductData(savedStateHandle: SavedStateHandle?) {
      savedStateHandle
         ?.getLiveData<ProductSelectionItem>(PRODUCT_SELECTION_KEY)
         ?.observe(viewLifecycleOwner) {
            val savedState = findTopNavController()
               .currentBackStackEntry
               ?.savedStateHandle ?: return@observe

            if (savedState.contains(PRODUCT_SELECTION_KEY)) {
               viewModel.setProduct(it)
               savedState.remove<ProductSelectionItem>(PRODUCT_SELECTION_KEY)
            }
         }
   }

   private fun observeDriverData(savedStateHandle: SavedStateHandle?) {
      savedStateHandle
         ?.getLiveData<DriverEntity>(DRIVER_SELECTION_KEY)
         ?.observe(viewLifecycleOwner) {
            if (it == null) return@observe
            with(binding) {
               inputDriverName.setText(it.driverFullName)
               inputDriverMobile.setText(it.phoneNumber)
               inputDriverAddress.setText(it.autoRegNumber)
            }
         }
   }

   private fun observeClientData(savedStateHandle: SavedStateHandle?) {
      savedStateHandle
         ?.getLiveData<ClientEntity>(CLIENT_SELECTION_KEY)
         ?.observe(viewLifecycleOwner) {
            if (it == null) return@observe
            with(binding) {
               inputClientName.setText(it.fullName)
               inputClientMobile.setText(it.phoneNumber)
               inputClientAddress.setText(it.address)
            }
         }
   }

   private fun addProductOnClick() {
      val args = NewOrderFragmentDirections
         .actionNewOrderFragmentToProductsForOrderFragment()
      findTopNavController().navigate(args)
   }

   private fun addDriverOnClick() {
      val args = NewOrderFragmentDirections
         .actionNewOrderFragmentToDriversForOrderFragment()
      findTopNavController().navigate(args)
   }

   private fun addClientButtonClick() {
      val args = NewOrderFragmentDirections
         .actionNewOrderFragmentToClientForOrderFragment()
      findTopNavController().navigate(args)
   }

   private fun setupRecyclerView() {
      binding.recyclerProducts.layoutManager = LinearLayoutManager(requireContext())
      binding.recyclerProducts.adapter = adapter
      binding.recyclerProducts.setHasFixedSize(true)
   }

   private val callback = object : OnBackPressedCallback(true) {
      override fun handleOnBackPressed() {
         AlertDialog.Builder(requireContext())
            .setTitle("Exit")
            .setMessage("Do you want to exit from this page and remove all saved dates")
            .setNegativeButton("NO") { _, _ -> }
            .setPositiveButton("YES") { _, _ ->
               findTopNavController().popBackStack()
            }
            .show()
      }
   }

   private companion object {
      const val CLIENT_SELECTION_KEY = "chosen_client"
      const val DRIVER_SELECTION_KEY = "chosen_driver"
      const val PRODUCT_SELECTION_KEY = "chosen_product"
   }
}