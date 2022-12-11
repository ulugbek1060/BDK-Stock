package com.android.bdkstock.screens.main.menu.sales

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.bdkstock.databinding.FragmentOrderDetailBinding
import com.android.bdkstock.databinding.RecyclerItemOrderProductBinding
import com.android.bdkstock.screens.main.base.BaseAdapter
import com.android.bdkstock.screens.main.base.BaseFragment
import com.android.bdkstock.screens.main.base.ViewHolderCreator
import com.android.bdkstock.views.findTopNavController
import com.android.bdkstock.views.getActionBar
import com.android.model.repository.sales.entity.OrderedProduct
import com.android.model.utils.gone
import com.android.model.utils.observeEvent
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
      observeOrder()
      observePayError()

      observePaymentResult()

      binding.fabPay.setOnClickListener { viewModel.payment() }
   }

   private fun observePaymentResult() {
      setFragmentResultListener(PAYMENTS_KEY) { _, bundle ->
         if (bundle.getBoolean(PAYMENTS_BUNDLE_KEY)) {
            viewModel.getOrder()
         }
      }
   }

   private fun observeOrder() {
      viewModel.order.observe(viewLifecycleOwner) { orderData ->
         val order = orderData.orderEntity
         val client = order?.client
         val driver = order?.driver
         val products = order?.products ?: listOf()

         binding.inputClientName.setText(client?.fullName)
         binding.inputClientMobile.setText(client?.phoneNumber)
         binding.inputClientAddress.setText(client?.address)

         binding.inputDriverName.setText(driver?.name)
         binding.inputDriverMobile.setText(driver?.phoneNumber)
         binding.inputDriverAutoNumber.setText(driver?.autoRegNumber)

         binding.tvTotalSum.text = order?.summa
         binding.tvDebit.text = order?.debit
         binding.tvPayed.text = order?.paid
         adapter.submitList(products)

         orderData.fabVisibility(binding.fabPay)

         getActionBar()?.title = orderData.getIdentification()
         binding.tvIdentification.text = orderData?.getStatusText(requireContext())
         binding.tvIdentification.setTextColor(orderData.getStatusColor(requireContext()))
         binding.ivIndicator.setColorFilter(orderData.getStatusColor(requireContext()))
         binding.ivIndicator.setImageDrawable(orderData.getStatusIndicator(requireContext()))
      }
   }

   private fun observePayError() {
      viewModel.navigatePaymentsFrag.observeEvent(viewLifecycleOwner) {
         val orderEntity = it ?: return@observeEvent
         val args = OrderDetailFragmentDirections
            .actionOrderDetailFragmentToPayFragment(orderEntity)
         findTopNavController().navigate(args)
      }
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
      }
   }

   private companion object {
      const val PAYMENTS_KEY = "payments"
      const val PAYMENTS_BUNDLE_KEY = "payments_bundle"
   }
}