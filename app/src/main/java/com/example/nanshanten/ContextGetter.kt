package com.example.nanshanten

import android.app.Application
import android.content.Context

class ContextGetter : Application() {

    init {
        instance = this
    }

    companion object {
        private var instance: ContextGetter? = null

        fun applicationContext() : Context {
            return instance!!.applicationContext
        }
    }
}