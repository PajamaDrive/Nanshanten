package com.example.nanshanten

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class YakuFragment : Fragment(R.layout.yaku_list){
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.yaku_list, container, false)
        return view
    }


    override fun onStart() {
        super.onStart()
    }
}