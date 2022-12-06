package com.android.bdkstock.screens.main.menu.sales

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
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

      }
      listeners {

      }
   }

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)

      binding.buttonAddClient.setOnClickListener {
         val args = NewOrderFragmentDirections
            .actionNewOrderFragmentToClientForOrderFragment()
         findTopNavController().navigate(args)
      }

      binding.buttonAddDriver.setOnClickListener {
         val args = NewOrderFragmentDirections
            .actionNewOrderFragmentToDriversForOrderFragment()
         findTopNavController().navigate(args)
      }

      binding.buttonAddProduct.setOnClickListener {
         val args = NewOrderFragmentDirections
            .actionNewOrderFragmentToProductsForOrderFragment()
         findTopNavController().navigate(args)
      }

      val savedStateHandle = findTopNavController().currentBackStackEntry
         ?.savedStateHandle

      savedStateHandle
         ?.getLiveData<ClientEntity>(CLIENT_SELECTION_KEY)
         ?.observe(viewLifecycleOwner) {
            if(it == null) return@observe
            with(binding) {
               inputClientName.setText(it.fullName)
               inputClientMobile.setText(it.phoneNumber)
               inputClientAddress.setText(it.address)
            }
         }

      savedStateHandle
         ?.getLiveData<DriverEntity>(DRIVER_SELECTION_KEY)
         ?.observe(viewLifecycleOwner) {
            if(it == null) return@observe
            with(binding) {
               inputDriverName.setText(it.driverFullName)
               inputDriverMobile.setText(it.phoneNumber)
               inputDriverAddress.setText(it.autoRegNumber)
            }
         }

      savedStateHandle
         ?.getLiveData<ProductSelectionItem>(PRODUCT_SELECTION_KEY)
         ?.observe(viewLifecycleOwner) {
            if(it == null) return@observe
            viewModel.setProduct(it)
            findTopNavController()
               .currentBackStackEntry
               ?.savedStateHandle
               ?.remove<ProductSelectionItem>(PRODUCT_SELECTION_KEY)
         }

//      savedStateHandle?.remove<ProductSelectionItem>(PRODUCT_SELECTION_KEY)
//      savedStateHandle?.remove<ClientEntity>(CLIENT_SELECTION_KEY)
//      savedStateHandle?.remove<DriverEntity>(DRIVER_SELECTION_KEY)

      setupRecyclerView()
   }

   private fun setupRecyclerView() {
      binding.recyclerProducts.layoutManager = LinearLayoutManager(requireContext())
      binding.recyclerProducts.adapter = adapter
      viewModel.productsList.observe(viewLifecycleOwner) {
         adapter.submitList(it)
         binding.recyclerProducts.adapter = adapter
      }
   }

   private companion object {
      const val CLIENT_SELECTION_KEY = "chosen_client"
      const val DRIVER_SELECTION_KEY = "chosen_driver"
      const val PRODUCT_SELECTION_KEY = "chosen_product"
   }
}