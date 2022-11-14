package com.android.bdkstock.screens.main.menu

import androidx.annotation.DrawableRes

data class MenuItem(
   val id: Int,
   @DrawableRes val icon: Int,
   val title: String,
   val fragmentId: Int
)