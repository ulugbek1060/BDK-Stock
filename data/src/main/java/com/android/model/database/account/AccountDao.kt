package com.android.model.database.account

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.android.model.database.BaseDao
import com.android.model.database.account.entity.AccountRoomEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao : BaseDao<AccountRoomEntity> {

   @Query("SELECT * FROM account")
   fun getAccount(): Flow<AccountRoomEntity?>

   @Query("DELETE FROM account")
   suspend fun delete()
}