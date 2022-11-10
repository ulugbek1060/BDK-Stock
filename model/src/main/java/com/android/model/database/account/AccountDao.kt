package com.android.model.database.account

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import com.android.model.database.account.entity.AccountRoomEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {

   @Insert(onConflict = REPLACE)
   suspend fun insert(accountRoomEntity: AccountRoomEntity)

   @Query("SELECT * FROM account")
   fun getAccount(): Flow<AccountRoomEntity?>

   @Query("DELETE FROM account")
   suspend fun delete()
}