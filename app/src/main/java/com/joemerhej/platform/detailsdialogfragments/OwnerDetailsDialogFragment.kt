package com.joemerhej.platform.detailsdialogfragments

import android.animation.ObjectAnimator
import android.animation.StateListAnimator
import android.os.Bundle
import android.view.View
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProviders
import com.joemerhej.platform.R
import com.joemerhej.platform.models.Owner
import com.joemerhej.platform.viewmodels.OwnersViewModel
import kotlinx.android.synthetic.main.autosize_dialog_fragment_child_edit_client.*
import kotlinx.android.synthetic.main.autosize_dialog_fragment_child_edit_owner.*
import java.lang.Exception


/**
 * Created by Joe Merhej on 10/28/18.
 *
 * EventDetailsDialogFragment is a child of AutoSizeDialogFragment and will handle the edit event/create new event dialog
 */
class OwnerDetailsDialogFragment : AutoSizeDialogFragment()
{
    interface OnSaveButtonListener
    {
        fun onSaveClick(newOwner: Boolean, owner: Owner, position: Int)
    }

    override val childLayoutResId: Int                                      // mandatory abstract id so the parent can inflate the view
        get() = R.layout.autosize_dialog_fragment_child_edit_owner
    private lateinit var ownersViewModel: OwnersViewModel                   // view model shared with parent activity
    private lateinit var owner: Owner                                       // owner shown, shallow copy of owner in viewmodel (empty if new owner)
    private lateinit var ownerBeforeEdit: Owner                             // copy of owner before edit used to undo changes
    private lateinit var saveButtonListener: OnSaveButtonListener           // save button listener from parent fragment
    private var isNewOwner: Boolean = true                                  // check if editing existing owner or adding a new one
    private var ownerPosition: Int = -1                                     // owner position in case of edit


    // companion object for static methods
    companion object
    {
        // new instance takes owner position in case of editing existing owner (it would be easy to pass it back and notify adapter)
        private fun newInstance(ownerPosition: Int): OwnerDetailsDialogFragment
        {
            val dialogFragment = OwnerDetailsDialogFragment()

            dialogFragment.ownerPosition = ownerPosition

            // empty bundle, not needed for now but this is how you'd pass arguments
            val args = Bundle()
            dialogFragment.arguments = args
            return dialogFragment
        }

        // main function to show the dialog
        fun show(targetFragment: Fragment, ownerPosition: Int, fragmentManager: FragmentManager?, tag: String)
        {
            val dialogFragment = newInstance(ownerPosition)

            // we need to set target fragment to check later if this fragment implemented the needed listeners
            dialogFragment.setTargetFragment(targetFragment, 1)
            fragmentManager?.let {
                dialogFragment.show(it, tag)
            } ?: throw Exception("Null Fragment Manager while showing Edit Owner Dialog")
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
        } ?: throw Exception("Invalid Activity for EditOwnerDialog")

        // check if owner exists in bundle (in case of edit) and fill in the owner properties in the dialog views
        val ownerFromViewModel: Owner? = ownersViewModel.getOwner(ownerPosition)
        ownerFromViewModel?.let {
            isNewOwner = false
            owner = it
        } ?: kotlin.run { owner = Owner() }

        // create backup copy of owner in case experience is cancelled and need to reset view
        ownerBeforeEdit = owner.clone()

        // fill in the dialog views with our owner
        fillDialogViewsFromOwner(owner)

        // set up cancel button click listener
        owner_dialog_cancel_button.setOnClickListener {
            dismiss()
        }

        // set up save button click listener
        owner_dialog_save_button.setOnClickListener {
            // get the last position in case of saving new owner
            if(isNewOwner)
                ownerPosition = ownersViewModel.getOwnersList().size

            // create owner based on dialog and pass it to the listener then dismiss the dialog
            val newOwner = createOwnerFromDialogViews()
            saveButtonListener.onSaveClick(isNewOwner, newOwner, ownerPosition)
            dismiss()
        }

    }

    /**
     * Function that will fill the dialog views from a given owner
     *
     * @param owner owner to use
     */
    private fun fillDialogViewsFromOwner(owner: Owner)
    {
        owner_dialog_name.setText(owner.name)
        owner_dialog_name.setSelection(owner.name.length)
        //TODO: fill in owner image here
    }

    /**
     * Function that will create an owner from the dialog views info
     *
     * @return owner created
     */
    private fun createOwnerFromDialogViews() : Owner
    {
        val owner = Owner()

        // owner name
        owner.name = owner_dialog_name.text.toString()

        // owner image TODO: provide correct image here
        //owner.imageUri =

        return owner
    }
}





















