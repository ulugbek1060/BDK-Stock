package com.android.bdkstock.screens.main.menu.clients

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.android.bdkstock.databinding.FragmentRegisterClientsBinding
import com.android.bdkstock.screens.main.ActionsFragmentDirections
import com.android.bdkstock.screens.main.base.BaseFragment
import com.android.bdkstock.views.findTopNavController
import com.android.model.utils.observeEvent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterClientsFragment :
   BaseFragment<RegisterClientsViewModel, FragmentRegisterClientsBinding>() {

   override val viewModel by viewModels<RegisterClientsViewModel>()
   override fun getViewBinding() = FragmentRegisterClientsBinding.inflate(layoutInflater)

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)

      observeState()

      observeNavigation()

      binding.buttonSave.setOnClickListener { saveOnClick() }
   }

   private fun observeNavigation() {
      viewModel.navigateToDisplay.observeEvent(viewLifecycleOwner) { (displayUser, clientEntity) ->
         findTopNavController().popBackStack()
         if (displayUser) findTopNavController().navigate(
            ActionsFragmentDirections
               .actionActionsFragmentToDisplayClientsFragment(
                  clientEntity
               )
         )
      }
   }

   private fun saveOnClick() {
      viewModel.registerClients(
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
         binding.progressbar.isVisible = state.isInProgress
      }
   }
}