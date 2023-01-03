package com.android.model.repository.settings

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPermissionsPreferences @Inject constructor(
   @ApplicationContext appContext: Context, private val gson: Gson
) : UserPermissions {

   private val sharedPreferences =
      appContext.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)

   override suspend fun savePermissions(perms: List<String>) {
      val permissions = gson.toJson(perms)
      val editor = sharedPreferences.edit()
      editor.putString(PREFERENCE_USER_PERMS, permissions)
      editor.apply()
   }

   override suspend fun getPermissions(): List<String> {
      val perms = sharedPreferences.getString(PREFERENCE_USER_PERMS, null)
      return gson.fromJson(perms, object : TypeToken<List<String>>() {}.type)
   }

   companion object {
      private const val PREFERENCE_NAME = "bdk_user_permissions"
      private const val PREFERENCE_USER_PERMS = "user_permissions"
   }
}