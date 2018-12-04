package com.joemerhej.platform.detailsdialogfragments.detailsadapters

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.joemerhej.platform.R
import com.joemerhej.platform.detailsdialogfragments.ClientDetailsDialogFragment
import kotlinx.android.synthetic.main.recylcer_item_client_email.view.*

/**
 * Created by Joe Merhej on 12/2/18.
 */
class ClientDetailsEmailsAdapter(var fragment: Fragment,
                                 var emailsList: MutableList<String> = mutableListOf(),
                                 var favoriteEmailIndex: Int = 0) : RecyclerView.Adapter<ClientDetailsEmailsAdapter.EmailsViewHolder>()
{
    var onEmailClickListener: OnEmailClickListener? = null
    var onLastAddedViewCreatedListener: OnEmailLastViewCreatedListener? = null
    var manuallyAddingNewViewFromDialog = false


    interface OnEmailClickListener
    {
        fun onEmailDeleteClick(view: View?, position: Int)
        fun onEmailFavoriteClick(view: View?, position: Int)
    }

    interface OnEmailLastViewCreatedListener
    {
        fun onEmailLastViewCreated(view: View?, position: Int)
    }


    // VIEW HOLDER -------------------------------------------------------------------------------------------------------------------------------------------------
    inner class EmailsViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener
    {
        // views of every email
        val emailDeleteButton: AppCompatImageButton = view.recycler_item_client_email_delete_button
        val emailText: AppCompatEditText = view.recycler_item_client_email_text
        val emailFavoriteButton: AppCompatImageButton = view.recycler_item_client_email_favorite_button


        init
        {
            emailDeleteButton.setOnClickListener(this)
            emailFavoriteButton.setOnClickListener(this)

            // listener for email text that will keep track of the edit text changes and will update the data accordingly
            emailText.addTextChangedListener(object : TextWatcher
            {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int)
                {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int)
                {
                }

                override fun afterTextChanged(s: Editable)
                {
                    emailsList[adapterPosition] = s.toString()
                }
            })
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
    // -------------------------------------------------------------------------------------------------------------------------------------------------------------

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientDetailsEmailsAdapter.EmailsViewHolder
    {
        return EmailsViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.recylcer_item_client_email, parent, false))
    }

    override fun getItemCount() = emailsList.size

    override fun onBindViewHolder(holder: EmailsViewHolder, position: Int)
    {
        // if creating last view in the case of manually adding a view, call the interface method
        if(position == itemCount-1 && manuallyAddingNewViewFromDialog)
        {
            manuallyAddingNewViewFromDialog = false
            onLastAddedViewCreatedListener?.onEmailLastViewCreated(holder.itemView, position)
        }

        // get the email to display
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

        if((fragment as ClientDetailsDialogFragment).inEditMode)
        {
            holder.emailDeleteButton.visibility = View.VISIBLE
            holder.emailText.isEnabled = true
        }
        else
        {
            holder.emailDeleteButton.visibility = View.GONE
            holder.emailText.isEnabled = false
        }
    }
}