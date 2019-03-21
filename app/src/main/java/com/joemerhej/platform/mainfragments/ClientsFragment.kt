package com.joemerhej.platform.mainfragments

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import com.joemerhej.platform.viewmodels.ClientsViewModel
import com.joemerhej.platform.R
import com.joemerhej.platform.utils.DebugUtils
import java.lang.Exception
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.joemerhej.platform.mainfragments.mainadapters.ClientsListAdapter
import com.joemerhej.platform.dialogfragments.detailsdialogfragments.ClientDetailsDialogFragment
import com.joemerhej.platform.models.Client
import kotlinx.android.synthetic.main.fragment_clients.*

/**
 * Created by Joe Merhej on 11/14/18.
 */
class ClientsFragment : Fragment(), ClientsListAdapter.OnClientClickListener, ClientDetailsDialogFragment.OnSaveButtonListener
{
    companion object
    {
        fun newInstance() = ClientsFragment()
    }

    // view model
    private lateinit var clientsViewModel: ClientsViewModel

    // recyclerview adapter
    private lateinit var clientsListAdapter: ClientsListAdapter


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        // set up view model
        clientsViewModel = activity?.run {
            ViewModelProviders.of(this).get(ClientsViewModel::class.java)
        } ?: throw Exception("Invalid Activity for ClientsFragment")

        // mock the view model
        if(savedInstanceState == null)
            clientsViewModel.mockClientsList(2)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        return inflater.inflate(R.layout.fragment_clients, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?)
    {
        super.onActivityCreated(savedInstanceState)

        // observe view model
        clientsViewModel.clients.observe(this, Observer {
            Log.d(DebugUtils.TAG, "Clients Changed! $it")
            clientsListAdapter.setClientsList(it)
            clientsListAdapter.notifyDataSetChanged()
        })

        // set up recycler view
        clients_recyclerview.layoutManager = LinearLayoutManager(activity)
        clients_recyclerview.addItemDecoration(DividerItemDecoration(activity, LinearLayoutManager.VERTICAL))
        clientsListAdapter = ClientsListAdapter(clientsViewModel.getClientsList(), this)
        clientsListAdapter.onClientClickListener = this
        clients_recyclerview.adapter = clientsListAdapter

        // set up the add client fab
        add_client_fab.animate().setDuration(200).scaleX(1.0f).scaleY(1.0f).interpolator = LinearOutSlowInInterpolator()
        add_client_fab.setOnClickListener {
            ClientDetailsDialogFragment.show(this, -1, fragmentManager, "tag")
        }
    }

    /**
     * Click listener for client in list.
     *
     * @param view view being clicked
     * @param position the position of the clicked view
     */
    override fun onClientClick(view: View?, position: Int)
    {
        Log.d(DebugUtils.TAG, "Click! Position = $position, Client = ${clientsViewModel.getClientsList()[position]}")

        ClientDetailsDialogFragment.show(this, position, fragmentManager, "tag")
    }

    /**
     * Listener for when a client view is long pressed.
     *
     * @param view view being long pressed
     * @param position the position of the long pressed view
     */
    override fun onClientLongPress(view: View?, position: Int)
    {
        val client: Client? = clientsViewModel.getClient(position)

        // create a delete client dialog
        client?.let {
            val builder = AlertDialog.Builder(activity!!)
            builder.setTitle("Delete Client?")
                    .setMessage("Deleting ${it.name} from your clients list will remove them from your database, this action is irreversible.")
                    .setPositiveButton("Delete") { _, _ ->
                        // if delete is pressed, remove the client
                        clientsViewModel.removeClient(position)
                        clientsListAdapter.notifyItemRemoved(position)
                    }
                    .setNegativeButton("Cancel") { dialog, _ ->
                        dialog.cancel()
                    }

            val alertDialog = builder.create()
            alertDialog.show()
        }
    }

    /**
     * Listener for when the user hits the save button.
     *
     * @param newClient boolean to indicate if a new client was created or editing an existing one
     * @param client the modified or newly created client
     * @param position the position of the edited/created client (useful for adapter transaction)
     */
    override fun onSaveClick(newClient: Boolean, client: Client, position: Int)
    {
        // modify view model accordingly (add new item or edit existing)
        if(newClient)
        {
            clientsViewModel.addClient(client)
            clientsListAdapter.notifyItemInserted(position)
        }
        else
        {
            clientsViewModel.getClientsList()[position] = client
            clientsListAdapter.notifyItemChanged(position)
        }
    }
}



















