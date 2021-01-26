package com.example.nanshanten

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment

abstract class ClaimDialogFragment : DialogFragment() {
    var claimPlayer = -1
    protected lateinit var listener: DialogListener

    interface DialogListener {
        fun onDialogPositiveClick(dialog: ClaimDialogFragment, claimPlayer: Int)
        fun onDialogNegativeClick(dialog: ClaimDialogFragment, claimPlayer: Int)
    }

    fun setFragment(fragment: Fragment){
        this.setTargetFragment(fragment, 0)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val target = targetFragment
        try {
            // 呼び出し元のActivityを変数listenerで保持する
            listener = if(target != null) target as DialogListener else context as DialogListener
        } catch (e: ClassCastException) {
            // 呼び出し元のActivityでコールバックインタフェースを実装していない場合
            throw ClassCastException((context.toString() + " must implement ClaimDialogListener"))
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle("Null")
                .setPositiveButton(R.string.ok){ dialog, id ->
                    listener.onDialogPositiveClick(this, claimPlayer)
                }
                .setNegativeButton(R.string.cancel) { dialog, id ->
                    listener.onDialogNegativeClick(this, claimPlayer)
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}

