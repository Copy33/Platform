package com.joemerhej.platform.detailsdialogfragments.detailsadapters

import android.graphics.Color
import android.graphics.drawable.Drawable
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
import kotlinx.android.synthetic.main.recylcer_item_client_phone_number.view.*


/**
 * Created by Joe Merhej on 12/2/18.
 */
class ClientDetailsPhoneNumbersAdapter(var fragment: Fragment,
                                       var phoneNumbersList: MutableList<String>,
                                       var favoritePhoneNumberIndex: Int = 0) : RecyclerView.Adapter<ClientDetailsPhoneNumbersAdapter.PhoneNumbersViewHolder>()
{
    var onPhoneNumberClickListener: OnPhoneNumberClickListener? = null
    var onLastAddedViewCreatedListener: OnPhoneNumberLastViewCreatedListener? = null
    var manuallyAddingNewViewFromDialog = false
    var originalPhoneNumberTextEditBackgrounds: MutableList<Drawable> = mutableListOf()


    interface OnPhoneNumberClickListener
    {
        fun onPhoneNumberDeleteClick(view: View?, position: Int)
        fun onPhoneNumberFavoriteClick(view: View?, position: Int)
    }

    interface OnPhoneNumberLastViewCreatedListener
    {
        fun onPhoneNumberLastViewCreated(view: View?, position: Int)
    }

    // VIEW HOLDER -------------------------------------------------------------------------------------------------------------------------------------------------
    inner class PhoneNumbersViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener
    {
        // views of every phone number
        val phoneNumberDeleteButton: AppCompatImageButton = view.recycler_item_client_phone_number_delete_button
        val phoneNumberText: AppCompatEditText = view.recycler_item_client_phone_number_text
        val phoneNumberFavoriteButton: AppCompatImageButton = view.recycler_item_client_phone_number_favorite_button

        init
        {
            phoneNumberDeleteButton.setOnClickListener(this)
            phoneNumberFavoriteButton.setOnClickListener(this)

            originalPhoneNumberTextEditBackgrounds.add(phoneNumberText.background)

            // listener for phone number text that will keep track of the edit text changes and will update the data accordingly
            phoneNumberText.addTextChangedListener(object : TextWatcher
            {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int)
                {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int)
                {
                }

                override fun afterTextChanged(s: Editable)
                {
                    phoneNumbersList[adapterPosition] = s.toString()
                }
            })
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
    // -------------------------------------------------------------------------------------------------------------------------------------------------------------

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientDetailsPhoneNumbersAdapter.PhoneNumbersViewHolder
    {
        return PhoneNumbersViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.recylcer_item_client_phone_number, parent, false))
    }

    override fun getItemCount() = phoneNumbersList.size

    override fun onBindViewHolder(holder: PhoneNumbersViewHolder, position: Int)
    {
        // if creating last view in the case of manually adding a view, call the interface method
        if(position == itemCount-1 && manuallyAddingNewViewFromDialog)
        {
            manuallyAddingNewViewFromDialog = false
            onLastAddedViewCreatedListener?.onPhoneNumberLastViewCreated(holder.itemView, position)
        }

        // get the phone number to display
        val phoneNumber = phoneNumbersList[position]

        // fill in the holder views
        holder.phoneNumberText.setText(phoneNumber)
        holder.phoneNumberText.setSelection(phoneNumber.length)

        /*TODO [improvement]:
            Instead of calling setImageResource in here we can add 2 buttons for every view and hide/unhide
            the necessary button on user click, for now it's ok since there shouldn't be many numbers per client*/
        if(position == favoritePhoneNumberIndex)
            holder.phoneNumberFavoriteButton.setImageResource(R.drawable.ic_star_fill)
        else
            holder.phoneNumberFavoriteButton.setImageResource(R.drawable.ic_star_hollow)

        if((fragment as ClientDetailsDialogFragment).inEditMode)
        {
            holder.phoneNumberDeleteButton.visibility = View.VISIBLE
            holder.phoneNumberText.isEnabled = true
            holder.phoneNumberText.background = originalPhoneNumberTextEditBackgrounds[position]
        }
        else
        {
            holder.phoneNumberDeleteButton.visibility = View.GONE
            holder.phoneNumberText.isEnabled = false
            holder.phoneNumberText.setBackgroundColor(Color.TRANSPARENT)
        }
    }
}