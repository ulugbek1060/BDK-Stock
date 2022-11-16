package com.android.bdkstock.screens.main.menu.clients

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.bdkstock.R
import com.android.bdkstock.databinding.FragmentClientsBinding
import com.android.bdkstock.databinding.RecyclerItemClientBinding
import com.android.bdkstock.screens.main.base.BaseFragment
import com.android.bdkstock.views.pagingAdapter
import com.android.model.repository.clients.entity.ClientEntity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ClientsFragment : BaseFragment(R.layout.fragment_clients) {

   override val viewModel by viewModels<ClientsViewModel>()
   private lateinit var binding: FragmentClientsBinding
   private lateinit var layoutManager: LinearLayoutManager

   private val adapter = pagingAdapter<ClientEntity, RecyclerItemClientBinding> {
      bind { client ->
         tvFullname.text = client.fullName
         tvPhoneNumber.text = client.phoneNumber
      }
      listeners {
         root.onClick {

         }
      }
   }

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)

      observeClients()
   }

   private fun observeClients() = lifecycleScope.launch {
      viewModel.clientFlow.collectLatest {
         adapter.submitData(it)
      }
   }

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)
      binding = FragmentClientsBinding.bind(view)

      setupRecyclerView()
   }

   private fun setupRecyclerView() {
      layoutManager = LinearLayoutManager(requireContext())
      binding.recyclerClients.layoutManager = layoutManager
      binding.recyclerClients.adapter = adapter
   }
}