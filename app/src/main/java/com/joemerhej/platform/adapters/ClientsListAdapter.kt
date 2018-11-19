package com.joemerhej.platform.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.joemerhej.platform.R
import com.joemerhej.platform.models.Client
import kotlinx.android.synthetic.main.recycler_item_client.view.*


/**
 * Created by Joe Merhej on 11/19/18.
 */

class ClientsListAdapter(private var clientsList: MutableList<Client>, private val fragment: Fragment) : RecyclerView.Adapter<ClientsListAdapter.ClientsViewHolder>()
{
    var onClientClickListener: OnClientClickListener? = null


    interface OnClientClickListener
    {
        fun onClientClick(view: View?, position: Int)
        fun onClientLongPress(view: View?, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientsListAdapter.ClientsViewHolder
    {
        return ClientsViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.recycler_item_client, parent, false))
    }

    override fun getItemCount() = clientsList.size

    override fun onBindViewHolder(holder: ClientsViewHolder, position: Int)
    {
        // get the client clicked
        val client = clientsList[position]

        // fill in the views
        holder.clientName.text = client.name
        holder.clientNumber.text = client.phoneNumber
        holder.clientLocation.text = client.locations?.get(client.defaultLocationIndex) ?: ""
        holder.clientBalance.text = client.balance.toString()

        fragment.activity?.let {
            if(client.balance < 0)
                holder.clientBalance.setTextColor(ContextCompat.getColor(it, R.color.colorTextNegativeBalance))
            else
                holder.clientBalance.setTextColor(ContextCompat.getColor(it, R.color.colorTextPositiveBalance))
        }
    }

    fun setClientsList(newList: MutableList<Client>?)
    {
        newList?.let {
            clientsList = it
        }
    }

    // VIEW HOLDER
    inner class ClientsViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener, View.OnLongClickListener
    {
        val clientLayout: ConstraintLayout = view.client_layout
        val clientName: TextView = view.client_name_textview
        val clientNumber: TextView = view.client_number_textview
        val clientLocation: TextView = view.client_location_textview
        val clientBalance: TextView = view.client_balance_textview

        init
        {
            clientLayout.setOnClickListener(this)
            clientLayout.setOnLongClickListener(this)
        }

        // click listener
        override fun onClick(view: View?)
        {
            onClientClickListener?.onClientClick(view, layoutPosition)
        }

        // long click listener
        override fun onLongClick(view: View?): Boolean
        {
            onClientClickListener?.onClientLongPress(view, layoutPosition)
            return true
        }
    }

}
