package com.android.bdkstock.screens.main.base

import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.viewbinding.ViewBinding
import com.android.bdkstock.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

abstract class BaseWithFabFragment<ViewModel : BaseViewModel, Binding : ViewBinding> :
   BaseFragment<ViewModel, Binding>() {

   private val rotateOpen: Animation by lazy {
      AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_open_anim)
   }
   private val rotateClose: Animation by lazy {
      AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_close_anim)
   }
   private val fromBottom: Animation by lazy {
      AnimationUtils.loadAnimation(requireContext(), R.anim.from_bottom_anim)
   }
   private val toBottom: Animation by lazy {
      AnimationUtils.loadAnimation(requireContext(), R.anim.to_bottom_anim)
   }

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)
      opened = false
   }

   private var opened = false

   fun onAddButtonClicked(
      mainFab: FloatingActionButton, childButtons: List<View>, titles: List<View>
   ) {
      setAnimation(mainFab, childButtons, titles)
      setVisibility(childButtons, titles)
      setClickable(childButtons)
      opened = !opened
   }

   private fun setVisibility(childButtons: List<View>, titles: List<View>) {
      val visibility = if (!opened) View.VISIBLE else View.GONE
      childButtons.forEach { it.visibility = visibility }
      titles.forEach { it.visibility = visibility }
   }

   private fun setAnimation(
      mainFab: FloatingActionButton, childButtons: List<View>, titles: List<View>
   ) {
      val childButtonAnimation = if (!opened) fromBottom else toBottom
      val mainButtonAnimation = if (!opened) rotateOpen else rotateClose
      mainFab.startAnimation(mainButtonAnimation)
      childButtons.forEach { it.startAnimation(childButtonAnimation) }
      titles.forEach { it.startAnimation(childButtonAnimation) }
   }

   private fun setClickable(childButtons: List<View>) {
      childButtons.forEach { it.isClickable = !opened }
   }

   private companion object {
      const val FAB_EVENT = "fab_event"
   }
}