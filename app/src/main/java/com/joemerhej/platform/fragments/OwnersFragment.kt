package com.joemerhej.platform.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.joemerhej.platform.R
import com.joemerhej.platform.utils.DebugUtils
import com.joemerhej.platform.viewmodels.OwnersViewModel

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


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        return inflater.inflate(R.layout.fragment_owners, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?)
    {
        super.onActivityCreated(savedInstanceState)

        ownersViewModel = activity?.run {
            ViewModelProviders.of(this).get(OwnersViewModel::class.java)
        } ?: throw Exception("Invalid Activity for OwnersFragment")

        ownersViewModel.owners.observe(this, Observer {
            Log.d(DebugUtils.TAG, "Owners Changed! $it")
        })
    }
}
