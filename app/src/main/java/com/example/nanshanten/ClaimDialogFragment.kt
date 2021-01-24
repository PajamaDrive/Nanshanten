package com.example.nanshanten

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment

class ClaimDialogFragment : DialogFragment() {
    private lateinit var listener: ClaimDialogListener

    interface ClaimDialogListener {
        fun onDialogPositiveClick(dialog: DialogFragment)
        fun onDialogNegativeClick(dialog: DialogFragment)
    }

    fun setFragment(fragment: Fragment){
        this.setTargetFragment(fragment, 0)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val target = targetFragment
        try {
            // 呼び出し元のActivityを変数listenerで保持する
            listener = if(target != null) target as ClaimDialogListener else context as ClaimDialogListener
        } catch (e: ClassCastException) {
            // 呼び出し元のActivityでコールバックインタフェースを実装していない場合
            throw ClassCastException((context.toString() +
                    " must implement ClaimDialogListener"))
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setMessage("鳴く牌を選択してください")
                .setPositiveButton(R.string.ok){ dialog, id ->
                    listener.onDialogPositiveClick(this)
                }
                .setNegativeButton(R.string.cancel) { dialog, id ->
                    listener.onDialogNegativeClick(this)
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}

