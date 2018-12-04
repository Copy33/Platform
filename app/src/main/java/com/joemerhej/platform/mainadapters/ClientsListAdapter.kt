package com.joemerhej.platform.mainadapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
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
    private var colorNegativeBalance: Int = 0
    private var colorPositiveBalance: Int = 0

    interface OnClientClickListener
    {
        fun onClientClick(view: View?, position: Int)
        fun onClientLongPress(view: View?, position: Int)
    }


    init
    {
        // get the negative and positive balance color resource id so we don't have to get them in onBindViewHolder
        fragment.activity?.let {
            colorNegativeBalance = ContextCompat.getColor(it, R.color.colorTextNegativeBalance)
            colorPositiveBalance = ContextCompat.getColor(it, R.color.colorTextPositiveBalance)
        }
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

        var favoritePhoneNumber = ""
        var favoriteEmail = ""
        if(!client.phoneNumbers.isEmpty())
            favoritePhoneNumber = client.phoneNumbers[client.favoritePhoneNumberIndex]
        if(!client.emails.isEmpty())
            favoriteEmail = client.emails[client.favoriteEmailIndex]

        //TODO [improvement]: we can passe the resource in the constructor of the adapter instead of getting it every time, doesn't seem to improve performance
        holder.clientNumberEmail.text = fragment.resources.getString(R.string.client_number_email_format, favoritePhoneNumber, favoriteEmail)

        var favoriteLocation = ""
        if(!client.locations.isEmpty())
            favoriteLocation = client.locations[client.favoriteLocationIndex]

        holder.clientLocation.text = favoriteLocation
        holder.clientBalance.text = client.balance.toString()

        if(client.balance < 0)
            holder.clientBalance.setTextColor(colorNegativeBalance)
        else
            holder.clientBalance.setTextColor(colorPositiveBalance)
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
        val clientLayout = view.client_layout
        val clientName: AppCompatTextView = view.client_name_textview
        val clientNumberEmail: AppCompatTextView = view.client_number_email_textview
        val clientLocation: AppCompatTextView = view.client_location_textview
        val clientBalance: AppCompatTextView = view.client_balance_textview

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
