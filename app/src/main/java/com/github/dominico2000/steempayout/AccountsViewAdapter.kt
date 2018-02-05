package com.github.dominico2000.steempayout

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView


/**
 * Created by dominik on 02.02.18.
 */
class AccountsViewAdapter( val list: ArrayList<AccountsData> ):RecyclerView.Adapter<AccountsViewAdapter.ViewHolder>()  {

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: AccountsViewAdapter.ViewHolder, position: Int) {
        holder.bindItems(list[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): AccountsViewAdapter.ViewHolder{
        val v = LayoutInflater.from(parent?.context).inflate(R.layout.account_card, parent, false)
        return ViewHolder(v)
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        fun bindItems(data: AccountsData) {
            val nameText: TextView = itemView.findViewById(R.id.name_text)
            val sbdText: TextView = itemView.findViewById(R.id.sbd_text)

            nameText.text = data.name
            sbdText.text = data.SBD.toString() + " SBD"

        }

    }

}