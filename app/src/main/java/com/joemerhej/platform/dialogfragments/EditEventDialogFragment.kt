package com.joemerhej.platform.dialogfragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProviders
import com.joemerhej.androidweekview.WeekViewUtil
import com.joemerhej.platform.models.Event
import com.joemerhej.platform.R
import com.joemerhej.platform.viewmodels.EventsViewModel
import kotlinx.android.synthetic.main.autosize_dialog_fragment_child_edit_event.*
import java.util.*
import android.animation.ObjectAnimator
import android.animation.StateListAnimator
import androidx.core.widget.NestedScrollView
import com.joemerhej.platform.utils.DebugUtils
import java.lang.Exception


/**
 * Created by Joe Merhej on 10/28/18.
 *
 * EditEventDialogFragment is a child of AutoSizeDialogFragment and will handle the edit event/create new event dialog
 */
class EditEventDialogFragment : AutoSizeDialogFragment()
{
    // TODO: This is not needed for now
    interface EventListener
    {
        fun onCertainEvent()
    }

    // view model shared with parent activity
    private lateinit var eventsViewModel: EventsViewModel

    // mandatory abstract id so the parent can inflate the view
    override val childLayoutResId: Int
        get() = R.layout.autosize_dialog_fragment_child_edit_event

    // listener
    private lateinit var listener: EventListener

    // companion object for static methods
    companion object
    {
        fun newInstance(): EditEventDialogFragment
        {
            val dialogFragment = EditEventDialogFragment()
            val args = Bundle()
            dialogFragment.arguments = args
            return dialogFragment
        }

        fun show(fragmentManager: FragmentManager?, tag: String)
        {
            val dialogFragment = newInstance()
            fragmentManager?.let {
                dialogFragment.show(it, tag)
            } ?: throw Exception("Null Fragment Manager while showing Edit Event Dialog")
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

        // initialize the view model in the activity scope to make sure it's same model shared with activity
        activity?.run {
            eventsViewModel = ViewModelProviders.of(this).get(EventsViewModel::class.java)
        } ?: throw Exception("Invalid Activity for EditEventDialog")

        // initialize the listeners
        edit_event_scrollview.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener{
            _, scrollX, scrollY, oldScrollX, oldScrollY ->
            Log.d(DebugUtils.TAG, "$scrollX, $scrollY, $oldScrollX, $oldScrollY")
            val stateListAnimator = StateListAnimator()
            if(scrollY > 0)
                stateListAnimator.addState(IntArray(0), ObjectAnimator.ofFloat(edit_event_appbarlayout, "elevation", 8f).also { it.duration = 0 })
            else
                stateListAnimator.addState(IntArray(0), ObjectAnimator.ofFloat(edit_event_appbarlayout, "elevation", 0f).also { it.duration = 0 })
            edit_event_appbarlayout.stateListAnimator = stateListAnimator
        })


        // use the view model here
        // TODO: check if an event already exists (ie not adding a new event) then populate the event views to edit
        val today = WeekViewUtil.today()

        val startTime = today.clone() as Calendar
        startTime.set(Calendar.HOUR_OF_DAY, 11)
        startTime.set(Calendar.MINUTE, 0)

        val endTime = startTime.clone() as Calendar
        endTime.add(Calendar.HOUR, 1)

//        eventsViewModel.events.value?.add(Event("1", "title", "subtitle", startTime, endTime, 0,
//                false, "owner", "location", null, Client("clientFirst", "clientLast"), Event.EventStatus.PAID,
//                100.0, false, "notes bla bla bla"))

//        title_edit_text.setText(eventsViewModel.event.value?.title)
//        amount_textview.text = eventsViewModel.event.value?.amountPaid.toString()
//        owner_textview.text = eventsViewModel.event.value?.owner
//        note_edittext.setText(eventsViewModel.event.value?.notes)

        edit_event_save_button.setOnClickListener {
            //            val today = WeekViewUtil.today()
//
//            val startTime = today.clone() as Calendar
//            startTime.set(Calendar.HOUR_OF_DAY, 11)
//            startTime.set(Calendar.MINUTE, 0)
//
//            val endTime = startTime.clone() as Calendar
//            endTime.add(Calendar.HOUR, 1)
//
//            eventsViewModel.event.value = Event("1", "12121212", "subtitle", startTime, endTime, 0,
//                    false, "owner", "location", null, Client("clientFirst", "clientLast"), Event.EventStatus.PAID,
//                    100.0, false, "notes bla bla bla")

            val newEvent = Event()
            newEvent.title = edit_event_title_edit_text.text.toString()
            newEvent.owner = edit_event_owner_textview.text.toString()
            newEvent.notes = edit_event_note_edittext.text.toString()

            eventsViewModel.addEvent(newEvent)
            dismiss()
        }
    }
}





















