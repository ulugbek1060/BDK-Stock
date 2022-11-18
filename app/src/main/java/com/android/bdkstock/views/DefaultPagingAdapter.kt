package com.android.bdkstock.views

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.android.bdkstock.databinding.RecyclerItemEmployeeBinding
import com.android.model.repository.employees.entity.EmployeeEntity

class DefaultPagingAdapter() :
   PagingDataAdapter<EmployeeEntity, DefaultPagingAdapter.PagingHolder>(DIF_UTIL) {

   override fun onCreateViewHolder(
      parent: ViewGroup, viewType: Int
   ): PagingHolder {
      val layoutInflater = LayoutInflater.from(parent.context)
      return PagingHolder(
         RecyclerItemEmployeeBinding.inflate(layoutInflater, parent, false)
      )
   }

   override fun onBindViewHolder(holder: PagingHolder, position: Int) {
      val item = getItem(position) ?: return
      holder.onBind(item)
   }

   inner class PagingHolder(
      private val binding: RecyclerItemEmployeeBinding
   ) : RecyclerView.ViewHolder(binding.root) {
      fun onBind(employeeEntity: EmployeeEntity) = with(binding) {
         tvFullname.text = "${employeeEntity.firstname} ${employeeEntity.lastname}"
         tvJobTitle.text = employeeEntity.job.name
         tvPhoneNumber.text = "+${employeeEntity.phoneNumber}"
      }
   }

   private companion object {
      val DIF_UTIL = object : DiffUtil.ItemCallback<EmployeeEntity>() {

         override fun areItemsTheSame(
            oldItem: EmployeeEntity, newItem: EmployeeEntity
         ): Boolean = oldItem.id == newItem.id

         override fun areContentsTheSame(
            oldItem: EmployeeEntity, newItem: EmployeeEntity
         ): Boolean = oldItem == newItem
      }
   }
}