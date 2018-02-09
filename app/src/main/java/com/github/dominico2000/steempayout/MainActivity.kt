package com.github.dominico2000.steempayout

import android.accounts.Account
import android.content.Context
import android.content.DialogInterface
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import com.fasterxml.jackson.databind.JsonSerializer

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.apache.commons.lang3.ObjectUtils


class MainActivity : AppCompatActivity() {

    private var db: AccountsDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        db = AccountsDatabase.getInstance(this)

        fab.setOnClickListener { view ->
            addNewAccount(view)
        }



        //accounts_view.visibility = View.GONE
        accounts_view.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)

        val items = ArrayList<AccountsData>()
        items.add(AccountsData("@dominico2000",971442780, 30.02.toFloat(), 15.0.toFloat()))
        items.add(AccountsData("@foxsil",1518426549, 20.6.toFloat(), 3.0.toFloat()))

        val adapter = AccountsViewAdapter(items)
        accounts_view.adapter = adapter

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun addNewAccount(view: View){
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
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

            Log.d("Database: ", result[0].toString())

            var message = "Adding account " + editText.text
            Snackbar.make(view, message, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()



        })
        dialogBuilder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, whichButton ->
            //pass
        })
        val b = dialogBuilder.create()
        b.show()
    }
}
