package com.example.nanshanten

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.children
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment

class ClaimDialogFragment : DialogFragment() {
    private lateinit var listener: ClaimDialogListener
    private var claimPlayer = -1
    private var chowLayout = LinearLayout(ContextGetter.applicationContext())
    private lateinit var selectedChow: MutableList<Tile>
    private var chowList: MutableList<MutableList<Tile>> = mutableListOf()

    val touchListener = View.OnClickListener { v: View ->
        chowList.forEachIndexed { index, list ->
            if(list.get(0).toString().equals((((v as LinearLayout).getChildAt(0) as LinearLayout).getChildAt(1) as TextView).text)) {
                chowLayout.getChildAt(index).setBackgroundResource(R.drawable.click_border)
                selectedChow = list
            } else
                chowLayout.getChildAt(index).background = null
        }
    }

    init{
        chowLayout.orientation = LinearLayout.HORIZONTAL
        chowLayout.gravity = Gravity.CENTER
    }

    interface ClaimDialogListener {
        fun onDialogPositiveClick(dialog: ClaimDialogFragment, claimPlayer: Int)
        fun onDialogNegativeClick(dialog: ClaimDialogFragment, claimPlayer: Int)
    }

    fun setFragment(fragment: Fragment){
        this.setTargetFragment(fragment, 0)
    }

    fun setClaimPlayer(claimPlayer: Int){
        this.claimPlayer = claimPlayer
    }

    fun addChow(layout: LinearLayout, chowList: MutableList<Tile>){
        layout.setOnClickListener(touchListener)
        chowLayout.addView(layout)
        this.chowList.add(chowList)
    }

    fun getSelecteChow(): MutableList<Tile>{ return selectedChow }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val target = targetFragment
        try {
            // 呼び出し元のActivityを変数listenerで保持する
            listener = if(target != null) target as ClaimDialogListener else context as ClaimDialogListener
        } catch (e: ClassCastException) {
            // 呼び出し元のActivityでコールバックインタフェースを実装していない場合
            throw ClassCastException((context.toString() + " must implement ClaimDialogListener"))
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        selectedChow = chowList.get(0)
        chowLayout.getChildAt(0).setBackgroundResource(R.drawable.click_border)
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle("チー")
                .setMessage("鳴く牌を選択してください")
                .setView(chowLayout)
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

