package com.android.bdkstock.screens.main.base

import android.content.Context
import com.android.bdkstock.R

open class MyFormattedDate {

   protected fun formatDate(date: String, context: Context): String = try {
      //2022-11-20T10:19:25.000000Z
      val timeArray = date.split('-')
      val year = timeArray[0]
      val month = timeArray[1]
      val day = timeArray[2].substring(0, 1)
      val txtMonth = when (month) {
         "01", "1" -> {
            context.getString(R.string.m_01)
         }
         "02", "2" -> {
            context.getString(R.string.m_02)
         }
         "03", "3" -> {
            context.getString(R.string.m_03)
         }
         "04", "4" -> {
            context.getString(R.string.m_04)
         }
         "05", "5" -> {
            context.getString(R.string.m_05)
         }
         "06", "6" -> {
            context.getString(R.string.m_06)
         }
         "07", "7" -> {
            context.getString(R.string.m_07)
         }
         "08", "8" -> {
            context.getString(R.string.m_08)
         }
         "09", "9" -> {
            context.getString(R.string.m_09)
         }
         "10" -> {
            context.getString(R.string.m_10)
         }
         "11" -> {
            context.getString(R.string.m_11)
         }
         "12" -> {
            context.getString(R.string.m_12)
         }
         else -> {
            date
         }
      }
      "$day $txtMonth $year"
   } catch (e: Exception) {
      date
   }
}