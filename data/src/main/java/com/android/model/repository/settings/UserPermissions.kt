package com.android.model.repository.settings

interface UserPermissions {

   /**
    * Save user permissions
    */
   suspend fun savePermissions(perms: List<String>)

   /**
    * Get permissions
    */
   suspend fun getPermissions(): List<String>
}