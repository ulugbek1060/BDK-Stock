package com.android.bdkstock.screens.main.menu.employees

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.bdkstock.R
import com.android.bdkstock.databinding.FragmentEmployeesBinding
import com.android.bdkstock.databinding.RecyclerItemEmployeeBinding
import com.android.bdkstock.databinding.RecyclerItemShimmerBinding
import com.android.bdkstock.screens.main.ActivityFragmentDirections
import com.android.bdkstock.screens.main.base.BaseFragment
import com.android.model.repository.employees.entity.EmployeeEntity
import com.elveum.elementadapter.simpleAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest

@ExperimentalCoroutinesApi
@FlowPreview
@AndroidEntryPoint
class EmployeesFragment : BaseFragment(R.layout.fragment_employees) {

   private lateinit var binding: FragmentEmployeesBinding
   override val viewModel by viewModels<EmployeesViewModel>()
   private lateinit var layoutManager: LinearLayoutManager
   private val TAG = this.javaClass.simpleName

   @SuppressLint("SetTextI18n")
   private val adapter = pagingAdapter<EmployeeEntity, RecyclerItemEmployeeBinding> {
      areItemsSame = { oldCat, newCat -> oldCat.id == newCat.id }
      bind { employee ->
         tvFullname.text = "${employee.firstname}, ${employee.lastname}"
         tvPhoneNumber.text = "+${employee.phoneNumber}"
         tvJobTitle.text = employee.jobTitle
      }
      listeners {
         root.onClick { employee ->
            val args =
               ActivityFragmentDirections.actionActivityFragmentToDisplayEmployeeFragment(employee)
            findTopNavController().navigate(args)

         }
      }
   }

   private val shimmerAdapter = simpleAdapter<Any, RecyclerItemShimmerBinding> {}

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)
      binding = FragmentEmployeesBinding.bind(view)

      setupRecyclerView()
      observeList()
   }

   private fun observeList() = lifecycleScope.launchWhenStarted {

      viewModel.employeesFlow.collectLatest {
         adapter.submitData(it)
      }
   }

   private fun setupRecyclerView() {
      layoutManager = LinearLayoutManager(requireContext())
      binding.recyclerEmployees.layoutManager = layoutManager
      binding.recyclerEmployees.adapter = adapter
   }
}