package com.android.model

import android.util.Log

object Logger {

   private const val TAG = "Logger"

   fun log(name: String, obj: Any) {
      Log.d(TAG, "$name : $obj")
   }
}