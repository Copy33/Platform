package com.joemerhej.platform.detailsdialogfragments

import android.animation.ObjectAnimator
import android.animation.StateListAnimator
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.joemerhej.platform.R
import com.joemerhej.platform.models.Client
import com.joemerhej.platform.detailsdialogfragments.detailsadapters.ClientDetailsEmailsAdapter
import com.joemerhej.platform.detailsdialogfragments.detailsadapters.ClientDetailsLocationsAdapter
import com.joemerhej.platform.detailsdialogfragments.detailsadapters.ClientDetailsPhoneNumbersAdapter
import com.joemerhej.platform.utils.DebugUtils
import com.joemerhej.platform.viewmodels.ClientsViewModel
import kotlinx.android.synthetic.main.autosize_dialog_fragment_child_edit_client.*
import java.lang.Exception
import java.lang.NumberFormatException


/**
 * Created by Joe Merhej on 10/28/18.
 *
 * EventDetailsDialogFragment is a child of AutoSizeDialogFragment and will handle the edit event/create new event dialog
 */
class ClientDetailsDialogFragment : AutoSizeDialogFragment(), ClientDetailsPhoneNumbersAdapter.OnPhoneNumberClickListener, ClientDetailsEmailsAdapter.OnEmailClickListener, ClientDetailsLocationsAdapter.OnLocationClickListener
{
    interface OnSaveButtonListener
    {
        //TODO: this could be improved, we can avoid passing in the client here and putting it/modifying it in the viewmodel
        fun onSaveClick(newClient: Boolean, client: Client, position: Int)
    }

    override val childLayoutResId: Int                                              // mandatory abstract id so the parent can inflate the view
        get() = R.layout.autosize_dialog_fragment_child_edit_client
    private lateinit var clientsViewModel: ClientsViewModel                         // viewmodel shared with parent activity
    private lateinit var client: Client                                             // client shown, shallow copy of client in view model (empty if new client)
    private lateinit var clientBeforeEdit: Client                                   // copy of client before edit used to undo changes
    private lateinit var saveButtonListener: OnSaveButtonListener                   // save button listener from parent fragment
    private var isNewClient: Boolean = true                                         // check if editing existing client or adding a new one
    private var clientPosition: Int = -1                                            // client position in case of edit
    private lateinit var phoneNumbersAdapter: ClientDetailsPhoneNumbersAdapter      // adapter for the phone numbers list
    private lateinit var emailsAdapter: ClientDetailsEmailsAdapter                  // adapter for the emails list
    private lateinit var locationsAdapter: ClientDetailsLocationsAdapter            // adapter for the locations list


    // companion object for static methods
    companion object
    {
        // new instance client position in case of editing existing client (it would be easy to pass it back and notify adapter)
        fun newInstance(clientPosition: Int): ClientDetailsDialogFragment
        {
            val dialogFragment = ClientDetailsDialogFragment()
            dialogFragment.clientPosition = clientPosition

            val args = Bundle()
            dialogFragment.arguments = args
            return dialogFragment
        }

        fun show(targetFragment: Fragment, clientPosition: Int, fragmentManager: FragmentManager?, tag: String)
        {
            val dialogFragment = newInstance(clientPosition)

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
            clientsViewModel = ViewModelProviders.of(this).get(ClientsViewModel::class.java)
        } ?: throw Exception("Invalid Activity for EditClientDialog")

        // check if editing existing client, else create an empty client
        var clientFromViewModel: Client? = clientsViewModel.getClient(clientPosition)
        if(clientFromViewModel != null)
        {
            isNewClient = false
            client = clientFromViewModel
        }
        else
            client = Client("")

        // create copy of client in case edit experience is canceled
        clientBeforeEdit = client.clone()

        // fill in the dialog views with our client
        fillDialogViewsFromClient(client)

        // set up scroll listener to change the toolbar elevation while scrolling
        edit_client_scrollview.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { _, _, scrollY, _, _ ->
            val stateListAnimator = StateListAnimator()
            if(scrollY > 0)
                stateListAnimator.addState(IntArray(0), ObjectAnimator.ofFloat(edit_client_appbarlayout, "elevation", 8f).also { it.duration = 0 })
            else
                stateListAnimator.addState(IntArray(0), ObjectAnimator.ofFloat(edit_client_appbarlayout, "elevation", 0f).also { it.duration = 0 })
            edit_client_appbarlayout.stateListAnimator = stateListAnimator
        })

        // set up phone number add button listener - clicking plus sign will simply create a new empty phone number and add it to the client
        edit_client_add_phone_number_button.setOnClickListener {
            client.phoneNumbers.add("")
            phoneNumbersAdapter.notifyItemInserted(client.phoneNumbers.size)
        }

        // set up email add button listener - clicking plus sign will simply create a new empty email and add it to the client
        edit_client_add_email_button.setOnClickListener {
            client.emails.add("")
            emailsAdapter.notifyItemInserted(client.emails.size)
        }

        // set up location add button listener - clicking plus sign will simply create a new empty location and add it to the client
        edit_client_add_location_button.setOnClickListener {
            client.locations.add("")
            locationsAdapter.notifyItemInserted(client.locations.size)
        }

        // set up cancel button click listener - clicking cancel will dismiss the dialog
        edit_client_cancel_imageview.setOnClickListener {
            if(!isNewClient)
            {
                // if not new client, reset the lists in case of delete or favorite (delete and favorite modify the shallow copy inside the adapter)
                client.phoneNumbers = clientBeforeEdit.phoneNumbers
                client.favoritePhoneNumberIndex = clientBeforeEdit.favoritePhoneNumberIndex
                client.emails = clientBeforeEdit.emails
                client.favoriteEmailIndex = clientBeforeEdit.favoriteEmailIndex
                client.locations = clientBeforeEdit.locations
                client.favoriteLocationIndex = clientBeforeEdit.favoriteLocationIndex
            }
            dismiss()
        }

        // set up save button click listener - clicking save will create a client out of the dialog views and return it to the main client fragment
        edit_client_save_button.setOnClickListener {
            // get the last client list position in case of saving a new client (clientsFragment will pass in -1)
            if(isNewClient)
                clientPosition = clientsViewModel.getClientsList().size

            // create client based on dialog views and pass it to the listener then dismiss the dialog
            val newClient = createClientFromDialogViews()
            saveButtonListener.onSaveClick(isNewClient, newClient, clientPosition)
            dismiss()
        }
    }

    /**
     * Function that will fill the client views in the dialog given a client
     *
     * @param client client to use
     */
    private fun fillDialogViewsFromClient(client: Client)
    {
        // client name
        edit_client_name_edittext.setText(client.name)
        edit_client_name_edittext.setSelection(client.name.length)

        // phone numbers
        edit_client_phone_number_recyclerview.layoutManager = LinearLayoutManager(context)
        edit_client_phone_number_recyclerview.itemAnimator = null
        phoneNumbersAdapter = ClientDetailsPhoneNumbersAdapter(client.phoneNumbers, client.favoritePhoneNumberIndex)
        edit_client_phone_number_recyclerview.adapter = phoneNumbersAdapter
        phoneNumbersAdapter.onPhoneNumberClickListener = this

        // emails
        edit_client_email_recyclerview.layoutManager = LinearLayoutManager(context)
        edit_client_email_recyclerview.itemAnimator = null
        emailsAdapter = ClientDetailsEmailsAdapter(client.emails, client.favoriteEmailIndex)
        edit_client_email_recyclerview.adapter = emailsAdapter
        emailsAdapter.onEmailClickListener = this

        // locations
        edit_client_location_recyclerview.layoutManager = LinearLayoutManager(context)
        edit_client_location_recyclerview.itemAnimator = null
        locationsAdapter = ClientDetailsLocationsAdapter(client.locations, client.favoriteLocationIndex)
        edit_client_location_recyclerview.adapter = locationsAdapter
        locationsAdapter.onLocationClickListener = this

        // balance
        val clientBalance = client.balance.toString()
        edit_client_balance_edittext.setText(clientBalance)
        edit_client_balance_edittext.setSelection(clientBalance.length)

        // notes
        edit_client_note_edittext.setText(client.notes)
        edit_client_note_edittext.setSelection(client.notes.length)
    }

    /**
     * Function that will create a client from the dialog views info
     *
     * @return client created
     */
    private fun createClientFromDialogViews(): Client
    {
        val client = Client()

        // client name
        client.name = edit_client_name_edittext.text.toString()

        // phone numbers
        client.phoneNumbers = phoneNumbersAdapter.phoneNumbersList
        client.favoritePhoneNumberIndex = phoneNumbersAdapter.favoritePhoneNumberIndex

        // emails
        client.emails = emailsAdapter.emailsList
        client.favoriteEmailIndex = emailsAdapter.favoriteEmailIndex

        // locations
        client.locations = locationsAdapter.locationsList
        client.favoriteLocationIndex = locationsAdapter.favoriteLocationIndex

        // balance
        if(!edit_client_balance_edittext.text.toString().isEmpty())
        {
            try
            {
                client.balance = edit_client_balance_edittext.text.toString().toDouble()
            }
            catch(e: NumberFormatException)
            {
                Log.d(DebugUtils.TAG, "Can't parse number")
            }
        }
        else
            client.balance = 0.0

        // notes
        client.notes = edit_client_note_edittext.text.toString()

        return client
    }

    /**
     * Phone Number delete button click listener
     *
     * @param view view to delete
     * @param position position of the view to delete
     */
    override fun onPhoneNumberDeleteClick(view: View?, position: Int)
    {
        phoneNumbersAdapter.phoneNumbersList.removeAt(position)
        phoneNumbersAdapter.notifyItemRemoved(position)

        // check if we're removing the favorite number and set it back to 0
        if(position == phoneNumbersAdapter.favoritePhoneNumberIndex)
        {
            phoneNumbersAdapter.favoritePhoneNumberIndex = 0
            phoneNumbersAdapter.notifyItemChanged(0)
        }

        // update favorite index in case we're removing number before it
        if(position < phoneNumbersAdapter.favoritePhoneNumberIndex)
            --phoneNumbersAdapter.favoritePhoneNumberIndex
    }

    /**
     * Phone Number favorite button click listener
     *
     * @param view view to favorite
     * @param position position of the view to favorite
     */
    override fun onPhoneNumberFavoriteClick(view: View?, position: Int)
    {
        val oldFavorite = phoneNumbersAdapter.favoritePhoneNumberIndex
        phoneNumbersAdapter.favoritePhoneNumberIndex = position
        phoneNumbersAdapter.notifyItemChanged(oldFavorite)
        phoneNumbersAdapter.notifyItemChanged(position)
    }

    /**
     * Email delete button click listener
     *
     * @param view view to delete
     * @param position position of the view to delete
     */
    override fun onEmailDeleteClick(view: View?, position: Int)
    {
        emailsAdapter.emailsList.removeAt(position)
        emailsAdapter.notifyItemRemoved(position)

        // check if we're removing the favorite number and set it back to 0
        if(position == emailsAdapter.favoriteEmailIndex)
        {
            emailsAdapter.favoriteEmailIndex = 0
            emailsAdapter.notifyItemChanged(0)
        }

        // update favorite index in case we're removing number before it
        if(position < emailsAdapter.favoriteEmailIndex)
            --emailsAdapter.favoriteEmailIndex
    }

    /**
     * Email favorite button click listener
     *
     * @param view view to favorite
     * @param position position of the view to favorite
     */
    override fun onEmailFavoriteClick(view: View?, position: Int)
    {
        val oldFavorite = emailsAdapter.favoriteEmailIndex
        emailsAdapter.favoriteEmailIndex = position
        emailsAdapter.notifyItemChanged(oldFavorite)
        emailsAdapter.notifyItemChanged(position)
    }

    /**
     * Location delete button click listener
     *
     * @param view view to delete
     * @param position position of the view to delete
     */
    override fun onLocationDeleteClick(view: View?, position: Int)
    {
        locationsAdapter.locationsList.removeAt(position)
        locationsAdapter.notifyItemRemoved(position)

        // check if we're removing the favorite number and set it back to 0
        if(position == locationsAdapter.favoriteLocationIndex)
        {
            locationsAdapter.favoriteLocationIndex = 0
            locationsAdapter.notifyItemChanged(0)
        }

        // update favorite index in case we're removing number before it
        if(position < locationsAdapter.favoriteLocationIndex)
            --locationsAdapter.favoriteLocationIndex
    }

    /**
     * Location favorite button click listener
     *
     * @param view view to favorite
     * @param position position of the view to favorite
     */
    override fun onLocationFavoriteClick(view: View?, position: Int)
    {
        val oldFavorite = locationsAdapter.favoriteLocationIndex
        locationsAdapter.favoriteLocationIndex = position
        locationsAdapter.notifyItemChanged(oldFavorite)
        locationsAdapter.notifyItemChanged(position)
    }
}





















