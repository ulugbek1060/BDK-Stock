package com.android.bdkstock.views

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
   class Holder(
      private val binding: LoadStateBinding,
      private val refreshLayout: SwipeRefreshLayout?,
      private val tryAgainAction: TryAgainAction
   ) : RecyclerView.ViewHolder(binding.root) {

      init {
         binding.buttonRetry.setOnClickListener { tryAgainAction() }
      }

      fun bind(loadState: LoadState) = with(binding) {
         tvError.isVisible = loadState is LoadState.Error
         buttonRetry.isVisible = loadState is LoadState.Error
         if (refreshLayout != null) {
            refreshLayout.isRefreshing = loadState is LoadState.Loading
            shimmerLayout.stopShimmer()
            shimmerLayout.isVisible = false
         } else {
            shimmerLayout.isVisible = loadState is LoadState.Loading
            shimmerLayout.startShimmer()
         }
      }

   }

   override fun onBindViewHolder(holder: Holder, loadState: LoadState) {
      holder.bind(loadState)
   }

   override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): Holder {
      val binding = LoadStateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
      return Holder(binding, refreshLayout, tryAgainAction)
   }
}