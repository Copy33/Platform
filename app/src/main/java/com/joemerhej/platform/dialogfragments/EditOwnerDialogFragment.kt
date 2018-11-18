package com.joemerhej.platform.dialogfragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProviders
import com.joemerhej.platform.R
import com.joemerhej.platform.models.Owner
import com.joemerhej.platform.viewmodels.OwnersViewModel
import kotlinx.android.synthetic.main.autosize_dialog_fragment_child_edit_owner.*
import java.lang.Exception


/**
 * Created by Joe Merhej on 10/28/18.
 *
 * EditEventDialogFragment is a child of AutoSizeDialogFragment and will handle the edit event/create new event dialog
 */

// parcelable key for owner in case dialog is opened with one (edit)
private const val OWNER_KEY = "owner"

class EditOwnerDialogFragment : AutoSizeDialogFragment()
{
    interface OnSaveButtonListener
    {
        fun onSaveClick(newOwner: Boolean, owner: Owner, position: Int)
    }

    // view model shared with parent activity
    private lateinit var ownersViewModel: OwnersViewModel

    // mandatory abstract id so the parent can inflate the view
    override val childLayoutResId: Int
        get() = R.layout.autosize_dialog_fragment_child_edit_owner

    // save button listener from parent fragment
    private lateinit var saveButtonListener: OnSaveButtonListener

    // check if editing existing owner or adding a new one
    private var isNewOwner: Boolean = true

    // owner position in case of edit
    private var ownerPosition: Int = -1


    // companion object for static methods
    companion object
    {
        // new instance takes owner and position in case of editing existing owner (it would be easy to pass it back and notify adapter)
        fun newInstance(owner: Owner?, position: Int): EditOwnerDialogFragment
        {
            val dialogFragment = EditOwnerDialogFragment()
            val args = Bundle()

            owner?.let {
                args.putParcelable(OWNER_KEY, it)
                dialogFragment.ownerPosition = position
            }

            dialogFragment.arguments = args
            return dialogFragment
        }

        fun show(targetFragment: Fragment, owner: Owner?, position: Int, fragmentManager: FragmentManager?, tag: String)
        {
            val dialogFragment = newInstance(owner, position)
            dialogFragment.setTargetFragment(targetFragment, 1)
            fragmentManager?.let {
                dialogFragment.show(it, tag)
            } ?: throw Exception("Null Fragment Manager while showing Edit Event Dialog")
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
            ownersViewModel = ViewModelProviders.of(activity!!).get(OwnersViewModel::class.java)
        } ?: throw Exception("Invalid Activity for EditEventDialog")

        // check if owner exists in arguments (in case of edit) and fill in the owner properties in the dialog
        val ownerToEdit: Owner? = arguments?.getParcelable(OWNER_KEY)
        ownerToEdit?.let {
            isNewOwner = false
            owner_dialog_name.setText(ownerToEdit.name)
            owner_dialog_name.setSelection(owner_dialog_name.text.length)
            // TODO: fill in the image here
        }

        // set up cancel button click listener
        owner_dialog_cancel_button.setOnClickListener {
            dismiss()
        }

        // set up save button click listener
        owner_dialog_save_button.setOnClickListener {
            // get the last position in case of saving new owner
            if(isNewOwner)
                ownerPosition = ownersViewModel.getOwnersList().size

            val newOwner = Owner(owner_dialog_name.text.toString(), null) //TODO: provide correct image here
            saveButtonListener.onSaveClick(isNewOwner, newOwner, ownerPosition)
            dismiss()
        }

    }
}





















