package com.example.nanshanten

import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog

class KongDialogFragment : ClaimDialogFragment() {
    lateinit var selectedKong: MutableList<Tile>
    private val scrollLayout = HorizontalScrollView(ContextGetter.applicationContext())
    var kongLayout = LinearLayout(ContextGetter.applicationContext())
    var kongList: MutableList<MutableList<Tile>> = mutableListOf()
    private var enable = false

    val touchListener = View.OnClickListener { v: View ->
        kongList.forEachIndexed { index, list ->
            if(list.get(0).toString().equals((((v as LinearLayout).getChildAt(0) as LinearLayout).getChildAt(1) as TextView).text)) {
                kongLayout.getChildAt(index).setBackgroundResource(R.drawable.click_border)
                selectedKong = list
            } else
                kongLayout.getChildAt(index).background = null
        }
    }

    init{
        kongLayout.orientation = LinearLayout.HORIZONTAL
        kongLayout.gravity = Gravity.CENTER
    }

    fun addKong(layout: LinearLayout, kongList: MutableList<Tile>){
        layout.setOnClickListener(touchListener)
        kongLayout.addView(layout)
        this.kongList.add(kongList)
    }

    fun enableScroll(enable: Boolean){
        if(enable){
            scrollLayout.addView(kongLayout)
        }
        this.enable = enable
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        selectedKong = kongList.get(0)
        kongLayout.getChildAt(0).setBackgroundResource(R.drawable.click_border)
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle("カン")
                .setMessage("鳴く牌を選択してください")
                .setView(if(enable) scrollLayout else kongLayout)
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