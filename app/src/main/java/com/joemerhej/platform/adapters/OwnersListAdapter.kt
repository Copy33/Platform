package com.joemerhej.platform.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.RequestOptions.circleCropTransform
import com.joemerhej.platform.R
import com.joemerhej.platform.models.Owner
import kotlinx.android.synthetic.main.recycler_item_owner.view.*


/**
 * Created by Joe Merhej on 11/15/18.
 */

class OwnersListAdapter(private val ownersList: MutableList<Owner>, private val fragment: Fragment) : RecyclerView.Adapter<OwnersListAdapter.OwnersViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OwnersViewHolder
    {
        return OwnersViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.recycler_item_owner, parent, false))
    }

    override fun getItemCount() = ownersList.size

    override fun onBindViewHolder(holder: OwnersViewHolder, position: Int)
    {
        val owner = ownersList[position]

        holder.ownerName.text = owner.name

        val imageUri = owner.imageUri?.let { Uri.parse(it) } ?: Uri.EMPTY
        Glide.with(fragment)
                .load(imageUri)
                .apply(RequestOptions.circleCropTransform())
                .apply(RequestOptions().placeholder(R.drawable.ic_launcher_background))
                .into(holder.ownerImage)
    }

    class OwnersViewHolder(view: View) : RecyclerView.ViewHolder(view)
    {
        val ownerImage = view.owner_imageview
        val ownerName = view.owner_name_textview
    }

}
