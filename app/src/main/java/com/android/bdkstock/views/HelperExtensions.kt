package com.android.bdkstock.views

import android.content.res.Resources
import android.graphics.Rect
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.annotation.CheckResult
import androidx.appcompat.app.ActionBar
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.viewbinding.ViewBinding
import com.android.bdkstock.R
import com.android.bdkstock.screens.main.MainActivity
import com.android.bdkstock.screens.main.base.BaseBottomSheetDialogFragment
import com.android.model.utils.*
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.onStart
import java.text.SimpleDateFormat
import java.util.*


fun Fragment.navigate(destinationId: Int) {
   this.findNavController().navigate(destinationId, null, navOptions {
      anim {
         enter = R.anim.enter
         exit = R.anim.exit
         popExit = R.anim.pop_enter
         popExit = R.anim.pop_exit
      }
   })
}

fun Fragment.findTopNavController(): NavController {
   val topLevelHost =
      requireActivity().supportFragmentManager.findFragmentById(R.id.fragment_main_container) as NavHostFragment?
   return topLevelHost?.navController ?: findNavController()
}

fun Fragment.getActionBar(): ActionBar? {
   return (requireActivity() as MainActivity).supportActionBar
}

fun <T> LiveData<Results<T>>.observeResults(
   fragment: Fragment,
   progress: View?,
   onSuccess: (T) -> Unit
) = observe(fragment.viewLifecycleOwner) { result ->
   if (result is Success) {
      progress?.gone()
      onSuccess(result.value)
   }
   if (result is Pending) progress?.visible()
}


@ExperimentalCoroutinesApi
@CheckResult
fun EditText.textChanges(): Flow<CharSequence?> {
   return callbackFlow {
      val listener = object : TextWatcher {
         override fun afterTextChanged(s: Editable?) = Unit
         override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
         override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            trySend(s)
         }
      }
      addTextChangedListener(listener)
      awaitClose { removeTextChangedListener(listener) }
   }.onStart { emit(text) }
}


/**
 * Call this method (in onActivityCreated or later) to set
 * the width of the dialog to a percentage of the current
 * screen width.
 */
fun DialogFragment.setWidthAndHeightPercent(percentageWidth: Int, percentageHeight: Int) {
   val percentWidth = percentageWidth.toFloat() / 100
   val percentHeight = percentageHeight.toFloat() / 100
   val dm = Resources.getSystem().displayMetrics
   val rect = dm.run { Rect(0, 0, widthPixels, heightPixels) }
   val width = rect.width() * percentWidth
   val height = rect.height() * percentHeight
   dialog?.window?.setLayout(width.toInt(), height.toInt())
}

/**
 * Call this method (in onActivityCreated or later)
 * to make the dialog near-full screen.
 */
fun DialogFragment.setFullScreen() {
   dialog?.window?.setLayout(
      ViewGroup.LayoutParams.MATCH_PARENT,
      ViewGroup.LayoutParams.WRAP_CONTENT
   )
}


fun BottomSheetDialogFragment.getDate(
   title: String, inputFromDate: TextInputEditText
) {
   val datePicker = MaterialDatePicker.Builder.datePicker().setTitleText(title)
      .setSelection(MaterialDatePicker.todayInUtcMilliseconds()).build()
   datePicker.addOnPositiveButtonClickListener { timeMiles ->
      val formatter = SimpleDateFormat("dd/MM/yyyy")
      val date = formatter.format(Date(timeMiles))
      inputFromDate.setText(date)
   }
   datePicker.addOnNegativeButtonClickListener {
      inputFromDate.setText("")
      datePicker.dismiss()
   }
   datePicker.show(parentFragmentManager, "tag")
}



