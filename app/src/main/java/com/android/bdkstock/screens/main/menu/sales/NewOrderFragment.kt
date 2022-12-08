package com.android.bdkstock.screens.main.menu.sales

import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.SavedStateHandle
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.bdkstock.R
import com.android.bdkstock.databinding.FragmentNewOrderBinding
import com.android.bdkstock.databinding.RecyclerItemOrderProductBinding
import com.android.bdkstock.screens.main.base.BaseFragment
import com.android.bdkstock.views.findTopNavController
import com.android.model.repository.clients.entity.ClientEntity
import com.android.model.repository.drivers.entity.DriverEntity
import com.android.model.repository.products.entity.ProductSelectionItem
import com.android.model.utils.observeEvent
import com.elveum.elementadapter.simpleAdapter
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class NewOrderFragment :
   BaseFragment<NewOrderViewModel, FragmentNewOrderBinding>() {

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
      getProductData()

      setupRecyclerView()
      observeProductList()

      observeBackEvent()

      binding.buttonAddClient.setOnClickListener { addClientButtonClick() }
      binding.buttonAddDriver.setOnClickListener { addDriverOnClick() }
      binding.buttonAddProduct.setOnClickListener { addProductOnClick() }
      binding.buttonSave.setOnClickListener { saveOnClick() }

      requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
   }


   private fun saveOnClick() {
      viewModel.createOrder()
   }

   private fun observeBackEvent() {
      viewModel.goBack.observeEvent(viewLifecycleOwner) {
         findTopNavController().popBackStack()
         findTopNavController().navigate(R.id.successMessageFragment)
      }
   }

   private fun observeProductList() {
      viewModel.productsList.observe(viewLifecycleOwner) {
         adapter.submitList(it)
         adapter.notifyDataSetChanged()
      }
   }

   private fun observeDriverData(savedStateHandle: SavedStateHandle?) {
      savedStateHandle
         ?.getLiveData<DriverEntity>(DRIVER_SELECTION_KEY)
         ?.observe(viewLifecycleOwner) {
            if (it == null) return@observe
            viewModel.setDriverId(it.id)
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
            viewModel.setClientId(it.clientId)
            with(binding) {
               inputClientName.setText(it.fullName)
               inputClientMobile.setText(it.phoneNumber)
               inputClientAddress.setText(it.address)

            }
         }
   }

   private fun getProductData() {
      setFragmentResultListener(PRODUCT_SELECTION_KEY) { _, bundle ->
         val result = if (Build.VERSION.SDK_INT >= 33) {
            bundle.getParcelable(PRODUCT_BUNDLE_KEY, ProductSelectionItem::class.java)
         } else {
            bundle.getParcelable(PRODUCT_BUNDLE_KEY)
         }
         viewModel.setProduct(result)
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
            .setTitle(getString(R.string.exit))
            .setMessage("Do you want to exit from this page and remove all saved dates")
            .setNegativeButton(getString(R.string.no)) { _, _ -> }
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
               findTopNavController().popBackStack()
            }
            .show()
      }
   }

   private companion object {
      const val CLIENT_SELECTION_KEY = "chosen_client"
      const val DRIVER_SELECTION_KEY = "chosen_driver"
      const val PRODUCT_SELECTION_KEY = "chosen_product"
      const val PRODUCT_BUNDLE_KEY = "bundle_product"
   }
}