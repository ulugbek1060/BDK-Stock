package com.android.bdkstock.screens.main.menu.sales

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.bdkstock.databinding.FragmentOrderDetailBinding
import com.android.bdkstock.databinding.RecyclerItemOrderProductBinding
import com.android.bdkstock.screens.main.base.BaseAdapter
import com.android.bdkstock.screens.main.base.BaseFragment
import com.android.bdkstock.screens.main.base.ViewHolderCreator
import com.android.model.repository.products.entity.ProductSelectionItem
import com.android.model.repository.sales.entity.OrderedProduct
import com.android.model.utils.gone
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrderDetailFragment :
   BaseFragment<OrderListViewModel, FragmentOrderDetailBinding>() {

   override val viewModel by viewModels<OrderDetailViewModel>()
   override fun getViewBinding() = FragmentOrderDetailBinding.inflate(layoutInflater)
   private lateinit var layoutManager: LinearLayoutManager

   private val viewHolderInflater = object : ViewHolderCreator<RecyclerItemOrderProductBinding> {
      override fun inflateBinding(layoutInflater: LayoutInflater, viewGroup: ViewGroup) =
         RecyclerItemOrderProductBinding.inflate(layoutInflater, viewGroup, false)
   }

   private val adapter =
      BaseAdapter<OrderedProduct, RecyclerItemOrderProductBinding>(viewHolderInflater) { product ->
         tvProductName.text = product.name
         tvWeight.text = "${product.amount} ${product.unit}"
         tvTotalSum.text = product.calculate()
         buttonRemove.gone()
      }

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)

      setupRecyclerView()
      observeState()
   }

   private fun setupRecyclerView() {
      layoutManager = LinearLayoutManager(requireContext())
      binding.recyclerProducts.layoutManager = layoutManager
      binding.recyclerProducts.adapter = adapter
      binding.recyclerProducts.setHasFixedSize(true)
   }

   private fun observeState() {
      viewModel.state.observe(viewLifecycleOwner) { state ->

         binding.progressbar.isVisible = state.isInProgress
         binding.mainContainer.isVisible = !state.isInProgress

         val order = state.orderEntity
         val client = order?.client
         val driver = order?.driver
         val products = order?.products?: listOf()

         binding.inputClientName.setText(client?.fullName)
         binding.inputClientMobile.setText(client?.phoneNumber)
         binding.inputClientAddress.setText(client?.address)

         binding.inputDriverName.setText(driver?.name)
         binding.inputDriverMobile.setText(driver?.phoneNumber)
         binding.inputDriverAutoNumber.setText(driver?.autoRegNumber)

         adapter.submitList(products)
         binding.ivEmpty.isVisible = products.isEmpty()
      }
   }
}