package com.github.dominico2000.steempayout

import android.content.Context
import android.content.DialogInterface
import android.os.AsyncTask
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
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



    fun addNewAccount(view: View){
        val dialogBuilder = AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context)
        val dialogView = inflater.inflate(R.layout.add_new_account_dialog, null)
        dialogBuilder.setView(dialogView)

        val editText = dialogView.findViewById<View>(R.id.add_account_name_etext) as EditText

        dialogBuilder.setTitle("Add new account")
        dialogBuilder.setMessage("Enter account name")
        dialogBuilder.setPositiveButton("Add", DialogInterface.OnClickListener { dialog, whichButton ->
            //do something with edt.getText().toString();

            var account = Accounts()
            account.name = editText.text.toString()
            account.timestamp = System.currentTimeMillis()/1000L

            class Worker: AsyncTask<Void, Void, List<Accounts>? >() {
                override fun doInBackground(vararg p0: Void?): List<Accounts>? {
                    db?.accountsDao()?.insertAccount(account)
                    return db?.accountsDao()?.getAllAccounts()
                }
            }

            var result = Worker().execute().get() as List<Accounts>

            Log.d("Database: ", result.toString())

            var message = "Adding account " + editText.text
            Snackbar.make(view, message, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()

            updateCardsFromDatabase()

        })
        dialogBuilder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, whichButton ->
            //pass
        })
        val b = dialogBuilder.create()
        b.show()
    }

    fun updateCardsFromDatabase(){

        class Worker: AsyncTask<Void, Void, List<Accounts>? >() {
            override fun doInBackground(vararg p0: Void?): List<Accounts>? {
                return db?.accountsDao()?.getAllAccounts()
            }
        }

        items.clear()
        Log.d("Items",items.toString())
        items.addAll(Worker().execute().get() as ArrayList<Accounts>)
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