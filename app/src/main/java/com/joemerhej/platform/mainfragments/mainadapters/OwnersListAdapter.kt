package com.joemerhej.platform.mainfragments.mainadapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.joemerhej.platform.R
import com.joemerhej.platform.models.Owner
import kotlinx.android.synthetic.main.recycler_item_owner.view.*


/**
 * Created by Joe Merhej on 11/15/18.
 */

class OwnersListAdapter(private val ownersList: MutableList<Owner>, private val fragment: Fragment) : RecyclerView.Adapter<OwnersListAdapter.OwnersViewHolder>()
{
    var onOwnerClickListener: OnOwnerClickListener? = null


    interface OnOwnerClickListener
    {
        fun onOwnerClick(view: View?, position: Int)
        fun onOwnerLongPress(view: View?, position: Int)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OwnersViewHolder
    {
        return OwnersViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.recycler_item_owner, parent, false))
    }

    override fun getItemCount() = ownersList.size

    override fun onBindViewHolder(holder: OwnersViewHolder, position: Int)
    {
        // get the owner clicked
        val owner = ownersList[position]

        // fill in the views
        holder.ownerName.text = owner.name

        //TODO: fill in owner image here
        val imageUri = Uri.parse(owner.imageUri)

//        Glide.with(fragment)
//                .load(imageUri)
//                .apply(RequestOptions.circleCropTransform())
//                .apply(RequestOptions().placeholder(R.drawable.ic_launcher_background))
//                .into(holder.ownerImage)
    }

    // VIEW HOLDER
    inner class OwnersViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener, View.OnLongClickListener
    {
        val ownerLayout: LinearLayout = view.owner_layout
        val ownerImage: AppCompatImageView = view.edit_event_owner_imageview
        val ownerName: AppCompatTextView = view.owner_name_textview

        init
        {
            ownerLayout.setOnClickListener(this)
            ownerLayout.setOnLongClickListener(this)
        }

        // click listener
        override fun onClick(view: View?)
        {
            onOwnerClickListener?.onOwnerClick(view, layoutPosition)
        }

        // long click listener
        override fun onLongClick(view: View?): Boolean
        {
            onOwnerClickListener?.onOwnerLongPress(view, layoutPosition)
            return true
        }
    }

}
