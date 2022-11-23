package com.android.bdkstock.views

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.annotation.CheckResult
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.android.bdkstock.R
import com.android.bdkstock.databinding.SearchBarLayoutBinding
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.onStart

class SearchBar @JvmOverloads constructor(
   context: Context,
   attrs: AttributeSet? = null,
   defStyle: Int = 0
) : ConstraintLayout(context, attrs, defStyle) {

   var textListener: ((String) -> Unit)? = null
   var backListener: (() -> Unit)? = null

   private val binding: SearchBarLayoutBinding

   init {

      val inflater = LayoutInflater.from(context)
      inflater.inflate(R.layout.search_bar_layout, this, true)

      binding = SearchBarLayoutBinding.bind(this)

      attrs?.let {
         val typedArray = context.obtainStyledAttributes(
            it,
            R.styleable.SearchBar, 0, 0
         )
         val searchHint = typedArray.getText(R.styleable.SearchBar_search_hint)

         binding.searchText.hint = searchHint
         typedArray.recycle()
      }

      binding.searchText.onTextChanged {
         binding.clear.isVisible = it.isNotBlank()
      }

      binding.clear.setOnClickListener {
         binding.searchText.setText("")
      }

      binding.searchText.requestFocus()

      binding.searchText.afterTextChanged {
         val listener = textListener ?: {}
         listener(it)
      }

      binding.back.setOnClickListener {
         val listener = backListener ?: {}
         listener()
      }

      binding.searchText.setOnEditorActionListener { textView, i, _ ->
         if (i == EditorInfo.IME_ACTION_DONE) {
            binding.searchText.clearFocus()
         }
         false
      }
   }

   @ExperimentalCoroutinesApi
   @CheckResult
   private fun EditText.textChanges(): Flow<CharSequence?> {
      return callbackFlow {
         val listener = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
               trySend(s)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) =
               Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
         }
         addTextChangedListener(listener)
         awaitClose { removeTextChangedListener(listener) }
      }.onStart { emit(text) }
   }

   private fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
      this.addTextChangedListener(object : TextWatcher {
         override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
         }

         override fun onTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {
         }

         override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
         }
      })
   }

   private fun EditText.onTextChanged(onTextChanged: (String) -> Unit) {
      this.addTextChangedListener(object : TextWatcher {
         override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
         }

         override fun onTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {
            onTextChanged.invoke(p0.toString())
         }

         override fun afterTextChanged(editable: Editable?) {
         }
      })
   }
}