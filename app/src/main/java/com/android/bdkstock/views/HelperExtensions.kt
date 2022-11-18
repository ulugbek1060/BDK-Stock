package com.android.bdkstock.views

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import androidx.annotation.CheckResult
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.android.bdkstock.R
import com.android.model.utils.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.onStart


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

