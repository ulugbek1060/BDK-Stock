package com.android.bdkstock.screens.main.menu.sales

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import com.android.bdkstock.R
import com.android.bdkstock.databinding.FragmentPayBinding
import com.android.bdkstock.views.findTopNavController
import com.android.model.utils.observeEvent
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PayFragment : BottomSheetDialogFragment(R.layout.fragment_pay) {

   private var _binding: FragmentPayBinding? = null
   private val binding: FragmentPayBinding get() = _binding!!
   private val viewModel: PayViewModel by viewModels()

   override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?
   ) = FragmentPayBinding
      .inflate(inflater, container, false)
      .also { _binding = it }
      .root

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)

      observeState()
      observeOrder()
      observeGoBack()

      binding.buttonPay.setOnClickListener { payOnClick() }
   }

   private fun observeGoBack() {
      viewModel.goBack.observeEvent(viewLifecycleOwner) {

         setFragmentResult(PAYMENTS_KEY, bundleOf(PAYMENTS_BUNDLE_KEY to true))

         findTopNavController().popBackStack()

         findTopNavController().navigate(
            R.id.successMessageFragment,
            bundleOf(SUCCESS_MESSAGE_BUNDLE_KEY to it)
         )
      }
   }


   private fun observeOrder() {
      viewModel.order.observe(viewLifecycleOwner) { order ->
         binding.tvTotalSum.text = "${order?.summa} UZS"
         binding.tvDebit.text = "${order?.debit} UZS"
         binding.tvPayed.text = "${order?.paid} UZS"
      }
   }

   private fun observeState() {
      viewModel.state.observe(viewLifecycleOwner) { state ->

         binding.inputLayoutCard.isEnabled = !state.isInProgress
         binding.inputLayoutCash.isEnabled = !state.isInProgress

         binding.inputLayoutCard.error = state.getEmptyPayError(requireContext())
         binding.inputLayoutCash.error = state.getEmptyPayError(requireContext())

         binding.progressbar.isVisible = state.isInProgress
         binding.buttonPay.isVisible = !state.isInProgress
      }
   }

   private fun payOnClick() {
      viewModel.pay(
         cash = binding.inputCash.text.toString(),
         card = binding.inputCard.text.toString()
      )
   }

   override fun onDestroy() {
      super.onDestroy()
      _binding = null
   }

   private companion object {
      const val PAYMENTS_KEY = "payments"
      const val PAYMENTS_BUNDLE_KEY = "payments_bundle"
      const val SUCCESS_MESSAGE_BUNDLE_KEY = "success_message"
   }
}