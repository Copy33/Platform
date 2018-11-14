package com.joemerhej.platform.fragments

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.joemerhej.platform.viewmodels.ClientsViewModel
import com.joemerhej.platform.R
import com.joemerhej.platform.utils.DebugUtils
import java.lang.Exception
import androidx.lifecycle.Observer

/**
 * Created by Joe Merhej on 11/14/18.
 */
class ClientsFragment : Fragment()
{
    companion object
    {
        fun newInstance() = ClientsFragment()
    }

    // view model
    private lateinit var clientsViewModel: ClientsViewModel


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        return inflater.inflate(R.layout.fragment_clients, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?)
    {
        super.onActivityCreated(savedInstanceState)

        clientsViewModel = activity?.run {
            ViewModelProviders.of(this).get(ClientsViewModel::class.java)
        } ?: throw Exception("Invalid Activity for ClientsFragment")

        clientsViewModel.clients.observe(this, Observer {
            Log.d(DebugUtils.TAG, "Clients Changed! $it")
        })
    }
}
