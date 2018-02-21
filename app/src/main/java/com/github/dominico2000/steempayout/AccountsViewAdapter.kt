package com.github.dominico2000.steempayout

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.AsyncTask
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by dominik on 02.02.18.
 */
class AccountsViewAdapter( private val context: Context, private val list: ArrayList<Accounts>, private val recycler: RecyclerView, var db: AccountsDatabase?):RecyclerView.Adapter<AccountsViewAdapter.ViewHolder>()  {



    override fun getItemCount(): Int {
        return list.size
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountsViewAdapter.ViewHolder{
        val v = LayoutInflater.from(parent.context).inflate(R.layout.account_card, parent, false)
        return ViewHolder(v, context)
    }

    override fun onBindViewHolder(holder: AccountsViewAdapter.ViewHolder, position: Int) {
        holder.bindItems(list[position])

        holder.mRemoveButton.setOnClickListener { view ->

            val removeDialogBuilder = AlertDialog.Builder(context)
            removeDialogBuilder.setMessage(context.getString(R.string.remove_account_dialog_message) + list[position].name)
                    .setTitle(context.getString(R.string.remove_account_dialog_title))

            removeDialogBuilder.setPositiveButton(R.string.yes, DialogInterface.OnClickListener { _, _ ->
                val res = deleteFromDatabase(list[position])
                Log.d("Db", res.toString())
                list.removeAt(position)
                recycler.removeViewAt(position)
                this.notifyItemRemoved(position)
                this.notifyItemRangeChanged(position, list.size)
            })
            removeDialogBuilder.setNegativeButton(R.string.no, DialogInterface.OnClickListener{ _, _ ->
                //pass
            })

           removeDialogBuilder.create().show()

        }

    }

    fun deleteFromDatabase(account: Accounts):  List<Accounts>? {
        class Worker : AsyncTask<Void, Void, List<Accounts>?>() {
            override fun doInBackground(vararg p0: Void?): List<Accounts>? {
                db?.accountsDao()?.deleteAccount(account)

                return db?.accountsDao()?.getAllAccounts()
            }

        }
        return Worker().execute().get()
    }

    class ViewHolder(view: View, private val context: Context): RecyclerView.ViewHolder(view) {

        val mRemoveButton: ImageButton = view.findViewById(R.id.account_delete_button)

        fun bindItems(data: Accounts) {
            val mNameText: TextView = itemView.findViewById(R.id.name_text)
            val mSbdText: TextView = itemView.findViewById(R.id.sbd_value)
            val mSpText: TextView = itemView.findViewById(R.id.sp_value)
            val mLastSyncDateText: TextView = itemView.findViewById(R.id.last_sync_date_text)
            val mRewardTypeText: TextView = itemView.findViewById(R.id.reward_type_text)

            mNameText.text = data.name
            mSbdText.text = ("%.2f").format(data.SBD) + " SBD"
            mSpText.text = ("%.2f").format(data.SP) + " SP"

            var lastSyncData: Date = Date(data.timestamp * 1000L)
            mLastSyncDateText.text = SimpleDateFormat("dd-MM-yyyy HH:mm").format(lastSyncData).toString()

            if(data.rewardType == 5050) mRewardTypeText.text = "50/50"
            else if(data.rewardType == 100) mRewardTypeText.text = "100SP"
            else mRewardTypeText.text = context.getString(R.string.no_data_shortcut)

        }

    }



}