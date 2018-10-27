package com.alphae.rishi.towatch.Dialogs

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import com.alphae.rishi.towatch.R

/**
 * Created by rishi on 27/10/18.
 */
class LoadingAdDialog : DialogFragment() {

    companion object {
        fun newInstance() = LoadingAdDialog()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(android.support.v4.app.DialogFragment.STYLE_NO_FRAME, R.style.MedalTallyDialogStyle)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val inflater = LayoutInflater.from(activity)
        val view = inflater.inflate(R.layout.dialog_showing_ad, null)

        return AlertDialog.Builder(this.activity!!)
                .setView(view)
                .setCancelable(false)
                .create()

    }

}