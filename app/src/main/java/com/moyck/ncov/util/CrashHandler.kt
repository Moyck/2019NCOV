package com.moyck.ncov.util

import android.content.Context
import android.util.Log

class CrashHandler : Thread.UncaughtExceptionHandler {

    /**
     *  单例对象
     */
    companion object {
        val Instance by lazy { CrashHandler() }
    }

    private lateinit var mContext: Context

    override fun uncaughtException(t: Thread?, e: Throwable?) {
        Log.e("uncaughtException", ""+e.toString())
    }

    /**
     * 在Application 中进行全局初始化
     */
    fun init(context: Context) {
        mContext = context
        Thread.setDefaultUncaughtExceptionHandler(this)
    }
}