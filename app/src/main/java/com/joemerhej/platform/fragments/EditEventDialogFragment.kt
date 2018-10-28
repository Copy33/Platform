package com.joemerhej.platform.fragments

import android.os.Bundle
import androidx.fragment.app.FragmentManager
import com.joemerhej.platform.R

/**
 * Created by Joe Merhej on 10/28/18.
 */
class EditEventDialogFragment : AutoSizeDialogFragment()
{
    interface EventListener
    {
        fun onCertainEvent()
    }

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
//            dialogFragment.setTargetFragment(fragment, 0)
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
}