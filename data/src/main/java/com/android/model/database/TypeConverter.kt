package com.android.model.database

import androidx.room.TypeConverter
import com.android.model.database.account.entity.AccountJobRoom
import com.android.model.database.employees.entity.EmployeeJobRoom
import com.google.gson.Gson

object TypeConverter {

   private val gson = Gson()

   @TypeConverter
   fun fromEmployeeJob(job: EmployeeJobRoom): String {
      return gson.toJson(job)
   }

   @TypeConverter
   fun toEmployeeJob(name: String): EmployeeJobRoom {
      return gson.fromJson(name, EmployeeJobRoom::class.java)
   }

   @TypeConverter
   fun fromAccountJob(job: AccountJobRoom): String {
      return gson.toJson(job)
   }

   @TypeConverter
   fun toAccountJob(name: String): AccountJobRoom {
      return gson.fromJson(name, AccountJobRoom::class.java)
   }

}