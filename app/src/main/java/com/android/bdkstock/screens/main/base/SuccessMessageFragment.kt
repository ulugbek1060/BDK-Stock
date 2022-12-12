package com.android.bdkstock.screens.main.base

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.android.bdkstock.R
import com.android.bdkstock.views.findTopNavController

class SuccessMessageFragment : Fragment(R.layout.fragment_success_message) {

   private var message: String? = null

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      arguments?.let {
         message = it.getString(SUCCESS_MESSAGE_BUNDLE_KEY)
      }
   }

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)
      view.findViewById<TextView>(R.id.tv_success_message).text = message
      view.findViewById<Button>(R.id.button_close).setOnClickListener {
         findTopNavController().popBackStack()
      }
   }

   private companion object {
      const val SUCCESS_MESSAGE_BUNDLE_KEY = "success_message"
   }
}