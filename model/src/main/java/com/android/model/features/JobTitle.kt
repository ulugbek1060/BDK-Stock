package com.android.model.features

import android.content.Context
import com.android.model.R

data class JobTitle(
   val jobId: Int,
   val jobName: String
) {

   companion object {
      fun getJobs(): List<JobTitle> {
         return listOf(
            JobTitle(1, "Admin"),
            JobTitle(2, "Buxgalter"),
            JobTitle(3, "Manager")
         )
      }
   }

   override fun toString(): String {
      return jobName
   }
}