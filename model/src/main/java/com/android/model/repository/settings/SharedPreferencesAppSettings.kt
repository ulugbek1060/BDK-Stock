package com.android.model.repository.settings

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedPreferencesAppSettings @Inject constructor(
   @ApplicationContext appContext: Context
) : AppSettings {
   private val sharedPreferences = appContext.getSharedPreferences("bdk_app_settings", Context.MODE_PRIVATE)

   override fun setCurrentToken(token: String?) {
      val editor = sharedPreferences.edit()
      if (token == null)
         editor.remove(PREF_CURRENT_ACCOUNT_TOKEN)
      else
         editor.putString(PREF_CURRENT_ACCOUNT_TOKEN, token)
      editor.apply()
   }

   override fun getCurrentToken(): String? =
      sharedPreferences.getString(PREF_CURRENT_ACCOUNT_TOKEN, null)

   companion object {
      private const val PREF_CURRENT_ACCOUNT_TOKEN = "currentToken"
   }
}