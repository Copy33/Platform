package com.joemerhej.platform.detailsdialogfragments.detailsadapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageButton
import androidx.recyclerview.widget.RecyclerView
import com.joemerhej.platform.R
import kotlinx.android.synthetic.main.recylcer_item_client_location.view.*

/**
 * Created by Joe Merhej on 12/2/18.
 */
class ClientDetailsLocationsAdapter(var locationsList: MutableList<String>,
                                    var favoriteLocationIndex: Int) : RecyclerView.Adapter<ClientDetailsLocationsAdapter.LocationsViewHolder>()
{
    var onLocationClickListener: OnLocationClickListener? = null


    interface OnLocationClickListener
    {
        fun onLocationDeleteClick(view: View?, position: Int)
        fun onLocationFavoriteClick(view: View?, position: Int)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientDetailsLocationsAdapter.LocationsViewHolder
    {
        return LocationsViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.recylcer_item_client_location, parent, false))
    }

    override fun getItemCount() = locationsList.size

    override fun onBindViewHolder(holder: LocationsViewHolder, position: Int)
    {
        val location = locationsList[position]

        holder.locationText.setText(location)
        holder.locationText.setSelection(location.length)

        /*TODO: This could be improved:
            Instead of calling setImageResource in here we can add 2 buttons for every view and hide/unhide
            the necessary button on user click, for now it's ok since there shouldn't be many numbers per client*/
        if(position == favoriteLocationIndex)
            holder.locationFavoriteButton.setImageResource(R.drawable.ic_star_fill)
        else
            holder.locationFavoriteButton.setImageResource(R.drawable.ic_star_hollow)
    }

    // VIEW HOLDER
    inner class LocationsViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener
    {
        val locationDeleteButton: AppCompatImageButton = view.recycler_item_client_location_delete_button
        val locationText: AppCompatEditText = view.recycler_item_client_location_text
        val locationFavoriteButton: AppCompatImageButton = view.recycler_item_client_location_favorite_button


        init
        {
            locationDeleteButton.setOnClickListener(this)
            locationFavoriteButton.setOnClickListener(this)
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
}