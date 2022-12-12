package com.android.bdkstock.screens.main.base.adapters

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.viewbinding.ViewBinding
import com.elveum.elementadapter.delegate.AdapterDelegate
import com.elveum.elementadapter.delegate.simpleAdapterDelegate
import com.elveum.elementadapter.dsl.BindingHolder
import com.elveum.elementadapter.dsl.ConcreteItemTypeScope

inline fun <reified T : Any, reified B : ViewBinding> pagingAdapter(
   noinline block: ConcreteItemTypeScope<T, B>.() -> Unit
): PagingDataAdapter<T, BindingHolder> {
   val delegate = simpleAdapterDelegate(block)
   return PagingAdapterBridge(delegate)
}

class PagingAdapterBridge<T : Any>(
   private val delegate: AdapterDelegate<T>
) : PagingDataAdapter<T, BindingHolder>(delegate.itemCallback()) {

   override fun onBindViewHolder(holder: BindingHolder, position: Int) {
      // please note, NULL values are not supported!
      val item = getItem(position) ?: return
      delegate.onBindViewHolder(holder, item)
   }

   override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingHolder {
      return delegate.onCreateViewHolder(parent, viewType)
   }

   override fun getItemViewType(position: Int): Int {
      // please note, NULL values are not supported!
      val item = getItem(position) ?: return 0
      return delegate.getItemViewType(item)
   }

}