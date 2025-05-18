package org.dallas.smartshelf.view.custom

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.junevrtech.smartshelf.R

class FullScreenDialog {
    var dialog: Dialog? = null
        private set

    fun show(context: Context): Dialog {
        return show(context, null)
    }

    fun show(context: Context, title: CharSequence?): Dialog {
        return show(context, title, false)
    }

    fun show(context: Context, title: CharSequence?, cancelable: Boolean): Dialog {
        return show(context, title, cancelable, null)
    }

    fun show(
        context: Context, title: CharSequence?, cancelable: Boolean,
        cancelListener: android. content. DialogInterface. OnCancelListener?
    ): Dialog {
        val inflator = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = inflator.inflate(R.layout.progress_bar, null)
        if (title != null) {
            val tv: TextView = view.findViewById(R.id.id_title)
            tv.text = title
        }

        dialog = Dialog(context, R.style.NewDialog)
        dialog?.setContentView(view)
        dialog?.setCancelable(cancelable)
        dialog?.setOnCancelListener(cancelListener)
        dialog?.show()

        return dialog!!
    }

    fun dismiss() {
        dialog?.dismiss()
    }
}