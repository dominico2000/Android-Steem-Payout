package com.github.dominico2000.steempayout


import android.content.Context
import android.content.DialogInterface
import android.os.AsyncTask
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.widget.SwipeRefreshLayout
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
import android.widget.RadioGroup
import eu.bittrade.libs.steemj.base.models.AccountName

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlin.collections.ArrayList
import android.net.ConnectivityManager
import android.content.Context.CONNECTIVITY_SERVICE
import android.content.Intent
import android.net.NetworkInfo






class MainActivity : AppCompatActivity() {


    var db: AccountsDatabase? = null
    var items: ArrayList<Accounts> = ArrayList()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)


        db = AccountsDatabase.getInstance(this)

        var mAccountsRefresh = accounts_refresh


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
        

        val adapter = AccountsViewAdapter(this, items,  accounts_view, db)
        accounts_view.adapter = adapter

        //TODO: Add refresh data

        fab.setOnClickListener { view ->
           addNewAccount(view, adapter, mAccountsRefresh)


        }

        mAccountsRefresh.setColorSchemeResources(R.color.swipe_orange, R.color.swipe_green, R.color.swipe_blue)
        mAccountsRefresh.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            refresh(adapter, mAccountsRefresh)
        })

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
        when (item.itemId) {
            R.id.action_about -> {
                startActivity( Intent(this, AboutActivity::class.java) )
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }


    fun addNewAccount(view: View, adapter: AccountsViewAdapter, mAccountsRefresh: SwipeRefreshLayout){
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val dialogView = inflater.inflate(R.layout.add_new_account_dialog, null)
        dialogBuilder.setView(dialogView)

        val mEditText = dialogView.findViewById<View>(R.id.add_account_name_etext) as EditText
        val mRadioGroup = dialogView.findViewById<View>(R.id.reward_type_radio_group) as RadioGroup

        mRadioGroup.check(R.id.ff_reward)

        dialogBuilder.setTitle(getString(R.string.add_new_account_dialog_title))
        //dialogBuilder.setMessage("Enter account name")
        dialogBuilder.setPositiveButton(getString(R.string.add), DialogInterface.OnClickListener { _, _->
            //do something with edt.getText().toString();



            var account = Accounts()
            account.name = mEditText.text.toString()
            account.timestamp = System.currentTimeMillis()/1000L

            if( mRadioGroup.checkedRadioButtonId == R.id.ff_reward ) account.rewardType = 5050
            else if( mRadioGroup. checkedRadioButtonId == R.id.full_sp_reward ) account.rewardType = 100
            else snackbar(view, getString(R.string.choose_reward_type_message))

            if(account.name[0] != '@') account.name = "@" + (account.name)

            items.add(account)
            adapter.notifyDataSetChanged()
            val res = addToDatabase(account)
            items[items.size - 1].id = res[res.size - 1].id
            Log.d("Db", res.toString())

            var message = getString(R.string.adding_account_message) + mEditText.text
            snackbar(view, message)

            mAccountsRefresh.post(Runnable {
                mAccountsRefresh.isRefreshing = true
                refresh(adapter, mAccountsRefresh)
            })



        })
        dialogBuilder.setNegativeButton(getString(R.string.cancel), DialogInterface.OnClickListener { _, _ ->
            //pass
        })
        dialogBuilder.create().show()

    }

    fun addToDatabase(account: Accounts):  List<Accounts>{
        class Worker: AsyncTask<Void, Void, List<Accounts>?>() {
            override fun doInBackground(vararg p0: Void?): List<Accounts>? {
                db?.accountsDao()?.insertAccount(account)
                return db?.accountsDao()?.getAllAccounts()
            }

        }
        return Worker().execute().get() as List<Accounts>

    }

    fun refresh(adapter: AccountsViewAdapter, mAccountsRefresh: SwipeRefreshLayout){

        class Worker: AsyncTask<SwipeRefreshLayout,Void,Unit>() {



            override fun onPostExecute(result: Unit?) {
                super.onPostExecute(result)
                adapter.notifyDataSetChanged()
                mAccountsRefresh.isRefreshing = false

            }

            override fun doInBackground(vararg p0: SwipeRefreshLayout?){

                if( CheckNetwork(applicationContext) ) {
                    for (item in items) {
                        val name = item.name.removePrefix("@")
                        Log.d("Name", name)

                        if( name.length in 3..16) {
                            val steemAccount = SteemAdapter(AccountName(name))
                            var payout = steemAccount.getPostsPotencialReward()
                            Log.d("Steem", payout.toString())
                            var reward: List<Float>
                            if (item.rewardType == 5050) reward = steemAccount.reward5050(payout)
                            else if (item.rewardType == 100) reward = steemAccount.reward100sp(payout)
                            else reward = listOf(-1F, -1F)

                            item.SBD = reward[0]
                            item.SP = reward[1]

                            db?.accountsDao()?.updateAccount(item)
                            Log.d("Db", db?.accountsDao()?.getAllAccounts().toString())
                        }

                    }
                }


            }
        }


        Worker().execute()




    }

    fun snackbar(view: View, message: String){

        Snackbar.make(view, message, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
    }

    fun CheckNetwork(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        if (activeNetwork != null && activeNetwork.isConnected) {
            val networkType = activeNetwork.type
            return networkType == ConnectivityManager.TYPE_WIFI || networkType == ConnectivityManager.TYPE_MOBILE
        } else {
            return false
        }
    }

}
