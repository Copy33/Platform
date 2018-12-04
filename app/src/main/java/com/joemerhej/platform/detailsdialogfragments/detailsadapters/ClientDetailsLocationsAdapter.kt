package com.joemerhej.platform.detailsdialogfragments.detailsadapters

import android.text.Editable
import android.text.TextWatcher
import android.view.FrameMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.joemerhej.platform.R
import com.joemerhej.platform.detailsdialogfragments.ClientDetailsDialogFragment
import kotlinx.android.synthetic.main.recylcer_item_client_location.view.*

/**
 * Created by Joe Merhej on 12/2/18.
 */
class ClientDetailsLocationsAdapter(var fragment: Fragment,
                                    var locationsList: MutableList<String>,
                                    var favoriteLocationIndex: Int) : RecyclerView.Adapter<ClientDetailsLocationsAdapter.LocationsViewHolder>()
{
    var onLocationClickListener: OnLocationClickListener? = null
    var onLastAddedViewCreatedListener: OnLocationLastViewCreatedListener? = null
    var manuallyAddingNewViewFromDialog = false


    interface OnLocationClickListener
    {
        fun onLocationDeleteClick(view: View?, position: Int)
        fun onLocationFavoriteClick(view: View?, position: Int)
    }

    interface OnLocationLastViewCreatedListener
    {
        fun onLocationLastViewCreated(view: View?, position: Int)
    }

    // VIEW HOLDER -------------------------------------------------------------------------------------------------------------------------------------------------
    inner class LocationsViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener
    {
        // views of every location
        val locationDeleteButton: AppCompatImageButton = view.recycler_item_client_location_delete_button
        val locationText: AppCompatEditText = view.recycler_item_client_location_text
        val locationFavoriteButton: AppCompatImageButton = view.recycler_item_client_location_favorite_button


        init
        {
            locationDeleteButton.setOnClickListener(this)
            locationFavoriteButton.setOnClickListener(this)

            // listener for location text that will keep track of the edit text changes and will update the data accordingly
            locationText.addTextChangedListener(object : TextWatcher
            {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int)
                {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int)
                {
                }

                override fun afterTextChanged(s: Editable)
                {
                    locationsList[adapterPosition] = s.toString()
                }
            })
        }

        override fun onClick(view: View?)
        {
            onLocationClickListener?.let {
                when(view?.id)
                {
                    R.id.recycler_item_client_location_delete_button -> it.onLocationDeleteClick(view, layoutPosition)
                    R.id.recycler_item_client_location_favorite_button -> it.onLocationFavoriteClick(view, layoutPosition)
                }
            }
        }
    }
    // -------------------------------------------------------------------------------------------------------------------------------------------------------------


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientDetailsLocationsAdapter.LocationsViewHolder
    {
        return LocationsViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.recylcer_item_client_location, parent, false))
    }

    override fun getItemCount() = locationsList.size

    override fun onBindViewHolder(holder: LocationsViewHolder, position: Int)
    {
        // if creating last view in the case of manually adding a view, call the interface method
        if(position == itemCount-1 && manuallyAddingNewViewFromDialog)
        {
            manuallyAddingNewViewFromDialog = false
            onLastAddedViewCreatedListener?.onLocationLastViewCreated(holder.itemView, position)
        }

        // get the location to display
        val location = locationsList[position]

        // fill in the holder views
        holder.locationText.setText(location)
        holder.locationText.setSelection(location.length)

        /*TODO [improvement]:
            Instead of calling setImageResource in here we can add 2 buttons for every view and hide/unhide
            the necessary button on user click, for now it's ok since there shouldn't be many numbers per client*/
        if(position == favoriteLocationIndex)
            holder.locationFavoriteButton.setImageResource(R.drawable.ic_star_fill)
        else
            holder.locationFavoriteButton.setImageResource(R.drawable.ic_star_hollow)

        if((fragment as ClientDetailsDialogFragment).inEditMode)
        {
            holder.locationDeleteButton.visibility = View.VISIBLE
            holder.locationText.isEnabled = true
        }
        else
        {
            holder.locationDeleteButton.visibility = View.GONE
            holder.locationText.isEnabled = false
        }
    }
}