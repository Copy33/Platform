package com.joemerhej.platform.detailsdialogfragments

import android.animation.ObjectAnimator
import android.animation.StateListAnimator
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.joemerhej.platform.R
import com.joemerhej.platform.detailsdialogfragments.detailsadapters.ClientDetailsEmailsAdapter
import com.joemerhej.platform.detailsdialogfragments.detailsadapters.ClientDetailsLocationsAdapter
import com.joemerhej.platform.detailsdialogfragments.detailsadapters.ClientDetailsPhoneNumbersAdapter
import com.joemerhej.platform.models.Client
import com.joemerhej.platform.utils.DebugUtils
import com.joemerhej.platform.viewmodels.ClientsViewModel
import kotlinx.android.synthetic.main.autosize_dialog_fragment_child_edit_client.*


/**
 * Created by Joe Merhej on 10/28/18.
 *
 * EventDetailsDialogFragment is a child of AutoSizeDialogFragment and will handle the edit event/create new event dialog
 */
class ClientDetailsDialogFragment : AutoSizeDialogFragment(),
        ClientDetailsPhoneNumbersAdapter.OnPhoneNumberClickListener, ClientDetailsEmailsAdapter.OnEmailClickListener, ClientDetailsLocationsAdapter.OnLocationClickListener,
        ClientDetailsPhoneNumbersAdapter.OnPhoneNumberLastViewCreatedListener, ClientDetailsEmailsAdapter.OnEmailLastViewCreatedListener, ClientDetailsLocationsAdapter.OnLocationLastViewCreatedListener
{
    interface OnSaveButtonListener
    {
        //TODO [improvement]: we can avoid passing in the client here and putting it/modifying it in the viewmodel
        fun onSaveClick(newClient: Boolean, client: Client, position: Int)
    }

    override val childLayoutResId: Int                                              // mandatory abstract id so the parent can inflate the view
        get() = R.layout.autosize_dialog_fragment_child_edit_client
    private lateinit var clientsViewModel: ClientsViewModel                         // viewmodel shared with parent activity
    private lateinit var client: Client                                             // client shown, shallow copy of client in viewmodel (empty if new client)
    private lateinit var clientBeforeEdit: Client                                   // copy of client before edit used to undo changes
    private lateinit var saveButtonListener: OnSaveButtonListener                   // save button listener from parent fragment
    private var isNewClient: Boolean = true                                         // check if editing existing client or adding a new one
    private var clientPosition: Int = -1                                            // client position in case of edit
    private lateinit var phoneNumbersAdapter: ClientDetailsPhoneNumbersAdapter      // adapter for the phone numbers list
    private lateinit var emailsAdapter: ClientDetailsEmailsAdapter                  // adapter for the emails list
    private lateinit var locationsAdapter: ClientDetailsLocationsAdapter            // adapter for the locations list
    var inEditMode = false                                                          // boolean to check if dialog in edit or view mode
        private set


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

        // hide the keyboard
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)

        // set up scroll listener to change the toolbar elevation while scrolling
        client_details_scrollview.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { _, _, scrollY, _, _ ->
            val stateListAnimator = StateListAnimator()
            if(scrollY > 0)
                stateListAnimator.addState(IntArray(0), ObjectAnimator.ofFloat(client_details_appbarlayout, "elevation", 8f).also { it.duration = 0 })
            else
                stateListAnimator.addState(IntArray(0), ObjectAnimator.ofFloat(client_details_appbarlayout, "elevation", 0f).also { it.duration = 0 })
            client_details_appbarlayout.stateListAnimator = stateListAnimator
        })

        // initialize the view model in the activity scope to make sure it's same model shared with activity
        activity?.run {
            clientsViewModel = ViewModelProviders.of(this).get(ClientsViewModel::class.java)
        } ?: throw Exception("Invalid Activity for EditClientDialog")

        // check if editing existing client, else create an empty client
        val clientFromViewModel: Client? = clientsViewModel.getClient(clientPosition)
        if(clientFromViewModel != null)
        {
            isNewClient = false
            client = clientFromViewModel
        }
        else
            client = Client()

        // create copy of client in case edit experience is cancelled
        clientBeforeEdit = client.clone()

        // fill in the dialog views with our client
        fillDialogViewsFromClient()

        // dialog launches in view mode if client exists, else in edit mode
        if(isNewClient)
            engageDialogEditMode()
        else
            engageDialogViewMode()

        // listeners: set up phone number add button listener - clicking plus sign will simply create a new empty phone number and add it to the client
        client_details_add_phone_number_button.setOnClickListener {
            client.phoneNumbers.add("")
            phoneNumbersAdapter.notifyItemInserted(client.phoneNumbers.size-1)
            phoneNumbersAdapter.manuallyAddingNewViewFromDialog = true
        }

        // listeners: set up email add button listener - clicking plus sign will simply create a new empty email and add it to the client
        client_details_add_email_button.setOnClickListener {
            client.emails.add("")
            emailsAdapter.notifyItemInserted(client.emails.size)
            emailsAdapter.manuallyAddingNewViewFromDialog = true
        }

        // listeners: set up location add button listener - clicking plus sign will simply create a new empty location and add it to the client
        client_details_add_location_button.setOnClickListener {
            client.locations.add("")
            locationsAdapter.notifyItemInserted(client.locations.size)
            locationsAdapter.manuallyAddingNewViewFromDialog = true
        }

        // listeners: set up cancel button click listener
        client_details_cancel_imageview.setOnClickListener {
            // if in edit mode then reset client to clientBeforeEdit and engage view mode
            if(inEditMode)
            {
                // if new client, just dismiss (will discard changes)
                if(isNewClient)
                    dismiss()

                // else reset the client details to clientBeforeEdit, refill the views, and put dialog in view mode
                resetClientToClientBeforeEdit()
                fillDialogViewsFromClient()
                engageDialogViewMode()
            }
            // if not in edit mode (meaning user saved a client), return to main fragment with updated/new client
            else
            {
                saveButtonListener.onSaveClick(isNewClient, client, clientPosition)
                dismiss()
            }
        }

        // listeners: set up save/edit button click listener - switch modes on click (save takes it to view mode, edit takes it to edit mode)
        client_details_edit_save_button.setOnClickListener {
            if(inEditMode)
            {
                // apply dialog changes to client
                updateClientFromDialogViews()

                // update client clone
                clientBeforeEdit = client.clone()

                // get the last client list position in case of saving a new client (clientsFragment will pass in -1)
                if(isNewClient)
                    clientPosition = clientsViewModel.getClientsList().size

                engageDialogViewMode()
            }
            else
            {
                engageDialogEditMode()
            }
        }
    }

    /**
     * Function that will reset client properties to clientBeforeEdit
     */
    private fun resetClientToClientBeforeEdit()
    {
        // client name
        client.name = clientBeforeEdit.name

        // phone numbers
        // reset both client and adapter favorite index since integers are copied and not referenced
        client.phoneNumbers.clear()
        client.phoneNumbers.addAll(clientBeforeEdit.phoneNumbers)
        client.favoritePhoneNumberIndex = clientBeforeEdit.favoritePhoneNumberIndex

        // emails
        client.emails.clear()
        client.emails.addAll(clientBeforeEdit.emails)
        client.favoriteEmailIndex = clientBeforeEdit.favoriteEmailIndex

        // locations
        client.locations.clear()
        client.locations.addAll(clientBeforeEdit.locations)
        client.favoriteLocationIndex = clientBeforeEdit.favoriteLocationIndex

        // balance
        client.balance = clientBeforeEdit.balance

        // notes
        client.notes = clientBeforeEdit.notes
    }

    /**
     * Function that will fill the dialog views from a given client
     */
    private fun fillDialogViewsFromClient()
    {
        // client name
        client_details_name_edittext.setText(client.name)
        client_details_name_edittext.setSelection(client.name.length)

        // phone numbers
        client_details_phone_number_recyclerview.layoutManager = LinearLayoutManager(context)
        client_details_phone_number_recyclerview.itemAnimator = null
        phoneNumbersAdapter = ClientDetailsPhoneNumbersAdapter(this, client.phoneNumbers, client.favoritePhoneNumberIndex)
        client_details_phone_number_recyclerview.adapter = phoneNumbersAdapter
        phoneNumbersAdapter.onPhoneNumberClickListener = this
        phoneNumbersAdapter.onLastAddedViewCreatedListener = this

        // emails
        client_details_email_recyclerview.layoutManager = LinearLayoutManager(context)
        client_details_email_recyclerview.itemAnimator = null
        emailsAdapter = ClientDetailsEmailsAdapter(this, client.emails, client.favoriteEmailIndex)
        client_details_email_recyclerview.adapter = emailsAdapter
        emailsAdapter.onEmailClickListener = this
        emailsAdapter.onLastAddedViewCreatedListener = this

        // locations
        client_details_location_recyclerview.layoutManager = LinearLayoutManager(context)
        client_details_location_recyclerview.itemAnimator = null
        locationsAdapter = ClientDetailsLocationsAdapter(this, client.locations, client.favoriteLocationIndex)
        client_details_location_recyclerview.adapter = locationsAdapter
        locationsAdapter.onLocationClickListener = this
        locationsAdapter.onLastAddedViewCreatedListener = this

        // balance
        val clientBalance = client.balance.toString()
        client_details_balance_edittext.setText(clientBalance)
        client_details_balance_edittext.setSelection(clientBalance.length)

        // notes
        client_details_note_edittext.setText(client.notes)
        client_details_note_edittext.setSelection(client.notes.length)
    }

    /**
     * Function that will update the client in question from the dialog views info
     *
     * @return client created
     */
    private fun updateClientFromDialogViews()
    {
        // client name
        client.name = client_details_name_edittext.text.toString()

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
        if(!client_details_balance_edittext.text.toString().isEmpty())
        {
            try
            {
                client.balance = client_details_balance_edittext.text.toString().toDouble()
            }
            catch(e: NumberFormatException)
            {
                Log.d(DebugUtils.TAG, "Can't parse number")
            }
        }
        else
            client.balance = 0.0

        // notes
        client.notes = client_details_note_edittext.text.toString()
    }

    /**
     * Function that will put the dialog views in view mode
     */
    private fun engageDialogViewMode()
    {
        inEditMode = false

        // edit/save button
        client_details_edit_save_button.text = resources.getText(R.string.button_edit)

        // client name
        client_details_name_edittext.isEnabled = false

        // phone numbers
        phoneNumbersAdapter.notifyItemRangeChanged(0, phoneNumbersAdapter.itemCount)
        client_details_add_phone_number_button.visibility = View.GONE

        // emails
        emailsAdapter.notifyItemRangeChanged(0, emailsAdapter.itemCount)
        client_details_add_email_button.visibility = View.GONE

        // locations
        locationsAdapter.notifyItemRangeChanged(0, locationsAdapter.itemCount)
        client_details_add_location_button.visibility = View.GONE

        // balance
        client_details_balance_edittext.isEnabled = false

        // notes
        client_details_note_edittext.isEnabled = false

        // delete button
        client_details_delete_button.visibility = View.GONE
    }

    /**
     * Function that will put the dialog views in edit mode
     */
    private fun engageDialogEditMode()
    {
        inEditMode = true

        // edit/save button
        client_details_edit_save_button.text = resources.getText(R.string.button_save)

        // client name
        client_details_name_edittext.isEnabled = true

        // phone numbers
        phoneNumbersAdapter.notifyItemRangeChanged(0, phoneNumbersAdapter.itemCount)
        client_details_add_phone_number_button.visibility = View.VISIBLE

        // emails
        emailsAdapter.notifyItemRangeChanged(0, emailsAdapter.itemCount)
        client_details_add_email_button.visibility = View.VISIBLE

        // locations
        locationsAdapter.notifyItemRangeChanged(0, locationsAdapter.itemCount)
        client_details_add_location_button.visibility = View.VISIBLE

        // balance
        client_details_balance_edittext.isEnabled = true

        // notes
        client_details_note_edittext.isEnabled = true

        // delete button
        client_details_delete_button.visibility = View.VISIBLE
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
            client.favoritePhoneNumberIndex = 0
            phoneNumbersAdapter.notifyItemChanged(0)
        }

        // update favorite index in case we're removing number before it
        if(position < phoneNumbersAdapter.favoritePhoneNumberIndex)
        {
            --phoneNumbersAdapter.favoritePhoneNumberIndex
            --client.favoritePhoneNumberIndex
        }
    }

    /**
     * Phone Number favorite button click listener
     *
     * @param view view to favorite
     * @param position position of the view to favorite
     */
    override fun onPhoneNumberFavoriteClick(view: View?, position: Int)
    {
        if(inEditMode)
        {
            val oldFavorite = phoneNumbersAdapter.favoritePhoneNumberIndex
            phoneNumbersAdapter.favoritePhoneNumberIndex = position
            client.favoritePhoneNumberIndex = position
            phoneNumbersAdapter.notifyItemChanged(oldFavorite)
            phoneNumbersAdapter.notifyItemChanged(position)
        }
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
            client.favoriteEmailIndex = 0
            emailsAdapter.notifyItemChanged(0)
        }

        // update favorite index in case we're removing number before it
        if(position < emailsAdapter.favoriteEmailIndex)
        {
            --emailsAdapter.favoriteEmailIndex
            --client.favoriteEmailIndex
        }
    }

    /**
     * Email favorite button click listener
     *
     * @param view view to favorite
     * @param position position of the view to favorite
     */
    override fun onEmailFavoriteClick(view: View?, position: Int)
    {
        if(inEditMode)
        {
            val oldFavorite = emailsAdapter.favoriteEmailIndex
            emailsAdapter.favoriteEmailIndex = position
            emailsAdapter.notifyItemChanged(oldFavorite)
            emailsAdapter.notifyItemChanged(position)
        }
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
            client.favoriteLocationIndex = 0
            locationsAdapter.notifyItemChanged(0)
        }

        // update favorite index in case we're removing number before it
        if(position < locationsAdapter.favoriteLocationIndex)
        {
            --locationsAdapter.favoriteLocationIndex
            --client.favoriteLocationIndex
        }
    }

    /**
     * Location favorite button click listener
     *
     * @param view view to favorite
     * @param position position of the view to favorite
     */
    override fun onLocationFavoriteClick(view: View?, position: Int)
    {
        if(inEditMode)
        {
            val oldFavorite = locationsAdapter.favoriteLocationIndex
            locationsAdapter.favoriteLocationIndex = position
            locationsAdapter.notifyItemChanged(oldFavorite)
            locationsAdapter.notifyItemChanged(position)
        }
    }

    /**
     * listener to when manually adding a phone number and the last one was created
     *
     * @param view last view created
     * @param position position of the view
     */
    override fun onPhoneNumberLastViewCreated(view: View?, position: Int)
    {
        // request focus when adding manual adding view
        view?.requestFocus()
    }

    /**
     * listener to when manually adding an email and the last one was created
     *
     * @param view last view created
     * @param position position of the view
     */
    override fun onEmailLastViewCreated(view: View?, position: Int)
    {
        // request focus when adding manual adding view
        view?.requestFocus()
    }

    /**
     * listener to when manually adding a location and the last one was created
     *
     * @param view last view created
     * @param position position of the view
     */
    override fun onLocationLastViewCreated(view: View?, position: Int)
    {
        // request focus when adding manual adding view
        view?.requestFocus()
    }
}





















