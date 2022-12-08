package com.android.bdkstock.screens.main.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.bdkstock.databinding.LoadStateBinding

typealias TryAgainAction = () -> Unit

class DefaultLoadStateAdapter(
   private val refreshLayout: SwipeRefreshLayout? = null,
   private val tryAgainAction: TryAgainAction
) : LoadStateAdapter<DefaultLoadStateAdapter.Holder>() {

   /**
    * The same layout is used for:
    * - footer
    * - main indicator
    */
   inner class Holder(
      private val swipeRefreshLayout: SwipeRefreshLayout? = null,
      private val binding: LoadStateBinding
   ) : RecyclerView.ViewHolder(binding.root) {

      init {
         binding.buttonRetry.setOnClickListener { tryAgainAction() }
      }

      fun bind(loadState: LoadState) = binding.apply {
         progressBar.isVisible = loadState is LoadState.Loading
         buttonRetry.isVisible = loadState !is LoadState.Loading
         textViewError.isVisible = loadState !is LoadState.Loading

         if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing) {
            swipeRefreshLayout.isRefreshing = false
         }
      }
   }

   override fun onBindViewHolder(holder: Holder, loadState: LoadState) {
      holder.bind(loadState)
   }

   override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): Holder {
      val binding = LoadStateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
      return Holder(refreshLayout, binding)
   }

   override fun displayLoadStateAsItem(loadState: LoadState): Boolean {
      return loadState is LoadState.Loading || loadState is LoadState.Error
   }
}