package com.joemerhej.platform.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.joemerhej.platform.R
import kotlinx.android.synthetic.main.autosize_dialog_fragment_parent.view.*

/**
 * Created by Joe Merhej on 10/28/18.
 */
abstract class AutoSizeDialogFragment : DialogFragment()
{

    protected abstract val childLayoutResId: Int

    private val isLargeScreen
        get() = resources.getBoolean(R.bool.device_wide)

    init
    {
        this.setStyle(DialogFragment.STYLE_NO_TITLE, 0)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        // Set to adjust screen height automatically, when soft keyboard appears on screen.
        dialog?.let {
            it.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        }

        // inflate the parent view and its child view in the content_view_group placeholder
        val view = inflater.inflate(R.layout.autosize_dialog_fragment_parent, container, false) ?: return null
        inflater.inflate(childLayoutResId, view.child_layout_container, true)

        return view
    }

    /**
     * returns the dialog theme based on screen size.
     *
     * @return returns dialog theme for tablets, and regular theme for phones.
     */
    override fun getTheme() = if(isLargeScreen) R.style.ThemeOverlay_AppCompat_Dialog else R.style.Theme_AppCompat_Light_NoActionBar
}