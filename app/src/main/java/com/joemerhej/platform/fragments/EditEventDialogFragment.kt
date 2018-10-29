package com.joemerhej.platform.fragments

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProviders
import com.joemerhej.androidweekview.WeekViewUtil
import com.joemerhej.platform.Client
import com.joemerhej.platform.Event
import com.joemerhej.platform.R
import com.joemerhej.platform.viewmodels.EditEventViewModel
import kotlinx.android.synthetic.main.autosize_dialog_fragment_child_edit_event.*
import java.util.*

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
        // TODO: check if an event already exists (ie not adding a new event) then populate the event views to edit
        val today = WeekViewUtil.today()

        val startTime = today.clone() as Calendar
        startTime.set(Calendar.HOUR_OF_DAY, 11)
        startTime.set(Calendar.MINUTE, 0)

        val endTime = startTime.clone() as Calendar
        endTime.add(Calendar.HOUR, 1)

        editEventViewModel.event.value = Event("1", "title", "subtitle", startTime, endTime, 0,
                false, "owner", "location", null, Client("clientFirst", "clientLast"), Event.EventStatus.PAID,
                100.0, false, "notes bla bla bla")

        title_edit_text.setText(editEventViewModel.event.value?.title)
        amount_textview.text = editEventViewModel.event.value?.amountPaid.toString()
        owner_textview.text = editEventViewModel.event.value?.owner
        note_edittext.setText(editEventViewModel.event.value?.notes)

        save_button.setOnClickListener {
//            val today = WeekViewUtil.today()
//
//            val startTime = today.clone() as Calendar
//            startTime.set(Calendar.HOUR_OF_DAY, 11)
//            startTime.set(Calendar.MINUTE, 0)
//
//            val endTime = startTime.clone() as Calendar
//            endTime.add(Calendar.HOUR, 1)
//
//            editEventViewModel.event.value = Event("1", "12121212", "subtitle", startTime, endTime, 0,
//                    false, "owner", "location", null, Client("clientFirst", "clientLast"), Event.EventStatus.PAID,
//                    100.0, false, "notes bla bla bla")
            editEventViewModel.event.value?.title = "11111111"
            dismiss()
        }
    }
}