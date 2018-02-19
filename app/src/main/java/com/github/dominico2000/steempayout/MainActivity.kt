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
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import com.fasterxml.jackson.databind.JsonSerializer

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.apache.commons.lang3.ObjectUtils
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {


    var db: AccountsDatabase? = null
    var items: ArrayList<Accounts> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        db = AccountsDatabase.getInstance(this)

        class Worker: AsyncTask<Void, Void, List<Accounts>?>() {
            override fun doInBackground(vararg p0: Void?): List<Accounts>? {
                return db?.accountsDao()?.getAllAccounts()
            }

        }
        val res = Worker().execute().get() as List<Accounts>
        items.addAll(res)

        Log.d("Db_START", res.toString())
        Log.d("Items_START", items.toString())

        //accounts_view.visibility = View.GONE
        accounts_view.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)


        //items.add(Accounts(0,"@dominico2000",971442780, 30.02.toFloat(), 15.0.toFloat()))
        //items.add(Accounts(0,"@foxsil",1518426549, 20.6.toFloat(), 3.0.toFloat()))

        val adapter = AccountsViewAdapter(items,  accounts_view, db)
        accounts_view.adapter = adapter

        TODO("Add refresh data")

        fab.setOnClickListener { view ->
           addNewAccount(view, adapter)

        }

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


    fun addNewAccount(view: View, adapter: AccountsViewAdapter){
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
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

            items.add(account)
            adapter.notifyDataSetChanged()
            val res = addToDatabase(account)
            items[items.size-1].id = res[res.size-1].id
            Log.d("Db", res.toString())

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

    fun addToDatabase(account: Accounts):  List<Accounts>{
        class Worker: AsyncTask<Void, Void, List<Accounts>?>() {
            override fun doInBackground(vararg p0: Void?): List<Accounts>? {
                db?.accountsDao()?.insertAccount(account)
                db?.accountsDao()?.deleteAccount(account)
                return db?.accountsDao()?.getAllAccounts()
            }

        }
        return Worker().execute().get() as List<Accounts>

    }



}
