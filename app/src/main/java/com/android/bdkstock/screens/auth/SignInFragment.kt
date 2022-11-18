package com.android.bdkstock.screens.auth

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.android.bdkstock.R
import com.android.bdkstock.databinding.FragmentSignInBinding
import com.android.model.utils.clear
import com.android.model.utils.observeEvent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignInFragment : Fragment(R.layout.fragment_sign_in) {

   private lateinit var binding: FragmentSignInBinding

   private val viewModel by viewModels<SignInViewModel>()

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)
      binding = FragmentSignInBinding.bind(view)

      binding.buttonSingIn.setOnClickListener { signInButtonPressed() }

      viewModel.state.observe(viewLifecycleOwner) { state ->
         binding.inputLayoutPhoneNumber.error = getPhoneNumberErrorText(state.emptyPhoneNumberError)
         binding.inputLayoutPassword.error = getPasswordErrorText(state.emptyPasswordError)
         binding.inputPhoneNumber.isEnabled = !state.signInInProgress
         binding.inputPassword.isEnabled = !state.signInInProgress
         setProgressState(state.signInInProgress)
      }

      observeNavigation()
      observeAuthError()
      observeClearText()
      observeMessageFromBackend()
   }

   private fun observeMessageFromBackend() {
      viewModel.showCauseMessage.observeEvent(viewLifecycleOwner) {
         Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
      }
   }

   // TODO: need to initialize a dialog
   private fun observeAuthError() = viewModel.showAuthError.observeEvent(viewLifecycleOwner) {
      Toast.makeText(
         requireContext(),
         R.string.invalid_phone_number_or_password,
         Toast.LENGTH_SHORT
      ).show()
   }

   private fun signInButtonPressed() {
      if (binding.inputPhoneNumber.text.toString().isNotBlank())
         viewModel.signIn(
            phoneNumber = getFormattedNumber(binding.inputPhoneNumber.text.toString()),
            password = binding.inputPassword.text.toString()
         )
   }

   private fun observeNavigation() {
      viewModel.navigate.observeEvent(viewLifecycleOwner) {
         findNavController().navigate(R.id.action_signInFragment_to_actionsFragment, null,
            navOptions {
               anim {
                  enter = R.anim.enter
                  exit = R.anim.exit
                  popEnter = R.anim.pop_enter
                  popExit = R.anim.pop_exit
               }
               popUpTo(R.id.signInFragment) {
                  inclusive = true
               }
            }
         )
      }
   }

   private fun observeClearText() = viewModel.clearFields.observeEvent(viewLifecycleOwner) {
      binding.inputPassword.clear()
      binding.inputPhoneNumber.clear()
   }

   private fun getFormattedNumber(number: String) = "998$number"

   private fun setProgressState(signInInProgress: Boolean) = binding.apply {
      lottiProgress.isVisible = signInInProgress
      buttonSingIn.isVisible = !signInInProgress
   }

   private fun getPhoneNumberErrorText(state: Boolean) =
      if (state) requireContext().getString(R.string.error_empty_phone_number) else null

   private fun getPasswordErrorText(state: Boolean) =
      if (state) requireContext().getString(R.string.error_empty_password) else null
}