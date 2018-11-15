package com.joemerhej.platform.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.joemerhej.platform.R
import com.joemerhej.platform.adapters.OwnersListAdapter
import com.joemerhej.platform.utils.DebugUtils
import com.joemerhej.platform.viewmodels.OwnersViewModel
import kotlinx.android.synthetic.main.fragment_owners.*


/**
 * Created by Joe Merhej on 11/14/18.
 */
class OwnersFragment : Fragment()
{
    companion object
    {
        fun newInstance() = OwnersFragment()
    }

    // view model
    private lateinit var ownersViewModel: OwnersViewModel

    // recyclerview adapter
    private lateinit var ownersListAdapter: OwnersListAdapter


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        return inflater.inflate(R.layout.fragment_owners, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?)
    {
        super.onActivityCreated(savedInstanceState)

        // set up view model and observe
        ownersViewModel = activity?.run {
            ViewModelProviders.of(this).get(OwnersViewModel::class.java)
        } ?: throw Exception("Invalid Activity for OwnersFragment")

        ownersViewModel.owners.observe(this, Observer {
            Log.d(DebugUtils.TAG, "Owners Changed! $it")
            ownersListAdapter.notifyDataSetChanged()
        })

        // mock list
        ownersViewModel.mockOwnersList(50)

        // set up recycler view
        owners_recyclerview.layoutManager = GridLayoutManager(activity, calculateNoOfColumns())
        ownersListAdapter = OwnersListAdapter(ownersViewModel.getOwnersList(), this)
        owners_recyclerview.adapter = ownersListAdapter

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
}
