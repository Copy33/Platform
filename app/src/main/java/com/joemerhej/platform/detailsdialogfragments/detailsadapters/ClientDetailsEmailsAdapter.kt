package com.joemerhej.platform.detailsdialogfragments.detailsadapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageButton
import androidx.recyclerview.widget.RecyclerView
import com.joemerhej.platform.R
import kotlinx.android.synthetic.main.recylcer_item_client_email.view.*

/**
 * Created by Joe Merhej on 12/2/18.
 */
class ClientDetailsEmailsAdapter(var emailsList: MutableList<String> = mutableListOf(),
                                 var favoriteEmailIndex: Int = 0) : RecyclerView.Adapter<ClientDetailsEmailsAdapter.EmailsViewHolder>()
{
    var onEmailClickListener: OnEmailClickListener? = null


    interface OnEmailClickListener
    {
        fun onEmailDeleteClick(view: View?, position: Int)
        fun onEmailFavoriteClick(view: View?, position: Int)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientDetailsEmailsAdapter.EmailsViewHolder
    {
        return EmailsViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.recylcer_item_client_email, parent, false))
    }

    override fun getItemCount() = emailsList.size

    override fun onBindViewHolder(holder: EmailsViewHolder, position: Int)
    {
        val email = emailsList[position]

        holder.emailText.setText(email)
        holder.emailText.setSelection(email.length)

        /*TODO [improvement]:
            Instead of calling setImageResource in here we can add 2 buttons for every view and hide/unhide
            the necessary button on user click, for now it's ok since there shouldn't be many numbers per client*/
        if(position == favoriteEmailIndex)
            holder.emailFavoriteButton.setImageResource(R.drawable.ic_star_fill)
        else
            holder.emailFavoriteButton.setImageResource(R.drawable.ic_star_hollow)
    }

    // VIEW HOLDER
    inner class EmailsViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener
    {
        val emailDeleteButton: AppCompatImageButton = view.recycler_item_client_email_delete_button
        val emailText: AppCompatEditText = view.recycler_item_client_email_text
        val emailFavoriteButton: AppCompatImageButton = view.recycler_item_client_email_favorite_button


        init
        {
            emailDeleteButton.setOnClickListener(this)
            emailFavoriteButton.setOnClickListener(this)
        }

        override fun onClick(view: View?)
        {
            onEmailClickListener?.let {
                when(view?.id)
                {
                    R.id.recycler_item_client_email_delete_button -> it.onEmailDeleteClick(view, layoutPosition)
                    R.id.recycler_item_client_email_favorite_button -> it.onEmailFavoriteClick(view, layoutPosition)
                }
            }
        }
    }
}