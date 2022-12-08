package com.android.bdkstock.screens.main.base

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.android.bdkstock.R
import com.android.bdkstock.views.findTopNavController
import com.android.bdkstock.views.setWidthAndHeightPercent

class GlobalErrorFragment : DialogFragment(R.layout.fragment_global_error) {
   private var message: String? = null

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      arguments?.let {
         message = it.getString(ERROR_MESSAGE_KEY)
      }
   }

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)

      setWidthAndHeightPercent(98, 40)

      view.findViewById<TextView>(R.id.tv_error_message).text = message
      view.findViewById<Button>(R.id.button_close).setOnClickListener {
         findTopNavController().popBackStack()
      }
   }

   companion object {
      const val ERROR_MESSAGE_KEY = "error_message"
   }
}