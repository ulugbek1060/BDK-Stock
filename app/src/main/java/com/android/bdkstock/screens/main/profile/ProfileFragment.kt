package com.android.bdkstock.screens.main.profile

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.android.bdkstock.R
import com.android.bdkstock.databinding.FragmentOperateIngredientsBinding
import com.android.bdkstock.databinding.FragmentProfileBinding
import com.android.bdkstock.screens.main.base.BaseFragment
import com.android.bdkstock.views.navigate
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment :
   BaseFragment<ProfileViewModel, FragmentProfileBinding>() {

   override val viewModel by viewModels<ProfileViewModel>()
   override fun getViewBinding() = FragmentProfileBinding.inflate(layoutInflater)

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)

      viewModel.accountInfo.observe(viewLifecycleOwner) {
         binding.inputName.setText(it.firstname)
         binding.inputSurname.setText(it.lastname)
         binding.inputPhoneNumber.setText(it.phoneNumber)
         binding.inputAddress.setText(it.address)
      }

      binding.buttonSettings.setOnClickListener {
         navigate(R.id.action_profileFragment_to_settingsFragment)
      }
   }
}