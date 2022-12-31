package com.android.model.database

import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE

interface BaseDao<T> {

   @Insert(onConflict = REPLACE)
   suspend fun insert(vararg objects: T)

   @Insert(onConflict = REPLACE)
   suspend fun insert(objects: List<T>)

}
