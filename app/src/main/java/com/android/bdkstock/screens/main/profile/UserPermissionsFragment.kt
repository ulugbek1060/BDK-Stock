package com.android.bdkstock.screens.main.profile

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.android.bdkstock.R
import com.android.bdkstock.databinding.FragmentUserPermissionsBinding
import com.android.bdkstock.screens.main.base.BaseBottomSheetDialogFragment
import com.android.model.utils.observeEvent
import com.google.android.material.button.MaterialButtonToggleGroup
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class UserPermissionsFragment :
   BaseBottomSheetDialogFragment<FragmentUserPermissionsBinding>(), View.OnClickListener {

   private val viewModel: UserPermissionsViewModel by viewModels()
   override fun getViewBinding() = FragmentUserPermissionsBinding.inflate(layoutInflater)

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)

      observeState()
      observeJobs()
      observeSelectedPermissions()
      observeErrorMessage()
      observeNavigateBack()
      setListenerToggleButtons()

      binding.buttonSave.setOnClickListener { saveOnClick() }
   }

   private fun observeErrorMessage() {
      viewModel.errorMessage.observeEvent(viewLifecycleOwner) {
         Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
      }
   }

   private fun observeNavigateBack() {
      viewModel.navigateBack.observeEvent(viewLifecycleOwner) {
         findNavController().popBackStack()
      }
   }

   private fun saveOnClick() {
      viewModel.updatePermissions()
   }

   private fun setListenerToggleButtons() {
      binding.layoutButtons.children.forEach { view ->
         if (view is ViewGroup) {
            view.children.forEach { child ->
               child.setOnClickListener(this)
            }
         }
      }
   }

   private fun observeJobs() {
      viewModel.jobs.observe(viewLifecycleOwner) { jobs ->
         val adapter = ArrayAdapter(requireContext(), R.layout.spinner_item, jobs)
         binding.autoCompleteJobTitle.setAdapter(adapter)
         binding.autoCompleteJobTitle.setOnItemClickListener { _, _, position, _ ->
            viewModel.getPermissionOfJobTitle(jobId = jobs[position].id)
         }
      }
   }

   private fun observeSelectedPermissions() = viewModel
      .selectedPermsOfJofTitle
      .observe(viewLifecycleOwner) { permsList ->
         binding.layoutButtons.children.forEach { viewGroup ->
            if (viewGroup is ViewGroup) {
               (viewGroup as MaterialButtonToggleGroup).clearChecked()
               viewGroup.children.forEach { button ->
                  permsList?.forEach { permId ->
                     if (button.tag.toString().toInt() == permId) {
                        viewGroup.check(button.id)
                     }
                  }
               }
            }
         }
      }

   private fun observeState() = viewModel.state.observe(viewLifecycleOwner) { state ->
      binding.mainContainer.isVisible = !state.isInProgress
      binding.progressbar.isVisible = state.isInProgress
      binding.buttonSave.isEnabled = !state.isInProgress
      binding.inputJobTitle.isEnabled = !state.isInProgress

      binding.inputJobTitle.error = state.getJobErrorMessage(requireContext())
   }

   override fun onClick(view: View?) {
      viewModel.setSelectedPerms(view?.tag.toString().toIntOrNull())
   }
}