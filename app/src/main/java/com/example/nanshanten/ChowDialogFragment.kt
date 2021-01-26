package com.example.nanshanten

import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog

class ChowDialogFragment : ClaimDialogFragment() {
    lateinit var selectedChow: MutableList<Tile>
    var chowLayout = LinearLayout(ContextGetter.applicationContext())
    var chowList: MutableList<MutableList<Tile>> = mutableListOf()

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

    fun addChow(layout: LinearLayout, chowList: MutableList<Tile>){
        layout.setOnClickListener(touchListener)
        chowLayout.addView(layout)
        this.chowList.add(chowList)
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