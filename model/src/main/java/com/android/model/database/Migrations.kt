package com.android.model.database

import android.database.sqlite.SQLiteDatabase
import androidx.core.content.contentValuesOf
import androidx.room.RenameColumn
import androidx.room.migration.AutoMigrationSpec
import androidx.sqlite.db.SupportSQLiteDatabase
import com.android.model.database.employees.entity.EmployeeJobRoom
import com.google.gson.Gson


@RenameColumn(tableName = "employee", fromColumnName = "jobTitle", toColumnName = "job")
class EmployeeMigrationFrom5To6 : AutoMigrationSpec {

   override fun onPostMigrate(db: SupportSQLiteDatabase) {
      super.onPostMigrate(db)
      db.query("SELECT * FROM employee").use { cursor ->
//         val jobIndex = cursor.getColumnIndex("job")
         val idIndex = cursor.getColumnIndex("id")
         while (cursor.moveToNext()) {
            val id: Long = cursor.getLong(idIndex)
            val jobEntity = EmployeeJobRoom(0, "non selected")
            val job = Gson().toJson(jobEntity)
            db.update(
               "employee",
               SQLiteDatabase.CONFLICT_NONE,
               contentValuesOf(
                  "job" to job
               ),
               "id = ?",
               arrayOf(id.toString())
            )
         }
      }
   }
}