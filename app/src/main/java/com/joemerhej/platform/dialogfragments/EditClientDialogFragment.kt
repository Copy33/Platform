package com.joemerhej.platform.dialogfragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProviders
import com.joemerhej.platform.R
import com.joemerhej.platform.models.Client
import com.joemerhej.platform.viewmodels.ClientsViewModel
import kotlinx.android.synthetic.main.autosize_dialog_fragment_child_edit_client.*
import java.lang.Exception


/**
 * Created by Joe Merhej on 10/28/18.
 *
 * EditEventDialogFragment is a child of AutoSizeDialogFragment and will handle the edit event/create new event dialog
 */

// parcelable key for client in case dialog is opened with one (edit)
private const val CLIENT_KEY = "client"

class EditClientDialogFragment : AutoSizeDialogFragment()
{
    interface OnSaveButtonListener
    {
        fun onSaveClick(newClient: Boolean, client: Client, position: Int)
    }

    // view model shared with parent activity
    private lateinit var clientsViewModel: ClientsViewModel

    // mandatory abstract id so the parent can inflate the view
    override val childLayoutResId: Int
        get() = R.layout.autosize_dialog_fragment_child_edit_client

    // save button listener from parent fragment
    private lateinit var saveButtonListener: OnSaveButtonListener

    // check if editing existing client or adding a new one
    private var isNewClient: Boolean = true

    // client position in case of edit
    private var clientPosition: Int = -1


    // companion object for static methods
    companion object
    {
        // new instance takes client and position in case of editing existing client (it would be easy to pass it back and notify adapter)
        fun newInstance(client: Client?, position: Int): EditClientDialogFragment
        {
            val dialogFragment = EditClientDialogFragment()
            val args = Bundle()

            client?.let {
                args.putParcelable(CLIENT_KEY, it)
                dialogFragment.clientPosition = position
            }

            dialogFragment.arguments = args
            return dialogFragment
        }

        fun show(targetFragment: Fragment, client: Client?, position: Int, fragmentManager: FragmentManager?, tag: String)
        {
            val dialogFragment = newInstance(client, position)

            // we need to set target fragment to check later if this fragment implemented the needed listeners
            dialogFragment.setTargetFragment(targetFragment, 1)
            fragmentManager?.let {
                dialogFragment.show(it, tag)
            } ?: throw Exception("Null Fragment Manager while showing Edit Client Dialog")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        // make sure parent implemented the listener interface
        val targetFragment = this.targetFragment
        val activity = this.activity
        saveButtonListener = when
        {
            targetFragment is OnSaveButtonListener -> targetFragment
            activity is OnSaveButtonListener -> activity
            else -> throw ClassCastException("Activity: $activity, or target fragment: $targetFragment must implement ${OnSaveButtonListener::class.java.name}")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        // initialize the view model in the activity scope to make sure it's same model shared with activity
        activity?.run {
            clientsViewModel = ViewModelProviders.of(activity!!).get(ClientsViewModel::class.java)
        } ?: throw Exception("Invalid Activity for EditClientDialog")

        // check if client exists in bundle (in case of edit) and fill in the client properties in the dialog views
        val clientToEdit: Client? = arguments?.getParcelable(CLIENT_KEY)
        clientToEdit?.let {
            isNewClient = false
            edit_client_name_edittext.setText(clientToEdit.name)
            edit_client_name_edittext.setSelection(edit_client_name_edittext.text.length)
            edit_client_phone_number_edittext.setText(clientToEdit.phoneNumbers?.get(clientToEdit.defaultPhoneNumberIndex))
            edit_client_phone_number_edittext.setSelection(edit_client_phone_number_edittext.text.length)
            edit_client_location_edittext.setText(clientToEdit.locations?.get(clientToEdit.defaultLocationIndex))
            edit_client_location_edittext.setSelection(edit_client_location_edittext.text.length)
            edit_client_balance_edittext.setText(clientToEdit.balance.toString())
            edit_client_balance_edittext.setSelection(edit_client_balance_edittext.text.length)
            edit_client_note_edittext.setText(clientToEdit.notes)
            edit_client_note_edittext.setSelection(edit_client_note_edittext.text.length)
        }

        // set up cancel button click listener
        edit_client_cancel_imageview.setOnClickListener {
            dismiss()
        }

        // set up save button click listener
        edit_client_save_button.setOnClickListener {
            // get the last position in case of saving new client
            if(isNewClient)
                clientPosition = clientsViewModel.getClientsList().size

            // TODO: we have clientToEdit so we MAYBE should re-use it for untouched fields
            // create client based on dialog and pass it to the listener then dismiss the dialog
            var clientBalance = 0.0
            if(!edit_client_balance_edittext.text.toString().isEmpty())
                clientBalance = edit_client_balance_edittext.text.toString().toDouble()

//            val newClient = Client(edit_client_name_edittext.text.toString(), edit_client_phone_number_edittext.text.toString(),
//                    clientBalance, mutableListOf(edit_client_location_edittext.text.toString()), 0,
//                    edit_client_note_edittext.text.toString())

            //saveButtonListener.onSaveClick(isNewClient, newClient, clientPosition)
            dismiss()
        }

    }
}





















