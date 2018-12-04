package com.joemerhej.platform.detailsdialogfragments.detailsadapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageButton
import androidx.recyclerview.widget.RecyclerView
import com.joemerhej.platform.R
import kotlinx.android.synthetic.main.recylcer_item_client_phone_number.view.*

/**
 * Created by Joe Merhej on 12/2/18.
 */
class ClientDetailsPhoneNumbersAdapter(var phoneNumbersList: MutableList<String> = mutableListOf(),
                                       var favoritePhoneNumberIndex: Int = 0) : RecyclerView.Adapter<ClientDetailsPhoneNumbersAdapter.PhoneNumbersViewHolder>()
{
    var onPhoneNumberClickListener: OnPhoneNumberClickListener? = null


    interface OnPhoneNumberClickListener
    {
        fun onPhoneNumberDeleteClick(view: View?, position: Int)
        fun onPhoneNumberFavoriteClick(view: View?, position: Int)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientDetailsPhoneNumbersAdapter.PhoneNumbersViewHolder
    {
        return PhoneNumbersViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.recylcer_item_client_phone_number, parent, false))
    }

    override fun getItemCount() = phoneNumbersList.size

    override fun onBindViewHolder(holder: PhoneNumbersViewHolder, position: Int)
    {
        val phoneNumber = phoneNumbersList[position]

        holder.phoneNumberText.setText(phoneNumber)
        holder.phoneNumberText.setSelection(phoneNumber.length)

        /*TODO [improvement]:
            Instead of calling setImageResource in here we can add 2 buttons for every view and hide/unhide
            the necessary button on user click, for now it's ok since there shouldn't be many numbers per client*/
        if(position == favoritePhoneNumberIndex)
            holder.phoneNumberFavoriteButton.setImageResource(R.drawable.ic_star_fill)
        else
            holder.phoneNumberFavoriteButton.setImageResource(R.drawable.ic_star_hollow)
    }

    // VIEW HOLDER
    inner class PhoneNumbersViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener
    {
        val phoneNumberDeleteButton: AppCompatImageButton = view.recycler_item_client_phone_number_delete_button
        val phoneNumberText: AppCompatEditText = view.recycler_item_client_phone_number_text
        val phoneNumberFavoriteButton: AppCompatImageButton = view.recycler_item_client_phone_number_favorite_button


        init
        {
            phoneNumberDeleteButton.setOnClickListener(this)
            phoneNumberFavoriteButton.setOnClickListener(this)
        }

        override fun onClick(view: View?)
        {
            onPhoneNumberClickListener?.let {
                when(view?.id)
                {
                    R.id.recycler_item_client_phone_number_delete_button -> it.onPhoneNumberDeleteClick(view, layoutPosition)
                    R.id.recycler_item_client_phone_number_favorite_button -> it.onPhoneNumberFavoriteClick(view, layoutPosition)
                }
            }
        }
    }
}