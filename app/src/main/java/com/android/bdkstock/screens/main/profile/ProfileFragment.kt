package com.android.bdkstock.screens.main.profile

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.android.bdkstock.R
import com.android.bdkstock.databinding.FragmentProfileBinding
import com.android.bdkstock.screens.main.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : BaseFragment(R.layout.fragment_profile) {

   private lateinit var binding: FragmentProfileBinding
   override val viewModel by viewModels<ProfileViewModel>()

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)
      binding = FragmentProfileBinding.bind(view)

      viewModel.accountInfo.observe(viewLifecycleOwner) {
         binding.inputName.setText(it.firstname)
         binding.inputSurname.setText(it.lastname)
         binding.inputPhoneNumber.setText(it.phoneNumber)
         binding.inputAddress.setText(it.address)
      }
   }
}