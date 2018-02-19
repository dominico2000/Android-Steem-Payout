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
        var accountsOperation = AccoutsOperation(this, db, items)


        //accounts_view.visibility = View.GONE
        accounts_view.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)


        //items.add(Accounts(0,"@dominico2000",971442780, 30.02.toFloat(), 15.0.toFloat()))
        //items.add(Accounts(0,"@foxsil",1518426549, 20.6.toFloat(), 3.0.toFloat()))

        val adapter = AccountsViewAdapter(items, accountsOperation)
        accounts_view.adapter = adapter



        fab.setOnClickListener { view ->
           accountsOperation.addNewAccount(view)


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





}
