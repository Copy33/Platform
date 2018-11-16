package com.joemerhej.platform.mainfragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.joemerhej.platform.R
import com.joemerhej.platform.adapters.OwnersListAdapter
import com.joemerhej.platform.dialogfragments.EditOwnerDialogFragment
import com.joemerhej.platform.models.Owner
import com.joemerhej.platform.utils.DebugUtils
import com.joemerhej.platform.viewmodels.OwnersViewModel
import kotlinx.android.synthetic.main.fragment_owners.*


/**
 * Created by Joe Merhej on 11/14/18.
 */
class OwnersFragment : Fragment(), OwnersListAdapter.OnOwnerClickListener, EditOwnerDialogFragment.OnSaveButtonListener
{
    companion object
    {
        fun newInstance() = OwnersFragment()
    }

    // view model
    private lateinit var ownersViewModel: OwnersViewModel

    // recyclerview adapter
    private lateinit var ownersListAdapter: OwnersListAdapter


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        // set up view model
        ownersViewModel = activity?.run {
            ViewModelProviders.of(this).get(OwnersViewModel::class.java)
        } ?: throw Exception("Invalid Activity for OwnersFragment")

        // mock the view model
        if(savedInstanceState == null)
            ownersViewModel.mockOwnersList(14)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        return inflater.inflate(R.layout.fragment_owners, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?)
    {
        super.onActivityCreated(savedInstanceState)

        // observe view model
        ownersViewModel.owners.observe(this, Observer {
            Log.d(DebugUtils.TAG, "Owners Changed! $it")
            ownersListAdapter.notifyDataSetChanged()
        })

        // set up recycler view
        owners_recyclerview.layoutManager = GridLayoutManager(activity, calculateNoOfColumns())
        ownersListAdapter = OwnersListAdapter(ownersViewModel.getOwnersList(), this)
        ownersListAdapter.onOwnerClickListener = this
        owners_recyclerview.adapter = ownersListAdapter

        // set up the add owner fab
        add_owner_fab.animate().setDuration(200).scaleX(1.0f).scaleY(1.0f).interpolator = LinearOutSlowInInterpolator()
        add_owner_fab.setOnClickListener{
            EditOwnerDialogFragment.show(this, null, -1, fragmentManager, "tag")
        }
    }

    private fun calculateNoOfColumns(): Int
    {
        val dpWidth = resources.displayMetrics.widthPixels / resources.displayMetrics.density
        val dpColumnWidth = resources.getDimension(R.dimen.owner_grid_col_width) / resources.displayMetrics.density
        val columns = (dpWidth / dpColumnWidth).toInt()
        return if(columns > 0)
            columns
        else
            1
    }

    override fun onOwnerClick(view: View?, position: Int)
    {
        Log.d(DebugUtils.TAG, "Click! Position = $position, Owner = ${ownersViewModel.getOwnersList()[position]}" )

        val owner: Owner? = ownersViewModel.getOwner(position)

        owner?.let {
            EditOwnerDialogFragment.show(this, it, position, fragmentManager, "tag")
        }
    }

    override fun onOwnerLongClick(view: View?, position: Int)
    {
        val owner: Owner? = ownersViewModel.getOwner(position)

        owner?.let {
            val builder = AlertDialog.Builder(activity!!)
            builder.setTitle("Delete Owner?")
                    .setMessage("Deleting ${it.name} from your owners list will remove all their past events from the schedule.")
                    .setPositiveButton("Delete") { _, _ ->
                        ownersViewModel.removeOwner(position)
                        ownersListAdapter.notifyItemRemoved(position)
                    }
                    .setNegativeButton("Cancel") { dialog, _ ->
                        dialog.cancel()
                    }

            val alertDialog = builder.create()
            alertDialog.show()
        }
    }

    override fun onSaveClick(newOwner:Boolean, owner: Owner, position: Int)
    {
        // modify view model accordingly
        if(newOwner)
        {
            ownersViewModel.addOwner(owner)
            ownersListAdapter.notifyItemInserted(position)
        }
        else
        {
            ownersViewModel.getOwnersList()[position] = owner
            ownersListAdapter.notifyItemChanged(position)
        }
    }
}



