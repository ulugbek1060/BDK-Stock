package com.android.model.utils

import android.app.AlertDialog
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.view.isVisible
import androidx.viewbinding.ViewBinding

fun AppCompatEditText.clear() {
   this.setText("")
}

fun View.visible() {
   isVisible = true
}

fun View.gone() {
   isVisible = false
}