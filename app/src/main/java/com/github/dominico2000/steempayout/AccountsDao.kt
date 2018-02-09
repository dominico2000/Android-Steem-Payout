package com.github.dominico2000.steempayout

import android.arch.persistence.room.Dao
import android.arch.persistence.room.*
import android.arch.persistence.room.OnConflictStrategy.REPLACE


/**
 * Created by dominik on 09.02.18.
 */
@Dao
interface AccountsDao {

    @Query("select * from task")
    fun getAllAccounts(): List<Accounts>

    @Insert(onConflict = REPLACE)
    fun insertAccount(account: Accounts)

    @Update(onConflict = REPLACE)
    fun updateAccount(account: Accounts)

    @Delete
    fun deleteAccount(account: Accounts)


}