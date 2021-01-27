package com.example.nanshanten

import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog

class DoraDialogFragment : ClaimDialogFragment() {
    lateinit var selectedDora: Tile
    private val scrollLayout = HorizontalScrollView(ContextGetter.applicationContext())
    var doraLayout = LinearLayout(ContextGetter.applicationContext())
    var doraList: MutableList<Tile> = mutableListOf()
    lateinit var clickView: LinearLayout

    val touchListener = View.OnClickListener { v: View ->
        doraList.forEachIndexed { index, tile ->
            if(tile.toString().equals(((v as LinearLayout).getChildAt(1) as TextView).text)) {
                doraLayout.getChildAt(index).setBackgroundResource(R.drawable.click_border)
                selectedDora = tile
            } else
                doraLayout.getChildAt(index).background = null
        }
    }

    init{
        doraLayout.orientation = LinearLayout.HORIZONTAL
        doraLayout.gravity = Gravity.CENTER
    }

    fun addDora(layout: LinearLayout, dora: Tile){
        layout.setOnClickListener(touchListener)
        doraLayout.addView(layout)
        this.doraList.add(dora)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        selectedDora = doraList.get(0)
        doraLayout.getChildAt(0).setBackgroundResource(R.drawable.click_border)
        scrollLayout.addView(doraLayout)
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle("ドラ")
                .setMessage("ドラ表示牌を選択してください")
                .setView(scrollLayout)
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