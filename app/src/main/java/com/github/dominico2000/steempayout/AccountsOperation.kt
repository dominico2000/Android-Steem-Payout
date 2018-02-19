package com.github.dominico2000.steempayout

import android.content.Context
import android.content.DialogInterface
import android.os.AsyncTask
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Adapter
import android.widget.EditText
import java.util.ArrayList

/**
 * Created by dominik on 19.02.18.
 */
class AccoutsOperation(val context: Context, val db: AccountsDatabase?, var items: ArrayList<Accounts>) {





    fun updateCardsFromDatabase(adapter: AccountsViewAdapter){

        class Worker: AsyncTask<Void, Void, List<Accounts>? >() {
            override fun doInBackground(vararg p0: Void?): List<Accounts>? {
                return db?.accountsDao()?.getAllAccounts()
            }
        }

        items.clear()
        Log.d("Items",items.size.toString())
        items.addAll(Worker().execute().get() as List<Accounts>)
        adapter.notifyItemRangeChanged(0, items.size);
        Log.d("Items",items.toString())
    }

    fun removeAccount(account: Accounts): List<Accounts>?{
        class Worker: AsyncTask<Void, Void, List<Accounts>?>() {
            override fun doInBackground(vararg p0: Void?): List<Accounts>? {
                db?.accountsDao()?.deleteAccount(account)
                return db?.accountsDao()?.getAllAccounts()
            }
        }

        return Worker().execute().get()
    }
}