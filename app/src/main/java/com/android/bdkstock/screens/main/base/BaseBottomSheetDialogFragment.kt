package com.android.bdkstock.screens.main.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

abstract class BaseBottomSheetDialogFragment<VB : ViewBinding> :
   BottomSheetDialogFragment() {

   private var _binding: VB? = null
   protected val binding: VB get() = _binding!!

   override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?
   ) = getViewBinding()
      .also { _binding = it }
      .root

   abstract fun getViewBinding(): VB

   override fun onDestroy() {
      super.onDestroy()
      _binding = null
   }
}