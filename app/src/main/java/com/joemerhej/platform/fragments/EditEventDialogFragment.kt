package com.joemerhej.platform.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProviders
import com.joemerhej.platform.Event
import com.joemerhej.platform.R
import com.joemerhej.platform.viewmodels.EditEventViewModel
import kotlinx.android.synthetic.main.autosize_dialog_fragment_child_edit_event.*

/**
 * Created by Joe Merhej on 10/28/18.
 */
class EditEventDialogFragment : AutoSizeDialogFragment()
{
    interface EventListener
    {
        fun onCertainEvent()
    }

    private lateinit var editEventViewModel: EditEventViewModel

    override val childLayoutResId: Int
        get() = R.layout.autosize_dialog_fragment_child_edit_event

    private lateinit var listener: EventListener

    companion object
    {
        fun newInstance(): EditEventDialogFragment
        {
            val dialogFragment = EditEventDialogFragment()
            val args = Bundle()
            dialogFragment.arguments = args
            return dialogFragment
        }

        fun show(fragmentManager: FragmentManager, tag: String)
        {
            val dialogFragment = newInstance()
            dialogFragment.show(fragmentManager, tag)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        val targetFragment = this.targetFragment
        val activity = this.activity
        listener = when
        {
            targetFragment is EventListener -> targetFragment
            activity is EventListener -> activity
            else -> throw ClassCastException("Activity: $activity, or target fragment: $targetFragment must implement ${EventListener::class.java.name}")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        // initialize the view model in the activity scope, now everyone in activity can share it
        activity?.let {
            editEventViewModel = ViewModelProviders.of(it).get(EditEventViewModel::class.java)
        }

        // use the view model here
        amount_textview.text = editEventViewModel.event.amountPaid.toString()
        owner_textview.text = editEventViewModel.event.owner
        note_edittext.setText(editEventViewModel.event.notes)
    }
}