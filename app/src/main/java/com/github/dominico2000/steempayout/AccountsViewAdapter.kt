package com.github.dominico2000.steempayout

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
class AccountsViewAdapter( val list: ArrayList<Accounts>, val accountsOperation: AccoutsOperation):RecyclerView.Adapter<AccountsViewAdapter.ViewHolder>()  {



    override fun getItemCount(): Int {
        return list.size
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountsViewAdapter.ViewHolder{
        val v = LayoutInflater.from(parent.context).inflate(R.layout.account_card, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: AccountsViewAdapter.ViewHolder, position: Int) {
        holder.bindItems(list[position])

        holder.mRemoveButton.setOnClickListener { view ->

            var result = accountsOperation.removeAccount(list[position])
            accountsOperation.updateCardsFromDatabase()
            Log.d("Remove",list[position].toString())
            Log.d("Database_State", result.toString())



        }

    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {

        val mRemoveButton: ImageButton = view.findViewById(R.id.account_delete_button)

        fun bindItems(data: Accounts) {
            val nameText: TextView = itemView.findViewById(R.id.name_text)
            val sbdText: TextView = itemView.findViewById(R.id.sbd_value)
            val spText: TextView = itemView.findViewById(R.id.sp_value)
            val lastSyncDateText: TextView = itemView.findViewById(R.id.last_sync_date_text)

            nameText.text = data.name
            sbdText.text = data.SBD.toString() + " SBD"
            spText.text = data.SP.toString() + " SP"

            var lastSyncData: Date = Date(data.timestamp * 1000L)
            lastSyncDateText.text = SimpleDateFormat("dd-MM-yyyy HH:mm").format(lastSyncData).toString()


        }

    }



}