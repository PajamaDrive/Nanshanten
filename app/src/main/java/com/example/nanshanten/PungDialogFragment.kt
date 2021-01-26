package com.example.nanshanten

import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog

class PungDialogFragment : ClaimDialogFragment() {
    lateinit var selectedPung: MutableList<Tile>
    var pungLayout = LinearLayout(ContextGetter.applicationContext())
    var pungList: MutableList<MutableList<Tile>> = mutableListOf()

    val touchListener = View.OnClickListener { v: View ->
        pungList.forEachIndexed { index, list ->
            if(list.get(0).toString().equals((((v as LinearLayout).getChildAt(0) as LinearLayout).getChildAt(1) as TextView).text)) {
                pungLayout.getChildAt(index).setBackgroundResource(R.drawable.click_border)
                selectedPung = list
            } else
                pungLayout.getChildAt(index).background = null
        }
    }

    init{
        pungLayout.orientation = LinearLayout.HORIZONTAL
        pungLayout.gravity = Gravity.CENTER
    }

    fun addPung(layout: LinearLayout, chowList: MutableList<Tile>){
        layout.setOnClickListener(touchListener)
        pungLayout.addView(layout)
        this.pungList.add(chowList)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        selectedPung = pungList.get(0)
        pungLayout.getChildAt(0).setBackgroundResource(R.drawable.click_border)
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle("ポン")
                .setMessage("鳴く牌を選択してください")
                .setView(pungLayout)
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