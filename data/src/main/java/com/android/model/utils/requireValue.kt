package com.android.model.utils

import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.ScrollView
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView

fun <T> LiveData<T>.requireValue(): T {
    return this.value ?: throw IllegalStateException("Value is empty")
}

operator fun String?.contains(substring: String): Boolean {
    return if (this is String) {
        // Need to convert to CharSequence, otherwise keeps calling my
        // contains in an endless loop.
        val charSequence: CharSequence = this
        charSequence.contains(substring)
    } else {
        false
    }
}

//fun <T> LiveData<com.android.navigation.data.model.Result<T>>.observeResults(
//    fragment: BaseFragment,
//    root: View,
//    resultView: ResultView,
//    onSuccess: (T) -> Unit
//) = observe(fragment.viewLifecycleOwner) { result ->
//    resultView.setResult(fragment, result)
//    val rootView: View = if (root is ScrollView) {
//        root.getChildAt(0)
//    } else {
//        root
//    }
//    if (rootView is ViewGroup && rootView !is RecyclerView && root !is AbsListView) {
//        rootView.children
//            .filter { it != resultView }
//            .forEach {
//                it.isVisible = result is Success<*>
//            }
//    }
//    if (result is Success) onSuccess.invoke(result.value)
//}

