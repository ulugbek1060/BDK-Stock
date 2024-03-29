package com.android.bdkstock.screens.main.menu.sales

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.bdkstock.R
import com.android.bdkstock.databinding.FragmentOrderDetailBinding
import com.android.bdkstock.databinding.RecyclerSingleItemBinding
import com.android.bdkstock.screens.main.base.BaseFragment
import com.android.bdkstock.screens.main.base.adapters.BaseAdapter
import com.android.bdkstock.screens.main.base.adapters.ViewHolderCreator
import com.android.bdkstock.views.findTopNavController
import com.android.bdkstock.views.getActionBar
import com.android.model.repository.sales.entity.OrderedProduct
import com.android.model.utils.gone
import com.android.model.utils.observeEvent
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "OrderDetailFragment"

@AndroidEntryPoint
class OrderDetailFragment :
   BaseFragment<OrderListViewModel, FragmentOrderDetailBinding>() {

   override val viewModel by viewModels<OrderDetailViewModel>()
   override fun getViewBinding() = FragmentOrderDetailBinding.inflate(layoutInflater)
   private lateinit var layoutManager: LinearLayoutManager

   private val viewHolderInflater = object : ViewHolderCreator<RecyclerSingleItemBinding> {
      override fun inflateBinding(layoutInflater: LayoutInflater, viewGroup: ViewGroup) =
         RecyclerSingleItemBinding.inflate(layoutInflater, viewGroup, false)
   }

   private val adapter =
      BaseAdapter<OrderedProduct, RecyclerSingleItemBinding>(viewHolderInflater) { product ->
         tvName.text = product.name
         tvAmount.text = product.amount
         tvUnit.text = product.unit
         buttonDelete.gone()
      }

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)

      setupRecyclerView()
      observeState()
      observePayError()
      fabBehavior()
      observePaymentResult()

      binding.fabPay.setOnClickListener { viewModel.payment() }
      binding.buttonCancel.setOnClickListener { cancelOnClick() }
   }

   private fun fabBehavior() {
      binding.mainContainer.setOnScrollChangeListener { v, x, y, oldX, oldY ->
         if (viewModel.getPayForOrderPerm()) {
            if (viewModel.statusIsEqualZero()) {
               if (y > oldY) {
                  binding.fabPay.hide()
               } else {
                  binding.fabPay.show()
               }
            }
         }
      }
   }

   private fun cancelOnClick() {
      AlertDialog.Builder(requireContext())
         .setTitle(getString(R.string.cancel_order))
         .setMessage(getString(R.string.cancel_order_decription))
         .setNegativeButton(getString(R.string.no)) { _, _ -> }
         .setPositiveButton(getString(R.string.yes)) { _, _ ->
            viewModel.cancelOrder()
         }
         .create()
         .show()
   }

   private fun observePaymentResult() {
      setFragmentResultListener(PAYMENTS_KEY) { _, bundle ->
         if (bundle.getBoolean(PAYMENTS_BUNDLE_KEY)) {
            viewModel.getOrder()
         }
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
         val order = state.orderEntity
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
         binding.tvTotalWeight.text = order?.calculateTotalWeight()

         if (products.isNotEmpty()) adapter.submitList(products)

         if (state.fabVisibility() != null)
            binding.fabPay.isVisible = state.fabVisibility() ?: false

         getActionBar()?.title = state.getIdentification()
         binding.tvIdentification.text = state?.getStatusText(requireContext())
         binding.tvIdentification.setTextColor(state.getStatusColor(requireContext()))
         binding.ivIndicator.setColorFilter(state.getStatusColor(requireContext()))
         binding.ivIndicator.setImageDrawable(state.getStatusIndicator(requireContext()))

         binding.progressbar.isVisible = state.isInProgress
         binding.mainContainer.isVisible = !state.isInProgress
         binding.fabPay.isVisible = state.payOrderPerm
         binding.buttonCancel.isVisible = state.cancelOrderPerm
      }
   }

   private companion object {
      const val PAYMENTS_KEY = "payments"
      const val PAYMENTS_BUNDLE_KEY = "payments_bundle"
   }
}