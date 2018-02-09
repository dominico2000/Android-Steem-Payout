package com.github.dominico2000.steempayout

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context

/**
 * Created by dominik on 09.02.18.
 */

@Database(entities = arrayOf(Accounts::class), version = 1, exportSchema = false)
abstract class AccountsDatabase: RoomDatabase() {

    abstract fun accountsDao(): AccountsDao

    companion object {
        private var INSTANCE: AccountsDatabase? = null

        fun getInstance(context: Context): AccountsDatabase? {
            if (INSTANCE == null) {
                synchronized(AccountsDatabase::class) {
                    INSTANCE = Room.inMemoryDatabaseBuilder(context.getApplicationContext(),
                            AccountsDatabase::class.java/*, "steem_accounts.db"*/)
                            .build()
                }
            }
            return INSTANCE
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }

}