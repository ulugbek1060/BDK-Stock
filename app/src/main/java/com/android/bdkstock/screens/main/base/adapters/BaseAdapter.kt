package com.android.bdkstock.screens.main.base

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

class BaseAdapter<T : Any, VB : ViewBinding>(
   private val adapterDelegate: ViewHolderCreator<VB>,
   internal var bind: VB.(T) -> Unit
) : ListAdapter<T, BaseViewHolder<VB>>(BaseDiffUtil()) {

   override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<VB> {
      val inflater = LayoutInflater.from(parent.context)
      return BaseViewHolder(adapterDelegate.inflateBinding(inflater, parent))
   }

   override fun onBindViewHolder(holder: BaseViewHolder<VB>, position: Int) {
      val item = getItem(position) ?: return
      bind(holder.binding, item)
   }
}

class BasePagingAdapter<T : Any, VB : ViewBinding>(
   private val adapterDelegate: ViewHolderCreator<VB>,
   internal var bind: VB.(T) -> Unit
) : PagingDataAdapter<T, BaseViewHolder<VB>>(BaseDiffUtil()) {

   override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<VB> {
      val inflater = LayoutInflater.from(parent.context)
      return BaseViewHolder(adapterDelegate.inflateBinding(inflater, parent))
   }

   override fun onBindViewHolder(holder: BaseViewHolder<VB>, position: Int) {
      val item = getItem(position) ?: return
      bind(holder.binding, item)
   }
}

interface ViewHolderCreator<VB : ViewBinding> {
   fun inflateBinding(layoutInflater: LayoutInflater, viewGroup: ViewGroup): VB
}

class BaseViewHolder<VB : ViewBinding>(val binding: VB) :
   RecyclerView.ViewHolder(binding.root)


class BaseDiffUtil<T : Any> : DiffUtil.ItemCallback<T>() {
   override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
      return oldItem.toString() == newItem.toString()
   }

   @SuppressLint("DiffUtilEquals")
   override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
      return oldItem == newItem
   }
}
