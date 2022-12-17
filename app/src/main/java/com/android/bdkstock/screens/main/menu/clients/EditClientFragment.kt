package com.android.bdkstock.screens.main.menu.clients

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import com.android.bdkstock.databinding.FragmentEditClientBinding
import com.android.bdkstock.screens.main.base.BaseFragment
import com.android.bdkstock.views.findTopNavController
import com.android.model.utils.observeEvent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditClientFragment : BaseFragment<EditClientViewModel, FragmentEditClientBinding>() {
   override val viewModel: EditClientViewModel by viewModels()
   override fun getViewBinding() = FragmentEditClientBinding.inflate(layoutInflater)

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)

      observeState()
      observeClient()
      observeNavigateBack()

      binding.buttonSave.setOnClickListener { saveOnClick() }
      binding.buttonDelete.setOnClickListener { deleteOnClick() }
   }

   private fun observeNavigateBack() {
      viewModel.navigateBack.observeEvent(viewLifecycleOwner){
         setFragmentResult(CLIENT_ENTITY_KEY, bundleOf(CLIENT_ENTITY_BUNDLE_KEY to it))
         findTopNavController().popBackStack()
      }
   }

   private fun deleteOnClick() {

   }

   private fun observeClient() {
      viewModel.client.observe(viewLifecycleOwner) { client ->
         with(binding) {
            inputName.setText(client.fullName)
            inputPhoneNumber.setText(client.phoneNumber)
            inputAddress.setText(client.address)
         }
      }
   }


   private fun saveOnClick() {
      viewModel.updateClient(
         fullName = binding.inputName.text.toString(),
         phoneNumber = "998${binding.inputPhoneNumber.text.toString()}",
         address = binding.inputAddress.text.toString()
      )
   }

   private fun observeState() {
      viewModel.state.observe(viewLifecycleOwner) { state ->

         binding.inputLayoutName.isEnabled = !state.isInProgress
         binding.inputLayoutPhoneNumber.isEnabled = !state.isInProgress
         binding.inputLayoutAddress.isEnabled = !state.isInProgress

         binding.inputLayoutName.error = state.getFullNameErrorMessage(requireContext())
         binding.inputLayoutPhoneNumber.error = state.getPhoneNumberErrorMessage(requireContext())
         binding.inputLayoutAddress.error = state.getAddressErrorMessage(requireContext())

         binding.buttonSave.isVisible = !state.isInProgress
         binding.buttonDelete.isVisible = !state.isInProgress
         binding.progressbar.isVisible = state.isInProgress
      }
   }

   private companion object{
      const val CLIENT_ENTITY_KEY = "client_entity"
      const val CLIENT_ENTITY_BUNDLE_KEY = "client_entity_bundle"
   }
}